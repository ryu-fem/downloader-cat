package com.ryu.downloadercat.data

data class AppSettings(
    val downloadFolder: String = "Download/DownloaderCat",
    val defaultQuality: String = "best",
    val skipPicker: Boolean = false,
    val concurrency: Int = 2,
    val speedLimitMb: Int = 0,
    val proxy: String = "",
    val themeMode: String = "dark",
)
