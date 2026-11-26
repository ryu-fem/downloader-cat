package com.ryu.downloadercat.ui.screens

import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.outlined.Link
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ryu.downloadercat.AppViewModel
import com.ryu.downloadercat.R
import com.ryu.downloadercat.Route
import com.ryu.downloadercat.ui.components.AppCard
import com.ryu.downloadercat.ui.components.AppTextField
import com.ryu.downloadercat.ui.components.AutoPill
import com.ryu.downloadercat.ui.components.PasteButton
import com.ryu.downloadercat.ui.components.PrimaryButton
import com.ryu.downloadercat.ui.components.SecondaryButton
import com.ryu.downloadercat.ui.components.StatusDot
import com.ryu.downloadercat.ui.components.Wordmark
import com.ryu.downloadercat.ui.components.localizedMessage
import com.ryu.downloadercat.ui.theme.AppTheme

@Composable
fun HomeScreen(viewModel: AppViewModel) {
    val colors = AppTheme.colors
    val context = LocalContext.current

    val pasteFirst = stringResource(R.string.home_paste_first)
    val httpOnly = stringResource(R.string.home_http_only)
    val clipboardEmpty = stringResource(R.string.home_clipboard_empty)
    val fetchLabel = stringResource(
        if (viewModel.isFetching) R.string.home_fetching else R.string.home_download_video,
    )
    val fetchErrorText = when (val error = viewModel.fetchError) {
        null -> null
        else -> error.localizedMessage()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 18.dp, bottom = 20.dp),
        verticalArrangement = Arrangement.spacedBy(22.dp),
    ) {
        Column {
            Wordmark()
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(R.string.app_subtitle),
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(
                text = stringResource(R.string.home_hero_line1),
                color = colors.onBackground,
                style = MaterialTheme.typography.headlineLarge,
            )
            Text(
                text = stringResource(R.string.home_hero_line2),
                color = colors.muted,
                style = MaterialTheme.typography.headlineLarge,
            )
            Spacer(Modifier.height(2.dp))
            Text(
                text = stringResource(R.string.home_hero_desc),
                color = colors.subtle,
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        AppCard {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = stringResource(R.string.home_direct_url),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                )
                Spacer(Modifier.weight(1f))
                AutoPill(stringResource(R.string.home_auto_detect))
            }
            Spacer(Modifier.height(10.dp))
            AppTextField(
                value = viewModel.url,
                onValueChange = {
                    viewModel.url = it
                    viewModel.homeStatus = null
                    viewModel.fetchError = null
                },
                modifier = Modifier.fillMaxWidth(),
                placeholder = stringResource(R.string.home_placeholder),
                leadingIcon = Icons.Outlined.Link,
                contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
                trailing = {
                    PasteButton(
                        onClick = {
                            val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as? ClipboardManager
                            val text = clipboard
                                ?.primaryClip
                                ?.takeIf { it.itemCount > 0 }
                                ?.getItemAt(0)
                                ?.coerceToText(context)
                                ?.toString()
                                ?.trim()
                                .orEmpty()
                            if (text.isEmpty()) {
                                viewModel.homeStatus = clipboardEmpty
                            } else {
                                viewModel.url = text
                                viewModel.homeStatus = null
                                viewModel.fetchError = null
                            }
                        },
                    )
                },
            )
            Spacer(Modifier.height(10.dp))
            PrimaryButton(
                text = fetchLabel,
                onClick = {
                    val trimmed = viewModel.url.trim()
                    viewModel.fetchError = null
                    when {
                        trimmed.isEmpty() -> viewModel.homeStatus = pasteFirst
                        !(trimmed.startsWith("http://") || trimmed.startsWith("https://")) ->
                            viewModel.homeStatus = httpOnly
                        else -> viewModel.fetchInfo()
                    }
                },
                enabled = !viewModel.isFetching,
                modifier = Modifier.fillMaxWidth(),
            )
            (fetchErrorText ?: viewModel.homeStatus)?.let { status ->
                Spacer(Modifier.height(8.dp))
                Text(
                    text = status,
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FeatureChip(stringResource(R.string.home_feat1))
            FeatureChip(stringResource(R.string.home_feat2))
            FeatureChip(stringResource(R.string.home_feat3))
        }

        SecondaryButton(
            text = stringResource(R.string.home_open_playlist),
            onClick = { viewModel.route = Route.Playlist },
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun FeatureChip(text: String) {
    val colors = AppTheme.colors
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(colors.surface)
            .border(1.dp, colors.borderSubtle, RoundedCornerShape(50))
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        StatusDot()
        Spacer(Modifier.width(6.dp))
        Text(
            text = text,
            color = colors.muted,
            style = MaterialTheme.typography.labelSmall,
        )
    }
}
