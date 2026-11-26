# Downloader Cat

Native Android downloader for video, audio and images from YouTube, TikTok, Instagram, Reddit, Pinterest, X and any website that contains media yt-dlp can extract. DRM-protected media is never supported and never bypassed.

**Status:** Phase 6 - signed release APK, GitHub Releases and adaptive launcher icon, on top of playlist downloads, the foreground download service, MediaStore output, Room history and DataStore settings.

## Stack

| Item | Version |
|---|---|
| Kotlin / Compose compiler | 2.3.21 |
| Jetpack Compose BOM | 2026.06.01 |
| Android Gradle Plugin | 8.13.2 |
| Gradle | 8.13 (installed by CI, no wrapper) |
| JDK | 17 |
| compileSdk / targetSdk | 36 |
| minSdk | 24 |

`minSdk 24` is required by the download engine (`youtubedl-android` 0.18.1).

## Build

This repository is built only on GitHub Actions. The development laptop is intentionally not used for builds, and there is no Gradle wrapper in the repo.

CI workflow: `.github/workflows/android.yml`

- installs Temurin JDK 17 and Gradle 8.13,
- runs `gradle assembleDebug` and `gradle assembleReleaseLike`,
- uploads `downloader-cat-debug-apk` (`app-debug.apk`),
- uploads `downloader-cat-release-like-apk` (`app-releaseLike.apk`, debug-signed, R8 enabled),
- uploads `downloader-cat-release-like-mapping` (`mapping.txt`).

Release workflow: `.github/workflows/release.yml`

- runs on `v*` tags or manual dispatch,
- builds `assembleRelease` with the real release keystore,
- verifies the APK signature,
- uploads `downloader-cat-release-apk`,
- creates or updates the GitHub Release.

### Release signing

Add these repository secrets in GitHub:

| Secret | Value |
|---|---|
| `RELEASE_KEYSTORE_BASE64` | base64 of the `.jks` release keystore |
| `RELEASE_STORE_PASSWORD` | keystore password |
| `RELEASE_KEY_ALIAS` | key alias |
| `RELEASE_KEY_PASSWORD` | key password |

Create and push a release tag:

```bash
git tag v0.1.0
git push origin v0.1.0
```

The workflow takes `versionName` from the tag and `versionCode` from the GitHub Actions run number. Never commit the keystore or passwords.

## Push to GitHub (HTTPS)

Create an empty repository named `downloader-cat-android` on GitHub, then from the project root:

```bash
git init
git add .
git commit -m "Phase 0: Android skeleton + CI"
git branch -M main
git remote add origin https://github.com/<your-user>/downloader-cat-android.git
git push -u origin main
```

If a remote already exists, update it instead:

```bash
git remote set-url origin https://github.com/<your-user>/downloader-cat-android.git
```

Then open the repository's **Actions** tab and watch the `Android CI` run.

### If git reports "dubious ownership"

This project lives on an external drive, so git may refuse to operate with:

```
fatal: detected dubious ownership in repository at '...'
```

Trust only this exact path (do not use `*`):

```bash
git config --global --add safe.directory '/run/media/ryu/New Volume/RYU files/ryu files/apps/downloader-cat-android'
```

## License

GPL-3.0. See [`LICENSE`](LICENSE).

The download engine wrapper (`youtubedl-android`) is GPL-3.0, so the distributed app is GPL-3.0 as well.

## Reference

`reference/` contains the previous Tauri desktop version. It is kept for design and logic reference only and is not built.
