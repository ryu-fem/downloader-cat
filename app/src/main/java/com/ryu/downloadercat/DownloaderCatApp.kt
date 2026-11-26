package com.ryu.downloadercat

import androidx.activity.compose.BackHandler
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ryu.downloadercat.ui.components.BottomBar
import com.ryu.downloadercat.ui.screens.DownloadsScreen
import com.ryu.downloadercat.ui.screens.FormatScreen
import com.ryu.downloadercat.ui.screens.HistoryScreen
import com.ryu.downloadercat.ui.screens.HomeScreen
import com.ryu.downloadercat.ui.screens.PlaylistScreen
import com.ryu.downloadercat.ui.screens.SettingsScreen
import com.ryu.downloadercat.ui.screens.ShareSheet
import com.ryu.downloadercat.ui.theme.AppTheme
import com.ryu.downloadercat.ui.theme.DownloaderCatTheme

@Composable
fun DownloaderCatApp(viewModel: AppViewModel = viewModel()) {
    DownloaderCatTheme(themeMode = viewModel.themeMode) {
        val colors = AppTheme.colors

        BackHandler(enabled = viewModel.route != Route.Home) {
            viewModel.route = Route.Home
        }

        Box(Modifier.fillMaxSize()) {
            Surface(
                modifier = Modifier.fillMaxSize(),
                color = colors.background,
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .windowInsetsPadding(WindowInsets.statusBars),
                ) {
                    Box(Modifier.weight(1f)) {
                        Crossfade(
                            targetState = viewModel.route,
                            modifier = Modifier.fillMaxSize(),
                            animationSpec = tween(220),
                            label = "screen",
                        ) { route ->
                            when (route) {
                                Route.Home -> HomeScreen(viewModel)
                                Route.Format -> FormatScreen(viewModel)
                                Route.Downloads -> DownloadsScreen(viewModel)
                                Route.History -> HistoryScreen(viewModel)
                                Route.Playlist -> PlaylistScreen(viewModel)
                                Route.Settings -> SettingsScreen(viewModel)
                            }
                        }
                    }
                    BottomBar(
                        selectedTab = viewModel.route.tab,
                        onSelect = viewModel::navigateToTab,
                    )
                }
            }

            if (viewModel.shareSheetVisible) {
                ShareSheet(
                    sharedUrl = viewModel.sharedUrl,
                    skipPicker = viewModel.skipPicker,
                    onDownload = viewModel::acceptSharedLink,
                    onDismiss = viewModel::dismissShareSheet,
                )
            }
        }
    }
}
