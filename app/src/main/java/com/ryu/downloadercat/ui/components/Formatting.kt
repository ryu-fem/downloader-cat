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

fun qualityLabelRes(id: String): Int? = when (id.lowercase()) {
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

@Composable
fun qualityLabel(id: String): String =
    qualityLabelRes(id)?.let { stringResource(it) } ?: id

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
