package com.ryu.downloadercat

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ryu.downloadercat.data.AppSettings
import com.ryu.downloadercat.data.DownloadRecord
import com.ryu.downloadercat.data.FormatKind
import com.ryu.downloadercat.data.HistoryRepository
import com.ryu.downloadercat.data.MediaFormat
import com.ryu.downloadercat.data.MediaInfo
import com.ryu.downloadercat.data.SettingsRepository
import com.ryu.downloadercat.download.DownloadRepository
import com.ryu.downloadercat.download.DownloadTask
import com.ryu.downloadercat.engine.DownloaderEngine
import com.ryu.downloadercat.engine.EngineError
import com.ryu.downloadercat.engine.EngineUpdateState
import com.ryu.downloadercat.engine.toMediaInfo
import com.ryu.downloadercat.ui.theme.ThemeMode
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@Stable
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext
    private val historyRepository = HistoryRepository(appContext)
    private val settingsRepository = SettingsRepository(appContext)
    private var settingsLoaded = false
    private lateinit var settingsJob: Job
    private var pendingFetchUrl: String? = null
    private var pendingFetchFromShare = false
    private val sharedUrlRegex = Regex("""https?://[^\s<>"']+""", RegexOption.IGNORE_CASE)

    var route by mutableStateOf<Route>(Route.Home)

    var url by mutableStateOf("")
    var homeStatus by mutableStateOf<String?>(null)
    var isFetching by mutableStateOf(false)
    var fetchError by mutableStateOf<EngineError?>(null)
    var mediaInfo by mutableStateOf<MediaInfo?>(null)
    var selectedFormatId by mutableStateOf("best")
    var subtitles by mutableStateOf(false)
    var embedThumbnail by mutableStateOf(false)
    var downloadStatus by mutableStateOf<String?>(null)

    var sharedUrl by mutableStateOf<String?>(null)
    var shareSheetVisible by mutableStateOf(false)
    var autoDownloadAfterShare by mutableStateOf(false)

    var engineVersion by mutableStateOf<String?>(null)
    var engineUpdateState by mutableStateOf(EngineUpdateState.IDLE)
    var engineError by mutableStateOf<EngineError?>(null)

    var themeMode by mutableStateOf(ThemeMode.DARK)

    var downloadFolder by mutableStateOf("Download/DownloaderCat")
    var defaultQuality by mutableStateOf("best")
    var skipPicker by mutableStateOf(false)
    var concurrency by mutableStateOf(2)
    var speedLimit by mutableStateOf("")
    var proxy by mutableStateOf("")
    var settingsStatus by mutableStateOf<String?>(null)

    val downloadTasks: StateFlow<List<DownloadTask>> = DownloadRepository.tasks
    val history: StateFlow<List<DownloadRecord>> = historyRepository.items.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000L),
        initialValue = emptyList(),
    )

    init {
        settingsJob = viewModelScope.launch { loadSettings() }
        viewModelScope.launch {
            try {
                DownloaderEngine.initialize(appContext)
                engineVersion = DownloaderEngine.versionName(appContext)
            } catch (e: CancellationException) {
                throw e
            } catch (throwable: Throwable) {
                engineError = DownloaderEngine.mapError(throwable)
                engineUpdateState = EngineUpdateState.FAILED
            }
        }
    }

    fun navigateToTab(tab: AppTab) {
        route = when (tab) {
            AppTab.HOME -> Route.Home
            AppTab.DOWNLOADS -> Route.Downloads
            AppTab.HISTORY -> Route.History
            AppTab.SETTINGS -> Route.Settings
        }
    }

    fun onSharedText(text: String) {
        sharedUrl = extractFirstUrl(text)
        shareSheetVisible = true
    }

    fun dismissShareSheet() {
        shareSheetVisible = false
        sharedUrl = null
    }

    fun acceptSharedLink() {
        val link = sharedUrl ?: return
        shareSheetVisible = false
        sharedUrl = null
        url = link
        homeStatus = null
        fetchError = null
        route = Route.Home

        viewModelScope.launch {
            settingsJob.join()
            fetchInfo(fromShare = true)
        }
    }

    fun fetchInfo(fromShare: Boolean = false) {
        val trimmed = url.trim()
        if (trimmed.isEmpty()) return
        if (isFetching) {
            pendingFetchUrl = trimmed
            pendingFetchFromShare = fromShare
            return
        }

        isFetching = true
        fetchError = null
        homeStatus = null
        downloadStatus = null
        mediaInfo = null
        autoDownloadAfterShare = false

        viewModelScope.launch {
            try {
                val info = DownloaderEngine.fetchInfo(appContext, trimmed)
                val media = info.toMediaInfo(trimmed)
                if (media.formats.isEmpty()) {
                    fetchError = EngineError.NO_FORMATS
                } else {
                    mediaInfo = media
                    selectedFormatId = resolveFormatId(media.formats, defaultQuality)
                    autoDownloadAfterShare = fromShare && skipPicker
                    route = Route.Format
                }
            } catch (e: CancellationException) {
                throw e
            } catch (throwable: Throwable) {
                fetchError = DownloaderEngine.mapError(throwable)
            } finally {
                isFetching = false
                pendingFetchUrl?.let { next ->
                    val nextFromShare = pendingFetchFromShare
                    pendingFetchUrl = null
                    pendingFetchFromShare = false
                    url = next
                    fetchInfo(fromShare = nextFromShare)
                }
            }
        }
    }

    fun updateEngine() {
        if (engineUpdateState == EngineUpdateState.UPDATING) return

        engineUpdateState = EngineUpdateState.UPDATING
        engineError = null

        viewModelScope.launch {
            try {
                engineUpdateState = DownloaderEngine.update(appContext)
                engineVersion = DownloaderEngine.versionName(appContext)
            } catch (e: CancellationException) {
                throw e
            } catch (throwable: Throwable) {
                engineError = DownloaderEngine.mapError(throwable)
                engineUpdateState = EngineUpdateState.FAILED
            }
        }
    }

    fun startDownload(qualityLabel: String) {
        val media = mediaInfo ?: return
        val format = media.formats.firstOrNull { it.id == selectedFormatId }
            ?: media.formats.firstOrNull()
            ?: return

        autoDownloadAfterShare = false
        downloadStatus = null
        try {
            DownloadRepository.enqueue(
                context = appContext,
                media = media,
                format = format,
                qualityLabel = qualityLabel,
                subtitles = subtitles,
                embedThumbnail = embedThumbnail,
            )
            route = Route.Downloads
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            downloadStatus = appContext.getString(R.string.error_unknown)
        }
    }

    fun cancelDownload(id: String) {
        viewModelScope.launch(Dispatchers.IO) {
            DownloadRepository.cancel(id)
        }
    }

    fun clearFinishedDownloads() {
        DownloadRepository.clearFinished()
    }

    fun deleteHistoryItem(id: String) {
        viewModelScope.launch {
            historyRepository.delete(id)
        }
    }

    fun persistSettings() {
        if (!settingsLoaded) return

        val snapshot = AppSettings(
            downloadFolder = downloadFolder,
            defaultQuality = defaultQuality,
            skipPicker = skipPicker,
            concurrency = concurrency.coerceIn(1, 8),
            speedLimitMb = speedLimit.toIntOrNull() ?: 0,
            proxy = proxy,
            themeMode = themeMode.name.lowercase(Locale.US),
        )

        viewModelScope.launch {
            try {
                settingsRepository.update(snapshot)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Throwable) {
                settingsStatus = appContext.getString(R.string.error_unknown)
            }
        }
    }

    private suspend fun loadSettings() {
        try {
            val stored = settingsRepository.settings.first()
            themeMode = runCatching {
                ThemeMode.valueOf(stored.themeMode.uppercase(Locale.US))
            }.getOrDefault(ThemeMode.DARK)
            downloadFolder = stored.downloadFolder
            defaultQuality = stored.defaultQuality
            skipPicker = stored.skipPicker
            concurrency = stored.concurrency.coerceIn(1, 8)
            speedLimit = stored.speedLimitMb.takeIf { it > 0 }?.toString().orEmpty()
            proxy = stored.proxy
        } catch (e: CancellationException) {
            throw e
        } catch (_: Throwable) {
            // Keep the defaults if the settings file cannot be read.
        }
        settingsLoaded = true
    }

    private fun extractFirstUrl(text: String): String? {
        val match = sharedUrlRegex.find(text)?.value ?: return null
        return match
            .trimEnd('.', ',', '!', '?', ';', ':', ')', ']', '}', '>', '"', '\'', '،', '؛', '؟')
            .takeIf { it.isNotBlank() }
    }

    // Map a settings preset to the closest available format.
    private fun resolveFormatId(formats: List<MediaFormat>, quality: String): String {
        if (formats.isEmpty()) return "best"

        val normalized = quality.trim().lowercase(Locale.US)
        if (normalized == "mp3") {
            return formats.firstOrNull { it.kind == FormatKind.AUDIO }?.id
                ?: formats.first().id
        }

        val targetHeight = when (normalized) {
            "4k" -> 2160
            "1440p" -> 1440
            "1080p" -> 1080
            "720p" -> 720
            "480p" -> 480
            "360p" -> 360
            else -> null
        }
        val video = formats.filter { it.kind != FormatKind.AUDIO }

        if (targetHeight != null) {
            video.filter { (it.height ?: 0) in 1..targetHeight }
                .maxByOrNull { it.height ?: 0 }
                ?.let { return it.id }
        }

        return video.firstOrNull()?.id ?: formats.first().id
    }
}
