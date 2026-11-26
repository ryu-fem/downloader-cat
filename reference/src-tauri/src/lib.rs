use serde::{Deserialize, Serialize};
use serde_json::Value;
use std::collections::HashMap;
use std::io::{BufRead, BufReader};
use std::path::PathBuf;
use std::process::{Child, Command, Stdio};
use std::sync::atomic::{AtomicU64, Ordering};
use std::sync::{Arc, Mutex};
use std::thread;
use std::time::{SystemTime, UNIX_EPOCH};
use tauri::{AppHandle, Emitter, Manager, State};
use tauri_plugin_opener::OpenerExt;

const FORMAT_HEIGHTS: [u64; 6] = [2160, 1440, 1080, 720, 480, 360];
const SETTINGS_FILE: &str = "settings.json";
const HISTORY_FILE: &str = "history.json";
const HISTORY_LIMIT: usize = 500;
const QUALITY_VALUES: [&str; 8] = ["best", "4k", "1440p", "1080p", "720p", "480p", "360p", "mp3"];
const COOKIE_BROWSERS: [&str; 5] = ["none", "chrome", "firefox", "chromium", "edge"];
const LANGUAGE_VALUES: [&str; 2] = ["en", "ar"];
const PROGRESS_MARKER: &str = "DCPROG|";
const PATH_MARKER: &str = "DCPATH|";
const PROGRESS_TEMPLATE: &str = "DCPROG|%(progress._percent_str)s|%(progress._speed_str)s|%(progress._eta_str)s|%(progress.downloaded_bytes)s|%(progress.total_bytes)s|%(progress.total_bytes_estimate)s";
const PATH_TEMPLATE: &str = "after_move:DCPATH|%(filepath)s";
static NEXT_DOWNLOAD_ID: AtomicU64 = AtomicU64::new(1);

#[derive(Serialize)]
struct EngineUpdate {
    version: String,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
struct FetchInfo {
    title: String,
    channel: String,
    duration: Option<f64>,
    thumbnail: Option<String>,
    site: String,
    formats: Vec<FormatInfo>,
    is_playlist: bool,
    video_in_playlist: bool,
    entries: Vec<EntryInfo>,
}

#[derive(Serialize)]
struct FormatInfo {
    id: String,
    label: String,
    size: Option<String>,
}

#[derive(Serialize)]
#[serde(rename_all = "camelCase")]
struct EntryInfo {
    id: String,
    title: String,
    duration: Option<f64>,
    thumbnail: Option<String>,
    url: Option<String>,
    filesize: Option<u64>,
}

#[derive(Default)]
struct DownloadState(Mutex<HashMap<String, Child>>);

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase")]
struct HistoryItem {
    id: String,
    title: String,
    site: String,
    #[serde(rename = "type")]
    kind: String,
    size: String,
    date: String,
    path: Option<String>,
    #[serde(default)]
    thumbnail: Option<String>,
}

impl HistoryItem {
    /// Drops empty/invalid entries and keeps stored values predictable.
    fn normalized(mut self) -> Option<Self> {
        self.id = self.id.trim().to_string();
        self.title = self.title.trim().to_string();
        if self.id.is_empty() || self.title.is_empty() {
            return None;
        }

        self.site = self.site.trim().to_string();
        if self.site.is_empty() {
            self.site = "Unknown site".to_string();
        }

        self.kind = self.kind.trim().to_ascii_lowercase();
        if !matches!(self.kind.as_str(), "video" | "audio" | "images") {
            self.kind = "video".to_string();
        }

        self.size = self.size.trim().to_string();
        if self.size.is_empty() {
            self.size = "—".to_string();
        }

        self.date = self.date.trim().to_string();
        self.path = self
            .path
            .map(|path| path.trim().to_string())
            .filter(|path| !path.is_empty());
        self.thumbnail = self
            .thumbnail
            .map(|thumbnail| thumbnail.trim().to_string())
            .filter(|thumbnail| !thumbnail.is_empty());

        Some(self)
    }
}

#[derive(Clone, Serialize)]
#[serde(rename_all = "camelCase")]
struct DownloadProgress {
    id: String,
    status: String,
    percent: Option<f64>,
    speed: Option<String>,
    eta: Option<String>,
    path: Option<String>,
    error: Option<String>,
}

#[derive(Debug, Clone, Serialize, Deserialize)]
#[serde(rename_all = "camelCase", default)]
struct AppSettings {
    download_folder: String,
    default_quality: String,
    concurrent_downloads: u32,
    speed_limit_mb: f64,
    proxy: String,
    cookies_from_browser: String,
    language: String,
    skip_picker_shared: bool,
}

impl Default for AppSettings {
    fn default() -> Self {
        Self {
            download_folder: default_download_folder()
                .map(|path| path.to_string_lossy().into_owned())
                .unwrap_or_default(),
            default_quality: "best".to_string(),
            concurrent_downloads: 2,
            speed_limit_mb: 0.0,
            proxy: String::new(),
            cookies_from_browser: "none".to_string(),
            language: "en".to_string(),
            skip_picker_shared: false,
        }
    }
}

impl AppSettings {
    fn normalized(mut self) -> Self {
        self.download_folder = normalize_download_folder(&self.download_folder);
        self.default_quality = self.default_quality.trim().to_ascii_lowercase();
        if !QUALITY_VALUES.contains(&self.default_quality.as_str()) {
            self.default_quality = "best".to_string();
        }

        self.concurrent_downloads = self.concurrent_downloads.clamp(1, 8);

        if !self.speed_limit_mb.is_finite() || self.speed_limit_mb < 0.0 {
            self.speed_limit_mb = 0.0;
        }

        self.proxy = self.proxy.trim().to_string();
        if !is_valid_proxy(&self.proxy) {
            self.proxy.clear();
        }

        self.cookies_from_browser = self.cookies_from_browser.trim().to_ascii_lowercase();
        if !COOKIE_BROWSERS.contains(&self.cookies_from_browser.as_str()) {
            self.cookies_from_browser = "none".to_string();
        }

        self.language = self.language.trim().to_ascii_lowercase();
        if !LANGUAGE_VALUES.contains(&self.language.as_str()) {
            self.language = "en".to_string();
        }

        self
    }
}

#[tauri::command]
fn get_settings(app: AppHandle) -> Result<AppSettings, String> {
    load_settings(&app)
}

#[tauri::command]
fn save_settings(app: AppHandle, mut settings: AppSettings) -> Result<AppSettings, String> {
    settings.proxy = settings.proxy.trim().to_string();
    if !is_valid_proxy(&settings.proxy) {
        return Err("Proxy must be empty or contain :// and must not start with -.".to_string());
    }

    let settings = settings.normalized();
    let path = settings_path(&app)?;
    if let Some(parent) = path.parent() {
        std::fs::create_dir_all(parent)
            .map_err(|error| format!("Could not create the settings folder: {error}"))?;
    }

    let json = serde_json::to_vec_pretty(&settings)
        .map_err(|error| format!("Could not serialize settings: {error}"))?;
    std::fs::write(&path, json)
        .map_err(|error| format!("Could not save settings: {error}"))?;

    Ok(settings)
}

#[tauri::command]
fn list_history(app: AppHandle) -> Result<Vec<HistoryItem>, String> {
    load_history(&app)
}

#[tauri::command]
fn add_history_item(app: AppHandle, item: HistoryItem) -> Result<(), String> {
    let item = item
        .normalized()
        .ok_or_else(|| "History item needs an id and a title.".to_string())?;
    let mut history = load_history(&app)?;
    history.retain(|entry| entry.id != item.id);
    history.insert(0, item);
    history.truncate(HISTORY_LIMIT);

    save_history(&app, &history)
}

#[tauri::command]
fn delete_history_item(app: AppHandle, id: String) -> Result<(), String> {
    let id = id.trim();
    if id.is_empty() {
        return Ok(());
    }

    let mut history = load_history(&app)?;
    history.retain(|entry| entry.id != id);

    save_history(&app, &history)
}

#[tauri::command]
fn open_path(app: AppHandle, path: String) -> Result<(), String> {
    let path = path.trim();
    if path.is_empty() {
        return Err("No file path available.".to_string());
    }

    let path = PathBuf::from(path);
    if !path.exists() {
        return Err(format!("Path no longer exists: {}", path.display()));
    }

    // Opens with the desktop's default app; directories open in the file manager.
    app.opener()
        .open_path(path.to_string_lossy().into_owned(), None::<&str>)
        .map_err(|error| format!("Could not open {}: {error}", path.display()))
}

#[tauri::command]
fn get_pending_share() -> Option<String> {
    // Desktop has no share intents; Android will provide this through a Kotlin plugin.
    // TODO(android): return the pending share intent URL.
    None
}

#[tauri::command]
async fn update_engine() -> Result<EngineUpdate, String> {
    let version = tauri::async_runtime::spawn_blocking(run_update_engine)
        .await
        .map_err(|error| format!("yt-dlp update task failed: {error}"))??;

    Ok(EngineUpdate { version })
}

fn run_update_engine() -> Result<String, String> {
    let update = Command::new("yt-dlp")
        .arg("-U")
        .output()
        .map_err(|error| {
            if error.kind() == std::io::ErrorKind::NotFound {
                "yt-dlp was not found. Install it or make sure it is on PATH.".to_string()
            } else {
                format!("Could not run yt-dlp: {error}")
            }
        })?;

    // Package-managed installs cannot self-update; surface yt-dlp's own message.
    if !update.status.success() {
        let stderr = String::from_utf8_lossy(&update.stderr);
        let stdout = String::from_utf8_lossy(&update.stdout);
        let detail = if stderr.trim().is_empty() {
            stdout.into_owned()
        } else {
            stderr.into_owned()
        };

        return Err(format!(
            "yt-dlp update failed ({:?}): {}",
            update.status,
            truncate_detail(&detail)
        ));
    }

    let version = Command::new("yt-dlp")
        .arg("--version")
        .output()
        .map_err(|error| format!("Could not read the yt-dlp version: {error}"))?;

    if !version.status.success() {
        return Err("Could not read the yt-dlp version after updating.".to_string());
    }

    let version = String::from_utf8_lossy(&version.stdout).trim().to_string();
    if version.is_empty() {
        return Err("yt-dlp returned an empty version string.".to_string());
    }

    Ok(version)
}

#[tauri::command]
fn greet(name: &str) -> String {
    format!("Hello, {}! You've been greeted from Rust!", name)
}

#[tauri::command]
async fn fetch_info(
    app: AppHandle,
    url: String,
    no_playlist: Option<bool>,
) -> Result<FetchInfo, String> {
    let url = url.trim().to_string();
    let lower_url = url.to_ascii_lowercase();

    if !lower_url.starts_with("http://") && !lower_url.starts_with("https://") {
        return Err("Only http:// and https:// links are supported.".to_string());
    }

    let settings = load_settings(&app).unwrap_or_else(|_| AppSettings::default().normalized());
    let proxy = settings.proxy;
    let cookies_browser = settings.cookies_from_browser;
    let yt_proxy = proxy.clone();
    let yt_cookies = cookies_browser.clone();
    let request_url = url.clone();
    let no_playlist = no_playlist.unwrap_or(false);
    let yt_output = tauri::async_runtime::spawn_blocking(move || {
        let mut command = Command::new("yt-dlp");
        command
            .arg("--dump-single-json")
            .arg("--no-warnings")
            .arg("--flat-playlist")
            .arg("--socket-timeout")
            .arg("15");

        if no_playlist {
            command.arg("--no-playlist");
        }

        if !yt_proxy.is_empty() {
            command.arg("--proxy").arg(&yt_proxy);
        }

        if yt_cookies != "none" {
            command.arg("--cookies-from-browser").arg(&yt_cookies);
        }

        command.arg("--").arg(&request_url).output()
    })
    .await
    .map_err(|error| format!("yt-dlp task failed: {error}"))?;

    // Try yt-dlp first, then fall back to gallery-dl for image-first sites
    // (Pinterest, Instagram, ...) that yt-dlp cannot extract.
    let yt_error = match yt_output {
        Ok(output) if output.status.success() => {
            let value: Value = serde_json::from_slice(&output.stdout)
                .map_err(|error| format!("Could not parse yt-dlp JSON: {error}"))?;

            return parse_fetch_info(&value, &url);
        }
        Ok(output) => {
            let stderr = String::from_utf8_lossy(&output.stderr);
            let stdout = String::from_utf8_lossy(&output.stdout);
            let detail = if stderr.trim().is_empty() {
                stdout.into_owned()
            } else {
                stderr.into_owned()
            };
            let detail = truncate_detail(&detail);

            if let Some(message) = cookie_failure_message(&detail, &cookies_browser) {
                message
            } else {
                format!("yt-dlp failed ({:?}): {detail}", output.status)
            }
        }
        Err(error) => {
            if error.kind() == std::io::ErrorKind::NotFound {
                "yt-dlp was not found. Install it or make sure it is on PATH.".to_string()
            } else {
                format!("Could not run yt-dlp: {error}")
            }
        }
    };

    let gallery_url = url.clone();
    let gallery_proxy = proxy;
    let gallery_cookies = cookies_browser;
    let gallery_result = tauri::async_runtime::spawn_blocking(move || {
        fetch_info_with_gallery_dl(&gallery_url, &gallery_proxy, &gallery_cookies)
    })
    .await
    .map_err(|error| format!("gallery-dl task failed: {error}"))?;

    gallery_result.map_err(|gallery_error| {
        if gallery_error.starts_with("gallery-dl was not found") {
            // gallery-dl is an optional fallback; keep the original yt-dlp error.
            yt_error
        } else {
            format!("{yt_error} (image fallback: {gallery_error})")
        }
    })
}

#[tauri::command]
fn start_download(
    app: AppHandle,
    state: State<'_, DownloadState>,
    url: String,
    quality: String,
    audio_only: Option<bool>,
    subtitles: Option<bool>,
    embed_thumbnail: Option<bool>,
) -> Result<String, String> {
    let url = url.trim().to_string();
    let lower_url = url.to_ascii_lowercase();

    if !lower_url.starts_with("http://") && !lower_url.starts_with("https://") {
        return Err("Only http:// and https:// links are supported.".to_string());
    }

    let settings = load_settings(&app).unwrap_or_else(|_| AppSettings::default().normalized());
    let folder = PathBuf::from(settings.download_folder.trim());
    let folder = if folder.as_os_str().is_empty() {
        download_folder()?
    } else {
        folder
    };
    let speed_limit_mb = settings.speed_limit_mb;
    let proxy = settings.proxy;
    let cookies_browser = settings.cookies_from_browser;
    let cookies_browser_for_command = cookies_browser.clone();

    std::fs::create_dir_all(&folder)
        .map_err(|error| format!("Could not create the download folder: {error}"))?;

    let quality_key = quality.trim().to_ascii_lowercase();

    // Image galleries are fetched with gallery-dl instead of yt-dlp.
    if quality_key == "images" {
        return start_gallery_download(app, state, &url, folder, proxy, cookies_browser);
    }

    let download_id = next_download_id();
    let audio_only = audio_only.unwrap_or(false) || quality_key == "mp3";
    let output_template = format!(
        "{}/%(title).150B [%(id)s].%(ext)s",
        folder.to_string_lossy()
    );

    let mut command = Command::new("yt-dlp");
    command
        .arg("--newline")
        .arg("--progress")
        .arg("--no-warnings")
        .arg("--no-playlist")
        .arg("--no-simulate")
        .arg("--windows-filenames")
        .arg("--socket-timeout")
        .arg("15")
        .arg("--progress-template")
        .arg(PROGRESS_TEMPLATE)
        .arg("--print")
        .arg(PATH_TEMPLATE)
        .arg("-o")
        .arg(output_template);

    if speed_limit_mb > 0.0 {
        let limit_kb = (speed_limit_mb * 1024.0).round().max(1.0) as u64;
        command.arg("--limit-rate").arg(format!("{limit_kb}K"));
    }

    if !proxy.is_empty() {
        command.arg("--proxy").arg(&proxy);
    }

    if cookies_browser_for_command != "none" {
        command
            .arg("--cookies-from-browser")
            .arg(&cookies_browser_for_command);
    }

    if audio_only {
        command
            .arg("-x")
            .arg("--audio-format")
            .arg("mp3")
            .arg("--audio-quality")
            .arg("192K");
    } else {
        command
            .arg("-f")
            .arg(quality_selector(&quality_key))
            .arg("--merge-output-format")
            .arg("mp4");
    }

    if subtitles.unwrap_or(false) {
        command
            .arg("--write-subs")
            .arg("--sub-langs")
            .arg("all")
            .arg("--embed-subs");
    }

    // Embedding subtitles/thumbnails needs ffmpeg on PATH; yt-dlp reports a clear
    // error if it is missing, which we surface through the download error event.
    if embed_thumbnail.unwrap_or(false) {
        command.arg("--embed-thumbnail");
    }

    command
        .arg("--")
        .arg(&url)
        .stdout(Stdio::piped())
        .stderr(Stdio::piped());

    let mut children = state
        .0
        .lock()
        .map_err(|_| "Download state is poisoned.".to_string())?;

    let mut child = command.spawn().map_err(|error| {
        if error.kind() == std::io::ErrorKind::NotFound {
            "yt-dlp was not found. Install it or make sure it is on PATH.".to_string()
        } else {
            format!("Could not run yt-dlp: {error}")
        }
    })?;

    let stdout = match child.stdout.take() {
        Some(stdout) => stdout,
        None => {
            let _ = child.kill();
            let _ = child.wait();
            return Err("Could not capture yt-dlp output.".to_string());
        }
    };

    let stderr = match child.stderr.take() {
        Some(stderr) => stderr,
        None => {
            let _ = child.kill();
            let _ = child.wait();
            return Err("Could not capture yt-dlp errors.".to_string());
        }
    };

    children.insert(download_id.clone(), child);
    drop(children);

    emit_progress(&app, &download_id, "starting", None, None, None, None, None);

    let thread_app = app.clone();
    let thread_id = download_id.clone();
    let thread_cookies_browser = cookies_browser;

    thread::spawn(move || {
        let stderr_tail = Arc::new(Mutex::new(String::new()));
        let stderr_tail_for_thread = Arc::clone(&stderr_tail);
        let stderr_handle = thread::spawn(move || {
            let reader = BufReader::new(stderr);
            for line in reader.lines() {
                let Ok(line) = line else {
                    break;
                };
                let line = line.trim();
                if !line.is_empty() {
                    if let Ok(mut tail) = stderr_tail_for_thread.lock() {
                        *tail = line.to_string();
                    }
                }
            }
        });

        let mut final_path = None;
        let mut processing_sent = false;
        let reader = BufReader::new(stdout);

        for line in reader.lines() {
            let Ok(line) = line else {
                break;
            };
            let line = line.trim();
            if line.is_empty() {
                continue;
            }

            let line = line
                .strip_prefix("[download] ")
                .or_else(|| line.strip_prefix("download:"))
                .unwrap_or(line);

            if let Some(progress_line) = line.strip_prefix(PROGRESS_MARKER) {
                let progress = parse_progress(progress_line);
                let percent = progress.percent;
                emit_progress(
                    &thread_app,
                    &thread_id,
                    "downloading",
                    percent,
                    progress.speed,
                    progress.eta,
                    None,
                    None,
                );

                if percent.unwrap_or(0.0) >= 100.0 && !processing_sent {
                    processing_sent = true;
                    emit_progress(
                        &thread_app,
                        &thread_id,
                        "processing",
                        Some(100.0),
                        None,
                        None,
                        None,
                        None,
                    );
                }
            } else if let Some(path_line) = line.strip_prefix(PATH_MARKER) {
                let path = path_line.trim();
                if !path.is_empty() && !is_missing_value(path) {
                    final_path = Some(path.to_string());
                }

                if !processing_sent {
                    processing_sent = true;
                    emit_progress(
                        &thread_app,
                        &thread_id,
                        "processing",
                        Some(100.0),
                        None,
                        None,
                        final_path.clone(),
                        None,
                    );
                }
            }
        }

        let _ = stderr_handle.join();
        let last_stderr = stderr_tail
            .lock()
            .map(|tail| tail.clone())
            .unwrap_or_default();

        let child = {
            let state = thread_app.state::<DownloadState>();
            let mut children = match state.0.lock() {
                Ok(children) => children,
                Err(_) => {
                    emit_progress(
                        &thread_app,
                        &thread_id,
                        "error",
                        None,
                        None,
                        None,
                        final_path,
                        Some("Download state is poisoned.".to_string()),
                    );
                    return;
                }
            };
            children.remove(&thread_id)
        };

        let Some(mut child) = child else {
            emit_progress(
                &thread_app,
                &thread_id,
                "cancelled",
                None,
                None,
                None,
                None,
                None,
            );
            return;
        };

        match child.wait() {
            Ok(status) if status.success() => {
                emit_progress(
                    &thread_app,
                    &thread_id,
                    "done",
                    Some(100.0),
                    None,
                    None,
                    final_path,
                    None,
                );
            }
            Ok(status) => {
                let error = if last_stderr.is_empty() {
                    format!("yt-dlp exited with {status:?}.")
                } else if let Some(message) =
                    cookie_failure_message(&last_stderr, &thread_cookies_browser)
                {
                    message
                } else {
                    format!("yt-dlp failed ({status:?}): {}", truncate_detail(&last_stderr))
                };
                emit_progress(
                    &thread_app,
                    &thread_id,
                    "error",
                    None,
                    None,
                    None,
                    final_path,
                    Some(error),
                );
            }
            Err(error) => {
                emit_progress(
                    &thread_app,
                    &thread_id,
                    "error",
                    None,
                    None,
                    None,
                    final_path,
                    Some(format!("Could not wait for yt-dlp: {error}")),
                );
            }
        }
    });

    Ok(download_id)
}

fn start_gallery_download(
    app: AppHandle,
    state: State<'_, DownloadState>,
    url: &str,
    folder: PathBuf,
    proxy: String,
    cookies_browser: String,
) -> Result<String, String> {
    let download_id = next_download_id();
    let mut command = Command::new("gallery-dl");
    command
        .arg("--no-mtime")
        .arg("--Print")
        .arg("after:{_path}")
        .arg("--directory")
        .arg(folder.to_string_lossy().to_string());

    if !proxy.is_empty() {
        command.arg("--proxy").arg(&proxy);
    }

    if cookies_browser != "none" {
        command.arg("--cookies-from-browser").arg(&cookies_browser);
    }

    command
        .arg("--")
        .arg(url)
        .stdout(Stdio::piped())
        .stderr(Stdio::piped());

    let mut children = state
        .0
        .lock()
        .map_err(|_| "Download state is poisoned.".to_string())?;

    let mut child = command.spawn().map_err(|error| {
        if error.kind() == std::io::ErrorKind::NotFound {
            "gallery-dl was not found. Install it or make sure it is on PATH.".to_string()
        } else {
            format!("Could not run gallery-dl: {error}")
        }
    })?;

    let stdout = match child.stdout.take() {
        Some(stdout) => stdout,
        None => {
            let _ = child.kill();
            let _ = child.wait();
            return Err("Could not capture gallery-dl output.".to_string());
        }
    };

    let stderr = match child.stderr.take() {
        Some(stderr) => stderr,
        None => {
            let _ = child.kill();
            let _ = child.wait();
            return Err("Could not capture gallery-dl errors.".to_string());
        }
    };

    children.insert(download_id.clone(), child);
    drop(children);

    emit_progress(&app, &download_id, "starting", None, None, None, None, None);

    let thread_app = app.clone();
    let thread_id = download_id.clone();
    let thread_cookies_browser = cookies_browser;
    let fallback_path = folder.to_string_lossy().to_string();

    thread::spawn(move || {
        emit_progress(
            &thread_app,
            &thread_id,
            "downloading",
            None,
            None,
            None,
            None,
            None,
        );

        let stderr_tail = Arc::new(Mutex::new(String::new()));
        let stderr_tail_for_thread = Arc::clone(&stderr_tail);
        let stderr_handle = thread::spawn(move || {
            let reader = BufReader::new(stderr);
            for line in reader.lines() {
                let Ok(line) = line else {
                    break;
                };
                let line = line.trim();
                if !line.is_empty() {
                    if let Ok(mut tail) = stderr_tail_for_thread.lock() {
                        *tail = line.to_string();
                    }
                }
            }
        });

        let mut downloaded_paths: Vec<String> = Vec::new();
        let reader = BufReader::new(stdout);

        // gallery-dl prints one absolute path per downloaded file via --Print,
        // sometimes more than once, so only keep unique paths.
        for line in reader.lines() {
            let Ok(line) = line else {
                break;
            };
            let line = line.trim();
            if !line.is_empty()
                && std::path::Path::new(line).is_absolute()
                && !downloaded_paths.iter().any(|path| path == line)
            {
                downloaded_paths.push(line.to_string());
            }
        }

        let _ = stderr_handle.join();
        let last_stderr = stderr_tail
            .lock()
            .map(|tail| tail.clone())
            .unwrap_or_default();

        let child = {
            let state = thread_app.state::<DownloadState>();
            let mut children = match state.0.lock() {
                Ok(children) => children,
                Err(_) => {
                    emit_progress(
                        &thread_app,
                        &thread_id,
                        "error",
                        None,
                        None,
                        None,
                        downloaded_paths.last().cloned(),
                        Some("Download state is poisoned.".to_string()),
                    );
                    return;
                }
            };
            children.remove(&thread_id)
        };

        let Some(mut child) = child else {
            emit_progress(
                &thread_app,
                &thread_id,
                "cancelled",
                None,
                None,
                None,
                None,
                None,
            );
            return;
        };

        // Open the folder for multi-image galleries, otherwise the single file.
        let final_path = if downloaded_paths.len() > 1 {
            Some(fallback_path)
        } else {
            downloaded_paths.pop().or(Some(fallback_path))
        };

        match child.wait() {
            Ok(status) if status.success() => {
                emit_progress(
                    &thread_app,
                    &thread_id,
                    "done",
                    Some(100.0),
                    None,
                    None,
                    final_path,
                    None,
                );
            }
            Ok(status) => {
                let error = if last_stderr.is_empty() {
                    format!("gallery-dl exited with {status:?}.")
                } else if let Some(message) =
                    cookie_failure_message(&last_stderr, &thread_cookies_browser)
                {
                    message
                } else {
                    format!(
                        "gallery-dl failed ({status:?}): {}",
                        truncate_detail(&last_stderr)
                    )
                };
                emit_progress(
                    &thread_app,
                    &thread_id,
                    "error",
                    None,
                    None,
                    None,
                    final_path,
                    Some(error),
                );
            }
            Err(error) => {
                emit_progress(
                    &thread_app,
                    &thread_id,
                    "error",
                    None,
                    None,
                    None,
                    final_path,
                    Some(format!("Could not wait for gallery-dl: {error}")),
                );
            }
        }
    });

    Ok(download_id)
}

#[tauri::command]
fn cancel_download(state: State<'_, DownloadState>, id: String) -> Result<(), String> {
    let mut children = state
        .0
        .lock()
        .map_err(|_| "Download state is poisoned.".to_string())?;

    {
        let Some(child) = children.get_mut(&id) else {
            return Err("No active download with that id.".to_string());
        };

        match child.try_wait() {
            Ok(Some(_)) => return Ok(()),
            Ok(None) => {}
            Err(error) => return Err(format!("Could not check download status: {error}")),
        }
    }

    let mut child = children
        .remove(&id)
        .ok_or_else(|| "No active download with that id.".to_string())?;

    if let Err(error) = child.kill() {
        children.insert(id, child);
        return Err(format!("Could not cancel download: {error}"));
    }

    drop(children);

    child
        .wait()
        .map_err(|error| format!("Could not wait for cancelled download: {error}"))?;

    Ok(())
}

#[tauri::command]
fn pause_download(
    app: AppHandle,
    state: State<'_, DownloadState>,
    id: String,
) -> Result<(), String> {
    signal_download(&state, &id, "STOP")?;
    emit_progress(&app, &id, "paused", None, None, None, None, None);
    Ok(())
}

#[tauri::command]
fn resume_download(
    app: AppHandle,
    state: State<'_, DownloadState>,
    id: String,
) -> Result<(), String> {
    signal_download(&state, &id, "CONT")?;
    emit_progress(&app, &id, "downloading", None, None, None, None, None);
    Ok(())
}

fn signal_download(state: &DownloadState, id: &str, signal: &str) -> Result<(), String> {
    // Look up the download pid while holding the lock, then release it before signalling.
    let pid = {
        let children = state
            .0
            .lock()
            .map_err(|_| "Download state is poisoned.".to_string())?;

        children
            .get(id)
            .map(Child::id)
            .ok_or_else(|| "No active download with that id.".to_string())?
    };

    // Linux target: `kill` is always available, so we avoid adding a signal crate.
    let status = Command::new("kill")
        .arg(format!("-{signal}"))
        .arg(pid.to_string())
        .status()
        .map_err(|error| format!("Could not signal the download process: {error}"))?;

    if status.success() {
        Ok(())
    } else {
        Err(format!(
            "Could not signal the download process (kill -{signal} exited with {status:?})."
        ))
    }
}

fn emit_progress(
    app: &AppHandle,
    id: &str,
    status: &str,
    percent: Option<f64>,
    speed: Option<String>,
    eta: Option<String>,
    path: Option<String>,
    error: Option<String>,
) {
    let payload = DownloadProgress {
        id: id.to_string(),
        status: status.to_string(),
        percent,
        speed,
        eta,
        path,
        error,
    };
    let _ = app.emit("download-progress", payload);
}

fn home_dir() -> Option<PathBuf> {
    std::env::var_os("HOME")
        .or_else(|| std::env::var_os("USERPROFILE"))
        .map(PathBuf::from)
}

fn default_download_folder() -> Option<PathBuf> {
    home_dir().map(|home| home.join("Downloads").join("DownloaderCat"))
}

fn download_folder() -> Result<PathBuf, String> {
    default_download_folder().ok_or_else(|| "Could not find the home folder.".to_string())
}

fn normalize_download_folder(value: &str) -> String {
    let trimmed = value.trim();
    let fallback = || {
        default_download_folder()
            .map(|path| path.to_string_lossy().into_owned())
            .unwrap_or_default()
    };

    if trimmed.is_empty() {
        return fallback();
    }

    if trimmed == "~" {
        return home_dir()
            .map(|path| path.to_string_lossy().into_owned())
            .unwrap_or_else(fallback);
    }

    if let Some(rest) = trimmed.strip_prefix("~/") {
        return home_dir()
            .map(|home| home.join(rest).to_string_lossy().into_owned())
            .unwrap_or_else(fallback);
    }

    let path = PathBuf::from(trimmed);
    if path.is_absolute() {
        path.to_string_lossy().into_owned()
    } else {
        fallback()
    }
}

fn is_valid_proxy(value: &str) -> bool {
    let value = value.trim();
    value.is_empty() || (!value.starts_with('-') && value.contains("://"))
}

fn config_file(app: &AppHandle, file: &str) -> Result<PathBuf, String> {
    if let Ok(dir) = app.path().app_config_dir() {
        return Ok(dir.join(file));
    }

    let home = home_dir().ok_or_else(|| "Could not find the home folder.".to_string())?;
    Ok(home.join(".config").join("downloader-cat").join(file))
}

fn settings_path(app: &AppHandle) -> Result<PathBuf, String> {
    config_file(app, SETTINGS_FILE)
}

fn history_path(app: &AppHandle) -> Result<PathBuf, String> {
    config_file(app, HISTORY_FILE)
}

fn load_history(app: &AppHandle) -> Result<Vec<HistoryItem>, String> {
    let path = history_path(app)?;
    let bytes = match std::fs::read(&path) {
        Ok(bytes) => bytes,
        Err(error) if error.kind() == std::io::ErrorKind::NotFound => return Ok(Vec::new()),
        Err(error) => return Err(format!("Could not read history: {error}")),
    };

    serde_json::from_slice(&bytes).map_err(|error| format!("Could not parse history: {error}"))
}

fn save_history(app: &AppHandle, history: &[HistoryItem]) -> Result<(), String> {
    let path = history_path(app)?;
    if let Some(parent) = path.parent() {
        std::fs::create_dir_all(parent)
            .map_err(|error| format!("Could not create the history folder: {error}"))?;
    }

    let json = serde_json::to_vec_pretty(history)
        .map_err(|error| format!("Could not serialize history: {error}"))?;
    std::fs::write(&path, json).map_err(|error| format!("Could not save history: {error}"))
}

fn load_settings(app: &AppHandle) -> Result<AppSettings, String> {
    let path = settings_path(app)?;
    let bytes = match std::fs::read(&path) {
        Ok(bytes) => bytes,
        Err(error) if error.kind() == std::io::ErrorKind::NotFound => {
            return Ok(AppSettings::default().normalized());
        }
        Err(error) => return Err(format!("Could not read settings: {error}")),
    };

    let settings: AppSettings = serde_json::from_slice(&bytes)
        .map_err(|error| format!("Could not parse settings: {error}"))?;
    Ok(settings.normalized())
}

fn browser_label(browser: &str) -> &str {
    match browser {
        "chrome" => "Chrome",
        "firefox" => "Firefox",
        "chromium" => "Chromium",
        "edge" => "Edge",
        _ => "the selected browser",
    }
}

fn cookie_failure_message(detail: &str, browser: &str) -> Option<String> {
    if browser.is_empty() || browser == "none" {
        return None;
    }

    let lower = detail.to_ascii_lowercase();
    if !lower.contains("cookie") && !lower.contains("decrypt") {
        return None;
    }

    Some(format!(
        "yt-dlp could not read cookies from {}. Close the browser and try again, or choose None for cookies. Details: {}",
        browser_label(browser),
        truncate_detail(detail)
    ))
}

fn next_download_id() -> String {
    let millis = SystemTime::now()
        .duration_since(UNIX_EPOCH)
        .map(|duration| duration.as_millis())
        .unwrap_or(0);
    let counter = NEXT_DOWNLOAD_ID.fetch_add(1, Ordering::Relaxed);

    format!("download-{millis}-{counter}")
}

fn quality_selector(quality: &str) -> String {
    let max_height = match quality {
        "4k" => Some(2160),
        "1440p" => Some(1440),
        "1080p" => Some(1080),
        "720p" => Some(720),
        "480p" => Some(480),
        "360p" => Some(360),
        _ => None,
    };

    match max_height {
        Some(height) => format!("bv*[height<={height}]+ba/b[height<={height}]/b"),
        None => "bv*+ba/b".to_string(),
    }
}

struct ParsedProgress {
    percent: Option<f64>,
    speed: Option<String>,
    eta: Option<String>,
}

fn parse_progress(line: &str) -> ParsedProgress {
    let mut parts = line.split('|');
    let percent_value = parts.next().unwrap_or_default();
    let speed_value = parts.next().unwrap_or_default();
    let eta_value = parts.next().unwrap_or_default();
    let downloaded_value = parts.next().unwrap_or_default();
    let total_value = parts.next().unwrap_or_default();
    let estimate_value = parts.next().unwrap_or_default();

    let downloaded = parse_number(downloaded_value);
    let total = parse_number(total_value)
        .filter(|total| *total > 0.0)
        .or_else(|| parse_number(estimate_value));
    let percent = parse_percent(percent_value).or_else(|| match (downloaded, total) {
        (Some(downloaded), Some(total)) if total > 0.0 => {
            Some((downloaded / total * 100.0).clamp(0.0, 100.0))
        }
        _ => None,
    });

    ParsedProgress {
        percent,
        speed: clean_value(speed_value),
        eta: clean_value(eta_value),
    }
}

fn parse_percent(value: &str) -> Option<f64> {
    let value = value.trim().trim_end_matches('%').trim();
    parse_number(value).map(|percent| percent.clamp(0.0, 100.0))
}

fn parse_number(value: &str) -> Option<f64> {
    let value = value.trim();
    if is_missing_value(value) {
        return None;
    }

    value
        .parse::<f64>()
        .ok()
        .filter(|number| number.is_finite() && *number >= 0.0)
}

fn clean_value(value: &str) -> Option<String> {
    let value = value.trim();
    if is_missing_value(value) {
        None
    } else {
        Some(value.to_string())
    }
}

fn is_missing_value(value: &str) -> bool {
    value.is_empty()
        || value.eq_ignore_ascii_case("na")
        || value.eq_ignore_ascii_case("n/a")
        || value.eq_ignore_ascii_case("unknown")
        || value.eq_ignore_ascii_case("none")
}

fn parse_fetch_info(value: &Value, url: &str) -> Result<FetchInfo, String> {
    let title = string_field(value, "title")
        .ok_or_else(|| "yt-dlp returned no title.".to_string())?;
    let channel = channel_from_value(value);
    let duration = number_field(value, "duration");
    let thumbnail = thumbnail_from_value(value);
    let site = site_from_value(value, url);
    let formats = build_formats(value);
    let entries = build_entries(value);
    let is_playlist = value.get("_type").and_then(Value::as_str) == Some("playlist")
        || !entries.is_empty();
    let video_in_playlist = has_video_in_playlist(url);

    Ok(FetchInfo {
        title,
        channel,
        duration,
        thumbnail,
        site,
        formats,
        is_playlist,
        video_in_playlist,
        entries,
    })
}

fn fetch_info_with_gallery_dl(
    url: &str,
    proxy: &str,
    cookies_browser: &str,
) -> Result<FetchInfo, String> {
    let mut command = Command::new("gallery-dl");
    command.arg("--dump-json");

    if !proxy.is_empty() {
        command.arg("--proxy").arg(proxy);
    }

    if cookies_browser != "none" {
        command.arg("--cookies-from-browser").arg(cookies_browser);
    }

    let output = command.arg("--").arg(url).output().map_err(|error| {
        if error.kind() == std::io::ErrorKind::NotFound {
            "gallery-dl was not found. Install it or make sure it is on PATH.".to_string()
        } else {
            format!("Could not run gallery-dl: {error}")
        }
    })?;

    if !output.status.success() {
        let stderr = String::from_utf8_lossy(&output.stderr);
        let stdout = String::from_utf8_lossy(&output.stdout);
        let detail = if stderr.trim().is_empty() {
            stdout.into_owned()
        } else {
            stderr.into_owned()
        };

        return Err(format!(
            "gallery-dl failed ({:?}): {}",
            output.status,
            truncate_detail(&detail)
        ));
    }

    let value: Value = serde_json::from_slice(&output.stdout)
        .map_err(|error| format!("Could not parse gallery-dl JSON: {error}"))?;
    let items = value
        .as_array()
        .ok_or_else(|| "gallery-dl returned an unexpected JSON shape.".to_string())?;

    let mut media_count = 0usize;
    let mut has_video = false;
    let mut title = None;
    let mut caption = None;
    let mut channel = None;
    let mut site = None;
    let mut thumbnail = None;

    for item in items {
        // Message.Url serializes as [status, url, metadata]; other messages as
        // [status, metadata]. Pick whichever element holds the metadata object.
        let entry = item
            .get(2)
            .filter(|value| value.is_object())
            .or_else(|| item.get(1).filter(|value| value.is_object()));

        let Some(entry) = entry else {
            continue;
        };

        if entry.get("error").is_some() {
            continue;
        }

        let message_url = item.get(1).and_then(Value::as_str);
        let extension = string_field(entry, "extension")
            .unwrap_or_default()
            .to_ascii_lowercase();

        // Directory messages carry the same metadata as their media entries.
        if title.is_none() {
            title = string_field(entry, "title").or_else(|| string_field(entry, "name"));
        }
        if caption.is_none() {
            caption = string_field(entry, "content")
                .or_else(|| string_field(entry, "caption"))
                .or_else(|| string_field(entry, "description"));
        }
        if channel.is_none() {
            channel = string_field(entry, "username")
                .or_else(|| string_field(entry, "user"))
                .or_else(|| string_field(entry, "fullname"))
                .or_else(|| string_field(entry, "uploader"))
                .or_else(|| string_field(entry, "author"))
                .or_else(|| string_field(entry, "owner"));
        }
        if site.is_none() {
            site = string_field(entry, "category");
        }

        // Only real media entries have an extension; URL results do not.
        if extension.is_empty() {
            continue;
        }

        media_count += 1;

        if is_video_extension(&extension) {
            has_video = true;
        }

        if thumbnail.is_none() {
            thumbnail = string_field(entry, "thumbnail");
            if thumbnail.is_none() && is_image_extension(&extension) {
                thumbnail = message_url
                    .map(ToOwned::to_owned)
                    .or_else(|| string_field(entry, "url"));
            }
        }
    }

    if media_count == 0 {
        return Err("gallery-dl found no downloadable media.".to_string());
    }

    let site = capitalize_first(
        &site.unwrap_or_else(|| host_from_url(url).unwrap_or_else(|| "Unknown site".to_string())),
    );
    let channel = channel.unwrap_or_else(|| "Unknown channel".to_string());
    let title = title
        .or_else(|| caption.map(|text| truncate_title(&text)))
        .unwrap_or_else(|| format!("{channel} on {site}"));

    Ok(FetchInfo {
        title,
        channel,
        duration: None,
        thumbnail,
        site,
        formats: vec![FormatInfo {
            id: "images".to_string(),
            label: if has_video {
                "Images & videos".to_string()
            } else {
                "Images".to_string()
            },
            size: None,
        }],
        is_playlist: false,
        video_in_playlist: false,
        entries: vec![],
    })
}

fn is_image_extension(extension: &str) -> bool {
    matches!(
        extension,
        "jpg" | "jpeg" | "png" | "webp" | "gif" | "bmp" | "avif" | "heic"
    )
}

fn is_video_extension(extension: &str) -> bool {
    matches!(extension, "mp4" | "webm" | "mkv" | "mov" | "m4v" | "ts")
}

fn capitalize_first(value: &str) -> String {
    let mut characters = value.chars();
    match characters.next() {
        Some(first) => first.to_uppercase().collect::<String>() + characters.as_str(),
        None => String::new(),
    }
}

fn truncate_title(value: &str) -> String {
    let collapsed = value.split_whitespace().collect::<Vec<_>>().join(" ");
    let mut characters = collapsed.chars();
    let shortened: String = characters.by_ref().take(120).collect();

    if characters.next().is_some() {
        format!("{shortened}…")
    } else {
        shortened
    }
}

fn build_formats(value: &Value) -> Vec<FormatInfo> {
    let mut seen = [false; FORMAT_HEIGHTS.len()];
    let mut sizes: [Option<u64>; FORMAT_HEIGHTS.len()] = [None; FORMAT_HEIGHTS.len()];
    let mut best_video_size = None;
    let mut best_audio_size = None;

    if let Some(formats) = value.get("formats").and_then(Value::as_array) {
        for format in formats {
            if is_ignored_format(format) {
                continue;
            }

            let size = format_size(format);

            if let Some(height) = format.get("height").and_then(Value::as_u64) {
                if let Some(index) = FORMAT_HEIGHTS.iter().position(|allowed| *allowed == height) {
                    seen[index] = true;
                    sizes[index] = max_size(sizes[index], size);
                }
                best_video_size = max_size(best_video_size, size);
            } else if is_audio_only(format) {
                best_audio_size = max_size(best_audio_size, size);
            }
        }
    }

    let mut formats = vec![FormatInfo {
        id: "best".to_string(),
        label: "Best".to_string(),
        size: max_size(best_video_size, best_audio_size).map(format_bytes),
    }];

    for (index, height) in FORMAT_HEIGHTS.iter().enumerate() {
        if !seen[index] {
            continue;
        }

        let is_4k = *height == 2160;
        formats.push(FormatInfo {
            id: if is_4k {
                "4k".to_string()
            } else {
                format!("{height}p")
            },
            label: if is_4k {
                "4K".to_string()
            } else {
                format!("{height}p")
            },
            size: sizes[index].map(format_bytes),
        });
    }

    formats.push(FormatInfo {
        id: "mp3".to_string(),
        label: "MP3".to_string(),
        size: best_audio_size.map(format_bytes),
    });

    formats
}

fn build_entries(value: &Value) -> Vec<EntryInfo> {
    value
        .get("entries")
        .and_then(Value::as_array)
        .map(|entries| entries.iter().filter_map(entry_from_value).collect())
        .unwrap_or_default()
}

fn entry_from_value(entry: &Value) -> Option<EntryInfo> {
    let url = string_field(entry, "url").or_else(|| string_field(entry, "webpage_url"));
    let id = string_field(entry, "id")
        .or_else(|| url.clone())
        .unwrap_or_else(|| "unknown".to_string());
    let title = string_field(entry, "title").unwrap_or_else(|| id.clone());
    let duration = number_field(entry, "duration");
    let thumbnail = direct_thumbnail(entry);
    let filesize = entry
        .get("filesize")
        .and_then(Value::as_u64)
        .or_else(|| entry.get("filesize_approx").and_then(Value::as_u64))
        .filter(|size| *size > 0);

    Some(EntryInfo {
        id,
        title,
        duration,
        thumbnail,
        url,
        filesize,
    })
}

fn is_ignored_format(format: &Value) -> bool {
    let ext = string_field(format, "ext")
        .unwrap_or_default()
        .to_ascii_lowercase();
    let protocol = string_field(format, "protocol")
        .unwrap_or_default()
        .to_ascii_lowercase();
    let format_id = string_field(format, "format_id")
        .unwrap_or_default()
        .to_ascii_lowercase();
    let note = string_field(format, "format_note")
        .unwrap_or_default()
        .to_ascii_lowercase();

    ext == "mhtml"
        || protocol.contains("mhtml")
        || note.contains("storyboard")
        || format_id.starts_with("sb")
}

fn is_audio_only(format: &Value) -> bool {
    let video_codec = string_field(format, "vcodec");
    let audio_codec = string_field(format, "acodec");

    video_codec.as_deref() == Some("none")
        && audio_codec
            .as_deref()
            .map(|codec| codec != "none")
            .unwrap_or(false)
}

fn format_size(format: &Value) -> Option<u64> {
    ["filesize", "filesize_approx"]
        .iter()
        .filter_map(|key| format.get(*key).and_then(numeric_u64))
        .max()
}

fn numeric_u64(value: &Value) -> Option<u64> {
    value
        .as_u64()
        .or_else(|| value.as_f64().filter(|number| number.is_finite() && *number >= 0.0).map(|number| number as u64))
}

fn max_size(current: Option<u64>, next: Option<u64>) -> Option<u64> {
    current.max(next)
}

fn format_bytes(bytes: u64) -> String {
    const KB: f64 = 1024.0;
    const MB: f64 = 1024.0 * 1024.0;
    const GB: f64 = 1024.0 * 1024.0 * 1024.0;
    let bytes = bytes as f64;

    if bytes >= GB {
        format!("{:.1} GB", bytes / GB)
    } else if bytes >= MB {
        format!("{:.1} MB", bytes / MB)
    } else {
        format!("{:.0} KB", (bytes / KB).max(1.0))
    }
}

fn string_field(value: &Value, key: &str) -> Option<String> {
    value
        .get(key)
        .and_then(Value::as_str)
        .map(str::trim)
        .filter(|text| !text.is_empty())
        .map(ToOwned::to_owned)
}

fn number_field(value: &Value, key: &str) -> Option<f64> {
    value
        .get(key)
        .and_then(Value::as_f64)
        .filter(|number| number.is_finite() && *number >= 0.0)
}

fn thumbnail_from_value(value: &Value) -> Option<String> {
    direct_thumbnail(value).or_else(|| first_entry_thumbnail(value))
}

fn direct_thumbnail(value: &Value) -> Option<String> {
    string_field(value, "thumbnail").or_else(|| {
        value
            .get("thumbnails")
            .and_then(Value::as_array)
            .and_then(|thumbnails| {
                thumbnails
                    .iter()
                    .rev()
                    .find_map(|thumbnail| string_field(thumbnail, "url"))
            })
    })
}

fn first_entry_thumbnail(value: &Value) -> Option<String> {
    value
        .get("entries")
        .and_then(Value::as_array)
        .and_then(|entries| entries.first())
        .and_then(direct_thumbnail)
}

fn channel_from_value(value: &Value) -> String {
    direct_channel(value)
        .or_else(|| first_entry_channel(value))
        .unwrap_or_else(|| "Unknown channel".to_string())
}

fn direct_channel(value: &Value) -> Option<String> {
    string_field(value, "channel")
        .or_else(|| string_field(value, "playlist_channel"))
        .or_else(|| string_field(value, "uploader"))
        .or_else(|| string_field(value, "playlist_uploader"))
        .or_else(|| string_field(value, "creator"))
}

fn first_entry_channel(value: &Value) -> Option<String> {
    value
        .get("entries")
        .and_then(Value::as_array)
        .and_then(|entries| entries.first())
        .and_then(direct_channel)
}

fn has_video_in_playlist(url: &str) -> bool {
    query_has_value(url, "v") && query_has_value(url, "list")
}

fn query_has_value(url: &str, key: &str) -> bool {
    let Some((_, query)) = url.split_once('?') else {
        return false;
    };
    let query = query.split('#').next().unwrap_or_default();

    query.split('&').any(|pair| {
        let Some((name, value)) = pair.split_once('=') else {
            return false;
        };

        name.eq_ignore_ascii_case(key) && !value.is_empty()
    })
}

fn site_from_value(value: &Value, url: &str) -> String {
    string_field(value, "extractor_key")
        .or_else(|| string_field(value, "extractor"))
        .or_else(|| host_from_url(url))
        .unwrap_or_else(|| "Unknown site".to_string())
}

fn host_from_url(url: &str) -> Option<String> {
    let (_, rest) = url.split_once("://")?;
    let authority = rest.split('/').next()?;
    let host = authority.rsplit('@').next()?.split(':').next()?;

    if host.is_empty() {
        None
    } else {
        Some(host.trim_start_matches("www.").to_string())
    }
}

fn truncate_detail(detail: &str) -> String {
    let detail = detail.trim();

    if detail.is_empty() {
        return "no error output".to_string();
    }

    let mut characters = detail.chars();
    let shortened: String = characters.by_ref().take(400).collect();

    if characters.next().is_some() {
        format!("{shortened}...")
    } else {
        shortened
    }
}

#[cfg_attr(mobile, tauri::mobile_entry_point)]
pub fn run() {
    tauri::Builder::default()
        .manage(DownloadState::default())
        .plugin(tauri_plugin_opener::init())
        .plugin(tauri_plugin_dialog::init())
        .invoke_handler(tauri::generate_handler![
            greet,
            fetch_info,
            start_download,
            cancel_download,
            pause_download,
            resume_download,
            get_settings,
            save_settings,
            update_engine,
            list_history,
            add_history_item,
            delete_history_item,
            open_path,
            get_pending_share
        ])
        .run(tauri::generate_context!())
        .expect("error while running tauri application");
}
