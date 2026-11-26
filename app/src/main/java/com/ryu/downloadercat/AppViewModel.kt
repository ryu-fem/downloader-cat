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
import com.ryu.downloadercat.data.HistoryRepository
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
        viewModelScope.launch { loadSettings() }
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

    fun fetchInfo() {
        val trimmed = url.trim()
        if (isFetching || trimmed.isEmpty()) return

        isFetching = true
        fetchError = null
        homeStatus = null
        downloadStatus = null
        mediaInfo = null

        viewModelScope.launch {
            try {
                val info = DownloaderEngine.fetchInfo(appContext, trimmed)
                val media = info.toMediaInfo(trimmed)
                if (media.formats.isEmpty()) {
                    fetchError = EngineError.NO_FORMATS
                } else {
                    mediaInfo = media
                    selectedFormatId = media.formats.first().id
                    route = Route.Format
                }
            } catch (e: CancellationException) {
                throw e
            } catch (throwable: Throwable) {
                fetchError = DownloaderEngine.mapError(throwable)
            } finally {
                isFetching = false
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
}
