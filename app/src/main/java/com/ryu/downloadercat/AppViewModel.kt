package com.ryu.downloadercat

import android.app.Application
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.ryu.downloadercat.data.MediaInfo
import com.ryu.downloadercat.engine.DownloaderEngine
import com.ryu.downloadercat.engine.EngineError
import com.ryu.downloadercat.engine.EngineUpdateState
import com.ryu.downloadercat.engine.toMediaInfo
import com.ryu.downloadercat.ui.theme.ThemeMode
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/**
 * Phase 1/2 keeps shell state in memory. Room and DataStore arrive in phase 3.
 */
@Stable
class AppViewModel(application: Application) : AndroidViewModel(application) {
    private val appContext = application.applicationContext

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

    var downloadFolder by mutableStateOf("Downloads/DownloaderCat")
    var defaultQuality by mutableStateOf("best")
    var skipPicker by mutableStateOf(false)
    var concurrency by mutableStateOf(2)
    var speedLimit by mutableStateOf("")
    var proxy by mutableStateOf("")
    var settingsStatus by mutableStateOf<String?>(null)

    init {
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
}
