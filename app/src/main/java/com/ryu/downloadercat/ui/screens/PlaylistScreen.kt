package com.ryu.downloadercat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import com.ryu.downloadercat.data.MockData
import com.ryu.downloadercat.data.PlaylistItem
import com.ryu.downloadercat.ui.components.AppChip
import com.ryu.downloadercat.ui.components.CheckboxMark
import com.ryu.downloadercat.ui.components.EmptyState
import com.ryu.downloadercat.ui.components.PrimaryButton
import com.ryu.downloadercat.ui.components.SubHeader
import com.ryu.downloadercat.ui.components.Thumb
import com.ryu.downloadercat.ui.components.groupedShape
import com.ryu.downloadercat.ui.components.sizeText
import com.ryu.downloadercat.ui.theme.AppTheme

@Composable
fun PlaylistScreen(viewModel: AppViewModel) {
    val colors = AppTheme.colors
    val selectedCount = viewModel.selectedPlaylist.size
    val allSelected = MockData.playlist.isNotEmpty() && selectedCount == MockData.playlist.size
    val queuedMessage = stringResource(R.string.playlist_mock_queued, selectedCount)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 14.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        SubHeader(
            title = stringResource(R.string.playlist_title),
            subtitle = stringResource(R.string.playlist_subtitle_mock, MockData.playlist.size),
            onBack = { viewModel.route = Route.Home },
        )

        Row(verticalAlignment = Alignment.CenterVertically) {
            AppChip(
                text = stringResource(
                    if (allSelected) R.string.playlist_clear_all else R.string.playlist_select_all,
                ),
                selected = allSelected,
                onClick = { viewModel.toggleSelectAllPlaylist() },
            )
            Spacer(Modifier.weight(1f))
            Text(
                text = stringResource(
                    R.string.playlist_selected_summary,
                    selectedCount,
                    MockData.playlist.size,
                ),
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (MockData.playlist.isEmpty()) {
            EmptyState(stringResource(R.string.playlist_empty))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                MockData.playlist.forEachIndexed { index, item ->
                    PlaylistRow(
                        item = item,
                        checked = item.id in viewModel.selectedPlaylist,
                        shape = groupedShape(
                            index = index,
                            count = MockData.playlist.size,
                            inner = 6.dp,
                            outer = 20.dp,
                        ),
                        onToggle = { viewModel.togglePlaylistItem(item.id) },
                    )
                }
            }
        }

        viewModel.playlistStatus?.let { status ->
            Text(
                text = status,
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(
                    text = stringResource(R.string.playlist_selected_count, selectedCount),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Spacer(Modifier.height(2.dp))
                Text(
                    text = sizeText(viewModel.selectedPlaylistSizeMb()),
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
            Spacer(Modifier.width(12.dp))
            PrimaryButton(
                text = stringResource(R.string.common_download),
                onClick = { viewModel.playlistStatus = queuedMessage },
                enabled = selectedCount > 0,
                icon = Icons.Outlined.Download,
            )
        }
    }
}

@Composable
private fun PlaylistRow(
    item: PlaylistItem,
    checked: Boolean,
    shape: Shape,
    onToggle: () -> Unit,
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(58.dp)
            .clip(shape)
            .background(if (checked) colors.surfaceRaised else colors.surface)
            .clickable(onClick = onToggle)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckboxMark(checked)
        Spacer(Modifier.width(10.dp))
        Thumb(
            width = 36.dp,
            height = 36.dp,
            icon = Icons.Outlined.Movie,
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
                text = "${item.duration} · ${sizeText(item.sizeMb)}",
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}
