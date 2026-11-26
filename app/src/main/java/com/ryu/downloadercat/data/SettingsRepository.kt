package com.ryu.downloadercat.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import java.io.IOException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

private val Context.settingsDataStore by preferencesDataStore(name = "downloader_cat_settings")

class SettingsRepository(context: Context) {
    private val dataStore = context.applicationContext.settingsDataStore

    val settings: Flow<AppSettings> = dataStore.data
        .catch { throwable ->
            if (throwable is IOException) emit(emptyPreferences()) else throw throwable
        }
        .map { preferences ->
            AppSettings(
                downloadFolder = preferences[KEY_DOWNLOAD_FOLDER] ?: "Download/DownloaderCat",
                defaultQuality = preferences[KEY_DEFAULT_QUALITY] ?: "best",
                skipPicker = preferences[KEY_SKIP_PICKER] ?: false,
                concurrency = (preferences[KEY_CONCURRENCY] ?: 2).coerceIn(1, 8),
                speedLimitMb = (preferences[KEY_SPEED_LIMIT] ?: 0).coerceAtLeast(0),
                proxy = preferences[KEY_PROXY] ?: "",
                themeMode = preferences[KEY_THEME_MODE] ?: "dark",
            )
        }

    suspend fun update(settings: AppSettings) {
        dataStore.edit { preferences ->
            preferences[KEY_DOWNLOAD_FOLDER] = settings.downloadFolder
            preferences[KEY_DEFAULT_QUALITY] = settings.defaultQuality
            preferences[KEY_SKIP_PICKER] = settings.skipPicker
            preferences[KEY_CONCURRENCY] = settings.concurrency.coerceIn(1, 8)
            preferences[KEY_SPEED_LIMIT] = settings.speedLimitMb.coerceAtLeast(0)
            preferences[KEY_PROXY] = settings.proxy
            preferences[KEY_THEME_MODE] = settings.themeMode
        }
    }

    private companion object {
        val KEY_DOWNLOAD_FOLDER = stringPreferencesKey("download_folder")
        val KEY_DEFAULT_QUALITY = stringPreferencesKey("default_quality")
        val KEY_SKIP_PICKER = booleanPreferencesKey("skip_picker")
        val KEY_CONCURRENCY = intPreferencesKey("concurrency")
        val KEY_SPEED_LIMIT = intPreferencesKey("speed_limit_mb")
        val KEY_PROXY = stringPreferencesKey("proxy")
        val KEY_THEME_MODE = stringPreferencesKey("theme_mode")
    }
}
