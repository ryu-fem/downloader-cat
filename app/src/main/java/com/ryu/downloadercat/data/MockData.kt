package com.ryu.downloadercat.data

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Image
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import com.ryu.downloadercat.R

data class FormatOption(
    val id: String,
    val sizeMb: Double,
)

data class MockResult(
    val site: String,
    val title: String,
    val channel: String,
    val duration: String,
    val formats: List<FormatOption>,
)

data class QueueItem(
    val id: String,
    val title: String,
    val site: String,
    val quality: String,
    val sizeMb: Double,
)

enum class HistoryType(
    @StringRes val labelRes: Int,
    val icon: ImageVector,
) {
    VIDEO(R.string.history_type_video, Icons.Outlined.Movie),
    AUDIO(R.string.history_type_audio, Icons.Outlined.MusicNote),
    IMAGES(R.string.history_type_images, Icons.Outlined.Image),
}

enum class HistoryFilter(
    @StringRes val labelRes: Int,
) {
    ALL(R.string.history_filter_all),
    VIDEO(R.string.history_filter_video),
    AUDIO(R.string.history_filter_audio),
    IMAGES(R.string.history_filter_images),
}

data class HistoryItem(
    val id: String,
    val title: String,
    val site: String,
    val type: HistoryType,
    val sizeMb: Double,
    val date: String,
)

data class PlaylistItem(
    val id: String,
    val title: String,
    val duration: String,
    val sizeMb: Double,
)

@Stable
class DownloadItem(
    val id: String,
    val title: String,
    val site: String,
    val quality: String,
    val sizeMb: Double,
    initialPercent: Float,
    val step: Float,
) {
    var percent by mutableStateOf(initialPercent)
    var paused by mutableStateOf(false)
    var complete by mutableStateOf(false)
}

object MockData {
    val result = MockResult(
        site = "YouTube",
        title = "Sample video title",
        channel = "Downloader Cat",
        duration = "3:42",
        formats = listOf(
            FormatOption("best", 48.2),
            FormatOption("4k", 312.0),
            FormatOption("1080p", 96.4),
            FormatOption("720p", 52.1),
            FormatOption("480p", 28.7),
            FormatOption("360p", 14.2),
            FormatOption("mp3", 4.1),
        ),
    )

    fun downloads(): List<DownloadItem> = listOf(
        DownloadItem(
            id = "d1",
            title = "Big Buck Bunny",
            site = "YouTube",
            quality = "1080p",
            sizeMb = 96.4,
            initialPercent = 34f,
            step = 4f,
        ),
        DownloadItem(
            id = "d2",
            title = "Late Night Mix",
            site = "TikTok",
            quality = "mp3",
            sizeMb = 4.1,
            initialPercent = 68f,
            step = 7f,
        ),
    )

    val queue = listOf(
        QueueItem("q1", "Travel vlog: Tokyo", "YouTube", "720p", 52.1),
        QueueItem("q2", "Cooking reel", "TikTok", "1080p", 18.6),
        QueueItem("q3", "Design inspiration", "Pinterest", "images", 12.4),
    )

    fun history(): List<HistoryItem> = listOf(
        HistoryItem("h1", "Lo-fi beats to relax to", "YouTube", HistoryType.VIDEO, 48.2, "Nov 19, 2026"),
        HistoryItem("h2", "Morning podcast episode 12", "Reddit", HistoryType.AUDIO, 32.7, "Nov 18, 2026"),
        HistoryItem("h3", "Cat photo dump", "Instagram", HistoryType.IMAGES, 8.3, "Nov 17, 2026"),
        HistoryItem("h4", "Cooking reel", "TikTok", HistoryType.VIDEO, 18.6, "Nov 16, 2026"),
        HistoryItem("h5", "Ambient rain loop", "YouTube", HistoryType.AUDIO, 24.1, "Nov 15, 2026"),
        HistoryItem("h6", "Design inspiration board", "Pinterest", HistoryType.IMAGES, 12.4, "Nov 14, 2026"),
    )

    val playlist = listOf(
        PlaylistItem("p1", "Sunset timelapse", "2:14", 42.5),
        PlaylistItem("p2", "City walk at night", "8:31", 96.4),
        PlaylistItem("p3", "Coffee brewing guide", "5:02", 52.1),
        PlaylistItem("p4", "Minimal desk setup", "6:48", 74.8),
        PlaylistItem("p5", "Mountain drone shot", "3:27", 61.2),
        PlaylistItem("p6", "Late night jazz", "12:05", 28.7),
        PlaylistItem("p7", "Pixel art tutorial", "9:16", 88.3),
        PlaylistItem("p8", "Rainy window ambience", "1:00:00", 312.0),
    )
}
