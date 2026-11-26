package com.ryu.downloadercat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.Pause
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ryu.downloadercat.AppViewModel
import com.ryu.downloadercat.R
import com.ryu.downloadercat.data.DownloadItem
import com.ryu.downloadercat.data.MockData
import com.ryu.downloadercat.data.QueueItem
import com.ryu.downloadercat.ui.components.AppIconButton
import com.ryu.downloadercat.ui.components.AppProgressBar
import com.ryu.downloadercat.ui.components.Badge
import com.ryu.downloadercat.ui.components.EmptyState
import com.ryu.downloadercat.ui.components.ScreenHeader
import com.ryu.downloadercat.ui.components.SectionHead
import com.ryu.downloadercat.ui.components.StatusDot
import com.ryu.downloadercat.ui.components.Thumb
import com.ryu.downloadercat.ui.components.downloadEtaText
import com.ryu.downloadercat.ui.components.downloadSpeedText
import com.ryu.downloadercat.ui.components.qualityLabel
import com.ryu.downloadercat.ui.components.sizeText
import com.ryu.downloadercat.ui.theme.AppTheme

@Composable
fun DownloadsScreen(viewModel: AppViewModel) {
    val colors = AppTheme.colors
    val activeCount = viewModel.downloads.count { !it.complete }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 18.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.downloads_title),
            subtitle = stringResource(R.string.downloads_subtitle),
            trailing = {
                Badge(
                    text = stringResource(R.string.downloads_active_count, activeCount),
                    small = true,
                )
            },
        )

        if (viewModel.downloads.isEmpty()) {
            EmptyState(stringResource(R.string.downloads_none_active))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                viewModel.downloads.forEach { item ->
                    DownloadCard(
                        item = item,
                        onTogglePause = { item.paused = !item.paused },
                        onCancel = { viewModel.cancelDownload(item.id) },
                    )
                }
            }
        }

        SectionHead(
            title = stringResource(R.string.downloads_queue),
            trailing = {
                Badge(
                    text = stringResource(R.string.downloads_waiting_count, MockData.queue.size),
                    small = true,
                )
            },
        )

        if (MockData.queue.isEmpty()) {
            EmptyState(stringResource(R.string.downloads_queue_empty))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                MockData.queue.forEach { item ->
                    QueueRow(item)
                }
            }
        }
    }
}

@Composable
private fun DownloadCard(
    item: DownloadItem,
    onTogglePause: () -> Unit,
    onCancel: () -> Unit,
) {
    val colors = AppTheme.colors
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(20.dp))
            .background(colors.surface)
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(20.dp))
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(9.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Thumb(
                width = 46.dp,
                height = 46.dp,
                icon = if (item.quality == "mp3") Icons.Outlined.MusicNote else Icons.Outlined.Movie,
            )
            Spacer(Modifier.width(10.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = item.title,
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = "${item.site} · ${qualityLabel(item.quality)} · ${sizeText(item.sizeMb)}",
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (item.complete) {
                StatusDot()
            } else {
                AppIconButton(
                    icon = if (item.paused) Icons.Outlined.PlayArrow else Icons.Outlined.Pause,
                    contentDescription = stringResource(
                        if (item.paused) R.string.aria_resume_download else R.string.aria_pause_download,
                    ),
                    onClick = onTogglePause,
                )
            }
            Spacer(Modifier.width(4.dp))
            AppIconButton(
                icon = Icons.Outlined.Close,
                contentDescription = stringResource(R.string.aria_cancel_download),
                onClick = onCancel,
            )
        }

        AppProgressBar(
            percent = item.percent,
            paused = item.paused,
            complete = item.complete,
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = downloadSpeedText(item),
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = if (item.complete) sizeText(item.sizeMb) else "${item.percent.toInt()}%",
                color = colors.onBackground,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
        }

        if (!item.complete && !item.paused) {
            Text(
                text = downloadEtaText(item),
                color = colors.subtle,
                style = MaterialTheme.typography.labelSmall,
            )
        }
    }
}

@Composable
private fun QueueRow(item: QueueItem) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(colors.surface)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Thumb(
            width = 40.dp,
            height = 40.dp,
            icon = Icons.Outlined.Schedule,
        )
        Spacer(Modifier.width(10.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = item.title,
                color = colors.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = "${item.site} · ${qualityLabel(item.quality)} · ${sizeText(item.sizeMb)}",
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(8.dp))
        Text(
            text = stringResource(R.string.downloads_queued),
            color = colors.subtle,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
