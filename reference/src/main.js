const THEME_KEY = "downloader-cat-theme";
const LEGACY_AUTO_DOWNLOAD_SHARED_KEY = "downloader-cat-auto-download-shared";
const SKIP_FORMAT_PICKER_SHARED_KEY = "downloader-cat-skip-format-picker-shared";
const DEFAULT_DOWNLOAD_KEY = "downloader-cat-default-download";
const SETTINGS_KEY = "downloader-cat-settings";

const ICONS = {
  pause: `<svg class="icon" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><rect x="6" y="4" width="3.5" height="16" rx="1" /><rect x="14.5" y="4" width="3.5" height="16" rx="1" /></svg>`,
  play: `<svg class="icon" viewBox="0 0 24 24" fill="currentColor" aria-hidden="true"><path d="M8 5.5v13l11-6.5-11-6.5Z" /></svg>`,
  cancel: `<svg class="icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" aria-hidden="true"><path d="M18 6 6 18M6 6l12 12" /></svg>`,
  video: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="5" width="18" height="14" rx="3" /><path d="m10 9 5 3-5 3V9Z" /></svg>`,
  audio: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><path d="M9 18V6l10-2v12" /><circle cx="6" cy="18" r="3" /><circle cx="16" cy="16" r="3" /></svg>`,
  images: `<svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="1.6" stroke-linecap="round" stroke-linejoin="round" aria-hidden="true"><rect x="3" y="4" width="18" height="16" rx="3" /><circle cx="9" cy="9" r="1.5" /><path d="m21 15-5-5-6 6-3-3-4 4" /></svg>`,
};

const I18N = {
  en: {
    "app.subtitle": "Videos, audio and images",
    "tabs.home": "Home",
    "tabs.downloads": "Downloads",
    "tabs.history": "History",
    "tabs.settings": "Settings",
    "common.paste": "Paste",
    "common.fetch": "Fetch",
    "common.download": "Download",
    "common.change": "Change",
    "common.unknownSite": "Unknown site",
    "common.untitled": "Untitled",
    "common.unknownChannel": "Unknown channel",
    "common.unknownError": "Unknown error",
    "common.unknown": "unknown",
    "aria.backToHome": "Back to Home",
    "aria.downloadFormat": "Download format",
    "aria.filterHistory": "Filter history",
    "aria.playlistSelection": "Playlist selection",
    "aria.concurrency": "Concurrent downloads",
    "aria.concurrencyDown": "Decrease concurrent downloads",
    "aria.concurrencyUp": "Increase concurrent downloads",
    "aria.toggleLanguage": "Toggle language",
    "aria.mainNav": "Main navigation",
    "aria.shareOptions": "Share download options",
    "aria.selectItem": "Select {title}",
    "aria.openItem": "Open {title}",
    "aria.deleteItem": "Delete {title}",
    "aria.downloadComplete": "Download complete",
    "aria.resumeDownload": "Resume download",
    "aria.pauseDownload": "Pause download",
    "aria.cancelDownload": "Cancel download",
    "aria.downloadProgress": "{title} download progress",
    "home.heroLine1": "Paste link.",
    "home.heroLine2": "Download instantly.",
    "home.heroDesc": "Grab high-resolution 4K video, lossless audio, or raw photos straight to your gallery.",
    "home.directUrl": "Direct URL",
    "home.placeholder": "Paste Instagram, YouTube, TikTok link...",
    "home.downloadVideo": "Download video",
    "home.autoDetect": "Auto-detect",
    "home.feat1": "No watermark",
    "home.feat2": "Up to 4K 60fps",
    "home.feat3": "320kbps MP3",
    "home.noShareLink": "No link found in the shared text.",
    "home.shareQueued": "Fetch in progress; shared link queued.",
    "home.mockNotice": "Tauri is unavailable. Showing mock data.",
    "home.choicePrompt": "Choose this video or the whole playlist.",
    "home.sharedLoaded": "Shared link loaded with saved defaults.",
    "home.playlistLoaded": "Playlist loaded: {count} item(s).",
    "home.infoLoaded": "Info loaded.",
    "home.pasteFirst": "Paste a link first.",
    "home.httpOnly": "Only http:// and https:// links are supported.",
    "home.fetchingVideo": "Fetching video...",
    "home.fetchingInfo": "Fetching info...",
    "home.fetchFailed": "Fetch failed: {error}",
    "home.clipboardUnavailable": "Clipboard access is unavailable.",
    "home.clipboardEmpty": "Clipboard is empty.",
    "home.fetchAgainFirst": "Fetch the link again first.",
    "format.title": "Format & Quality",
    "format.subtitle": "Choose download options",
    "result.playlistNotice": "This video is part of a playlist.",
    "result.videoOnly": "This video only",
    "result.wholePlaylist": "Whole playlist",
    "result.format": "Format",
    "result.sizes": "Sizes",
    "result.options": "Options",
    "result.subtitles": "Subtitles",
    "result.embedThumbnail": "Embed thumbnail",
    "result.saveDefault": "Save as default",
    "result.pickFormatFirst": "Pick a format first.",
    "result.savedDefault": "Saved as default: {label}",
    "result.fetchFirst": "Fetch a link first.",
    "result.startingDownload": "Starting download...",
    "result.downloadStarted": "Download started: {label}{size}.",
    "result.couldNotStart": "Could not start download: {error}",
    "result.noSavedDefaults": "No saved defaults yet. Press Save as default first.",
    "result.sharedMissingLink": "Could not start shared download: missing link.",
    "result.sharedStarting": "Starting shared download: {label}...",
    "result.sharedStarted": "Download started: {label}.",
    "result.sharedFailed": "Could not start shared download: {error}",
    "downloads.title": "Downloads",
    "downloads.subtitle": "Active transfers and the queue.",
    "downloads.queue": "Queue",
    "downloads.queueEmpty": "No queued downloads.",
    "downloads.noneActive": "No active downloads.",
    "downloads.activeCount": "{count} active",
    "downloads.waitingCount": "{count} waiting",
    "downloads.statusDone": "Done",
    "downloads.statusFailed": "Failed",
    "downloads.statusCancelled": "Cancelled",
    "downloads.statusPaused": "Paused",
    "downloads.statusProcessing": "Processing",
    "downloads.statusStarting": "Starting...",
    "downloads.etaLeft": "{eta} left",
    "downloads.allFinished": "All active downloads finished.",
    "downloads.pausedItem": "Paused {title}.",
    "downloads.resumedItem": "Resumed {title}.",
    "downloads.cancelledItem": "Cancelled {title}.",
    "downloads.failedItem": "Download failed: {error}",
    "downloads.couldNotPause": "Could not pause {title}: {error}",
    "downloads.couldNotResume": "Could not resume {title}: {error}",
    "downloads.couldNotCancel": "Could not cancel {title}: {error}",
    "downloads.listenerError": "Could not listen for download progress: {error}",
    "downloads.listenerUnavailable": "Download progress events are unavailable.",
    "downloads.tauriUnavailable": "Tauri is unavailable.",
    "queue.queued": "Queued",
    "history.title": "History",
    "history.subtitle": "Finished downloads on this device.",
    "history.searchLabel": "Search history",
    "history.searchPlaceholder": "Search title or site",
    "history.filterAll": "All",
    "history.filterVideo": "Video",
    "history.filterAudio": "Audio",
    "history.filterImages": "Images",
    "history.empty": "No matching downloads.",
    "history.typeVideo": "Video",
    "history.typeAudio": "Audio",
    "history.typeImages": "Images",
    "history.open": "Open",
    "history.delete": "Delete",
    "history.saveError": "Could not save history: {error}",
    "history.loadError": "Could not load history: {error}",
    "history.mockAction": "Mock {action}: {title}",
    "history.noFilePath": "No file path saved for this item.",
    "history.noFolderPath": "No folder path saved for this item.",
    "history.openedItem": "Opened {title}.",
    "history.openedFolder": "Opened folder for {title}.",
    "history.couldNotOpen": "Could not open {title}: {error}",
    "history.couldNotDelete": "Could not delete {title}: {error}",
    "history.deletedItem": "Deleted {title}.",
    "playlist.title": "Playlist",
    "playlist.subtitleEmpty": "Fetch a playlist link first.",
    "playlist.subtitleMock": "Mock playlist with {count} items.",
    "playlist.subtitleLoaded": "{count} item(s) from yt-dlp.",
    "playlist.noEntries": "Playlist has no entries.",
    "playlist.selectAll": "Select all",
    "playlist.clearAll": "Clear all",
    "playlist.selectedSummary": "{selected} of {total} selected",
    "playlist.selectedCount": "{count} selected",
    "playlist.downloadingItem": "Downloading {current} of {total}: {title}",
    "playlist.couldNotStartItem": "Could not start {title}: {error}",
    "playlist.finishedCount": "Finished {done} of {total} playlist download(s).",
    "playlist.mockQueued": "Mock download queued for {count} item(s).",
    "playlist.alreadyRunning": "A playlist download is already running.",
    "playlist.missingUrl": "No direct URL for {title}.",
    "settings.title": "Settings",
    "settings.subtitle": "Preferences for this device.",
    "settings.saved": "Saved.",
    "settings.loadError": "Could not load settings: {error}. Changes will not be saved.",
    "settings.stillLoading": "Settings are still loading. Change not saved.",
    "settings.saveError": "Could not save settings: {error}",
    "settings.downloads": "Downloads",
    "settings.folder": "Download folder",
    "settings.folderDesktopOnly": "Folder picker is only available in the desktop app.",
    "settings.chooseFolderTitle": "Choose download folder",
    "settings.folderSaved": "Download folder saved.",
    "settings.folderError": "Could not choose folder: {error}",
    "settings.defaultQuality": "Default quality",
    "settings.defaultQualitySet": "Default quality set to {label}.",
    "settings.skipPicker": "Skip the format picker for shared links",
    "settings.concurrency": "Concurrent downloads",
    "settings.speedLimit": "Speed limit",
    "settings.unlimited": "Unlimited",
    "settings.speedInvalid": "Speed limit must be a positive number in MB/s.",
    "settings.speedSet": "Speed limit set to {value} MB/s.",
    "settings.speedUnlimited": "Speed limit set to unlimited.",
    "settings.appearance": "Appearance",
    "settings.theme": "Theme",
    "settings.language": "Language",
    "settings.network": "Network",
    "settings.proxy": "Proxy",
    "settings.proxyInvalid": "Proxy must contain :// and must not start with -.",
    "settings.proxySaved": "Proxy saved.",
    "settings.proxyDisabled": "Proxy disabled.",
    "settings.cookies": "Cookies from browser",
    "settings.cookiesSet": "Cookies from browser: {label}.",
    "settings.engine": "Engine",
    "settings.engineNotChecked": "Not checked",
    "settings.updateEngine": "Update engine",
    "settings.engineDesktopOnly": "Engine update needs the desktop app.",
    "settings.engineUpdating": "Updating yt-dlp...",
    "settings.engineUpdated": "yt-dlp updated to {version}.",
    "settings.engineError": "Could not update yt-dlp: {error}",
    "settings.about": "About",
    "settings.version": "Version",
    "settings.versionValue": "1.4.2 (Stable)",
    "settings.versionStatus": "You are on v1.4.2 (Stable).",
    "settings.checkUpdates": "Check for updates",
    "settings.github": "Source code / GitHub",
    "settings.repoStatus": "Repository: github.com/downloader-cat",
    "settings.sharedDefaultsOn": "Shared links will use saved defaults.",
    "settings.sharedDefaultsOff": "Shared links will open for review.",
    "sheet.ready": "Ready",
    "sheet.videos": "videos",
    "sheet.videoSub": "MP4 • Video with Audio",
    "sheet.quality": "Quality:",
    "sheet.audioSub": "Audio only • 320 kbps",
    "sheet.downloadVideoBtn": "Download {type} ({quality} • {size})",
    "sheet.downloadAudioBtn": "Download mp3 (320 kbps • 3.2 MB)",
    "quality.best": "Best",
    "theme.system": "System",
    "theme.oled": "OLED",
    "theme.yaru": "Yaru",
    "theme.dark": "Dark",
    "theme.light": "Light",
    "cookies.none": "None",
  },
  ar: {
    "app.subtitle": "فيديو وصوت وصور",
    "tabs.home": "الرئيسية",
    "tabs.downloads": "التنزيلات",
    "tabs.history": "السجل",
    "tabs.settings": "الإعدادات",
    "common.paste": "لصق",
    "common.fetch": "جلب",
    "common.download": "تنزيل",
    "common.change": "تغيير",
    "common.unknownSite": "موقع غير معروف",
    "common.untitled": "بدون عنوان",
    "common.unknownChannel": "قناة غير معروفة",
    "common.unknownError": "خطأ غير معروف",
    "common.unknown": "غير معروف",
    "aria.backToHome": "العودة إلى الرئيسية",
    "aria.downloadFormat": "صيغة التنزيل",
    "aria.filterHistory": "تصفية السجل",
    "aria.playlistSelection": "تحديد قائمة التشغيل",
    "aria.concurrency": "التنزيلات المتزامنة",
    "aria.concurrencyDown": "تقليل التنزيلات المتزامنة",
    "aria.concurrencyUp": "زيادة التنزيلات المتزامنة",
    "aria.toggleLanguage": "تبديل اللغة",
    "aria.mainNav": "التنقل الرئيسي",
    "aria.shareOptions": "خيارات تنزيل المشاركة",
    "aria.selectItem": "تحديد {title}",
    "aria.openItem": "فتح {title}",
    "aria.deleteItem": "حذف {title}",
    "aria.downloadComplete": "اكتمل التنزيل",
    "aria.resumeDownload": "استئناف التنزيل",
    "aria.pauseDownload": "إيقاف التنزيل مؤقتًا",
    "aria.cancelDownload": "إلغاء التنزيل",
    "aria.downloadProgress": "تقدّم تنزيل {title}",
    "home.heroLine1": "الصق الرابط.",
    "home.heroLine2": "نزّل فورًا.",
    "home.heroDesc": "احصل على فيديو 4K عالي الدقة، وصوت بلا فقدان، أو صور أصلية مباشرة إلى معرضك.",
    "home.directUrl": "رابط مباشر",
    "home.placeholder": "الصق رابط إنستغرام أو يوتيوب أو تيك توك...",
    "home.downloadVideo": "تنزيل الفيديو",
    "home.autoDetect": "كشف تلقائي",
    "home.feat1": "بدون علامة مائية",
    "home.feat2": "حتى 4K 60fps",
    "home.feat3": "MP3 بجودة 320kbps",
    "home.noShareLink": "لم يُعثر على رابط في النص المشارَك.",
    "home.shareQueued": "هناك جلب جارٍ؛ تم وضع الرابط المشارَك في الانتظار.",
    "home.mockNotice": "واجهة Tauri غير متاحة. يتم عرض بيانات تجريبية.",
    "home.choicePrompt": "اختر هذا الفيديو أو قائمة التشغيل كاملة.",
    "home.sharedLoaded": "تم تحميل الرابط المشارَك بالإعدادات الافتراضية المحفوظة.",
    "home.playlistLoaded": "تم تحميل قائمة التشغيل: {count} عنصر.",
    "home.infoLoaded": "تم جلب المعلومات.",
    "home.pasteFirst": "الصق رابطًا أولًا.",
    "home.httpOnly": "تُدعم روابط http:// و https:// فقط.",
    "home.fetchingVideo": "جارٍ جلب الفيديو...",
    "home.fetchingInfo": "جارٍ جلب المعلومات...",
    "home.fetchFailed": "فشل الجلب: {error}",
    "home.clipboardUnavailable": "الوصول إلى الحافظة غير متاح.",
    "home.clipboardEmpty": "الحافظة فارغة.",
    "home.fetchAgainFirst": "اجلب الرابط مرة أخرى أولًا.",
    "format.title": "الصيغة والجودة",
    "format.subtitle": "اختر خيارات التنزيل",
    "result.playlistNotice": "هذا الفيديو جزء من قائمة تشغيل.",
    "result.videoOnly": "هذا الفيديو فقط",
    "result.wholePlaylist": "قائمة التشغيل كاملة",
    "result.format": "الصيغة",
    "result.sizes": "الأحجام",
    "result.options": "الخيارات",
    "result.subtitles": "الترجمة",
    "result.embedThumbnail": "تضمين الصورة المصغرة",
    "result.saveDefault": "حفظ كافتراضي",
    "result.pickFormatFirst": "اختر صيغة أولًا.",
    "result.savedDefault": "تم الحفظ كافتراضي: {label}",
    "result.fetchFirst": "اجلب رابطًا أولًا.",
    "result.startingDownload": "جارٍ بدء التنزيل...",
    "result.downloadStarted": "بدأ التنزيل: {label}{size}.",
    "result.couldNotStart": "تعذّر بدء التنزيل: {error}",
    "result.noSavedDefaults": "لا توجد إعدادات افتراضية محفوظة بعد. اضغط حفظ كافتراضي أولًا.",
    "result.sharedMissingLink": "تعذّر بدء التنزيل المشارَك: الرابط مفقود.",
    "result.sharedStarting": "جارٍ بدء التنزيل المشارَك: {label}...",
    "result.sharedStarted": "بدأ التنزيل: {label}.",
    "result.sharedFailed": "تعذّر بدء التنزيل المشارَك: {error}",
    "downloads.title": "التنزيلات",
    "downloads.subtitle": "التنزيلات النشطة وقائمة الانتظار.",
    "downloads.queue": "قائمة الانتظار",
    "downloads.queueEmpty": "لا توجد تنزيلات في الانتظار.",
    "downloads.noneActive": "لا توجد تنزيلات نشطة.",
    "downloads.activeCount": "{count} نشط",
    "downloads.waitingCount": "{count} في الانتظار",
    "downloads.statusDone": "مكتمل",
    "downloads.statusFailed": "فشل",
    "downloads.statusCancelled": "ملغى",
    "downloads.statusPaused": "متوقف مؤقتًا",
    "downloads.statusProcessing": "جارٍ المعالجة",
    "downloads.statusStarting": "جارٍ البدء...",
    "downloads.etaLeft": "متبقي {eta}",
    "downloads.allFinished": "اكتملت جميع التنزيلات النشطة.",
    "downloads.pausedItem": "تم إيقاف {title} مؤقتًا.",
    "downloads.resumedItem": "تم استئناف {title}.",
    "downloads.cancelledItem": "تم إلغاء {title}.",
    "downloads.failedItem": "فشل التنزيل: {error}",
    "downloads.couldNotPause": "تعذّر إيقاف {title} مؤقتًا: {error}",
    "downloads.couldNotResume": "تعذّر استئناف {title}: {error}",
    "downloads.couldNotCancel": "تعذّر إلغاء {title}: {error}",
    "downloads.listenerError": "تعذّر الاستماع لتقدّم التنزيل: {error}",
    "downloads.listenerUnavailable": "أحداث تقدّم التنزيل غير متاحة.",
    "downloads.tauriUnavailable": "واجهة Tauri غير متاحة.",
    "queue.queued": "في الانتظار",
    "history.title": "السجل",
    "history.subtitle": "التنزيلات المكتملة على هذا الجهاز.",
    "history.searchLabel": "البحث في السجل",
    "history.searchPlaceholder": "ابحث في العنوان أو الموقع",
    "history.filterAll": "الكل",
    "history.filterVideo": "فيديو",
    "history.filterAudio": "صوت",
    "history.filterImages": "صور",
    "history.empty": "لا توجد تنزيلات مطابقة.",
    "history.typeVideo": "فيديو",
    "history.typeAudio": "صوت",
    "history.typeImages": "صور",
    "history.open": "فتح",
    "history.delete": "حذف",
    "history.saveError": "تعذّر حفظ السجل: {error}",
    "history.loadError": "تعذّر تحميل السجل: {error}",
    "history.mockAction": "إجراء تجريبي ({action}): {title}",
    "history.noFilePath": "لا يوجد مسار ملف محفوظ لهذا العنصر.",
    "history.noFolderPath": "لا يوجد مسار مجلد محفوظ لهذا العنصر.",
    "history.openedItem": "تم فتح {title}.",
    "history.openedFolder": "تم فتح مجلد {title}.",
    "history.couldNotOpen": "تعذّر فتح {title}: {error}",
    "history.couldNotDelete": "تعذّر حذف {title}: {error}",
    "history.deletedItem": "تم حذف {title}.",
    "playlist.title": "قائمة التشغيل",
    "playlist.subtitleEmpty": "اجلب رابط قائمة تشغيل أولًا.",
    "playlist.subtitleMock": "قائمة تشغيل تجريبية تحتوي على {count} عناصر.",
    "playlist.subtitleLoaded": "{count} عنصر عبر yt-dlp.",
    "playlist.noEntries": "قائمة التشغيل فارغة.",
    "playlist.selectAll": "تحديد الكل",
    "playlist.clearAll": "إلغاء التحديد",
    "playlist.selectedSummary": "تم تحديد {selected} من {total}",
    "playlist.selectedCount": "تم تحديد {count}",
    "playlist.downloadingItem": "جارٍ تنزيل {current} من {total}: {title}",
    "playlist.couldNotStartItem": "تعذّر بدء {title}: {error}",
    "playlist.finishedCount": "اكتمل {done} من أصل {total} تنزيلات.",
    "playlist.mockQueued": "تمت إضافة {count} عنصر إلى قائمة الانتظار التجريبية.",
    "playlist.alreadyRunning": "هناك تنزيل لقائمة تشغيل قيد التشغيل بالفعل.",
    "playlist.missingUrl": "لا يوجد رابط مباشر لـ {title}.",
    "settings.title": "الإعدادات",
    "settings.subtitle": "تفضيلات هذا الجهاز.",
    "settings.saved": "تم الحفظ.",
    "settings.loadError": "تعذّر تحميل الإعدادات: {error}. لن يتم حفظ التغييرات.",
    "settings.stillLoading": "لا تزال الإعدادات قيد التحميل. لم يتم حفظ التغيير.",
    "settings.saveError": "تعذّر حفظ الإعدادات: {error}",
    "settings.downloads": "التنزيلات",
    "settings.folder": "مجلد التنزيل",
    "settings.folderDesktopOnly": "منتقي المجلدات متاح في تطبيق سطح المكتب فقط.",
    "settings.chooseFolderTitle": "اختر مجلد التنزيل",
    "settings.folderSaved": "تم حفظ مجلد التنزيل.",
    "settings.folderError": "تعذّر اختيار المجلد: {error}",
    "settings.defaultQuality": "الجودة الافتراضية",
    "settings.defaultQualitySet": "تم ضبط الجودة الافتراضية على {label}.",
    "settings.skipPicker": "تخطي اختيار الصيغة عند مشاركة الروابط",
    "settings.concurrency": "التنزيلات المتزامنة",
    "settings.speedLimit": "حد السرعة",
    "settings.unlimited": "غير محدود",
    "settings.speedInvalid": "يجب أن يكون حد السرعة رقمًا موجبًا بوحدة MB/s.",
    "settings.speedSet": "تم ضبط حد السرعة على {value} MB/s.",
    "settings.speedUnlimited": "تم ضبط حد السرعة على غير محدود.",
    "settings.appearance": "المظهر",
    "settings.theme": "النمط",
    "settings.language": "اللغة",
    "settings.network": "الشبكة",
    "settings.proxy": "الوكيل",
    "settings.proxyInvalid": "يجب أن يحتوي الوكيل على :// وألا يبدأ بـ -.",
    "settings.proxySaved": "تم حفظ الوكيل.",
    "settings.proxyDisabled": "تم تعطيل الوكيل.",
    "settings.cookies": "ملفات تعريف الارتباط من المتصفح",
    "settings.cookiesSet": "ملفات تعريف الارتباط من المتصفح: {label}.",
    "settings.engine": "المحرّك",
    "settings.engineNotChecked": "لم يتم الفحص",
    "settings.updateEngine": "تحديث المحرّك",
    "settings.engineDesktopOnly": "يتطلب تحديث المحرّك تطبيق سطح المكتب.",
    "settings.engineUpdating": "جارٍ تحديث yt-dlp...",
    "settings.engineUpdated": "تم تحديث yt-dlp إلى {version}.",
    "settings.engineError": "تعذّر تحديث yt-dlp: {error}",
    "settings.about": "حول",
    "settings.version": "الإصدار",
    "settings.versionValue": "1.4.2 (مستقر)",
    "settings.versionStatus": "أنت على الإصدار v1.4.2 (مستقر).",
    "settings.checkUpdates": "التحقق من التحديثات",
    "settings.github": "الكود المصدري / GitHub",
    "settings.repoStatus": "المستودع: github.com/downloader-cat",
    "settings.sharedDefaultsOn": "ستستخدم الروابط المشارَكة الإعدادات الافتراضية المحفوظة.",
    "settings.sharedDefaultsOff": "ستُفتح الروابط المشارَكة للمراجعة.",
    "sheet.ready": "جاهز",
    "sheet.videos": "فيديو",
    "sheet.videoSub": "MP4 • فيديو مع صوت",
    "sheet.quality": "الجودة:",
    "sheet.audioSub": "صوت فقط • 320 kbps",
    "sheet.downloadVideoBtn": "تنزيل {type} ({quality} • {size})",
    "sheet.downloadAudioBtn": "تنزيل MP3 (320 kbps • 3.2 MB)",
    "quality.best": "الأفضل",
    "theme.system": "النظام",
    "theme.oled": "OLED",
    "theme.yaru": "Yaru",
    "theme.dark": "داكن",
    "theme.light": "فاتح",
    "cookies.none": "بدون",
  },
};

let currentLanguage = "en";

function translate(key, vars = {}) {
  const table = I18N[currentLanguage] ?? I18N.en;
  let text = table[key] ?? I18N.en[key] ?? key;

  for (const [name, value] of Object.entries(vars)) {
    text = text.split(`{${name}}`).join(String(value));
  }

  return text;
}

const themeSelect = document.querySelector("#theme-select");

const tabs = Array.from(document.querySelectorAll(".tab"));
const screens = Array.from(document.querySelectorAll(".screen"));
const appMain = document.querySelector(".app-main");
const homeTab = document.querySelector("#tab-home");
const downloadsTab = document.querySelector("#tab-downloads");
const historyTab = document.querySelector("#tab-history");
const settingsTab = document.querySelector("#tab-settings");

const linkForm = document.querySelector("#link-form");
const urlInput = document.querySelector("#url-input");
const pasteButton = document.querySelector("#paste-button");
const fetchButton = document.querySelector("#fetch-button");
const homeStatus = document.querySelector("#home-status");
const resultCard = document.querySelector("#mock-result");
const resultSite = document.querySelector("#result-site");
const resultTitle = document.querySelector("#result-title");
const resultChannel = document.querySelector("#result-channel");
const resultDuration = document.querySelector("#result-duration");
const resultThumb = document.querySelector("#result-thumb");
const resultThumbWrap = document.querySelector("#mock-result .thumb");
const formatList = document.querySelector("#format-list");
const saveDefaultButton = document.querySelector("#save-default-button");
const downloadButton = document.querySelector("#download-button");
const downloadStatus = document.querySelector("#download-status");
const optionSubtitles = document.querySelector("#option-subtitles");
const optionEmbed = document.querySelector("#option-embed");
const playlistChoice = document.querySelector("#playlist-choice");
const choiceVideoButton = document.querySelector("#choice-video");
const choicePlaylistButton = document.querySelector("#choice-playlist");

const formatBackButton = document.querySelector("#format-back");
const playlistBack = document.querySelector("#playlist-back");
const playlistSubtitle = document.querySelector("#playlist-subtitle");
const playlistList = document.querySelector("#playlist-list");
const selectAllButton = document.querySelector("#select-all");
const playlistSummary = document.querySelector("#playlist-summary");
const selectedCount = document.querySelector("#selected-count");
const selectedSize = document.querySelector("#selected-size");
const playlistDownload = document.querySelector("#playlist-download");
const playlistStatus = document.querySelector("#playlist-status");

const downloadList = document.querySelector("#download-list");
const queueList = document.querySelector("#queue-list");
const downloadsSummary = document.querySelector("#downloads-summary");
const queueCount = document.querySelector("#queue-count");
const downloadsStatus = document.querySelector("#downloads-status");

const historySearch = document.querySelector("#history-search");
const historyFilters = document.querySelector("#history-filters");
const historyList = document.querySelector("#history-list");
const historyEmpty = document.querySelector("#history-empty");
const historyStatus = document.querySelector("#history-status");

const chooseFolderButton = document.querySelector("#choose-folder");
const downloadFolderInput = document.querySelector("#download-folder");
const defaultQuality = document.querySelector("#default-quality");
const concurrencyDown = document.querySelector("#concurrency-down");
const concurrencyUp = document.querySelector("#concurrency-up");
const concurrencyValue = document.querySelector("#concurrency-value");
const speedLimit = document.querySelector("#speed-limit");
const proxyInput = document.querySelector("#proxy-input");
const cookiesSelect = document.querySelector("#cookies-select");
const langCapSwitch = document.querySelector("#lang-cap-switch");
const updateEngineButton = document.querySelector("#update-engine");
const engineVersion = document.querySelector("#engine-version");
const skipFormatPickerShared = document.querySelector("#skip-format-picker-shared");
const settingsStatus = document.querySelector("#settings-status");
const checkUpdatesLink = document.querySelector("#check-updates-link");
const githubLink = document.querySelector("#github-link");

const shareSheetOverlay = document.querySelector("#share-sheet-overlay");
const shareSheetBackdrop = document.querySelector("#share-sheet-backdrop");
const sheetQualityPills = document.querySelector("#sheet-quality-pills");
const sheetMp3Btn = document.querySelector("#sheet-mp3-btn");
const sheetDownloadBtn = document.querySelector("#sheet-download-btn");
const sheetSize = document.querySelector("#sheet-size");
const sheetDlText = document.querySelector("#sheet-dl-text");

const MOCK_RESULT = {
  site: "YouTube",
  title: "Sample video title",
  channel: "Downloader Cat",
  duration: "3:42",
  formats: [
    { id: "best", label: "Best", size: "48.2 MB" },
    { id: "4k", label: "4K", size: "312 MB" },
    { id: "1080p", label: "1080p", size: "96.4 MB" },
    { id: "720p", label: "720p", size: "52.1 MB" },
    { id: "480p", label: "480p", size: "28.7 MB" },
    { id: "360p", label: "360p", size: "14.2 MB" },
    { id: "mp3", label: "MP3", size: "4.1 MB" },
  ],
};

const QUALITY_IDS = new Set(["best", "4k", "1440p", "1080p", "720p", "480p", "360p", "mp3"]);
const QUALITY_HEIGHTS = new Map([
  ["4k", 2160],
  ["1440p", 1440],
  ["1080p", 1080],
  ["720p", 720],
  ["480p", 480],
  ["360p", 360],
]);

const COOKIE_BROWSERS = new Set(["none", "chrome", "firefox", "chromium", "edge"]);
const DEFAULT_APP_SETTINGS = Object.freeze({
  downloadFolder: "~/Downloads/DownloaderCat",
  defaultQuality: "best",
  concurrentDownloads: 2,
  speedLimitMb: 0,
  proxy: "",
  cookiesFromBrowser: "none",
  language: "en",
  skipPickerShared: false,
});

let selectedFormat = "best";
let currentFormats = MOCK_RESULT.formats;
let currentFetchInfo = null;
let currentFetchUrl = "";
let isFetchRunning = false;
const pendingSharedTexts = [];
let defaultDownloadPrefs = { quality: "best", subtitles: false, embedThumbnail: false };
let hasSavedDefaults = false;
let appSettings = { ...DEFAULT_APP_SETTINGS };
let settingsLoaded = false;
let settingsLoadError = "";

const realDownloads = new Map();
const downloadMeta = new Map();
let realHistory = [];
let playlistQueue = [];
const playlistActiveIds = new Set();
let playlistScheduling = false;
let playlistTotal = 0;
let playlistFinished = 0;

function isTauriAvailable() {
  return typeof window.__TAURI__?.core?.invoke === "function";
}

function defaultAppSettings() {
  return { ...DEFAULT_APP_SETTINGS };
}

function isProxyValid(value) {
  const proxy = value.trim();
  return !proxy || (!proxy.startsWith("-") && proxy.includes("://"));
}

function isDownloadFolderValid(value) {
  const folder = value.trim();
  if (!folder) return false;
  if (folder === "~" || folder.startsWith("~/")) return true;
  if (folder.startsWith("/") || folder.startsWith("\\\\")) return true;
  return /^[A-Za-z]:[\\/]/.test(folder);
}

function normalizeAppSettings(raw) {
  const source = raw && typeof raw === "object" ? raw : {};
  const quality =
    typeof source.defaultQuality === "string" && QUALITY_IDS.has(source.defaultQuality)
      ? source.defaultQuality
      : DEFAULT_APP_SETTINGS.defaultQuality;
  const concurrency = Number(source.concurrentDownloads);
  const speedLimit = Number(source.speedLimitMb);
  const cookies =
    typeof source.cookiesFromBrowser === "string"
      ? source.cookiesFromBrowser.trim().toLowerCase()
      : "none";
  const folder =
    typeof source.downloadFolder === "string" && source.downloadFolder.trim()
      ? source.downloadFolder.trim()
      : DEFAULT_APP_SETTINGS.downloadFolder;
  const proxy = typeof source.proxy === "string" ? source.proxy.trim() : "";

  return {
    downloadFolder: folder,
    defaultQuality: quality,
    concurrentDownloads: Number.isFinite(concurrency)
      ? Math.min(8, Math.max(1, Math.round(concurrency)))
      : DEFAULT_APP_SETTINGS.concurrentDownloads,
    speedLimitMb: Number.isFinite(speedLimit) && speedLimit > 0 ? speedLimit : 0,
    proxy: isProxyValid(proxy) ? proxy : "",
    cookiesFromBrowser: COOKIE_BROWSERS.has(cookies) ? cookies : "none",
    language: source.language === "ar" ? "ar" : "en",
    skipPickerShared: Boolean(source.skipPickerShared),
  };
}

let playlistLoadedFromTauri = false;
let sheetSelectedMode = "video";

function localizeFormatLabel(formatOrId, fallbackLabel) {
  const id = typeof formatOrId === "string" ? formatOrId : formatOrId?.id;
  if (id === "best") return translate("quality.best");
  if (id === "images") return translate("history.typeImages");
  return (typeof formatOrId === "object" ? formatOrId?.label : null) || fallbackLabel || id || translate("quality.best");
}

function updatePlaylistSubtitle() {
  if (!playlistSubtitle) return;
  if (isTauriAvailable()) {
    playlistSubtitle.textContent =
      playlistLoadedFromTauri || playlistItems.length > 0
        ? translate("playlist.subtitleLoaded", { count: playlistItems.length })
        : translate("playlist.subtitleEmpty");
  } else {
    playlistSubtitle.textContent = translate("playlist.subtitleMock", { count: playlistItems.length });
  }
}

function updateSheetDownloadText() {
  if (!sheetDlText) return;
  if (sheetSelectedMode === "mp3") {
    sheetDlText.textContent = translate("sheet.downloadAudioBtn");
    return;
  }

  const activePill = sheetQualityPills?.querySelector(".quality-pill.is-selected");
  const quality = activePill?.dataset.q || "1080p";
  const size = activePill?.dataset.s || "42.5 MB";
  sheetDlText.textContent = translate("sheet.downloadVideoBtn", {
    type: translate("sheet.videos"),
    quality,
    size,
  });
}

function applyLanguage(language) {
  currentLanguage = language === "ar" ? "ar" : "en";
  const isArabic = currentLanguage === "ar";
  document.documentElement.lang = currentLanguage;
  document.documentElement.dir = isArabic ? "rtl" : "ltr";
  langCapSwitch?.classList.toggle("on", isArabic);
  langCapSwitch?.setAttribute("aria-pressed", String(isArabic));

  for (const element of document.querySelectorAll("[data-i18n]")) {
    element.textContent = translate(element.dataset.i18n);
  }

  for (const element of document.querySelectorAll("[data-i18n-placeholder]")) {
    element.setAttribute("placeholder", translate(element.dataset.i18nPlaceholder));
  }

  for (const element of document.querySelectorAll("[data-i18n-aria]")) {
    element.setAttribute("aria-label", translate(element.dataset.i18nAria));
  }

  // Refresh labels and counters that are built in JS.
  if (!resultCard.hidden) {
    renderFormats(currentFormats);
  }
  updatePlaylistSubtitle();
  updateSheetDownloadText();
  updateDownloadsSummary();
  renderDownloads();
  renderQueue();
  renderHistory();
  renderPlaylist();
}

function applySettingsToUi() {
  if (downloadFolderInput) downloadFolderInput.value = appSettings.downloadFolder;
  if (defaultQuality) defaultQuality.value = appSettings.defaultQuality;
  if (skipFormatPickerShared) skipFormatPickerShared.checked = appSettings.skipPickerShared;

  concurrency = appSettings.concurrentDownloads;
  if (concurrencyValue) concurrencyValue.textContent = String(concurrency);
  if (concurrencyDown) concurrencyDown.disabled = concurrency <= 1;
  if (concurrencyUp) concurrencyUp.disabled = concurrency >= 8;

  if (speedLimit) speedLimit.value = appSettings.speedLimitMb > 0 ? String(appSettings.speedLimitMb) : "";
  if (proxyInput) proxyInput.value = appSettings.proxy;
  if (cookiesSelect) cookiesSelect.value = appSettings.cookiesFromBrowser;

  applyLanguage(appSettings.language);
  defaultDownloadPrefs = { ...defaultDownloadPrefs, quality: appSettings.defaultQuality };
  syncDefaultQualitySelect();
}

function saveLocalSettings(settings = appSettings) {
  try {
    localStorage.setItem(SETTINGS_KEY, JSON.stringify(settings));
  } catch {
    // Keep the in-memory settings for this session.
  }
}

function loadLocalSettings() {
  let local = null;
  let legacyPrefs = null;
  let legacySkip = null;

  try {
    const raw = localStorage.getItem(SETTINGS_KEY);
    if (raw) local = JSON.parse(raw);
  } catch {
    local = null;
  }

  try {
    const raw = localStorage.getItem(DEFAULT_DOWNLOAD_KEY);
    if (raw) legacyPrefs = JSON.parse(raw);
  } catch {
    legacyPrefs = null;
  }

  try {
    legacySkip = localStorage.getItem(SKIP_FORMAT_PICKER_SHARED_KEY);
    if (legacySkip === null) {
      legacySkip = localStorage.getItem(LEGACY_AUTO_DOWNLOAD_SHARED_KEY);
    }
  } catch {
    legacySkip = null;
  }

  const candidate = defaultAppSettings();
  if (legacyPrefs?.quality && QUALITY_IDS.has(legacyPrefs.quality)) {
    candidate.defaultQuality = legacyPrefs.quality;
  }
  if (legacySkip !== null) {
    candidate.skipPickerShared = legacySkip === "true";
  }
  if (local && typeof local === "object") {
    Object.assign(candidate, local);
  }

  const settings = normalizeAppSettings(candidate);
  saveLocalSettings(settings);
  return settings;
}

async function loadAppSettings() {
  if (isTauriAvailable()) {
    try {
      const raw = await window.__TAURI__.core.invoke("get_settings");
      settingsLoaded = true;
      settingsLoadError = "";
      const normalized = normalizeAppSettings(raw);
      saveLocalSettings(normalized);
      return normalized;
    } catch (error) {
      const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
      settingsLoaded = false;
      settingsLoadError = translate("settings.loadError", { error: message });
      setStatus(settingsStatus, settingsLoadError);
      return defaultAppSettings();
    }
  }

  settingsLoaded = true;
  settingsLoadError = "";
  return loadLocalSettings();
}

async function persistAppSettings() {
  if (!settingsLoaded) {
    setStatus(settingsStatus, settingsLoadError || translate("settings.stillLoading"));
    return false;
  }

  if (isTauriAvailable()) {
    try {
      const saved = await window.__TAURI__.core.invoke("save_settings", {
        settings: appSettings,
      });
      appSettings = normalizeAppSettings(saved ?? appSettings);
      saveLocalSettings(appSettings);
      applySettingsToUi();
      return true;
    } catch (error) {
      const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
      setStatus(settingsStatus, translate("settings.saveError", { error: message }));
      return false;
    }
  }

  saveLocalSettings();
  return true;
}

let downloads = [
  { id: "d1", title: "Big Buck Bunny", site: "YouTube", quality: "1080p", sizeMb: 96.4, percent: 34, step: 4, paused: false, complete: false },
  { id: "d2", title: "Late Night Mix", site: "TikTok", quality: "MP3", sizeMb: 4.1, percent: 68, step: 7, paused: false, complete: false },
];

const queue = [
  { id: "q1", title: "Travel vlog: Tokyo", site: "YouTube", quality: "720p", size: "52.1 MB" },
  { id: "q2", title: "Cooking reel", site: "TikTok", quality: "1080p", size: "18.6 MB" },
  { id: "q3", title: "Design inspiration", site: "Pinterest", quality: "Images", size: "12.4 MB" },
];

let historyItems = [
  { id: "h1", title: "Lo-fi beats to relax to", site: "YouTube", type: "video", size: "48.2 MB", date: "Nov 19, 2026" },
  { id: "h2", title: "Morning podcast episode 12", site: "Reddit", type: "audio", size: "32.7 MB", date: "Nov 18, 2026" },
  { id: "h3", title: "Cat photo dump", site: "Instagram", type: "images", size: "8.3 MB", date: "Nov 17, 2026" },
  { id: "h4", title: "Cooking reel", site: "TikTok", type: "video", size: "18.6 MB", date: "Nov 16, 2026" },
  { id: "h5", title: "Ambient rain loop", site: "YouTube", type: "audio", size: "24.1 MB", date: "Nov 15, 2026" },
  { id: "h6", title: "Design inspiration board", site: "Pinterest", type: "images", size: "12.4 MB", date: "Nov 14, 2026" },
];

let historyFilter = "all";

const MOCK_PLAYLIST_ITEMS = [
  { id: "p1", title: "Sunset timelapse", duration: "2:14", sizeMb: 42.5 },
  { id: "p2", title: "City walk at night", duration: "8:31", sizeMb: 96.4 },
  { id: "p3", title: "Coffee brewing guide", duration: "5:02", sizeMb: 52.1 },
  { id: "p4", title: "Minimal desk setup", duration: "6:48", sizeMb: 74.8 },
  { id: "p5", title: "Mountain drone shot", duration: "3:27", sizeMb: 61.2 },
  { id: "p6", title: "Late night jazz", duration: "12:05", sizeMb: 28.7 },
  { id: "p7", title: "Pixel art tutorial", duration: "9:16", sizeMb: 88.3 },
  { id: "p8", title: "Rainy window ambience", duration: "1:00:00", sizeMb: 312 },
];

let playlistItems = MOCK_PLAYLIST_ITEMS;

const selectedPlaylist = new Set();

let concurrency = 2;
let progressTimer = null;

const VALID_THEMES = new Set(["system", "oled", "yaru", "light"]);
const systemThemeQuery = window.matchMedia?.("(prefers-color-scheme: light)") ?? null;

function normalizeThemeChoice(theme) {
  if (theme === "dark") return "oled";
  return VALID_THEMES.has(theme) ? theme : "oled";
}

function resolveDatasetTheme(choice) {
  if (choice === "system") {
    return systemThemeQuery?.matches ? "light" : "dark";
  }
  if (choice === "light") return "light";
  if (choice === "yaru") return "yaru";
  return "dark";
}

function applyTheme(theme) {
  const choice = normalizeThemeChoice(theme);
  document.documentElement.dataset.theme = resolveDatasetTheme(choice);

  if (themeSelect) {
    themeSelect.value = choice;
  }
}

function setTheme(theme) {
  const choice = normalizeThemeChoice(theme);
  applyTheme(choice);
  localStorage.setItem(THEME_KEY, choice);
}

applyTheme(localStorage.getItem(THEME_KEY));

systemThemeQuery?.addEventListener("change", () => {
  if (normalizeThemeChoice(localStorage.getItem(THEME_KEY)) === "system") {
    applyTheme("system");
  }
});

if (themeSelect) {
  themeSelect.addEventListener("change", () => setTheme(themeSelect.value));
}

function setActiveTab(tab) {
  for (const item of tabs) {
    const isActive = item === tab;
    item.classList.toggle("is-active", isActive);
    item.setAttribute("aria-selected", String(isActive));
    if (isActive) {
      item.setAttribute("aria-current", "page");
    } else {
      item.removeAttribute("aria-current");
    }
  }
}

function showScreen(screenId) {
  for (const screen of screens) {
    const isActive = screen.id === screenId;
    screen.hidden = !isActive;
    screen.classList.toggle("is-active", isActive);
  }
}

function activateTab(tab) {
  setActiveTab(tab);
  showScreen(tab.dataset.screen);
  appMain.scrollTop = 0;

  if (tab.dataset.screen === "screen-downloads" && !isTauriAvailable()) {
    startFakeProgress();
  }
}

for (const tab of tabs) {
  tab.addEventListener("click", () => activateTab(tab));
}

function showPlaylistScreen() {
  setActiveTab(homeTab);
  showScreen("screen-playlist");
  appMain.scrollTop = 0;
}

function showFormatScreen() {
  setActiveTab(homeTab);
  showScreen("screen-format");
  appMain.scrollTop = 0;
}

function applyDevPreview() {
  if (isTauriAvailable()) return;

  const params = new URLSearchParams(window.location.search);
  const lang = params.get("lang");
  if (lang === "ar" || lang === "en") {
    applyLanguage(lang);
  }

  const theme = params.get("theme");
  if (theme === "dark" || theme === "light") {
    applyTheme(theme);
  }

  const screen = params.get("screen");
  if (!screen) return;

  if (screen === "home") {
    activateTab(homeTab);
    urlInput.value = "";
    resultCard.hidden = true;
    resultCard.classList.remove("is-choice-mode", "is-skip-picker");
    setStatus(homeStatus, "");
    setStatus(downloadStatus, "");
    return;
  }

  if (screen === "home-result" || screen === "format") {
    activateTab(homeTab);
    urlInput.value = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    showMockResult();
    return;
  }

  if (screen === "downloads") {
    activateTab(downloadsTab);
    return;
  }

  if (screen === "history") {
    activateTab(historyTab);
    return;
  }

  if (screen === "settings") {
    activateTab(settingsTab);
    return;
  }

  if (screen === "playlist") {
    playlistItems = MOCK_PLAYLIST_ITEMS;
    selectedPlaylist.clear();
    playlistSubtitle.textContent = translate("playlist.subtitleMock", { count: playlistItems.length });
    setStatus(playlistStatus, "");
    renderPlaylist();
    showPlaylistScreen();
    return;
  }

  if (screen === "share-sheet" && shareSheetOverlay) {
    activateTab(homeTab);
    shareSheetOverlay.hidden = false;
  }
}

formatBackButton.addEventListener("click", () => {
  setStatus(homeStatus, "");
  setActiveTab(homeTab);
  showScreen("screen-home");
  appMain.scrollTop = 0;
});

playlistBack.addEventListener("click", () => activateTab(homeTab));

function setStatus(element, message) {
  element.textContent = message;
}

function extractFirstUrl(text) {
  if (typeof text !== "string") return null;

  const match = text.match(/https?:\/\/[^\s<>"']+/i);
  if (!match) return null;

  const url = match[0].replace(/[.,!?;:)\]}]+$/g, "");
  return url || null;
}

function sharedTextFrom(value) {
  if (typeof value === "string") return value;
  if (typeof value?.url === "string" && value.url.trim()) return value.url;
  if (typeof value?.text === "string") return value.text;
  return "";
}

function loadDefaultDownloadPrefs() {
  let prefs = { quality: appSettings.defaultQuality, subtitles: false, embedThumbnail: false };

  try {
    const raw = localStorage.getItem(DEFAULT_DOWNLOAD_KEY);
    if (raw) {
      const parsed = JSON.parse(raw);
      const hasValidQuality =
        typeof parsed?.quality === "string" && QUALITY_IDS.has(parsed.quality);

      prefs = {
        quality: appSettings.defaultQuality || (hasValidQuality ? parsed.quality : "best"),
        subtitles: Boolean(parsed?.subtitles),
        embedThumbnail: Boolean(parsed?.embedThumbnail),
      };
      hasSavedDefaults =
        Boolean(parsed?.saved) ||
        hasValidQuality ||
        Boolean(parsed?.subtitles) ||
        Boolean(parsed?.embedThumbnail);
    } else {
      hasSavedDefaults = false;
    }
  } catch {
    hasSavedDefaults = false;
  }

  return prefs;
}

function saveDefaultDownloadPrefs(prefs) {
  defaultDownloadPrefs = prefs;
  hasSavedDefaults = true;

  try {
    const stored = isTauriAvailable()
      ? {
          subtitles: prefs.subtitles,
          embedThumbnail: prefs.embedThumbnail,
          saved: true,
        }
      : prefs;
    localStorage.setItem(DEFAULT_DOWNLOAD_KEY, JSON.stringify(stored));
  } catch {
    // Keep the in-memory defaults for this session.
  }
}

function syncDefaultQualitySelect() {
  if (!defaultQuality) return;

  const hasOption = Array.from(defaultQuality.options).some(
    (option) => option.value === defaultDownloadPrefs.quality,
  );
  defaultQuality.value = hasOption ? defaultDownloadPrefs.quality : "best";
}

function syncMediaOptions() {
  const imageOnly =
    currentFormats.length > 0 && currentFormats.every((format) => format.id === "images");

  optionSubtitles?.closest(".options")?.toggleAttribute("hidden", imageOnly);
  if (saveDefaultButton) saveDefaultButton.hidden = imageOnly;
}

function loadSkipFormatPickerShared() {
  if (!skipFormatPickerShared) return;
  skipFormatPickerShared.checked = Boolean(appSettings.skipPickerShared);
}

function saveSkipFormatPickerShared() {
  if (!skipFormatPickerShared) return;
  appSettings = { ...appSettings, skipPickerShared: skipFormatPickerShared.checked };
  void persistAppSettings();
}

function resolveSavedFormat(formats, savedQuality) {
  const available = Array.isArray(formats) ? formats : [];
  if (available.some((item) => item.id === savedQuality)) return savedQuality;

  const targetHeight = QUALITY_HEIGHTS.get(savedQuality);
  if (targetHeight != null) {
    const lower = available
      .map((item) => ({ id: item.id, height: QUALITY_HEIGHTS.get(item.id) }))
      .filter((item) => item.height != null && item.height <= targetHeight)
      .sort((a, b) => b.height - a.height);

    if (lower.length) return lower[0].id;
  }

  if (available.some((item) => item.id === "best")) return "best";
  return available[0]?.id || "best";
}

async function handleSharedText(text) {
  const url = extractFirstUrl(text);
  activateTab(homeTab);

  if (!url) {
    setStatus(homeStatus, translate("home.noShareLink"));
    return;
  }

  urlInput.value = url;
  setStatus(homeStatus, "");

  if (isFetchRunning) {
    pendingSharedTexts.push(url);
    setStatus(homeStatus, translate("home.shareQueued"));
    return;
  }

  await runFetchInfo({ fromShare: true });
}

function drainSharedQueue() {
  while (!isFetchRunning && pendingSharedTexts.length) {
    const next = pendingSharedTexts.shift();
    void handleSharedText(next);
  }
}

async function startSharedLinkAutoDownload() {
  const url = currentFetchUrl || urlInput.value.trim();
  if (!url) {
    resultCard.classList.remove("is-skip-picker");
    setStatus(downloadStatus, translate("result.sharedMissingLink"));
    return;
  }

  const format = currentFormats.find((item) => item.id === selectedFormat) || {
    id: selectedFormat,
    label: selectedFormat,
    size: null,
  };
  const label = localizeFormatLabel(format);

  downloadButton.disabled = true;
  setStatus(downloadStatus, translate("result.sharedStarting", { label }));

  try {
    await startRealDownload({
      url,
      title: currentFetchInfo?.title || resultTitle.textContent || translate("common.download"),
      site: currentFetchInfo?.site || resultSite.textContent || translate("common.unknownSite"),
      quality: selectedFormat,
      qualityLabel: format.label || selectedFormat,
      sizeText: format.size || null,
      audioOnly: selectedFormat === "mp3",
      subtitles: Boolean(optionSubtitles?.checked),
      embedThumbnail: Boolean(optionEmbed?.checked),
      thumbnail: currentFetchInfo?.thumbnail || null,
    });
    setStatus(downloadStatus, translate("result.sharedStarted", { label }));
  } catch (error) {
    resultCard.classList.remove("is-skip-picker");
    const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
    setStatus(downloadStatus, translate("result.sharedFailed", { error: message }));
  } finally {
    downloadButton.disabled = false;
  }
}

async function loadPendingShare() {
  if (!isTauriAvailable()) return;

  // Returns null on desktop; Android will provide the pending share link.
  try {
    const pending = await window.__TAURI__.core.invoke("get_pending_share");
    const text = sharedTextFrom(pending);
    if (text.trim()) {
      await handleSharedText(text);
    }
  } catch {
    // Ignore missing/unavailable command.
  }
}

function selectFormat(formatId) {
  selectedFormat = formatId;

  for (const button of formatList.querySelectorAll(".format-button")) {
    const isSelected = button.dataset.format === formatId;
    button.classList.toggle("is-selected", isSelected);
    button.setAttribute("aria-checked", String(isSelected));
  }
}

function renderFormats(formats) {
  formatList.replaceChildren();

  for (const format of formats) {
    const button = document.createElement("button");
    button.type = "button";
    button.className = "format-button";
    button.dataset.format = format.id;
    button.setAttribute("role", "radio");
    button.setAttribute("aria-checked", String(format.id === selectedFormat));

    if (format.id === selectedFormat) {
      button.classList.add("is-selected");
    }

    const name = document.createElement("span");
    name.className = "format-name";
    name.textContent = localizeFormatLabel(format);

    const size = document.createElement("span");
    size.className = "format-size";
    size.textContent = format.size || "—";

    button.append(name, size);
    button.addEventListener("click", () => selectFormat(format.id));
    formatList.append(button);
  }
}

function formatDuration(seconds) {
  if (typeof seconds !== "number" || !Number.isFinite(seconds) || seconds <= 0) {
    return "—";
  }

  const totalSeconds = Math.round(seconds);
  const hours = Math.floor(totalSeconds / 3600);
  const minutes = Math.floor((totalSeconds % 3600) / 60);
  const remainingSeconds = totalSeconds % 60;

  if (hours > 0) {
    return `${hours}:${String(minutes).padStart(2, "0")}:${String(remainingSeconds).padStart(2, "0")}`;
  }

  return `${minutes}:${String(remainingSeconds).padStart(2, "0")}`;
}

function normalizeThumbnailUrl(value) {
  if (typeof value !== "string" || !value.trim()) {
    return null;
  }

  try {
    const url = new URL(value);
    return url.protocol === "http:" || url.protocol === "https:" ? url.href : null;
  } catch {
    return null;
  }
}

function setResultThumbnail(value) {
  const thumbnailUrl = normalizeThumbnailUrl(value);

  if (!thumbnailUrl) {
    resultThumb.hidden = true;
    resultThumb.removeAttribute("src");
    resultThumbWrap.classList.remove("has-image");
    return;
  }

  resultThumb.src = thumbnailUrl;
  resultThumb.hidden = false;
  resultThumbWrap.classList.add("has-image");
}

resultThumb.addEventListener("error", () => {
  resultThumb.hidden = true;
  resultThumb.removeAttribute("src");
  resultThumbWrap.classList.remove("has-image");
});

function showMockResult() {
  currentFetchInfo = null;
  resultSite.textContent = MOCK_RESULT.site;
  resultTitle.textContent = MOCK_RESULT.title;
  resultChannel.textContent = MOCK_RESULT.channel;
  resultDuration.textContent = MOCK_RESULT.duration;
  resultDuration.hidden = resultDuration.textContent === "—";
  setResultThumbnail(null);

  currentFormats = MOCK_RESULT.formats;
  selectedFormat = resolveSavedFormat(currentFormats, defaultDownloadPrefs.quality);
  if (optionSubtitles) optionSubtitles.checked = Boolean(defaultDownloadPrefs.subtitles);
  if (optionEmbed) optionEmbed.checked = Boolean(defaultDownloadPrefs.embedThumbnail);
  syncMediaOptions();
  renderFormats(currentFormats);
  resultCard.classList.remove("is-choice-mode", "is-skip-picker");
  playlistChoice.hidden = true;
  resultCard.hidden = false;
  setStatus(homeStatus, translate("home.mockNotice"));
  setStatus(downloadStatus, "");
  showFormatScreen();
}

function applyFetchInfo(info, { showChoice = false, skipPicker = false } = {}) {
  const formats = Array.isArray(info.formats) ? info.formats : [];
  const entries = Array.isArray(info.entries) ? info.entries : [];

  currentFetchInfo = info;
  resultSite.textContent = info.site || translate("common.unknownSite");
  resultTitle.textContent = info.title || translate("common.untitled");
  resultChannel.textContent = info.channel || translate("common.unknownChannel");
  resultDuration.textContent = formatDuration(info.duration);
  resultDuration.hidden = resultDuration.textContent === "—";
  setResultThumbnail(info.thumbnail);

  currentFormats = formats;
  selectedFormat = resolveSavedFormat(currentFormats, defaultDownloadPrefs.quality);
  if (optionSubtitles) optionSubtitles.checked = Boolean(defaultDownloadPrefs.subtitles);
  if (optionEmbed) optionEmbed.checked = Boolean(defaultDownloadPrefs.embedThumbnail);
  syncMediaOptions();
  resultCard.classList.toggle("is-choice-mode", showChoice);
  resultCard.classList.toggle("is-skip-picker", skipPicker);
  playlistChoice.hidden = !showChoice;
  resultCard.hidden = false;
  setStatus(downloadStatus, "");

  if (showChoice) {
    setStatus(homeStatus, translate("home.choicePrompt"));
  } else {
    renderFormats(currentFormats);
    setStatus(
      homeStatus,
      skipPicker
        ? translate("home.sharedLoaded")
        : info.isPlaylist
          ? translate("home.playlistLoaded", { count: entries.length })
          : translate("home.infoLoaded"),
    );
  }

  showFormatScreen();
}

function openRealPlaylist(info) {
  const entries = Array.isArray(info.entries) ? info.entries : [];

  playlistLoadedFromTauri = true;
  playlistItems = entries.map((entry, index) => ({
    id: entry.id || `item-${index}`,
    title: entry.title || translate("common.untitled"),
    duration: formatDuration(entry.duration),
    thumbnail: entry.thumbnail || info.thumbnail || null,
    url: entry.url || null,
    sizeMb: typeof entry.filesize === "number" ? entry.filesize / (1024 * 1024) : null,
  }));
  selectedPlaylist.clear();
  updatePlaylistSubtitle();
  setStatus(playlistStatus, playlistItems.length ? "" : translate("playlist.noEntries"));
  renderPlaylist();
  resultCard.hidden = true;
  resultCard.classList.remove("is-choice-mode", "is-skip-picker");
  playlistChoice.hidden = true;
  setStatus(homeStatus, translate("home.playlistLoaded", { count: playlistItems.length }));
  showPlaylistScreen();
}

async function runFetchInfo({ noPlaylist = false, fromShare = false } = {}) {
  if (isFetchRunning) {
    return;
  }

  isFetchRunning = true;

  try {
    const url = urlInput.value.trim();
    if (!url) {
      setStatus(homeStatus, translate("home.pasteFirst"));
      urlInput.focus();
      return;
    }

    if (!/^https?:\/\//i.test(url)) {
      setStatus(homeStatus, translate("home.httpOnly"));
      urlInput.focus();
      return;
    }

    currentFetchUrl = url;

    const invoke = window.__TAURI__?.core?.invoke;
    if (typeof invoke !== "function") {
      showMockResult();
      return;
    }

    fetchButton.disabled = true;
    choiceVideoButton.disabled = true;
    choicePlaylistButton.disabled = true;
    resultCard.hidden = true;
    setStatus(homeStatus, translate(noPlaylist ? "home.fetchingVideo" : "home.fetchingInfo"));
    setStatus(downloadStatus, "");

    try {
      const info = await invoke("fetch_info", { url, noPlaylist });

      if (info.isPlaylist && info.videoInPlaylist && !noPlaylist) {
        applyFetchInfo(info, { showChoice: true });
      } else if (info.isPlaylist) {
        openRealPlaylist(info);
      } else {
        const skipPicker = fromShare && skipFormatPickerShared?.checked && isTauriAvailable();

        if (skipPicker && !hasSavedDefaults) {
          applyFetchInfo(info);
          setStatus(downloadStatus, translate("result.noSavedDefaults"));
        } else if (skipPicker) {
          applyFetchInfo(info, { skipPicker: true });
          await startSharedLinkAutoDownload();
        } else {
          applyFetchInfo(info);
        }
      }
    } catch (error) {
      const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
      setStatus(homeStatus, translate("home.fetchFailed", { error: message }));
    } finally {
      fetchButton.disabled = false;
      choiceVideoButton.disabled = false;
      choicePlaylistButton.disabled = false;
    }
  } finally {
    isFetchRunning = false;
    drainSharedQueue();
  }
}

pasteButton.addEventListener("click", async () => {
  if (!navigator.clipboard?.readText) {
    setStatus(homeStatus, translate("home.clipboardUnavailable"));
    return;
  }

  try {
    const text = await navigator.clipboard.readText();
    if (!text.trim()) {
      setStatus(homeStatus, translate("home.clipboardEmpty"));
      return;
    }

    urlInput.value = text.trim();
    setStatus(homeStatus, "");
    urlInput.focus();
  } catch {
    setStatus(homeStatus, translate("home.clipboardUnavailable"));
  }
});

linkForm.addEventListener("submit", (event) => {
  event.preventDefault();
  void runFetchInfo();
});

choiceVideoButton.addEventListener("click", () => {
  void runFetchInfo({ noPlaylist: true });
});

choicePlaylistButton.addEventListener("click", () => {
  if (!currentFetchInfo) {
    setStatus(homeStatus, translate("home.fetchAgainFirst"));
    return;
  }

  openRealPlaylist(currentFetchInfo);
});

saveDefaultButton?.addEventListener("click", () => {
  const format = currentFormats.find((item) => item.id === selectedFormat);
  if (!format) {
    setStatus(downloadStatus, translate("result.pickFormatFirst"));
    return;
  }

  saveDefaultDownloadPrefs({
    quality: selectedFormat,
    subtitles: Boolean(optionSubtitles?.checked),
    embedThumbnail: Boolean(optionEmbed?.checked),
  });
  syncDefaultQualitySelect();

  const label = localizeFormatLabel(format);
  setStatus(downloadStatus, translate("result.savedDefault", { label }));
  setStatus(settingsStatus, translate("settings.defaultQualitySet", { label }));
});

downloadButton.addEventListener("click", async () => {
  const format = currentFormats.find((item) => item.id === selectedFormat);
  if (!format) {
    setStatus(downloadStatus, translate("result.pickFormatFirst"));
    return;
  }

  if (!isTauriAvailable()) {
    const parsedSize = Number.parseFloat(String(format.size || ""));
    const sizeMb = Number.isFinite(parsedSize) && parsedSize > 0 ? parsedSize : 48.2;
    downloads.unshift({
      id: `mock-${Date.now()}`,
      title: resultTitle.textContent || MOCK_RESULT.title,
      site: resultSite.textContent || MOCK_RESULT.site,
      quality: format.id === "best" ? "best" : format.label || selectedFormat,
      sizeMb,
      percent: 0,
      step: 12,
      paused: false,
      complete: false,
    });
    renderDownloads();
    setStatus(downloadStatus, "");
    activateTab(downloadsTab);
    return;
  }

  const url = currentFetchUrl || urlInput.value.trim();
  if (!url) {
    setStatus(downloadStatus, translate("result.fetchFirst"));
    return;
  }

  downloadButton.disabled = true;
  setStatus(downloadStatus, translate("result.startingDownload"));

  try {
    await startRealDownload({
      url,
      title: currentFetchInfo?.title || resultTitle.textContent || translate("common.download"),
      site: currentFetchInfo?.site || resultSite.textContent || translate("common.unknownSite"),
      quality: selectedFormat,
      qualityLabel: format.label,
      sizeText: format.size || null,
      audioOnly: selectedFormat === "mp3",
      subtitles: Boolean(optionSubtitles?.checked),
      embedThumbnail: Boolean(optionEmbed?.checked),
      thumbnail: currentFetchInfo?.thumbnail || null,
    });

    const size = format.size ? ` (${format.size})` : "";
    const label = localizeFormatLabel(format);
    setStatus(downloadStatus, translate("result.downloadStarted", { label, size }));
    activateTab(downloadsTab);
  } catch (error) {
    const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
    setStatus(downloadStatus, translate("result.couldNotStart", { error: message }));
  } finally {
    downloadButton.disabled = false;
  }
});

function formatSpeed(item) {
  const megabytesPerTick = (item.sizeMb * item.step) / 100;
  return `${(megabytesPerTick / 0.9).toFixed(1)} MB/s`;
}

function formatEta(item) {
  const seconds = Math.max(0, Math.ceil(((100 - item.percent) / item.step) * 0.9));
  const minutes = Math.floor(seconds / 60);
  return `${minutes}:${String(seconds % 60).padStart(2, "0")}`;
}

function realDownloadItem(id, meta = {}) {
  return {
    id,
    title: meta.title || translate("common.download"),
    site: meta.site || translate("common.unknownSite"),
    quality: meta.qualityLabel || meta.quality || "best",
    sizeText: meta.sizeText || null,
    thumbnail: meta.thumbnail || null,
    percent: 0,
    speed: null,
    eta: null,
    status: "starting",
    path: null,
    error: null,
    complete: false,
    paused: false,
    real: true,
  };
}

function isActiveDownload(item) {
  if (item.real) {
    return (
      item.status === "starting" ||
      item.status === "downloading" ||
      item.status === "processing" ||
      item.status === "paused"
    );
  }
  return !item.complete;
}

function isCompleteDownload(item) {
  return item.real
    ? item.status === "done" || item.status === "error" || item.status === "cancelled"
    : item.complete;
}

function visibleDownloads() {
  return isTauriAvailable() ? Array.from(realDownloads.values()) : downloads;
}

function visibleQueue() {
  return isTauriAvailable() ? playlistQueue : queue;
}

function downloadSizeText(item) {
  if (item.real) {
    return item.sizeText ? ` • ${item.sizeText}` : "";
  }
  return ` • ${item.sizeMb.toFixed(1)} MB`;
}

function downloadSpeedText(item) {
  if (item.real) {
    if (item.status === "done") return translate("downloads.statusDone");
    if (item.status === "error") return translate("downloads.statusFailed");
    if (item.status === "cancelled") return translate("downloads.statusCancelled");
    if (item.status === "paused") return translate("downloads.statusPaused");
    if (item.status === "processing") return translate("downloads.statusProcessing");
    if (item.status === "starting") return translate("downloads.statusStarting");
    return item.speed || "—";
  }
  return item.complete
    ? translate("downloads.statusDone")
    : item.paused
      ? translate("downloads.statusPaused")
      : formatSpeed(item);
}

function downloadEtaText(item) {
  if (item.real) {
    if (item.status === "downloading" && item.eta) return translate("downloads.etaLeft", { eta: item.eta });
    return "";
  }
  return item.complete ? "" : item.paused ? "—" : translate("downloads.etaLeft", { eta: formatEta(item) });
}

async function startRealDownload(options) {
  const invoke = window.__TAURI__?.core?.invoke;
  if (typeof invoke !== "function") {
    throw new Error(translate("downloads.tauriUnavailable"));
  }

  const id = await invoke("start_download", {
    url: options.url,
    quality: options.quality,
    audioOnly: Boolean(options.audioOnly),
    subtitles: Boolean(options.subtitles),
    embedThumbnail: Boolean(options.embedThumbnail),
  });

  const meta = {
    title: options.title || translate("common.download"),
    site: options.site || translate("common.unknownSite"),
    quality: options.quality,
    qualityLabel: options.qualityLabel || options.quality || "best",
    sizeText: options.sizeText || null,
    thumbnail: options.thumbnail || null,
  };
  downloadMeta.set(id, meta);

  const existing = realDownloads.get(id);
  if (existing) {
    existing.title = meta.title;
    existing.site = meta.site;
    existing.quality = meta.qualityLabel;
    existing.sizeText = meta.sizeText;
    existing.thumbnail = meta.thumbnail;
  } else {
    realDownloads.set(id, realDownloadItem(id, meta));
  }

  renderDownloads();
  return id;
}

async function addRealHistoryItem(item) {
  if (realHistory.some((entry) => entry.id === item.id)) {
    return;
  }

  const quality = String(item.quality || "").toLowerCase();
  const entry = {
    id: item.id,
    title: item.title,
    site: item.site,
    type: quality === "mp3" ? "audio" : quality.includes("images") ? "images" : "video",
    size: item.sizeText || "—",
    date: new Date().toLocaleDateString(currentLanguage === "ar" ? "ar-EG-u-nu-latn" : "en-US", {
      month: "short",
      day: "numeric",
      year: "numeric",
    }),
    path: item.path || null,
    thumbnail: item.thumbnail || null,
  };

  if (isTauriAvailable()) {
    try {
      await window.__TAURI__.core.invoke("add_history_item", { item: entry });
    } catch (error) {
      const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
      setStatus(historyStatus, translate("history.saveError", { error: message }));
    }
  }

  realHistory.unshift(entry);
  renderHistory();
}

async function loadRealHistory() {
  if (!isTauriAvailable()) {
    return;
  }

  try {
    const items = await window.__TAURI__.core.invoke("list_history");
    realHistory = Array.isArray(items) ? items : [];
    renderHistory();
  } catch (error) {
    const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
    setStatus(historyStatus, translate("history.loadError", { error: message }));
  }
}

function finishPlaylistItem(id) {
  if (!playlistActiveIds.has(id)) {
    return;
  }

  playlistActiveIds.delete(id);
  playlistFinished += 1;
  renderQueue();
  updateDownloadsSummary();
  void startNextPlaylistDownload();
}

function handleDownloadProgress(event) {
  const payload = event?.payload ?? event;
  if (!payload || typeof payload.id !== "string") {
    return;
  }

  const meta = downloadMeta.get(payload.id) || {};
  const existed = realDownloads.has(payload.id);
  let item = realDownloads.get(payload.id);
  if (!item) {
    item = realDownloadItem(payload.id, meta);
    realDownloads.set(payload.id, item);
  }

  if (typeof payload.status === "string") {
    item.status = payload.status;
    item.paused = payload.status === "paused";
  }
  if (typeof payload.percent === "number" && Number.isFinite(payload.percent)) {
    item.percent = Math.max(0, Math.min(100, payload.percent));
  }
  if (typeof payload.speed === "string" && payload.speed.trim()) {
    item.speed = payload.speed;
  }
  if (typeof payload.eta === "string" && payload.eta.trim()) {
    item.eta = payload.eta;
  }
  if (typeof payload.path === "string" && payload.path.trim()) {
    item.path = payload.path;
  }
  if (typeof payload.error === "string" && payload.error.trim()) {
    item.error = payload.error;
  }

  if (payload.status === "processing") {
    item.percent = 100;
  }
  if (payload.status === "done") {
    item.percent = 100;
    item.complete = true;
    void addRealHistoryItem(item);
  }
  if (payload.status === "error") {
    item.complete = true;
    setStatus(downloadsStatus, translate("downloads.failedItem", { error: item.error || item.title }));
  }
  if (payload.status === "cancelled") {
    item.complete = true;
    setStatus(downloadsStatus, translate("downloads.cancelledItem", { title: item.title }));
  }

  const terminal = ["done", "error", "cancelled"].includes(payload.status);
  if (existed && !terminal) {
    updateDownloadItem(item);
    updateDownloadsSummary();
  } else {
    renderDownloads();
  }

  if (terminal && playlistActiveIds.has(payload.id)) {
    finishPlaylistItem(payload.id);
  }
}

function updateDownloadsSummary() {
  const items = visibleDownloads();
  const activeCount = items.filter(isActiveDownload).length;
  downloadsSummary.textContent = translate("downloads.activeCount", { count: activeCount });
  queueCount.textContent = translate("downloads.waitingCount", { count: visibleQueue().length });
}

function updateDownloadItem(item) {
  const article = downloadList.querySelector(`.download-item[data-id="${item.id}"]`);
  if (!article) {
    return;
  }

  const complete = isCompleteDownload(item);
  const percent = Math.max(0, Math.min(100, item.percent || 0));

  article.classList.toggle("is-paused", Boolean(item.paused));
  article.classList.toggle("is-complete", complete);
  article.classList.toggle("is-error", item.real && item.status === "error");
  article.classList.toggle("is-cancelled", item.real && item.status === "cancelled");

  const progress = article.querySelector(".a16-progress");
  progress.setAttribute("aria-valuenow", String(Math.round(percent)));
  progress.classList.toggle("is-complete", complete);
  article.querySelector(".a16-progress-fill").style.width = `${percent}%`;
  article.querySelector(".download-percent").textContent = `${Math.round(percent)}%`;
  article.querySelector(".download-speed").textContent = downloadSpeedText(item);
  article.querySelector(".download-eta").textContent = downloadEtaText(item);

  const meta = article.querySelector(".download-copy .muted");
  if (meta) {
    meta.textContent = `${item.site} • ${localizeFormatLabel(String(item.quality || "").toLowerCase(), item.quality)}${downloadSizeText(item)}`;
  }

  const pauseButton = article.querySelector('[data-action="pause"]');
  if (pauseButton) {
    pauseButton.disabled = complete;
    pauseButton.setAttribute(
      "aria-label",
      translate(complete ? "aria.downloadComplete" : item.paused ? "aria.resumeDownload" : "aria.pauseDownload"),
    );
    pauseButton.innerHTML = item.paused ? ICONS.play : ICONS.pause;
  }

  const cancelButton = article.querySelector('[data-action="cancel"]');
  if (cancelButton) {
    cancelButton.disabled = item.real && complete;
  }
}

function renderDownloads() {
  downloadList.replaceChildren();

  const items = visibleDownloads();

  if (!items.length) {
    const empty = document.createElement("p");
    empty.className = "empty-inline";
    empty.textContent = translate("downloads.noneActive");
    downloadList.append(empty);
  }

  for (const item of items) {
    const article = document.createElement("article");
    article.className = "download-item";
    article.dataset.id = item.id;
    const complete = isCompleteDownload(item);
    article.classList.toggle("is-paused", Boolean(item.paused));
    article.classList.toggle("is-complete", complete);
    article.classList.toggle("is-error", item.real && item.status === "error");
    article.classList.toggle("is-cancelled", item.real && item.status === "cancelled");

    const top = document.createElement("div");
    top.className = "download-top";

    const copy = document.createElement("div");
    copy.className = "download-copy";

    const title = document.createElement("h3");
    title.dir = "auto";
    title.textContent = item.title;

    const meta = document.createElement("p");
    meta.className = "muted";
    meta.textContent = `${item.site} • ${localizeFormatLabel(String(item.quality || "").toLowerCase(), item.quality)}${downloadSizeText(item)}`;

    copy.append(title, meta);

    const actions = document.createElement("div");
    actions.className = "download-actions";

    const pauseButton = document.createElement("button");
    pauseButton.type = "button";
    pauseButton.className = "icon-button";
    pauseButton.dataset.action = "pause";
    pauseButton.disabled = complete;
    pauseButton.setAttribute(
      "aria-label",
      translate(complete ? "aria.downloadComplete" : item.paused ? "aria.resumeDownload" : "aria.pauseDownload"),
    );
    pauseButton.innerHTML = item.paused ? ICONS.play : ICONS.pause;
    actions.append(pauseButton);

    const cancelButton = document.createElement("button");
    cancelButton.type = "button";
    cancelButton.className = "icon-button";
    cancelButton.dataset.action = "cancel";
    cancelButton.disabled = item.real && complete;
    cancelButton.setAttribute("aria-label", translate("aria.cancelDownload"));
    cancelButton.innerHTML = ICONS.cancel;

    actions.append(cancelButton);
    top.append(copy, actions);

    const progress = document.createElement("div");
    progress.className = "a16-progress";
    progress.classList.toggle("is-complete", complete);
    progress.setAttribute("role", "progressbar");
    progress.setAttribute("aria-valuemin", "0");
    progress.setAttribute("aria-valuemax", "100");
    const percent = Math.max(0, Math.min(100, item.percent || 0));
    progress.setAttribute("aria-valuenow", String(Math.round(percent)));
    progress.setAttribute("aria-label", translate("aria.downloadProgress", { title: item.title }));

    const fill = document.createElement("div");
    fill.className = "a16-progress-fill";
    fill.style.width = `${percent}%`;

    const track = document.createElement("div");
    track.className = "a16-progress-track";

    progress.append(fill, track);

    const metaRow = document.createElement("div");
    metaRow.className = "download-meta";

    const percentLabel = document.createElement("span");
    percentLabel.className = "download-percent";
    percentLabel.textContent = `${Math.round(percent)}%`;

    const speed = document.createElement("span");
    speed.className = "download-speed";
    speed.textContent = downloadSpeedText(item);

    const eta = document.createElement("span");
    eta.className = "download-eta";
    eta.textContent = downloadEtaText(item);
    if (item.real && item.error) {
      eta.title = item.error;
    }

    metaRow.append(percentLabel, speed, eta);
    article.append(top, progress, metaRow);

    if (item.real && item.error) {
      const error = document.createElement("p");
      error.className = "download-error";
      error.textContent = item.error;
      article.append(error);
    }

    downloadList.append(article);
  }

  updateDownloadsSummary();
}

function renderQueue() {
  queueList.replaceChildren();
  const items = visibleQueue();

  if (!items.length) {
    const empty = document.createElement("li");
    empty.className = "empty-inline";
    empty.textContent = translate("downloads.queueEmpty");
    queueList.append(empty);
    queueCount.textContent = translate("downloads.waitingCount", { count: 0 });
    return;
  }

  items.forEach((item, index) => {
    const row = document.createElement("li");
    row.className = "queue-item";

    const number = document.createElement("span");
    number.className = "queue-index";
    number.textContent = String(index + 1).padStart(2, "0");

    const copy = document.createElement("div");
    copy.className = "queue-copy";

    const title = document.createElement("h4");
    title.dir = "auto";
    title.textContent = item.title;

    const meta = document.createElement("p");
    meta.textContent = `${item.site || translate("common.unknownSite")} • ${localizeFormatLabel(String(item.quality || "").toLowerCase(), item.quality)} • ${item.size || "—"}`;

    copy.append(title, meta);

    const badge = document.createElement("span");
    badge.className = "queue-badge";
    badge.textContent = translate("queue.queued");

    row.append(number, copy, badge);
    queueList.append(row);
  });

  queueCount.textContent = translate("downloads.waitingCount", { count: items.length });
}

function hasActiveDownload() {
  return downloads.some((item) => !item.complete && !item.paused);
}

function stopFakeProgressIfIdle() {
  if (hasActiveDownload() || progressTimer === null) {
    return;
  }

  window.clearInterval(progressTimer);
  progressTimer = null;
}

function startFakeProgress() {
  if (isTauriAvailable() || progressTimer !== null || !hasActiveDownload()) {
    return;
  }

  progressTimer = window.setInterval(() => {
    for (const item of downloads) {
      if (item.complete || item.paused) {
        continue;
      }

      item.percent = Math.min(100, item.percent + item.step);
      if (item.percent >= 100) {
        item.complete = true;
      }
      updateDownloadItem(item);
    }

    updateDownloadsSummary();
    stopFakeProgressIfIdle();

    if (downloads.length > 0 && downloads.every((item) => item.complete)) {
      setStatus(downloadsStatus, translate("downloads.allFinished"));
    }
  }, 900);
}

downloadList.addEventListener("click", async (event) => {
  const button = event.target.closest("button[data-action]");
  if (!button) {
    return;
  }

  const article = button.closest(".download-item");
  const id = article?.dataset.id;
  if (!id) {
    return;
  }

  const realItem = realDownloads.get(id);
  if (realItem) {
    if (button.dataset.action === "pause") {
      const resuming = realItem.status === "paused";
      button.disabled = true;
      try {
        await window.__TAURI__.core.invoke(resuming ? "resume_download" : "pause_download", { id });
        realItem.status = resuming ? "downloading" : "paused";
        realItem.paused = !resuming;
        setStatus(
          downloadsStatus,
          translate(resuming ? "downloads.resumedItem" : "downloads.pausedItem", { title: realItem.title }),
        );
        updateDownloadItem(realItem);
        updateDownloadsSummary();
      } catch (error) {
        const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
        setStatus(
          downloadsStatus,
          translate(resuming ? "downloads.couldNotResume" : "downloads.couldNotPause", {
            title: realItem.title,
            error: message,
          }),
        );
        button.disabled = false;
      }
      return;
    }

    if (button.dataset.action === "cancel") {
      if (realItem.complete) {
        return;
      }

      button.disabled = true;
      try {
        await window.__TAURI__.core.invoke("cancel_download", { id });
        realItem.status = "cancelled";
        realItem.complete = true;
        realItem.paused = false;
        setStatus(downloadsStatus, translate("downloads.cancelledItem", { title: realItem.title }));
        updateDownloadItem(realItem);
        updateDownloadsSummary();

        if (playlistActiveIds.has(id)) {
          finishPlaylistItem(id);
        }
      } catch (error) {
        const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
        setStatus(
          downloadsStatus,
          translate("downloads.couldNotCancel", { title: realItem.title, error: message }),
        );
        button.disabled = false;
      }
    }
    return;
  }

  const item = downloads.find((download) => download.id === id);
  if (!item) {
    return;
  }

  if (button.dataset.action === "pause") {
    // Mock-only items (browser preview); real downloads call pause_download/resume_download above.
    item.paused = !item.paused;
    updateDownloadItem(item);
    setStatus(
      downloadsStatus,
      translate(item.paused ? "downloads.pausedItem" : "downloads.resumedItem", { title: item.title }),
    );

    if (!item.paused) {
      startFakeProgress();
    }
    stopFakeProgressIfIdle();
  }

  if (button.dataset.action === "cancel") {
    downloads = downloads.filter((download) => download.id !== item.id);
    renderDownloads();
    setStatus(downloadsStatus, translate("downloads.cancelledItem", { title: item.title }));
    stopFakeProgressIfIdle();
  }
});

// Returns the containing directory for a saved file path (supports / and \).
function parentDirectory(path) {
  if (!path) {
    return "";
  }

  const trimmed = path.replace(/[\\/]+$/, "");
  const index = Math.max(trimmed.lastIndexOf("/"), trimmed.lastIndexOf("\\"));
  return index > 0 ? trimmed.slice(0, index) : "";
}

function renderHistory() {
  const source = isTauriAvailable() ? realHistory : historyItems;
  const query = historySearch.value.trim().toLowerCase();
  const filtered = source.filter((item) => {
    const matchesFilter = historyFilter === "all" || item.type === historyFilter;
    const searchable = `${item.title} ${item.site} ${item.type}`.toLowerCase();
    return matchesFilter && searchable.includes(query);
  });

  historyList.replaceChildren();
  historyEmpty.hidden = filtered.length > 0;

  const typeLabels = {
    video: translate("history.typeVideo"),
    audio: translate("history.typeAudio"),
    images: translate("history.typeImages"),
  };

  for (const item of filtered) {
    const article = document.createElement("article");
    article.className = "history-item";
    article.dataset.id = item.id;

    const thumb = document.createElement("div");
    thumb.className = "thumb";
    thumb.setAttribute("aria-hidden", "true");

    const thumbnailUrl = normalizeThumbnailUrl(item.thumbnail);
    if (thumbnailUrl) {
      const image = document.createElement("img");
      image.className = "thumb-image";
      image.alt = "";
      image.src = thumbnailUrl;
      image.addEventListener("error", () => {
        thumb.classList.remove("has-image");
        image.remove();
        thumb.innerHTML = ICONS[item.type] ?? ICONS.video;
      });
      thumb.classList.add("has-image");
      thumb.append(image);
    } else {
      thumb.innerHTML = ICONS[item.type] ?? ICONS.video;
    }

    if (item.duration) {
      const duration = document.createElement("span");
      duration.className = "thumb-duration";
      duration.textContent = item.duration;
      thumb.append(duration);
    }

    const copy = document.createElement("div");
    copy.className = "history-copy";

    const title = document.createElement("h3");
    title.dir = "auto";
    title.textContent = item.title;

    const meta = document.createElement("p");
    meta.textContent = `${item.site} • ${typeLabels[item.type]} • ${item.date}`;

    const size = document.createElement("span");
    size.className = "history-size";
    size.textContent = item.size;

    copy.append(title, meta, size);

    const actions = document.createElement("div");
    actions.className = "item-actions";

    const openButton = document.createElement("button");
    openButton.type = "button";
    openButton.className = "secondary";
    openButton.dataset.action = "open";
    openButton.textContent = translate("history.open");
    openButton.setAttribute("aria-label", translate("aria.openItem", { title: item.title }));

    const deleteButton = document.createElement("button");
    deleteButton.type = "button";
    deleteButton.className = "secondary";
    deleteButton.dataset.action = "delete";
    deleteButton.textContent = translate("history.delete");
    deleteButton.setAttribute("aria-label", translate("aria.deleteItem", { title: item.title }));

    actions.append(openButton, deleteButton);
    article.append(thumb, copy, actions);
    historyList.append(article);
  }
}

historySearch.addEventListener("input", renderHistory);

historyFilters.addEventListener("click", (event) => {
  const button = event.target.closest("[data-filter]");
  if (!button) {
    return;
  }

  historyFilter = button.dataset.filter;

  for (const chip of historyFilters.querySelectorAll("[data-filter]")) {
    const isActive = chip === button;
    chip.classList.toggle("is-selected", isActive);
    chip.setAttribute("aria-pressed", String(isActive));
  }

  renderHistory();
});

historyList.addEventListener("click", async (event) => {
  const button = event.target.closest("button[data-action]");
  if (!button) {
    return;
  }

  const article = button.closest(".history-item");
  const source = isTauriAvailable() ? realHistory : historyItems;
  const item = source.find((entry) => entry.id === article?.dataset.id);
  if (!item) {
    return;
  }

  if (button.dataset.action === "open" || button.dataset.action === "folder") {
    if (!isTauriAvailable()) {
      setStatus(historyStatus, translate("history.mockAction", { action: button.dataset.action, title: item.title }));
      return;
    }

    const target = button.dataset.action === "open" ? item.path : parentDirectory(item.path);
    if (!target) {
      setStatus(
        historyStatus,
        translate(button.dataset.action === "open" ? "history.noFilePath" : "history.noFolderPath"),
      );
      return;
    }

    try {
      await window.__TAURI__.core.invoke("open_path", { path: target });
      setStatus(
        historyStatus,
        translate(button.dataset.action === "open" ? "history.openedItem" : "history.openedFolder", {
          title: item.title,
        }),
      );
    } catch (error) {
      const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
      setStatus(historyStatus, translate("history.couldNotOpen", { title: item.title, error: message }));
    }
  }

  if (button.dataset.action === "delete") {
    if (isTauriAvailable()) {
      try {
        await window.__TAURI__.core.invoke("delete_history_item", { id: item.id });
      } catch (error) {
        const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
        setStatus(historyStatus, translate("history.couldNotDelete", { title: item.title, error: message }));
        return;
      }

      realHistory = realHistory.filter((entry) => entry.id !== item.id);
    } else {
      historyItems = historyItems.filter((entry) => entry.id !== item.id);
    }

    renderHistory();
    setStatus(historyStatus, translate("history.deletedItem", { title: item.title }));
  }
});

function updatePlaylistSelection() {
  const selectedItems = playlistItems.filter((item) => selectedPlaylist.has(item.id));
  const totalSize = selectedItems.reduce(
    (sum, item) => sum + (typeof item.sizeMb === "number" ? item.sizeMb : 0),
    0,
  );
  const allHaveSizes =
    playlistItems.length > 0 && playlistItems.every((item) => typeof item.sizeMb === "number");

  selectedCount.textContent = translate("playlist.selectedCount", { count: selectedItems.length });
  selectedSize.textContent = allHaveSizes
    ? totalSize > 0
      ? `${totalSize.toFixed(1)} MB`
      : "0 MB"
    : "—";
  playlistSummary.textContent = translate("playlist.selectedSummary", {
    selected: selectedItems.length,
    total: playlistItems.length,
  });
  playlistDownload.disabled = selectedItems.length === 0;
  selectAllButton.textContent =
    playlistItems.length > 0 && selectedItems.length === playlistItems.length
      ? translate("playlist.clearAll")
      : translate("playlist.selectAll");
}

function renderPlaylist() {
  playlistList.replaceChildren();

  for (const item of playlistItems) {
    const row = document.createElement("label");
    row.className = "playlist-item";
    row.dataset.id = item.id;
    row.classList.toggle("is-selected", selectedPlaylist.has(item.id));

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.checked = selectedPlaylist.has(item.id);
    checkbox.dataset.playlistId = item.id;
    checkbox.setAttribute("aria-label", translate("aria.selectItem", { title: item.title }));

    const thumb = document.createElement("div");
    thumb.className = "thumb";
    thumb.setAttribute("aria-hidden", "true");

    const thumbnailUrl = normalizeThumbnailUrl(item.thumbnail);
    if (thumbnailUrl) {
      const image = document.createElement("img");
      image.className = "thumb-image";
      image.alt = "";
      image.src = thumbnailUrl;
      image.addEventListener("error", () => {
        thumb.classList.remove("has-image");
        image.remove();
        thumb.innerHTML = ICONS.video;
      });
      thumb.classList.add("has-image");
      thumb.append(image);
    } else {
      thumb.innerHTML = ICONS.video;
    }

    const copy = document.createElement("div");
    copy.className = "playlist-copy";

    const title = document.createElement("h3");
    title.dir = "auto";
    title.textContent = item.title;

    const meta = document.createElement("p");
    const sizeText = typeof item.sizeMb === "number" ? ` • ${item.sizeMb.toFixed(1)} MB` : "";
    meta.textContent = `${item.duration || "—"}${sizeText}`;

    copy.append(title, meta);
    row.append(checkbox, thumb, copy);
    playlistList.append(row);
  }

  updatePlaylistSelection();
}

playlistList.addEventListener("change", (event) => {
  const checkbox = event.target.closest('input[type="checkbox"][data-playlist-id]');
  if (!checkbox) {
    return;
  }

  if (checkbox.checked) {
    selectedPlaylist.add(checkbox.dataset.playlistId);
  } else {
    selectedPlaylist.delete(checkbox.dataset.playlistId);
  }

  checkbox.closest(".playlist-item").classList.toggle("is-selected", checkbox.checked);
  updatePlaylistSelection();
});

selectAllButton.addEventListener("click", () => {
  if (selectedPlaylist.size === playlistItems.length) {
    selectedPlaylist.clear();
  } else {
    for (const item of playlistItems) {
      selectedPlaylist.add(item.id);
    }
  }

  renderPlaylist();
});

function downloadConcurrencyLimit() {
  return Math.min(8, Math.max(1, Math.round(Number(concurrency) || 2)));
}

async function startNextPlaylistDownload() {
  if (!isTauriAvailable() || playlistScheduling) {
    return;
  }

  playlistScheduling = true;
  try {
    // Fill open slots while respecting the concurrent downloads setting.
    while (playlistQueue.length && playlistActiveIds.size < downloadConcurrencyLimit()) {
      const next = playlistQueue.shift();
      setStatus(
        playlistStatus,
        translate("playlist.downloadingItem", {
          current: playlistFinished + playlistActiveIds.size + 1,
          total: playlistTotal,
          title: next.title,
        }),
      );

      try {
        const id = await startRealDownload({
          url: next.url,
          title: next.title,
          site: next.site,
          quality: next.quality,
          qualityLabel: "Best",
          audioOnly: false,
          subtitles: false,
          embedThumbnail: false,
          thumbnail: next.thumbnail || null,
        });
        playlistActiveIds.add(id);

        const item = realDownloads.get(id);
        if (item && isCompleteDownload(item)) {
          finishPlaylistItem(id);
        }
      } catch (error) {
        playlistFinished += 1;
        const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
        setStatus(playlistStatus, translate("playlist.couldNotStartItem", { title: next.title, error: message }));
      }

      renderQueue();
      updateDownloadsSummary();
    }
  } finally {
    playlistScheduling = false;
  }

  renderQueue();
  updateDownloadsSummary();

  if (!playlistQueue.length && playlistActiveIds.size === 0) {
    playlistDownload.disabled = selectedPlaylist.size === 0;

    if (playlistTotal > 0) {
      setStatus(
        playlistStatus,
        translate("playlist.finishedCount", { done: playlistFinished, total: playlistTotal }),
      );
    }
  }
}

playlistDownload.addEventListener("click", () => {
  const selectedItems = playlistItems.filter((item) => selectedPlaylist.has(item.id));
  if (!selectedItems.length) {
    return;
  }

  if (!isTauriAvailable()) {
    setStatus(playlistStatus, translate("playlist.mockQueued", { count: selectedItems.length }));
    return;
  }

  if (playlistActiveIds.size || playlistQueue.length) {
    setStatus(playlistStatus, translate("playlist.alreadyRunning"));
    return;
  }

  const missingUrl = selectedItems.find((item) => !item.url);
  if (missingUrl) {
    setStatus(playlistStatus, translate("playlist.missingUrl", { title: missingUrl.title }));
    return;
  }

  playlistDownload.disabled = true;
  playlistQueue = selectedItems.map((item) => ({
    id: item.id,
    title: item.title,
    url: item.url,
    site: currentFetchInfo?.site || translate("common.unknownSite"),
    quality: "best",
    size: "—",
  }));
  playlistTotal = playlistQueue.length;
  playlistFinished = 0;
  playlistActiveIds.clear();
  renderQueue();
  updateDownloadsSummary();
  void startNextPlaylistDownload();
});

chooseFolderButton.addEventListener("click", async () => {
  if (!isTauriAvailable()) {
    setStatus(settingsStatus, translate("settings.folderDesktopOnly"));
    return;
  }

  chooseFolderButton.disabled = true;
  try {
    const selected = await window.__TAURI__.core.invoke("plugin:dialog|open", {
      options: {
        directory: true,
        multiple: false,
        title: translate("settings.chooseFolderTitle"),
      },
    });
    const path = Array.isArray(selected) ? selected[0] : selected;
    if (!path) {
      return;
    }

    appSettings = { ...appSettings, downloadFolder: path };
    downloadFolderInput.value = path;
    if (await persistAppSettings()) {
      setStatus(settingsStatus, translate("settings.folderSaved"));
    }
  } catch (error) {
    const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
    setStatus(settingsStatus, translate("settings.folderError", { error: message }));
  } finally {
    chooseFolderButton.disabled = false;
  }
});

defaultQuality.addEventListener("change", () => {
  if (!QUALITY_IDS.has(defaultQuality.value)) return;

  const label = defaultQuality.selectedOptions[0]?.text ?? defaultQuality.value;
  saveDefaultDownloadPrefs({
    ...defaultDownloadPrefs,
    quality: defaultQuality.value,
  });
  setStatus(settingsStatus, translate("settings.defaultQualitySet", { label }));
});

function updateConcurrency() {
  concurrencyValue.textContent = String(concurrency);
  concurrencyDown.disabled = concurrency <= 1;
  concurrencyUp.disabled = concurrency >= 8;
}

function setConcurrency(next) {
  concurrency = Math.min(8, Math.max(1, next));
  appSettings = { ...appSettings, concurrentDownloads: concurrency };
  updateConcurrency();
  // Increasing the limit can release downloads that were waiting.
  void startNextPlaylistDownload();
  void persistAppSettings();
}

concurrencyDown.addEventListener("click", () => setConcurrency(concurrency - 1));
concurrencyUp.addEventListener("click", () => setConcurrency(concurrency + 1));

speedLimit.addEventListener("change", async () => {
  const raw = speedLimit.value.trim();
  const value = raw ? Number(raw) : 0;
  if (!Number.isFinite(value) || value < 0) {
    setStatus(settingsStatus, translate("settings.speedInvalid"));
    return;
  }

  appSettings = { ...appSettings, speedLimitMb: value > 0 ? value : 0 };
  if (await persistAppSettings()) {
    setStatus(
      settingsStatus,
      appSettings.speedLimitMb > 0
        ? translate("settings.speedSet", { value: appSettings.speedLimitMb })
        : translate("settings.speedUnlimited"),
    );
  }
});

proxyInput.addEventListener("change", async () => {
  const value = proxyInput.value.trim();
  if (!isProxyValid(value)) {
    setStatus(settingsStatus, translate("settings.proxyInvalid"));
    return;
  }

  appSettings = { ...appSettings, proxy: value };
  if (await persistAppSettings()) {
    setStatus(settingsStatus, translate(appSettings.proxy ? "settings.proxySaved" : "settings.proxyDisabled"));
  }
});

cookiesSelect.addEventListener("change", async () => {
  const value = cookiesSelect.value;
  if (!COOKIE_BROWSERS.has(value)) {
    return;
  }

  appSettings = { ...appSettings, cookiesFromBrowser: value };
  if (await persistAppSettings()) {
    const label = cookiesSelect.selectedOptions[0]?.text ?? translate("cookies.none");
    setStatus(settingsStatus, translate("settings.cookiesSet", { label }));
  }
});

langCapSwitch?.addEventListener("click", async (event) => {
  const picked = event.target.closest("[data-cap-lang]")?.dataset.capLang;
  const value = picked ? (picked === "ar" ? "ar" : "en") : currentLanguage === "ar" ? "en" : "ar";
  if (value === currentLanguage) return;
  appSettings = { ...appSettings, language: value };
  applyLanguage(value);
  if (await persistAppSettings()) {
    setStatus(settingsStatus, translate("settings.saved"));
  }
});

checkUpdatesLink?.addEventListener("click", (event) => {
  event.preventDefault();
  // TODO(rust): Check for app releases/updates via Tauri updater or backend command.
  setStatus(settingsStatus, translate("settings.versionStatus"));
});

githubLink?.addEventListener("click", (event) => {
  event.preventDefault();
  // TODO(rust): Open the repository URL in the system browser via Tauri opener.
  setStatus(settingsStatus, translate("settings.repoStatus"));
});

shareSheetBackdrop?.addEventListener("click", () => {
  if (shareSheetOverlay) shareSheetOverlay.hidden = true;
});

sheetQualityPills?.addEventListener("click", (event) => {
  const pill = event.target.closest(".quality-pill");
  if (!pill) return;
  for (const item of sheetQualityPills.querySelectorAll(".quality-pill")) {
    item.classList.remove("is-selected");
  }
  pill.classList.add("is-selected");
  sheetSelectedMode = "video";
  if (sheetSize) sheetSize.textContent = pill.dataset.s || "";
  updateSheetDownloadText();
});

sheetMp3Btn?.addEventListener("click", () => {
  sheetSelectedMode = "mp3";
  updateSheetDownloadText();
});

sheetDownloadBtn?.addEventListener("click", () => {
  if (shareSheetOverlay) shareSheetOverlay.hidden = true;
  activateTab(downloadsTab);
});

updateEngineButton.addEventListener("click", async () => {
  if (!isTauriAvailable()) {
    setStatus(settingsStatus, translate("settings.engineDesktopOnly"));
    return;
  }

  updateEngineButton.disabled = true;
  setStatus(settingsStatus, translate("settings.engineUpdating"));

  try {
    const result = await window.__TAURI__.core.invoke("update_engine");
    const version = result?.version || translate("common.unknown");
    engineVersion.textContent = version;
    engineVersion.removeAttribute("data-i18n");
    setStatus(settingsStatus, translate("settings.engineUpdated", { version }));
  } catch (error) {
    const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
    setStatus(settingsStatus, translate("settings.engineError", { error: message }));
  } finally {
    updateEngineButton.disabled = false;
  }
});

skipFormatPickerShared?.addEventListener("change", () => {
  saveSkipFormatPickerShared();
  setStatus(
    settingsStatus,
    translate(skipFormatPickerShared.checked ? "settings.sharedDefaultsOn" : "settings.sharedDefaultsOff"),
  );
});

window.addEventListener("shared-url", (event) => {
  void handleSharedText(sharedTextFrom(event?.detail ?? {}));
});

defaultDownloadPrefs = loadDefaultDownloadPrefs();
syncDefaultQualitySelect();
loadSkipFormatPickerShared();

const initialLocalSettings = loadLocalSettings();
applyLanguage(initialLocalSettings.language);

if (isTauriAvailable()) {
  const listen = window.__TAURI__?.event?.listen;

  if (typeof listen === "function") {
    window.__TAURI__.event
      .listen("download-progress", handleDownloadProgress)
      .catch((error) => {
        const message = typeof error === "string" ? error : error?.message ?? translate("common.unknownError");
        setStatus(downloadsStatus, translate("downloads.listenerError", { error: message }));
      });
  } else {
    setStatus(downloadsStatus, translate("downloads.listenerUnavailable"));
  }

  playlistItems = [];
  updatePlaylistSubtitle();
  void loadPendingShare();
  void loadRealHistory();
} else {
  updatePlaylistSubtitle();
}

updateConcurrency();
renderDownloads();
renderQueue();
renderHistory();
renderPlaylist();

void loadAppSettings().then((settings) => {
  appSettings = settings;
  applySettingsToUi();
  // Settings may change the concurrency limit, so fill any open slots.
  void startNextPlaylistDownload();
});
applyDevPreview();
