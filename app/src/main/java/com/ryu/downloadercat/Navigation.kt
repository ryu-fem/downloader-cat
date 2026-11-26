package com.ryu.downloadercat

sealed interface Route {
    data object Home : Route
    data object Format : Route
    data object Downloads : Route
    data object History : Route
    data object Playlist : Route
    data object Settings : Route

    val tab: AppTab
        get() = when (this) {
            Home, Format, Playlist -> AppTab.HOME
            Downloads -> AppTab.DOWNLOADS
            History -> AppTab.HISTORY
            Settings -> AppTab.SETTINGS
        }
}

enum class AppTab {
    HOME,
    DOWNLOADS,
    HISTORY,
    SETTINGS,
}
