package com.ryu.downloadercat.engine

import com.ryu.downloadercat.data.FormatKind
import com.ryu.downloadercat.data.MediaFormat
import com.ryu.downloadercat.data.MediaInfo
import com.yausername.youtubedl_android.mapper.VideoFormat
import com.yausername.youtubedl_android.mapper.VideoInfo
import java.net.URI
import java.util.Locale

private val audioExtensions = setOf("m4a", "mp3", "opus", "ogg", "wav", "flac", "aac")

fun VideoInfo.toMediaInfo(sourceUrl: String): MediaInfo {
    val mapped = formats.orEmpty().mapIndexedNotNull { index, format ->
        format.toMediaFormat(index)
    }
    val fallback = if (mapped.isEmpty()) url?.let { toFallbackFormat() } else null
    val curated = (mapped + listOfNotNull(fallback))
        .distinctBy { it.id }
        .curate()

    return MediaInfo(
        site = extractorKey ?: extractor ?: hostOf(sourceUrl).orEmpty(),
        title = title ?: fulltitle ?: displayId ?: sourceUrl,
        channel = (uploader ?: uploaderId)?.takeIf { it.isNotBlank() },
        durationSeconds = duration,
        formats = curated,
    )
}

private fun VideoFormat.toMediaFormat(index: Int): MediaFormat? {
    val extValue = ext?.lowercase(Locale.US).orEmpty()
    if (extValue == "mhtml") return null
    if (formatNote?.contains("storyboard", ignoreCase = true) == true) return null
    if (url.isNullOrBlank()) return null

    val hasVideo = !vcodec.isNullOrBlank() && vcodec != "none"
    val hasAudio = !acodec.isNullOrBlank() && acodec != "none"
    val kind = when {
        hasVideo && hasAudio -> FormatKind.VIDEO
        hasVideo -> FormatKind.VIDEO_ONLY
        hasAudio -> FormatKind.AUDIO
        else -> return null
    }

    return MediaFormat(
        id = formatId?.takeIf { it.isNotBlank() } ?: "format-$index",
        kind = kind,
        height = height.takeIf { it > 0 },
        fps = fps.takeIf { it > 0 },
        ext = extValue,
        sizeMb = sizeMb(),
        note = formatNote?.takeIf { it.isNotBlank() },
    )
}

private fun VideoInfo.toFallbackFormat(): MediaFormat {
    val extValue = ext?.lowercase(Locale.US).orEmpty()
    return MediaFormat(
        id = formatId?.takeIf { it.isNotBlank() } ?: "best",
        kind = if (extValue in audioExtensions) FormatKind.AUDIO else FormatKind.VIDEO,
        height = height.takeIf { it > 0 },
        fps = null,
        ext = extValue,
        sizeMb = sizeMb(),
        note = format?.takeIf { it.isNotBlank() },
    )
}

private fun VideoFormat.sizeMb(): Double? = bytesToMb(
    when {
        fileSize > 0 -> fileSize
        fileSizeApproximate > 0 -> fileSizeApproximate
        else -> 0L
    },
)

private fun VideoInfo.sizeMb(): Double? = bytesToMb(
    when {
        fileSize > 0 -> fileSize
        fileSizeApproximate > 0 -> fileSizeApproximate
        else -> 0L
    },
)

private fun bytesToMb(bytes: Long): Double? =
    if (bytes > 0) bytes / (1024.0 * 1024.0) else null

// Keep one best entry per height, then a couple of audio containers.
private fun List<MediaFormat>.curate(): List<MediaFormat> {
    val video = filter { it.kind != FormatKind.AUDIO }
        .groupBy { it.height ?: 0 }
        .map { (_, group) ->
            group.maxWithOrNull(
                compareBy<MediaFormat> { it.kind == FormatKind.VIDEO }
                    .thenBy { it.sizeMb ?: 0.0 },
            ) ?: group.first()
        }
        .sortedByDescending { it.height ?: 0 }
        .take(10)

    val audio = filter { it.kind == FormatKind.AUDIO }
        .sortedByDescending { it.sizeMb ?: 0.0 }
        .distinctBy { it.ext }
        .take(2)

    return video + audio
}

private fun hostOf(url: String): String? =
    runCatching { URI(url).host }.getOrNull()?.removePrefix("www.")
