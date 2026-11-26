package com.ryu.downloadercat.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryu.downloadercat.AppViewModel
import com.ryu.downloadercat.R
import com.ryu.downloadercat.data.FormatKind
import com.ryu.downloadercat.download.DownloadStatus
import com.ryu.downloadercat.download.DownloadTask
import com.ryu.downloadercat.ui.components.AppCard
import com.ryu.downloadercat.ui.components.AppIconButton
import com.ryu.downloadercat.ui.components.AppProgressBar
import com.ryu.downloadercat.ui.components.Badge
import com.ryu.downloadercat.ui.components.EmptyState
import com.ryu.downloadercat.ui.components.ScreenHeader
import com.ryu.downloadercat.ui.components.SecondaryButton
import com.ryu.downloadercat.ui.components.Thumb
import com.ryu.downloadercat.ui.components.localizedMessage
import com.ryu.downloadercat.ui.theme.AppTheme

@Composable
fun DownloadsScreen(viewModel: AppViewModel) {
    val tasks by viewModel.downloadTasks.collectAsStateWithLifecycle()
    val hasFinished = tasks.any { it.isFinished }

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
                if (hasFinished) {
                    SecondaryButton(
                        text = stringResource(R.string.downloads_clear_finished),
                        onClick = viewModel::clearFinishedDownloads,
                        compact = true,
                    )
                }
            },
        )

        if (tasks.isEmpty()) {
            EmptyState(stringResource(R.string.downloads_none_active))
        } else {
            tasks.forEach { task ->
                DownloadCard(
                    task = task,
                    onCancel = { viewModel.cancelDownload(task.id) },
                )
            }
        }
    }
}

@Composable
private fun DownloadCard(
    task: DownloadTask,
    onCancel: () -> Unit,
) {
    val colors = AppTheme.colors
    val icon = if (task.kind == FormatKind.AUDIO) {
        Icons.Outlined.MusicNote
    } else {
        Icons.Outlined.Movie
    }

    AppCard {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Thumb(
                width = 54.dp,
                height = 54.dp,
                icon = icon,
            )
            Spacer(Modifier.width(12.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (task.site.isNotBlank()) {
                        Badge(task.site, small = true)
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        text = task.quality,
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
            if (task.status == DownloadStatus.QUEUED || task.isActive) {
                Spacer(Modifier.width(6.dp))
                AppIconButton(
                    icon = Icons.Outlined.Close,
                    contentDescription = stringResource(R.string.aria_cancel_download),
                    onClick = onCancel,
                )
            }
        }

        Spacer(Modifier.height(14.dp))
        AppProgressBar(
            percent = task.progress,
            paused = task.status == DownloadStatus.QUEUED,
            complete = task.status == DownloadStatus.COMPLETED,
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))

        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = statusText(task.status),
                color = colors.onBackground,
                style = MaterialTheme.typography.bodySmall,
                fontWeight = FontWeight.SemiBold,
            )
            Spacer(Modifier.weight(1f))
            if (task.status == DownloadStatus.DOWNLOADING ||
                task.status == DownloadStatus.PROCESSING ||
                task.status == DownloadStatus.COMPLETED
            ) {
                Text(
                    text = stringResource(
                        R.string.downloads_percent,
                        task.progress.toInt().coerceIn(0, 100),
                    ),
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        val speed = task.speed
        val eta = task.eta
        if (speed != null || eta != null) {
            Spacer(Modifier.height(4.dp))
            val detail = when {
                speed != null && eta != null -> stringResource(
                    R.string.downloads_progress_detail,
                    speed,
                    stringResource(R.string.downloads_eta, eta),
                )
                speed != null -> speed
                eta != null -> stringResource(R.string.downloads_eta, eta)
                else -> ""
            }
            Text(
                text = detail,
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }

        task.error?.let { error ->
            Spacer(Modifier.height(6.dp))
            Text(
                text = error.localizedMessage(),
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

@Composable
private fun statusText(status: DownloadStatus): String = when (status) {
    DownloadStatus.QUEUED -> stringResource(R.string.downloads_status_queued)
    DownloadStatus.STARTING -> stringResource(R.string.downloads_status_starting)
    DownloadStatus.DOWNLOADING -> stringResource(R.string.downloads_status_downloading)
    DownloadStatus.PROCESSING -> stringResource(R.string.downloads_status_processing)
    DownloadStatus.COMPLETED -> stringResource(R.string.downloads_status_completed)
    DownloadStatus.FAILED -> stringResource(R.string.downloads_status_failed)
    DownloadStatus.CANCELED -> stringResource(R.string.downloads_status_canceled)
}
