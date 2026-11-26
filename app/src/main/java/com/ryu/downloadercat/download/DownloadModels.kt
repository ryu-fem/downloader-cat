package com.ryu.downloadercat.download

import com.ryu.downloadercat.data.FormatKind
import com.ryu.downloadercat.engine.EngineError

enum class DownloadStatus {
    QUEUED,
    STARTING,
    DOWNLOADING,
    PROCESSING,
    COMPLETED,
    FAILED,
    CANCELED,
}

data class DownloadTask(
    val id: String,
    val url: String,
    val title: String,
    val site: String,
    val formatId: String,
    val kind: FormatKind,
    val ext: String,
    val quality: String,
    val subtitles: Boolean,
    val embedThumbnail: Boolean,
    val createdAt: Long,
    val status: DownloadStatus = DownloadStatus.QUEUED,
    val progress: Float = 0f,
    val speed: String? = null,
    val eta: String? = null,
    val error: EngineError? = null,
    val outputUri: String? = null,
    val outputPath: String? = null,
) {
    val isActive: Boolean
        get() = status == DownloadStatus.STARTING ||
            status == DownloadStatus.DOWNLOADING ||
            status == DownloadStatus.PROCESSING

    val isFinished: Boolean
        get() = status == DownloadStatus.COMPLETED ||
            status == DownloadStatus.FAILED ||
            status == DownloadStatus.CANCELED
}
