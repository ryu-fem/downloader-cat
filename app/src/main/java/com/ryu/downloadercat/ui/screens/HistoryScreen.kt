package com.ryu.downloadercat.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
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
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Search
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
import com.ryu.downloadercat.data.HistoryFilter
import com.ryu.downloadercat.data.HistoryItem
import com.ryu.downloadercat.ui.components.AppChip
import com.ryu.downloadercat.ui.components.AppIconButton
import com.ryu.downloadercat.ui.components.AppTextField
import com.ryu.downloadercat.ui.components.EmptyState
import com.ryu.downloadercat.ui.components.ScreenHeader
import com.ryu.downloadercat.ui.components.Thumb
import com.ryu.downloadercat.ui.components.groupedShape
import com.ryu.downloadercat.ui.components.sizeText
import com.ryu.downloadercat.ui.theme.AppTheme

@Composable
fun HistoryScreen(viewModel: AppViewModel) {
    val colors = AppTheme.colors
    val items = viewModel.filteredHistory()

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

        AppTextField(
            value = viewModel.historyQuery,
            onValueChange = { viewModel.historyQuery = it },
            modifier = Modifier.fillMaxWidth(),
            placeholder = stringResource(R.string.history_search_placeholder),
            leadingIcon = Icons.Outlined.Search,
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            HistoryFilter.entries.forEach { filter ->
                AppChip(
                    text = stringResource(filter.labelRes),
                    selected = viewModel.historyFilter == filter,
                    onClick = { viewModel.historyFilter = filter },
                )
            }
        }

        viewModel.historyStatus?.let { status ->
            Text(
                text = status,
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (items.isEmpty()) {
            EmptyState(stringResource(R.string.history_empty))
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                items.forEachIndexed { index, item ->
                    val mockAction = stringResource(
                        R.string.history_mock_action,
                        stringResource(item.type.labelRes),
                        item.title,
                    )
                    HistoryRow(
                        item = item,
                        shape = groupedShape(
                            index = index,
                            count = items.size,
                            inner = 6.dp,
                            outer = 20.dp,
                        ),
                        onOpen = { viewModel.historyStatus = mockAction },
                        onDelete = { viewModel.deleteHistory(item.id) },
                    )
                }
            }
        }
    }
}

@Composable
private fun HistoryRow(
    item: HistoryItem,
    shape: Shape,
    onOpen: () -> Unit,
    onDelete: () -> Unit,
) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(64.dp)
            .clip(shape)
            .background(colors.surface)
            .clickable(onClick = onOpen)
            .padding(horizontal = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Thumb(
            width = 40.dp,
            height = 40.dp,
            icon = item.type.icon,
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
                text = "${item.site} · ${sizeText(item.sizeMb)} · ${item.date}",
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Spacer(Modifier.width(6.dp))
        AppIconButton(
            icon = Icons.Outlined.Delete,
            contentDescription = stringResource(R.string.aria_delete_item, item.title),
            onClick = onDelete,
        )
    }
}
