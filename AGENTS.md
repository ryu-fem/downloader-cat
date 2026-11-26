# AGENTS.md - Downloader Cat (Android)

Instructions for AI coding agents working in this repo. Read this fully before changing anything.

## 1. Project

Downloader Cat is a native Android app that downloads video, audio and images from YouTube, TikTok, Instagram, Reddit, Pinterest, X and any website yt-dlp can extract (generic extractor), as long as the media is not DRM-protected. It never tries to bypass DRM; it shows a clear message instead.

- Language / UI: Kotlin + Jetpack Compose. Material 3 components are used but restyled to our black/white design.
- Android only.
- `reference/` is the old Tauri project. It is read-only design and logic reference. Never edit, delete or rename anything inside it.
- Download engine: `youtubedl-android` (maintained upstream: `yausername/youtubedl-android`; Maven group `io.github.junkfood02.youtubedl-android`, current 0.18.1) plus its ffmpeg module.
- gallery-dl is not available on Android. Images rely on yt-dlp only for now.
- License: GPL-3.0 (see `LICENSE`). The app links a GPL-3.0 library, so distributed builds are GPL-3.0.

## 2. HARD RULES

1. NEVER build or run anything on this machine: no `gradle` / `gradlew`, `adb`, emulator, `sdkmanager`, `npm`, and no installs. The laptop is weak. Only read and edit files.
2. All builds happen on GitHub Actions. If a build fails, the developer pastes the log; fix the code.
3. Never invent library versions, plugin IDs, APIs or Gradle syntax. Verify every version and API against its official source (Google Maven, Maven Central, developer.android.com, the library's repo/releases). If you have no web access or are unsure, say so and ask.
4. One phase at a time. After each phase: list the files created/changed, say what to look for in the CI build, then STOP until the developer says "next".
5. Do not delete or rename files in `reference/`. Do not run any git command (including commit/push). Keep code simple and commented where the logic is not obvious. No unnecessary abstractions.
6. Wherever something is blocked, leave `// TODO(blocked): reason`.

## 3. Verified toolchain

| Item | Version | Notes |
|---|---|---|
| Android Gradle Plugin | 8.13.2 | max API 36.1; requires Gradle >= 8.13 and JDK 17 |
| Gradle | 8.13 | installed by `gradle/actions/setup-gradle` in CI; there is no wrapper |
| JDK | 17 (Temurin) | required by AGP 8.13 |
| Kotlin / Compose compiler | 2.3.21 | KGP 2.3.21 supports Gradle 7.6.3-9.3.0 and AGP 8.2.2-9.0.0 |
| Compose BOM | 2026.06.01 | ui 1.11.4, material3 1.4.0 |
| compileSdk / targetSdk | 36 | API 37 is still beta |
| minSdk | 24 | required by youtubedl-android 0.18.1 |
| core-ktx | 1.18.0 | 1.19.1 requires compileSdk 37; do not bump until compileSdk moves |
| activity-compose | 1.13.0 | |
| lifecycle-runtime-ktx | 2.11.1 | |

All versions live in `gradle/libs.versions.toml`. Do not hardcode versions in module build files.

Do not move to AGP 9.x casually: AGP 9 uses built-in Kotlin and is incompatible with the `org.jetbrains.kotlin.android` plugin. Migrating requires a separate, verified task.

## 4. Project structure

```
downloader-cat-android/
├── AGENTS.md
├── README.md
├── LICENSE
├── .gitignore
├── settings.gradle.kts
├── build.gradle.kts
├── gradle.properties
├── gradle/libs.versions.toml
├── app/
│   ├── build.gradle.kts
│   ├── proguard-rules.pro
│   └── src/main/
│       ├── AndroidManifest.xml
│       ├── java/com/ryu/downloadercat/
│       └── res/
├── .github/workflows/android.yml
└── reference/                  <- old Tauri project, read-only
```

## 5. Design system

- Black, white and grays only. No accent colors (tiny status dots allowed). No gradients, no heavy shadows.
- 1px borders, 12-16dp corners, 48dp touch targets, subtle animations only.
- Dark by default, plus a light theme and a "follow system" option.
- Bottom navigation with 4 tabs: Home, Downloads, History, Settings.
- English and Arabic through standard Android resources (`res/values/strings.xml`, `res/values-ar/strings.xml`), `supportsRtl=true`, and the AndroidX per-app language API. No hardcoded user-visible strings.
- Wordmark: text only. "Downloader" in weight 300 and "Cat" in weight 800 inside a rounded pill (pill background = onBackground, text = background). Font Outfit bundled in `res/font` (never downloaded at runtime), letter spacing about -0.03em, always left-to-right even in Arabic.

## 6. Phases

0. DONE - repo + CI skeleton (empty screen).
1. Design system + app shell with mock data (all 6 screens, en + ar, dark + light).
2. Engine: `fetch_info`, yt-dlp init/update, localized error mapping.
3. Downloads: foreground service, queue, Room history, DataStore settings, MediaStore.
4. Share target: `ACTION_SEND` + `ModalBottomSheet` over the other app.
5. Playlists, subtitles, settings polish.
6. Release pipeline: signed APK, GitHub Releases, adaptive icon.

Always work on the current phase only.

## 7. Build and CI

- Workflow: `.github/workflows/android.yml`.
- It installs Temurin 17 and Gradle 8.13, runs `gradle assembleDebug`, and uploads the artifact `downloader-cat-debug-apk`.
- Never run Gradle locally.
