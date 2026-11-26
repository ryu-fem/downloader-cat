package com.ryu.downloadercat.data

data class PlaylistEntry(
    val id: String,
    val title: String,
    val url: String?,
    val durationSeconds: Int,
    val sizeMb: Double?,
)

data class PlaylistInfo(
    val sourceUrl: String,
    val title: String,
    val site: String,
    val entries: List<PlaylistEntry>,
)
