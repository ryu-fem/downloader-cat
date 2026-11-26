package com.ryu.downloadercat.data

enum class FormatKind {
    VIDEO,
    VIDEO_ONLY,
    AUDIO,
}

data class MediaFormat(
    val id: String,
    val kind: FormatKind,
    val height: Int?,
    val fps: Int?,
    val ext: String,
    val sizeMb: Double?,
    val note: String?,
)

data class MediaInfo(
    val sourceUrl: String,
    val site: String,
    val title: String,
    val channel: String?,
    val durationSeconds: Int,
    val formats: List<MediaFormat>,
)
