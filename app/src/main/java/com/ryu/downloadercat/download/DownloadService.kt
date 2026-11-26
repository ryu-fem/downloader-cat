package com.ryu.downloadercat.download

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.app.ServiceCompat
import androidx.core.content.ContextCompat
import com.ryu.downloadercat.MainActivity
import com.ryu.downloadercat.R
import com.ryu.downloadercat.data.AppSettings
import com.ryu.downloadercat.data.DownloadRecord
import com.ryu.downloadercat.data.FormatKind
import com.ryu.downloadercat.data.HistoryRepository
import com.ryu.downloadercat.data.SettingsRepository
import com.ryu.downloadercat.engine.DownloaderEngine
import com.ryu.downloadercat.engine.EngineError
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLRequest
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class DownloadService : Service() {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private val mutex = Mutex()
    private val activeJobs = mutableMapOf<String, Job>()
    private val settingsRepository by lazy { SettingsRepository(applicationContext) }
    private val historyRepository by lazy { HistoryRepository(applicationContext) }
    private var notificationJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        ensureForeground()

        if (notificationJob == null) {
            notificationJob = scope.launch {
                while (isActive) {
                    updateNotification()
                    delay(1_000)
                }
            }
        }

        scope.launch { pump() }
        return START_NOT_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        DownloadRepository.tasks.value
            .filter { it.isActive }
            .forEach { task ->
                runCatching { YoutubeDL.getInstance().destroyProcessById(task.id) }
                DownloadRepository.markCanceled(task.id)
            }
        notificationJob?.cancel()
        scope.cancel()
        stopForeground(STOP_FOREGROUND_REMOVE)
        super.onDestroy()
    }

    private suspend fun pump() {
        mutex.withLock {
            val settings = try {
                settingsRepository.settings.first()
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Throwable) {
                AppSettings()
            }

            val tasks = DownloadRepository.tasks.value
            val slots = (settings.concurrency - activeJobs.size).coerceAtLeast(0)
            tasks
                .filter { it.status == DownloadStatus.QUEUED }
                .take(slots)
                .forEach { task ->
                    val job = scope.launch(start = kotlinx.coroutines.CoroutineStart.LAZY) {
                        runTask(task, settings)
                    }
                    activeJobs[task.id] = job
                    job.invokeOnCompletion {
                        scope.launch {
                            mutex.withLock { activeJobs.remove(task.id) }
                            pump()
                        }
                    }
                    job.start()
                }

            updateNotification()

            if (activeJobs.isEmpty() && DownloadRepository.tasks.value.none {
                    it.isActive || it.status == DownloadStatus.QUEUED
                }
            ) {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
            }
        }
    }

    private suspend fun runTask(task: DownloadTask, settings: AppSettings) {
        if (DownloadRepository.task(task.id)?.status == DownloadStatus.CANCELED) return

        DownloadRepository.markStarting(task.id)
        val context = applicationContext
        val taskDir = File(context.cacheDir, "downloads/${task.id}")
        taskDir.mkdirs()

        try {
            DownloaderEngine.initialize(context)
            if (DownloadRepository.task(task.id)?.status == DownloadStatus.CANCELED) return

            val request = buildRequest(task, settings, taskDir)
            val finalPath = AtomicReference<String?>(null)

            val response = YoutubeDL.getInstance().execute(request, task.id) { _, _, line ->
                val clean = line.trim()
                val progressIndex = clean.indexOf(PROGRESS_MARKER)
                val pathIndex = clean.indexOf(PATH_MARKER)

                when {
                    progressIndex >= 0 -> {
                        val progress = parseProgress(
                            clean.substring(progressIndex + PROGRESS_MARKER.length),
                        )
                        progress.percent?.let { percent ->
                            DownloadRepository.markProgress(
                                id = task.id,
                                percent = percent,
                                speed = progress.speed,
                                eta = progress.eta,
                            )
                            if (percent >= 100f) {
                                DownloadRepository.markProcessing(task.id)
                            }
                        }
                    }

                    pathIndex >= 0 -> {
                        val path = clean.substring(pathIndex + PATH_MARKER.length).trim()
                        if (path.isNotBlank() && path != "NA") {
                            finalPath.set(path)
                        }
                    }
                }
            }

            if (DownloadRepository.task(task.id)?.status == DownloadStatus.CANCELED) return

            val path = finalPath.get()?.takeIf { it.isNotBlank() } ?: parsePath(response.out)
            val source = path
                ?.let(::File)
                ?.takeIf {
                    it.isFile &&
                        !it.name.endsWith(".part") &&
                        !it.name.endsWith(".ytdl")
                }
                ?: taskDir.listFiles()
                    ?.filter { it.isFile && it.isMediaFile() }
                    ?.maxByOrNull { it.lastModified() }
                ?: throw IOException("No output file was produced")

            DownloadRepository.markProcessing(task.id)

            val published = try {
                MediaStorePublisher.publish(context, source, settings.downloadFolder)
            } catch (exception: SecurityException) {
                throw exception
            } catch (exception: StorageException) {
                throw exception
            } catch (exception: IOException) {
                throw StorageException(exception)
            }

            if (DownloadRepository.task(task.id)?.status == DownloadStatus.CANCELED) return

            val sizeBytes = source.length().takeIf { it > 0 }
            DownloadRepository.markCompleted(
                id = task.id,
                uri = published.uri.toString(),
                path = published.path,
            )

            try {
                historyRepository.add(
                    DownloadRecord(
                        id = task.id,
                        title = task.title,
                        site = task.site,
                        kind = if (task.kind == FormatKind.AUDIO) "audio" else "video",
                        quality = task.quality,
                        sizeBytes = sizeBytes,
                        dateMillis = System.currentTimeMillis(),
                        uri = published.uri.toString(),
                        filePath = published.path,
                        mimeType = published.mimeType,
                    ),
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (_: Throwable) {
                // The download succeeded; a history write failure must not undo it.
            }
        } catch (exception: CancellationException) {
            throw exception
        } catch (exception: YoutubeDL.CanceledException) {
            DownloadRepository.markCanceled(task.id)
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            DownloadRepository.markCanceled(task.id)
        } catch (exception: SecurityException) {
            DownloadRepository.markFailed(task.id, EngineError.PERMISSION)
        } catch (exception: StorageException) {
            DownloadRepository.markFailed(task.id, EngineError.STORAGE)
        } catch (throwable: Throwable) {
            DownloadRepository.markFailed(task.id, DownloaderEngine.mapError(throwable))
        } finally {
            taskDir.deleteRecursively()
        }
    }

    private fun buildRequest(
        task: DownloadTask,
        settings: AppSettings,
        taskDir: File,
    ): YoutubeDLRequest {
        val request = YoutubeDLRequest(task.url)
            .addOption("--newline")
            .addOption("--progress")
            .addOption("--no-warnings")
            .addOption("--no-playlist")
            .addOption("--no-simulate")
            .addOption("--windows-filenames")
            .addOption("--socket-timeout", "15")
            .addOption("--progress-template", PROGRESS_TEMPLATE)
            .addOption("--print", PATH_TEMPLATE)
            .addOption("-o", File(taskDir, "%(title).150B [%(id)s].%(ext)s").absolutePath)

        if (settings.speedLimitMb > 0) {
            request.addOption("--limit-rate", "${settings.speedLimitMb * 1024}K")
        }
        if (settings.proxy.isNotBlank()) {
            request.addOption("--proxy", settings.proxy)
        }

        if (task.kind == FormatKind.AUDIO) {
            request.addOption("-f", audioSelector(task))
            request.addOption("-x")
            request.addOption("--audio-format", audioFormat(task.ext))
            request.addOption("--audio-quality", "192K")
        } else {
            request.addOption("-f", videoSelector(task))
            request.addOption("--merge-output-format", "mp4")
        }

        if (task.subtitles) {
            request.addOption("--write-subs")
            request.addOption("--sub-langs", "all")
            request.addOption("--embed-subs")
        }
        if (task.embedThumbnail) {
            request.addOption("--embed-thumbnail")
        }

        request.addOption("--")
        return request
    }

    private fun videoSelector(task: DownloadTask): String {
        val id = task.formatId.takeIf { it.isNotBlank() && !it.startsWith("format-") } ?: "best"
        if (id == "best" || id.startsWith("best")) return "best"
        return if (task.kind == FormatKind.VIDEO_ONLY) {
            "$id+bestaudio/best"
        } else {
            id
        }
    }

    private fun audioSelector(task: DownloadTask): String =
        task.formatId.takeIf { it.isNotBlank() && !it.startsWith("format-") } ?: "bestaudio/best"

    private fun audioFormat(extension: String): String = when (extension.lowercase(Locale.US)) {
        "aac" -> "aac"
        "flac" -> "flac"
        "m4a" -> "m4a"
        "mp3" -> "mp3"
        "ogg", "opus", "webm" -> "opus"
        "wav" -> "wav"
        else -> "mp3"
    }

    private fun parseProgress(line: String): ParsedProgress {
        val parts = line.split('|')
        val percent = parts.getOrNull(0)
            ?.trim()
            ?.removeSuffix("%")
            ?.toFloatOrNull()
            ?.coerceIn(0f, 100f)
            ?: run {
                val downloaded = parts.getOrNull(3)?.trim()?.toDoubleOrNull()
                val total = parts.getOrNull(4)?.trim()?.toDoubleOrNull()
                    ?: parts.getOrNull(5)?.trim()?.toDoubleOrNull()
                if (downloaded != null && total != null && total > 0.0) {
                    ((downloaded / total) * 100.0).toFloat().coerceIn(0f, 100f)
                } else {
                    null
                }
            }

        return ParsedProgress(
            percent = percent,
            speed = cleanValue(parts.getOrNull(1)),
            eta = cleanValue(parts.getOrNull(2)),
        )
    }

    private fun cleanValue(value: String?): String? {
        val cleaned = value?.trim()
        return cleaned?.takeIf {
            it.isNotEmpty() &&
                it != "NA" &&
                it != "N/A" &&
                !it.equals("unknown", ignoreCase = true)
        }
    }

    private fun parsePath(output: String): String? = output
        .lineSequence()
        .mapNotNull { line ->
            val marker = line.indexOf(PATH_MARKER)
            if (marker >= 0) line.substring(marker + PATH_MARKER.length).trim() else null
        }
        .firstOrNull { it.isNotBlank() && it != "NA" }

    private fun File.isMediaFile(): Boolean {
        val lower = name.lowercase(Locale.US)
        if (lower.endsWith(".part") || lower.endsWith(".ytdl")) return false
        return extension.lowercase(Locale.US) in MEDIA_EXTENSIONS
    }

    private fun ensureForeground() {
        val notification = buildNotification()
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            ServiceInfo.FOREGROUND_SERVICE_TYPE_DATA_SYNC
        } else {
            0
        }
        ServiceCompat.startForeground(this, NOTIFICATION_ID, notification, type)
    }

    private fun updateNotification() {
        val notification = buildNotification()
        try {
            NotificationManagerCompat.from(this).notify(NOTIFICATION_ID, notification)
        } catch (_: SecurityException) {
            // Android 13+ can hide the notification if POST_NOTIFICATIONS was denied.
        }
    }

    private fun buildNotification(): Notification {
        val tasks = DownloadRepository.tasks.value
        val active = tasks.filter { it.isActive }
        val percent = active
            .map { it.progress }
            .average()
            .takeIf { active.isNotEmpty() }
            ?.toInt()
            ?.coerceIn(0, 100)
            ?: 0

        val title = when {
            active.isNotEmpty() -> getString(R.string.notification_downloading, active.size)
            tasks.any { it.status == DownloadStatus.QUEUED } -> getString(R.string.notification_preparing)
            tasks.isNotEmpty() -> getString(R.string.notification_finished)
            else -> getString(R.string.notification_preparing)
        }
        val text = active.firstOrNull()?.title ?: getString(R.string.app_name)

        val contentIntent = PendingIntent.getActivity(
            this,
            0,
            Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
            },
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )

        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(title)
            .setContentText(text)
            .setContentIntent(contentIntent)
            .setProgress(100, percent, active.isEmpty())
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_PROGRESS)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            description = getString(R.string.notification_channel_description)
            setShowBadge(false)
        }

        getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    private data class ParsedProgress(
        val percent: Float?,
        val speed: String?,
        val eta: String?,
    )

    companion object {
        private const val CHANNEL_ID = "downloads"
        private const val NOTIFICATION_ID = 1001
        private const val PROGRESS_MARKER = "DCPROG|"
        private const val PATH_MARKER = "DCPATH|"
        private val MEDIA_EXTENSIONS = setOf(
            "mp4", "webm", "mkv", "mov", "avi",
            "m4a", "mp3", "opus", "ogg", "flac", "wav", "aac",
            "jpg", "jpeg", "png", "webp", "gif",
        )
        private const val PROGRESS_TEMPLATE =
            "DCPROG|%(progress._percent_str)s|%(progress._speed_str)s|" +
                "%(progress._eta_str)s|%(progress.downloaded_bytes)s|" +
                "%(progress.total_bytes)s|%(progress.total_bytes_estimate)s"
        private const val PATH_TEMPLATE = "after_move:DCPATH|%(filepath)s"

        fun start(context: Context) {
            ContextCompat.startForegroundService(
                context,
                Intent(context, DownloadService::class.java),
            )
        }
    }
}
