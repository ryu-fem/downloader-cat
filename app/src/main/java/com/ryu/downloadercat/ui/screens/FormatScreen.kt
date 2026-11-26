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
import androidx.compose.material.icons.outlined.PlaylistPlay
import androidx.compose.material3.Icon
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
import com.ryu.downloadercat.data.FormatOption
import com.ryu.downloadercat.data.MockData
import com.ryu.downloadercat.ui.components.AppCard
import com.ryu.downloadercat.ui.components.Badge
import com.ryu.downloadercat.ui.components.OptionRow
import com.ryu.downloadercat.ui.components.PrimaryButton
import com.ryu.downloadercat.ui.components.SecondaryButton
import com.ryu.downloadercat.ui.components.SectionHead
import com.ryu.downloadercat.ui.components.SubHeader
import com.ryu.downloadercat.ui.components.Thumb
import com.ryu.downloadercat.ui.components.groupedShape
import com.ryu.downloadercat.ui.components.qualityLabel
import com.ryu.downloadercat.ui.components.sizeText
import com.ryu.downloadercat.ui.theme.AppTheme

@Composable
fun FormatScreen(viewModel: AppViewModel) {
    val colors = AppTheme.colors
    val notConnected = stringResource(R.string.downloads_not_connected)
    // TODO(phase2): replace with the real engine.
    val result = MockData.result

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

        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Thumb(
                    width = 100.dp,
                    height = 62.dp,
                    icon = Icons.Outlined.Movie,
                    duration = result.duration,
                )
                Spacer(Modifier.width(12.dp))
                Column(Modifier.weight(1f)) {
                    Text(
                        text = result.title,
                        color = colors.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(3.dp))
                    Text(
                        text = result.channel,
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                    Spacer(Modifier.height(5.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Badge(result.site, small = true)
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = sizeText(
                                result.formats.firstOrNull { it.id == viewModel.selectedFormatId }?.sizeMb
                                    ?: result.formats.first().sizeMb,
                            ),
                            color = colors.muted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
        }

        SectionHead(stringResource(R.string.format_section_format))

        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            result.formats.forEachIndexed { index, format ->
                FormatOptionRow(
                    format = format,
                    selected = viewModel.selectedFormatId == format.id,
                    shape = groupedShape(
                        index = index,
                        count = result.formats.size,
                        inner = 10.dp,
                        outer = 20.dp,
                    ),
                    onClick = { viewModel.selectedFormatId = format.id },
                )
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

        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.PlaylistPlay,
                    contentDescription = null,
                    tint = colors.onBackground,
                    modifier = Modifier.size(18.dp),
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = stringResource(R.string.format_playlist_notice),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Spacer(Modifier.height(10.dp))
            PrimaryButton(
                text = stringResource(R.string.format_whole_playlist),
                onClick = { viewModel.route = Route.Playlist },
                modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            SecondaryButton(
                text = stringResource(R.string.format_video_only),
                onClick = { viewModel.downloadStatus = notConnected },
                modifier = Modifier.fillMaxWidth(),
            )
        }

        PrimaryButton(
            text = stringResource(R.string.common_download),
            onClick = { viewModel.downloadStatus = notConnected },
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
}

@Composable
private fun FormatOptionRow(
    format: FormatOption,
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
            text = qualityLabel(format.id),
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
