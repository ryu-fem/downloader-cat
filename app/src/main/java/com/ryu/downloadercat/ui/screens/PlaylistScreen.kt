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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Link
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
import com.ryu.downloadercat.data.PlaylistEntry
import com.ryu.downloadercat.ui.components.AppCard
import com.ryu.downloadercat.ui.components.AppTextField
import com.ryu.downloadercat.ui.components.CheckboxMark
import com.ryu.downloadercat.ui.components.EmptyState
import com.ryu.downloadercat.ui.components.PrimaryButton
import com.ryu.downloadercat.ui.components.SecondaryButton
import com.ryu.downloadercat.ui.components.SubHeader
import com.ryu.downloadercat.ui.components.Thumb
import com.ryu.downloadercat.ui.components.durationText
import com.ryu.downloadercat.ui.components.groupedShape
import com.ryu.downloadercat.ui.components.localizedMessage
import com.ryu.downloadercat.ui.components.sizeText
import com.ryu.downloadercat.ui.theme.AppTheme

@Composable
fun PlaylistScreen(viewModel: AppViewModel) {
    val colors = AppTheme.colors
    val playlist = viewModel.playlistInfo
    val selectedIds = viewModel.selectedPlaylistIds
    val allIds = playlist?.entries?.map { it.id }?.toSet() ?: emptySet()
    val allSelected = allIds.isNotEmpty() && selectedIds.containsAll(allIds)
    val subtitle = when {
        viewModel.playlistFetching -> stringResource(R.string.playlist_fetching)
        playlist == null -> stringResource(R.string.playlist_subtitle)
        else -> stringResource(R.string.playlist_subtitle_loaded, playlist.entries.size)
    }

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
            subtitle = subtitle,
            onBack = { viewModel.route = Route.Home },
        )

        AppCard {
            AppTextField(
                value = viewModel.playlistUrl,
                onValueChange = {
                    viewModel.playlistUrl = it
                    viewModel.playlistStatus = null
                    viewModel.playlistError = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.playlist_url_placeholder),
                leadingIcon = Icons.Outlined.Link,
            )
            Spacer(Modifier.height(10.dp))
            PrimaryButton(
                text = stringResource(
                    if (viewModel.playlistFetching) {
                        R.string.playlist_fetching
                    } else {
                        R.string.playlist_fetch
                    },
                ),
                onClick = viewModel::fetchPlaylist,
                enabled = !viewModel.playlistFetching,
                modifier = Modifier.fillMaxWidth(),
            )
        }

        (viewModel.playlistError?.localizedMessage() ?: viewModel.playlistStatus)?.let { status ->
            Text(
                text = status,
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 6.dp),
            )
        }

        when {
            playlist == null -> EmptyState(stringResource(R.string.playlist_empty))
            playlist.entries.isEmpty() -> EmptyState(stringResource(R.string.playlist_no_entries))
            else -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(
                            R.string.playlist_selected_summary,
                            selectedIds.size,
                            playlist.entries.size,
                        ),
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.weight(1f),
                    )
                    SecondaryButton(
                        text = stringResource(
                            if (allSelected) R.string.playlist_clear_all else R.string.playlist_select_all,
                        ),
                        onClick = viewModel::toggleSelectAllPlaylist,
                        compact = true,
                    )
                }

                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    playlist.entries.forEachIndexed { index, entry ->
                        PlaylistEntryRow(
                            entry = entry,
                            selected = entry.id in selectedIds,
                            shape = groupedShape(
                                index = index,
                                count = playlist.entries.size,
                                inner = 10.dp,
                                outer = 20.dp,
                            ),
                            onClick = { viewModel.togglePlaylistEntry(entry.id) },
                        )
                    }
                }

                PrimaryButton(
                    text = stringResource(R.string.common_download),
                    onClick = viewModel::downloadSelectedPlaylist,
                    enabled = selectedIds.isNotEmpty(),
                    modifier = Modifier.fillMaxWidth(),
                    icon = Icons.Outlined.Download,
                )
            }
        }
    }
}

@Composable
private fun PlaylistEntryRow(
    entry: PlaylistEntry,
    selected: Boolean,
    shape: Shape,
    onClick: () -> Unit,
) {
    val colors = AppTheme.colors
    val duration = if (entry.durationSeconds > 0) durationText(entry.durationSeconds) else null
    val size = if (entry.sizeMb != null) sizeText(entry.sizeMb) else null
    val meta = listOfNotNull(duration, size).joinToString(" · ")

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 64.dp)
            .clip(shape)
            .background(colors.surfaceRaised)
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        CheckboxMark(selected)
        Spacer(Modifier.width(12.dp))
        Thumb(
            width = 54.dp,
            height = 54.dp,
            icon = Icons.Outlined.Movie,
        )
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(
                text = entry.title,
                color = colors.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            if (meta.isNotBlank()) {
                Spacer(Modifier.height(4.dp))
                Text(
                    text = meta,
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}
