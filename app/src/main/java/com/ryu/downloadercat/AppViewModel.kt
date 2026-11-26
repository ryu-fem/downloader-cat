package com.ryu.downloadercat

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.ryu.downloadercat.data.DownloadItem
import com.ryu.downloadercat.data.HistoryFilter
import com.ryu.downloadercat.data.HistoryItem
import com.ryu.downloadercat.data.MockData
import com.ryu.downloadercat.data.PlaylistItem
import com.ryu.downloadercat.ui.theme.ThemeMode

/**
 * Phase 1 keeps all shell state in memory. Room and DataStore arrive in phase 3.
 */
class AppViewModel : ViewModel() {
    var route by mutableStateOf<Route>(Route.Home)

    var url by mutableStateOf("")
    var homeStatus by mutableStateOf<String?>(null)
    var selectedFormatId by mutableStateOf("best")
    var subtitles by mutableStateOf(false)
    var embedThumbnail by mutableStateOf(false)

    val downloads = mutableStateListOf<DownloadItem>().apply { addAll(MockData.downloads()) }
    private var downloadCounter = 0

    var historyQuery by mutableStateOf("")
    var historyFilter by mutableStateOf(HistoryFilter.ALL)
    val history = mutableStateListOf<HistoryItem>().apply { addAll(MockData.history()) }
    var historyStatus by mutableStateOf<String?>(null)

    var selectedPlaylist by mutableStateOf<Set<String>>(emptySet())
    var playlistStatus by mutableStateOf<String?>(null)

    var themeMode by mutableStateOf(ThemeMode.DARK)

    var downloadFolder by mutableStateOf("Downloads/DownloaderCat")
    var defaultQuality by mutableStateOf("best")
    var skipPicker by mutableStateOf(false)
    var concurrency by mutableStateOf(2)
    var speedLimit by mutableStateOf("")
    var proxy by mutableStateOf("")
    var settingsStatus by mutableStateOf<String?>(null)

    fun navigateToTab(tab: AppTab) {
        route = when (tab) {
            AppTab.HOME -> Route.Home
            AppTab.DOWNLOADS -> Route.Downloads
            AppTab.HISTORY -> Route.History
            AppTab.SETTINGS -> Route.Settings
        }
    }

    fun tickDownloads() {
        downloads.forEach { item ->
            if (!item.paused && !item.complete) {
                item.percent = (item.percent + item.step).coerceAtMost(100f)
                if (item.percent >= 100f) {
                    item.complete = true
                }
            }
        }
    }

    fun startMockDownload() {
        val format = MockData.result.formats.firstOrNull { it.id == selectedFormatId }
            ?: MockData.result.formats.first()
        downloads.add(
            0,
            DownloadItem(
                id = "d-new-${++downloadCounter}",
                title = MockData.result.title,
                site = MockData.result.site,
                quality = format.id,
                sizeMb = format.sizeMb,
                initialPercent = 0f,
                step = 4f,
            ),
        )
        route = Route.Downloads
    }

    fun cancelDownload(id: String) {
        downloads.removeAll { it.id == id }
    }

    fun deleteHistory(id: String) {
        history.removeAll { it.id == id }
    }

    fun filteredHistory(): List<HistoryItem> {
        val query = historyQuery.trim().lowercase()
        return history.filter { item ->
            val matchesFilter = historyFilter == HistoryFilter.ALL || item.type.name == historyFilter.name
            val searchable = "${item.title} ${item.site} ${item.type.name}".lowercase()
            matchesFilter && (query.isEmpty() || searchable.contains(query))
        }
    }

    fun togglePlaylistItem(id: String) {
        selectedPlaylist = if (id in selectedPlaylist) selectedPlaylist - id else selectedPlaylist + id
    }

    fun toggleSelectAllPlaylist() {
        selectedPlaylist = if (selectedPlaylist.size == MockData.playlist.size) {
            emptySet()
        } else {
            MockData.playlist.map { it.id }.toSet()
        }
    }

    fun selectedPlaylistItems(): List<PlaylistItem> =
        MockData.playlist.filter { it.id in selectedPlaylist }

    fun selectedPlaylistSizeMb(): Double =
        selectedPlaylistItems().sumOf { it.sizeMb }
}
