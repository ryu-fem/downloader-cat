# AGENTS.md - Downloader Cat

Instructions for AI coding agents working in this repo. Read this fully before changing anything.

## 1. Project

Downloader Cat is a video/audio/image downloader app (YouTube, TikTok, Instagram, Reddit, Pinterest, X and more) built on yt-dlp.

- Framework: Tauri 2
- Backend: Rust (`src-tauri/`)
- Frontend: plain HTML + CSS + vanilla JavaScript (no framework), served from `src/`
- Targets: Linux desktop first, Android later
- Design: minimal black and white UI, mobile-first (window is 400x800), dark theme by default with a light theme toggle

## 2. HARD RULES: DO NOT BUILD OR RUN ANYTHING

The developer already runs `npm run tauri dev` in his own terminal, with live reload. The laptop is weak (4 cores, limited disk), and a full Rust build takes 9+ minutes and gigabytes in `target/`.

**Never run any of these commands, directly or indirectly:**

- `npm run tauri dev`, `npm run tauri build`, `npm run tauri android ...`
- `cargo build`, `cargo run`, `cargo check`, `cargo clippy`, `cargo test`, `cargo install`
- `npm run build`, `npm run dev`, `vite build`
- `npm install` or adding dependencies, unless I ask for it explicitly (propose the exact command and wait)
- Anything that starts a server, opens a window, or compiles code

**What to do instead:**

- Only read and edit files. Saved changes show up in the running app automatically (frontend instantly, Rust after an automatic rebuild that I trigger).
- If you need to verify something, tell me what to run or what to look for and I will check it.
- If I paste a compiler or runtime error, fix it by editing the code.

## 3. Other rules

- Do not delete or rename files without asking.
- Do not run `git commit`, `git push` or any git write command unless I ask.
- One task at a time. Do not refactor unrelated code.
- Explain before editing `Cargo.toml`, `tauri.conf.json` or anything in `capabilities/`.
- Keep the code simple and commented where the logic is not obvious. No unnecessary abstractions.
- Put a comment `// TODO(rust): ...` wherever the frontend is waiting on a backend command.
- Never invent library versions or APIs. If unsure, say so and ask.

## 4. Project structure

```
downloader-cat/
├── AGENTS.md
├── src/                     <- frontend (served as the web root)
│   ├── index.html
│   ├── main.js
│   ├── styles.css
│   └── assets/
└── src-tauri/
    ├── src/lib.rs           <- Rust commands go here
    ├── tauri.conf.json
    ├── Cargo.toml
    └── capabilities/
```

Important: `index.html` lives inside `src/`, so asset paths are `/styles.css` and `/main.js`, never `/src/styles.css`.

## 5. Design system

- Colors: black `#000`, white `#fff`, grays only. No accent colors (tiny status dots allowed).
- Everything is driven by CSS variables (colors, spacing scale, radius scale, type scale). Dark is the default, light is toggled with a `data-theme="light"` attribute on `<html>`.
- 1px borders, rounded corners (12-16px), no gradients, no heavy shadows.
- Tap targets are at least 44px. Bottom tab bar with 4 tabs: Home, Downloads, History, Settings.
- Subtle animations only (progress bars, tab switches).
- Languages: English and Arabic, with RTL support.

## 6. Screens

1. **Home**: link input with Paste button, Fetch button, supported-site chips.
2. **Video result**: thumbnail, title, channel, duration, format picker (Best / 4K / 1080p / 720p / 480p / MP3) with sizes, toggles for subtitles and embed thumbnail, Download button.
3. **Downloads**: active downloads with progress, speed, ETA, pause/cancel/retry, plus a queue.
4. **History**: search, filters (All / Video / Audio / Images), open file, open folder, delete.
5. **Playlist**: item list with checkboxes, Select all, "N selected, size" bar, Download.
6. **Settings**: download folder, default quality, theme, concurrent downloads, speed limit, proxy, cookies from browser, language, update yt-dlp.

## 7. Backend commands (planned)

| Command | Input | Output |
|---|---|---|
| `fetch_info` | `url` | `{ title, channel, duration, thumbnail, site, formats[], is_playlist, entries[] }` |
| `start_download` | `url, quality, options` | `download_id` |
| `cancel_download` / `pause_download` / `resume_download` | `download_id` | `ok` |
| `list_history` / `delete_history_item` | filters / `id` | items / `ok` |
| `get_settings` / `save_settings` | - / settings | settings / `ok` |
| `update_engine` | - | `{ version }` |

Download progress is pushed to the frontend with Tauri events (`download-progress`: id, percent, speed, eta, status).

## 8. Download engine notes

- Desktop: run yt-dlp as a sidecar/child process and parse its JSON/progress output. ffmpeg is needed for merging and mp3.
- Reference behavior lives in the original Python script `down.py` (short-link expansion for pin.it, format selection, playlist presets, gallery-dl fallback for images). Port the logic, not the interactive `input()` prompts.
- Android: yt-dlp cannot run as a plain binary there. The plan is a Tauri mobile plugin in Kotlin wrapping `youtubedl-android`. Do not attempt this before the desktop version works.

## 9. Roadmap

1. Frontend shell: tab bar, theme toggle, Home screen (mock data).
2. Remaining screens with mock data and fake progress.
3. `fetch_info` command with real yt-dlp.
4. `start_download` with progress events, queue, history storage.
5. Settings, playlists, subtitles.
6. Android: Kotlin plugin, share intent, APK via GitHub Actions.

Always work on the current step only, and tell me which files you changed.
