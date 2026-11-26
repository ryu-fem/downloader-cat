package com.ryu.downloadercat.ui.components

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.ryu.downloadercat.R
import com.ryu.downloadercat.data.FormatKind
import com.ryu.downloadercat.data.MediaFormat
import java.util.Locale

fun formatSizeMb(value: Double): String =
    if (value % 1.0 == 0.0) {
        String.format(Locale.US, "%.0f", value)
    } else {
        String.format(Locale.US, "%.1f", value)
    }

@Composable
fun sizeText(sizeMb: Double?): String =
    if (sizeMb == null) {
        stringResource(R.string.size_unknown)
    } else {
        stringResource(R.string.size_mb, formatSizeMb(sizeMb))
    }

@Composable
fun durationText(seconds: Int): String {
    if (seconds <= 0) return stringResource(R.string.duration_unknown)

    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    val remainingSeconds = seconds % 60
    return if (hours > 0) {
        String.format(Locale.US, "%d:%02d:%02d", hours, minutes, remainingSeconds)
    } else {
        String.format(Locale.US, "%d:%02d", minutes, remainingSeconds)
    }
}

@Composable
fun formatLabel(format: MediaFormat): String {
    val ext = format.ext.uppercase(Locale.US)
    val height = format.height
    val note = format.note
    val base = when {
        format.kind == FormatKind.AUDIO -> if (ext.isBlank()) {
            stringResource(R.string.format_audio_label)
        } else {
            "${stringResource(R.string.format_audio_label)} · $ext"
        }
        height != null -> buildString {
            append(height)
            append('p')
            if ((format.fps ?: 0) > 30) append(format.fps)
        }
        !note.isNullOrBlank() -> note
        ext.isNotBlank() -> ext
        else -> stringResource(R.string.size_unknown)
    }

    return if (format.kind == FormatKind.VIDEO_ONLY) {
        "$base · ${stringResource(R.string.format_video_only_label)}"
    } else {
        base
    }
}

fun qualityLabelRes(id: String): Int? = when (id.lowercase(Locale.US)) {
    "best" -> R.string.quality_best
    "4k" -> R.string.quality_4k
    "1440p" -> R.string.quality_1440p
    "1080p" -> R.string.quality_1080p
    "720p" -> R.string.quality_720p
    "480p" -> R.string.quality_480p
    "360p" -> R.string.quality_360p
    "mp3" -> R.string.quality_mp3
    "images" -> R.string.quality_images
    else -> null
}

fun groupedShape(
    index: Int,
    count: Int,
    inner: Dp = 8.dp,
    outer: Dp = 22.dp,
): RoundedCornerShape = when {
    count <= 1 -> RoundedCornerShape(outer)
    index == 0 -> RoundedCornerShape(
        topStart = outer,
        topEnd = outer,
        bottomStart = inner,
        bottomEnd = inner,
    )
    index == count - 1 -> RoundedCornerShape(
        topStart = inner,
        topEnd = inner,
        bottomStart = outer,
        bottomEnd = outer,
    )
    else -> RoundedCornerShape(inner)
}

@Composable
fun Modifier.mirrorForRtl(): Modifier {
    val rtl = LocalLayoutDirection.current == LayoutDirection.Rtl
    return if (rtl) graphicsLayer(scaleX = -1f) else this
}
