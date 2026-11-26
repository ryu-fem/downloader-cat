package com.ryu.downloadercat.ui.screens

import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import com.ryu.downloadercat.AppViewModel
import com.ryu.downloadercat.R
import com.ryu.downloadercat.data.MockData
import com.ryu.downloadercat.ui.components.AppDropdown
import com.ryu.downloadercat.ui.components.AppStepper
import com.ryu.downloadercat.ui.components.AppSwitchVisual
import com.ryu.downloadercat.ui.components.AppTextField
import com.ryu.downloadercat.ui.components.DropdownOption
import com.ryu.downloadercat.ui.components.LanguageToggle
import com.ryu.downloadercat.ui.components.ScreenHeader
import com.ryu.downloadercat.ui.components.SettingsGroup
import com.ryu.downloadercat.ui.components.SettingsLogo
import com.ryu.downloadercat.ui.components.SettingRowContainer
import com.ryu.downloadercat.ui.components.mirrorForRtl
import com.ryu.downloadercat.ui.components.qualityLabelRes
import com.ryu.downloadercat.ui.theme.AppTheme
import com.ryu.downloadercat.ui.theme.ThemeMode

@Composable
fun SettingsScreen(viewModel: AppViewModel) {
    val colors = AppTheme.colors
    val isArabic = LocalConfiguration.current.locales.get(0).language == "ar"

    val resources = LocalContext.current.resources
    val themeOptions = remember(resources) {
        listOf(
            DropdownOption(ThemeMode.SYSTEM, resources.getString(R.string.theme_system)),
            DropdownOption(ThemeMode.DARK, resources.getString(R.string.theme_dark)),
            DropdownOption(ThemeMode.LIGHT, resources.getString(R.string.theme_light)),
        )
    }
    val qualityOptions = remember(resources) {
        MockData.result.formats.map { format ->
            DropdownOption(
                value = format.id,
                label = qualityLabelRes(format.id)?.let { resources.getString(it) } ?: format.id,
            )
        }
    }

    val folderSoon = stringResource(R.string.settings_folder_soon)
    val engineSoon = stringResource(R.string.settings_engine_soon)
    val upToDate = stringResource(R.string.settings_up_to_date)
    val repoSoon = stringResource(R.string.settings_repo_soon)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 18.dp)
            .padding(top = 18.dp, bottom = 24.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        ScreenHeader(
            title = stringResource(R.string.settings_title),
            subtitle = stringResource(R.string.settings_subtitle),
            trailing = { SettingsLogo() },
        )

        SettingsGroup(stringResource(R.string.settings_downloads)) {
            SettingRowContainer(onClick = { viewModel.settingsStatus = folderSoon }) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_folder),
                        color = colors.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = viewModel.downloadFolder,
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = colors.muted,
                    modifier = Modifier
                        .size(18.dp)
                        .mirrorForRtl(),
                )
            }

            SettingRowContainer {
                Text(
                    text = stringResource(R.string.settings_default_quality),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                AppDropdown(
                    value = viewModel.defaultQuality,
                    options = qualityOptions,
                    onSelect = { viewModel.defaultQuality = it },
                    dropdownContentDescription = stringResource(R.string.aria_default_quality),
                )
            }

            SettingRowContainer(onClick = { viewModel.skipPicker = !viewModel.skipPicker }) {
                Text(
                    text = stringResource(R.string.settings_skip_picker),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
                AppSwitchVisual(viewModel.skipPicker)
            }

            SettingRowContainer {
                Text(
                    text = stringResource(R.string.settings_concurrency),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
                AppStepper(
                    value = viewModel.concurrency,
                    onDecrease = { if (viewModel.concurrency > 1) viewModel.concurrency -= 1 },
                    onIncrease = { if (viewModel.concurrency < 6) viewModel.concurrency += 1 },
                    canDecrease = viewModel.concurrency > 1,
                    canIncrease = viewModel.concurrency < 6,
                )
            }

            SettingRowContainer {
                Text(
                    text = stringResource(R.string.settings_speed_limit),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
                AppTextField(
                    value = viewModel.speedLimit,
                    onValueChange = { value ->
                        viewModel.speedLimit = value.filter { it.isDigit() }.take(4)
                    },
                    modifier = Modifier.width(118.dp),
                    placeholder = stringResource(R.string.settings_unlimited),
                    keyboardType = KeyboardType.Number,
                    minHeight = 48.dp,
                    contentPadding = PaddingValues(horizontal = 12.dp),
                    textAlign = TextAlign.End,
                    trailing = {
                        Text(
                            text = stringResource(R.string.unit_mb_s),
                            color = colors.subtle,
                            style = MaterialTheme.typography.labelSmall,
                        )
                    },
                )
            }
        }

        SettingsGroup(stringResource(R.string.settings_appearance)) {
            SettingRowContainer {
                Text(
                    text = stringResource(R.string.settings_theme),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                AppDropdown(
                    value = viewModel.themeMode,
                    options = themeOptions,
                    onSelect = { viewModel.themeMode = it },
                    dropdownContentDescription = stringResource(R.string.aria_theme),
                )
            }

            SettingRowContainer {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_language),
                        color = colors.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(
                            if (isArabic) R.string.language_arabic else R.string.language_english,
                        ),
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                LanguageToggle(
                    isArabic = isArabic,
                    onToggle = {
                        val next = if (isArabic) "en" else "ar"
                        AppCompatDelegate.setApplicationLocales(
                            LocaleListCompat.forLanguageTags(next),
                        )
                    },
                )
            }
        }

        SettingsGroup(stringResource(R.string.settings_network)) {
            SettingRowContainer {
                Text(
                    text = stringResource(R.string.settings_proxy),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Spacer(Modifier.width(12.dp))
                AppTextField(
                    value = viewModel.proxy,
                    onValueChange = { viewModel.proxy = it },
                    modifier = Modifier.width(168.dp),
                    placeholder = stringResource(R.string.settings_proxy_placeholder),
                    keyboardType = KeyboardType.Uri,
                    minHeight = 48.dp,
                    contentPadding = PaddingValues(horizontal = 12.dp),
                )
            }

            SettingRowContainer {
                Text(
                    text = stringResource(R.string.settings_cookies),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.cookies_none),
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        SettingsGroup(stringResource(R.string.settings_engine)) {
            SettingRowContainer(onClick = { viewModel.settingsStatus = engineSoon }) {
                Column(Modifier.weight(1f)) {
                    Text(
                        text = stringResource(R.string.settings_update_engine),
                        color = colors.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                    )
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = stringResource(R.string.settings_engine_not_checked),
                        color = colors.muted,
                        style = MaterialTheme.typography.bodySmall,
                    )
                }
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = colors.muted,
                    modifier = Modifier
                        .size(18.dp)
                        .mirrorForRtl(),
                )
            }
        }

        SettingsGroup(stringResource(R.string.settings_about)) {
            SettingRowContainer {
                Text(
                    text = stringResource(R.string.settings_version),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Text(
                    text = stringResource(R.string.settings_version_value),
                    color = colors.muted,
                    style = MaterialTheme.typography.bodySmall,
                )
            }

            SettingRowContainer(onClick = { viewModel.settingsStatus = upToDate }) {
                Text(
                    text = stringResource(R.string.settings_check_updates),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = colors.muted,
                    modifier = Modifier
                        .size(18.dp)
                        .mirrorForRtl(),
                )
            }

            SettingRowContainer(onClick = { viewModel.settingsStatus = repoSoon }) {
                Text(
                    text = stringResource(R.string.settings_github),
                    color = colors.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.weight(1f),
                )
                Icon(
                    imageVector = Icons.Outlined.ChevronRight,
                    contentDescription = null,
                    tint = colors.muted,
                    modifier = Modifier
                        .size(18.dp)
                        .mirrorForRtl(),
                )
            }
        }

        viewModel.settingsStatus?.let { status ->
            Text(
                text = status,
                color = colors.muted,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(horizontal = 6.dp),
            )
        }
    }
}
