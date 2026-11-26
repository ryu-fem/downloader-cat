package com.ryu.downloadercat.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Movie
import androidx.compose.material.icons.outlined.MusicNote
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ryu.downloadercat.AppViewModel
import com.ryu.downloadercat.R
import com.ryu.downloadercat.data.DownloadRecord
import com.ryu.downloadercat.ui.components.AppCard
import com.ryu.downloadercat.ui.components.AppIconButton
import com.ryu.downloadercat.ui.components.Badge
import com.ryu.downloadercat.ui.components.EmptyState
import com.ryu.downloadercat.ui.components.ScreenHeader
import com.ryu.downloadercat.ui.components.Thumb
import com.ryu.downloadercat.ui.components.sizeText
import com.ryu.downloadercat.ui.theme.AppTheme
import java.text.DateFormat
import java.util.Date

@Composable
fun HistoryScreen(viewModel: AppViewModel) {
    val items by viewModel.history.collectAsStateWithLifecycle()
    val context = LocalContext.current
    var status by remember { mutableStateOf<String?>(null) }

    fun open(record: DownloadRecord) {
        status = null
        val uri = record.uri
        if (uri.isNullOrBlank()) {
            status = context.getString(R.string.history_no_file)
            return
        }

        try {
            context.startActivity(
                Intent(Intent.ACTION_VIEW).apply {
                    setDataAndType(Uri.parse(uri), record.mimeType ?: "*/*")
                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                },
            )
        } catch (_: Exception) {
            status = context.getString(R.string.history_open_failed, record.title)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 18.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.history_title),
            subtitle = stringResource(R.string.history_subtitle),
        )

        if (items.isEmpty()) {
            EmptyState(stringResource(R.string.history_empty))
        } else {
            items.forEach { record ->
                HistoryCard(
                    record = record,
                    onOpen = { open(record) },
                    onDelete = { viewModel.deleteHistoryItem(record.id) },
                )
            }
        }

        status?.let { message ->
            Text(
                text = message,
                color = AppTheme.colors.muted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 6.dp),
            )
        }
    }
}

@Composable
private fun HistoryCard(
    record: DownloadRecord,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    val colors = AppTheme.colors
    val icon = if (record.kind == "audio") {
        Icons.Outlined.MusicNote
    } else {
        Icons.Outlined.Movie
    }
    val date = remember(record.dateMillis) {
        DateFormat.getDateInstance(DateFormat.MEDIUM).format(Date(record.dateMillis))
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
                    text = record.title,
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    if (record.site.isNotBlank()) {
                        Badge(record.site, small = true)
                        Spacer(Modifier.width(6.dp))
                    }
                    Text(
                        text = record.quality,
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = date,
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                    record.sizeBytes?.let { bytes ->
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = sizeText(bytes / (1024.0 * 1024.0)),
                            color = colors.muted,
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                }
            }
            Row {
                AppIconButton(
                    icon = Icons.Outlined.PlayArrow,
                    contentDescription = stringResource(R.string.aria_open_item, record.title),
                    onClick = onOpen,
                )
                AppIconButton(
                    icon = Icons.Outlined.Delete,
                    contentDescription = stringResource(R.string.aria_delete_item, record.title),
                    onClick = onDelete,
                )
            }
        }
    }
}
