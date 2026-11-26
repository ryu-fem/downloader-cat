package com.ryu.downloadercat.data

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

object MockData {
    // TODO(phase2): replace with the real engine.
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
}
