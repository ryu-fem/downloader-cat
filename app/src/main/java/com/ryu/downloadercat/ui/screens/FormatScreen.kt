package com.ryu.downloadercat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ryu.downloadercat.AppViewModel
import com.ryu.downloadercat.R
import com.ryu.downloadercat.Route
import com.ryu.downloadercat.data.MediaFormat
import com.ryu.downloadercat.data.MediaInfo
import com.ryu.downloadercat.ui.components.AppCard
import com.ryu.downloadercat.ui.components.Badge
import com.ryu.downloadercat.ui.components.EmptyState
import com.ryu.downloadercat.ui.components.OptionRow
import com.ryu.downloadercat.ui.components.PrimaryButton
import com.ryu.downloadercat.ui.components.SectionHead
import com.ryu.downloadercat.ui.components.SubHeader
import com.ryu.downloadercat.ui.components.Thumb
import com.ryu.downloadercat.ui.components.durationText
import com.ryu.downloadercat.ui.components.formatLabel
import com.ryu.downloadercat.ui.components.groupedShape
import com.ryu.downloadercat.ui.components.sizeText
import com.ryu.downloadercat.ui.theme.AppTheme

@Composable
fun FormatScreen(viewModel: AppViewModel) {
    val media = viewModel.mediaInfo

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 14.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        SubHeader(
            title = stringResource(R.string.format_title),
            subtitle = stringResource(R.string.format_subtitle),
            onBack = { viewModel.route = Route.Home },
        )

        if (media == null) {
            EmptyState(stringResource(R.string.format_no_media))
        } else {
            FormatContent(viewModel = viewModel, media = media)
        }
    }
}

@Composable
private fun FormatContent(
    viewModel: AppViewModel,
    media: MediaInfo,
) {
    val colors = AppTheme.colors
    val notConnected = stringResource(R.string.downloads_not_connected)
    val selectedFormat = media.formats.firstOrNull { it.id == viewModel.selectedFormatId }
        ?: media.formats.firstOrNull()

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Thumb(
                width = 100.dp,
                height = 62.dp,
                icon = Icons.Outlined.Movie,
                duration = durationText(media.durationSeconds),
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = media.title,
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                media.channel?.let { channel ->
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = channel,
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(5.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (media.site.isNotBlank()) {
                        Badge(media.site, small = true)
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        text = sizeText(selectedFormat?.sizeMb),
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
            }
        }
    }

    SectionHead(stringResource(R.string.format_section_format))

    if (media.formats.isEmpty()) {
        EmptyState(stringResource(R.string.format_no_formats))
    } else {
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            media.formats.forEachIndexed { index, format ->
                FormatOptionRow(
                    format = format,
                    selected = viewModel.selectedFormatId == format.id,
                    shape = groupedShape(
                        index = index,
                        count = media.formats.size,
                        inner = 10.dp,
                        outer = 20.dp,
                    ),
                    onClick = { viewModel.selectedFormatId = format.id },
                )
            }
        }
    }

    SectionHead(stringResource(R.string.format_options))

    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        OptionRow(
            text = stringResource(R.string.format_subtitles),
            checked = viewModel.subtitles,
            onCheckedChange = { viewModel.subtitles = it },
            shape = groupedShape(0, 2, inner = 10.dp, outer = 20.dp),
        )
        OptionRow(
            text = stringResource(R.string.format_embed_thumbnail),
            checked = viewModel.embedThumbnail,
            onCheckedChange = { viewModel.embedThumbnail = it },
            shape = groupedShape(1, 2, inner = 10.dp, outer = 20.dp),
        )
    }

    PrimaryButton(
        text = stringResource(R.string.common_download),
        // TODO(phase3): start the download service.
        onClick = { viewModel.downloadStatus = notConnected },
        enabled = media.formats.isNotEmpty(),
        modifier = Modifier.fillMaxWidth(),
        icon = Icons.Outlined.Download,
    )

    viewModel.downloadStatus?.let { status ->
        Text(
            text = status,
            color = colors.muted,
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

@Composable
private fun FormatOptionRow(
    format: MediaFormat,
    selected: Boolean,
    shape: Shape,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(54.dp)
            .clip(shape)
            .background(if (selected) colors.onBackground else colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = formatLabel(format),
            color = if (selected) colors.background else colors.onBackground,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = sizeText(format.sizeMb),
            color = if (selected) colors.background.copy(alpha = 0.75f) else colors.muted,
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.width(10.dp))
        Box(
            modifier = Modifier
                .size(20.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = if (selected) colors.background else colors.borderStrong,
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            if (selected) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(colors.background),
                )
            }
        }
    }
}
