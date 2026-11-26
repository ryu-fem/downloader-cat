package com.ryu.downloadercat.download

import android.content.Context
import com.ryu.downloadercat.R
import com.ryu.downloadercat.data.MediaFormat
import com.ryu.downloadercat.data.MediaInfo
import com.ryu.downloadercat.engine.EngineError
import com.yausername.youtubedl_android.YoutubeDL
import java.util.concurrent.atomic.AtomicLong
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

object DownloadRepository {
    private val nextId = AtomicLong(1)
    private val _tasks = MutableStateFlow<List<DownloadTask>>(emptyList())
    val tasks: StateFlow<List<DownloadTask>> = _tasks.asStateFlow()

    fun enqueue(
        context: Context,
        media: MediaInfo,
        format: MediaFormat,
        qualityLabel: String,
        subtitles: Boolean,
        embedThumbnail: Boolean,
    ): String {
        val id = "download-${System.currentTimeMillis()}-${nextId.getAndIncrement()}"
        val task = DownloadTask(
            id = id,
            url = media.sourceUrl,
            title = media.title.ifBlank { context.getString(R.string.common_untitled) },
            site = media.site.ifBlank { context.getString(R.string.common_unknown_site) },
            formatId = format.id,
            kind = format.kind,
            ext = format.ext,
            quality = qualityLabel.ifBlank { context.getString(R.string.quality_best) },
            subtitles = subtitles,
            embedThumbnail = embedThumbnail,
            createdAt = System.currentTimeMillis(),
        )
        _tasks.update { current -> current + task }
        try {
            DownloadService.start(context)
        } catch (throwable: Throwable) {
            _tasks.update { current -> current.filterNot { it.id == id } }
            throw throwable
        }
        return id
    }

    fun task(id: String): DownloadTask? = _tasks.value.firstOrNull { it.id == id }

    fun markStarting(id: String) = update(id) { task ->
        if (task.isFinished) {
            task
        } else {
            task.copy(status = DownloadStatus.STARTING, progress = 0f, error = null)
        }
    }

    fun markProgress(id: String, percent: Float, speed: String?, eta: String?) = update(id) { task ->
        when (task.status) {
            DownloadStatus.CANCELED,
            DownloadStatus.COMPLETED,
            DownloadStatus.PROCESSING,
            DownloadStatus.FAILED -> task

            else -> task.copy(
                status = DownloadStatus.DOWNLOADING,
                progress = percent.coerceIn(0f, 100f),
                speed = speed,
                eta = eta,
            )
        }
    }

    fun markProcessing(id: String) = update(id) { task ->
        if (task.isFinished) {
            task
        } else {
            task.copy(
                status = DownloadStatus.PROCESSING,
                progress = 100f,
                speed = null,
                eta = null,
            )
        }
    }

    fun markCompleted(id: String, uri: String?, path: String?) = update(id) { task ->
        if (task.isFinished) {
            task
        } else {
            task.copy(
                status = DownloadStatus.COMPLETED,
                progress = 100f,
                speed = null,
                eta = null,
                error = null,
                outputUri = uri,
                outputPath = path,
            )
        }
    }

    fun markFailed(id: String, error: EngineError) = update(id) { task ->
        if (task.isFinished) {
            task
        } else {
            task.copy(
                status = DownloadStatus.FAILED,
                speed = null,
                eta = null,
                error = error,
            )
        }
    }

    fun markCanceled(id: String) = update(id) { task ->
        if (task.isFinished) {
            task
        } else {
            task.copy(
                status = DownloadStatus.CANCELED,
                speed = null,
                eta = null,
            )
        }
    }

    fun cancel(id: String) {
        val task = task(id) ?: return
        if (task.isFinished) return

        markCanceled(id)
        runCatching { YoutubeDL.getInstance().destroyProcessById(id) }
    }

    fun clearFinished() {
        _tasks.update { tasks -> tasks.filterNot { it.isFinished } }
    }

    private fun update(id: String, transform: (DownloadTask) -> DownloadTask) {
        _tasks.update { tasks ->
            tasks.map { task -> if (task.id == id) transform(task) else task }
        }
    }
}
