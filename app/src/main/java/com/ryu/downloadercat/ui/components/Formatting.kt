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
import com.ryu.downloadercat.data.DownloadItem
import java.util.Locale

fun formatSizeMb(value: Double): String =
    if (value % 1.0 == 0.0) {
        String.format(Locale.US, "%.0f", value)
    } else {
        String.format(Locale.US, "%.1f", value)
    }

@Composable
fun sizeText(sizeMb: Double): String =
    stringResource(R.string.size_mb, formatSizeMb(sizeMb))

@Composable
fun qualityLabel(id: String): String = when (id.lowercase()) {
    "best" -> stringResource(R.string.quality_best)
    "4k" -> stringResource(R.string.quality_4k)
    "1440p" -> stringResource(R.string.quality_1440p)
    "1080p" -> stringResource(R.string.quality_1080p)
    "720p" -> stringResource(R.string.quality_720p)
    "480p" -> stringResource(R.string.quality_480p)
    "360p" -> stringResource(R.string.quality_360p)
    "mp3" -> stringResource(R.string.quality_mp3)
    "images" -> stringResource(R.string.quality_images)
    else -> id
}

@Composable
fun downloadSpeedText(item: DownloadItem): String = when {
    item.complete -> stringResource(R.string.downloads_status_done)
    item.paused -> stringResource(R.string.downloads_status_paused)
    else -> {
        val speed = item.sizeMb * item.step / 100.0 / 0.9
        stringResource(R.string.speed_mb_s, String.format(Locale.US, "%.1f", speed))
    }
}

@Composable
fun downloadEtaText(item: DownloadItem): String {
    if (item.complete) return ""
    if (item.paused) return "—"

    val speed = item.sizeMb * item.step / 100.0 / 0.9
    val remaining = item.sizeMb * (100.0 - item.percent) / 100.0
    val seconds = if (speed > 0.0) (remaining / speed).toInt() else 0
    val minutes = seconds / 60
    val secs = seconds % 60
    return stringResource(
        R.string.downloads_eta_left,
        String.format(Locale.US, "%d:%02d", minutes, secs),
    )
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
