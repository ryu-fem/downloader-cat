package com.ryu.downloadercat.engine

import android.content.Context
import com.ryu.downloadercat.data.PlaylistEntry
import com.ryu.downloadercat.data.PlaylistInfo
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.YoutubeDLRequest
import com.yausername.youtubedl_android.mapper.VideoInfo
import java.io.IOException
import java.net.URI
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONException
import org.json.JSONObject

object DownloaderEngine {
    suspend fun initialize(context: Context) = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        // Both init calls are synchronized and idempotent upstream.
        YoutubeDL.getInstance().init(appContext)
        FFmpeg.getInstance().init(appContext)
    }

    suspend fun update(context: Context): EngineUpdateState = withContext(Dispatchers.IO) {
        val appContext = context.applicationContext
        initialize(appContext)
        when (YoutubeDL.getInstance().updateYoutubeDL(appContext)) {
            YoutubeDL.UpdateStatus.DONE -> EngineUpdateState.UPDATED
            YoutubeDL.UpdateStatus.ALREADY_UP_TO_DATE -> EngineUpdateState.UP_TO_DATE
            null -> EngineUpdateState.UP_TO_DATE
        }
    }

    suspend fun fetchInfo(
        context: Context,
        url: String,
        proxy: String = "",
        cookiesFromBrowser: String = "",
    ): VideoInfo = withContext(Dispatchers.IO) {
        initialize(context.applicationContext)
        val request = YoutubeDLRequest(url)
        applyNetworkOptions(request, proxy, cookiesFromBrowser)
        YoutubeDL.getInstance().getInfo(request)
    }

    suspend fun fetchPlaylist(
        context: Context,
        url: String,
        proxy: String = "",
        cookiesFromBrowser: String = "",
    ): PlaylistInfo = withContext(Dispatchers.IO) {
        initialize(context.applicationContext)
        val request = YoutubeDLRequest(url)
            .addOption("--dump-single-json")
            .addOption("--flat-playlist")
            .addOption("--ignore-errors")
            .addOption("--no-warnings")
            .addOption("--socket-timeout", "15")
        applyNetworkOptions(request, proxy, cookiesFromBrowser)
        val response = YoutubeDL.getInstance().execute(request)
        try {
            parsePlaylist(url, response.out)
        } catch (exception: JSONException) {
            throw YoutubeDLException("Unable to parse playlist information", exception)
        }
    }

    suspend fun versionName(context: Context): String? = withContext(Dispatchers.IO) {
        YoutubeDL.getInstance().versionName(context.applicationContext)
    }

    private fun applyNetworkOptions(
        request: YoutubeDLRequest,
        proxy: String,
        cookiesFromBrowser: String,
    ) {
        if (proxy.isNotBlank()) {
            request.addOption("--proxy", proxy)
        }
        val cookies = cookiesFromBrowser.trim().lowercase(Locale.US)
        if (cookies.isNotBlank() && cookies != "none") {
            request.addOption("--cookies-from-browser", cookies)
        }
    }

    private fun parsePlaylist(sourceUrl: String, output: String): PlaylistInfo {
        val root = JSONObject(output)
        val entriesJson = root.optJSONArray("entries")
        val entries = buildList {
            if (entriesJson != null) {
                for (index in 0 until entriesJson.length()) {
                    val entry = entriesJson.optJSONObject(index) ?: continue
                    val url = entry.httpUrl("url") ?: entry.httpUrl("webpage_url")
                    val id = entry.string("id") ?: url ?: "entry-$index"
                    val title = entry.string("title") ?: id
                    val duration = entry.optDouble("duration", 0.0)
                        .takeIf { it > 0.0 }
                        ?.toInt()
                        ?: 0
                    val bytes = entry.optLong("filesize", 0L).takeIf { it > 0L }
                        ?: entry.optLong("filesize_approx", 0L).takeIf { it > 0L }
                    add(
                        PlaylistEntry(
                            id = id,
                            title = title,
                            url = url,
                            durationSeconds = duration,
                            sizeMb = bytes?.let { it / (1024.0 * 1024.0) },
                        ),
                    )
                }
            }
        }
        val title = root.string("title")
            ?: root.string("fulltitle")
            ?: root.string("id")
            ?: sourceUrl
        val site = root.string("extractor_key")
            ?: root.string("extractor")
            ?: hostOf(sourceUrl).orEmpty()
        return PlaylistInfo(
            sourceUrl = sourceUrl,
            title = title,
            site = site,
            entries = entries,
        )
    }

    private fun JSONObject.string(key: String): String? =
        optString(key).takeIf { it.isNotBlank() && it != "null" }

    private fun JSONObject.httpUrl(key: String): String? =
        string(key)?.takeIf { it.startsWith("http://") || it.startsWith("https://") }

    private fun hostOf(url: String): String? =
        runCatching { URI(url).host }.getOrNull()?.removePrefix("www.")

    fun mapError(throwable: Throwable): EngineError {
        // yt-dlp reports failures as stderr text, so match stable phrases.
        if (throwable is InterruptedException) return EngineError.CANCELED
        if (throwable is YoutubeDL.CanceledException) return EngineError.CANCELED

        val message = generateSequence(throwable) { it.cause }
            .mapNotNull { it.message }
            .joinToString(" ")
            .lowercase(Locale.US)

        return when {
            message.contains("instance not initialized") ||
                message.contains("failed to initialize") -> EngineError.ENGINE_INIT

            message.contains("unsupported url") ||
                message.contains("no suitable extractor") -> EngineError.UNSUPPORTED_URL

            message.contains("is not a valid url") ||
                message.contains("invalid url") -> EngineError.INVALID_URL

            message.contains("drm") ||
                message.contains("protected by drm") -> EngineError.DRM

            message.contains("sign in") ||
                message.contains("login") ||
                message.contains("private video") ||
                message.contains("private content") ||
                message.contains("members-only") ||
                message.contains("confirm your age") ||
                message.contains("http error 401") ||
                message.contains("http error 403") -> EngineError.LOGIN_REQUIRED

            message.contains("http error 404") ||
                message.contains("video unavailable") ||
                message.contains("not available") ||
                message.contains("has been removed") ||
                message.contains("does not exist") -> EngineError.UNAVAILABLE

            message.contains("unable to download webpage") ||
                message.contains("urlopen error") ||
                message.contains("timed out") ||
                message.contains("connection") ||
                message.contains("network") ||
                message.contains("http error") -> EngineError.NETWORK

            message.contains("no video formats") ||
                message.contains("no formats") -> EngineError.NO_FORMATS

            message.contains("unable to extract") ||
                message.contains("extractor") -> EngineError.EXTRACTOR

            message.contains("unable to parse") -> EngineError.PARSE

            generateSequence(throwable) { it.cause }.any { it is IOException } -> EngineError.NETWORK
            throwable is YoutubeDLException -> EngineError.UNKNOWN
            else -> EngineError.UNKNOWN
        }
    }
}
