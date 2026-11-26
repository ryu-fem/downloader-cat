package com.ryu.downloadercat.engine

import android.content.Context
import com.yausername.ffmpeg.FFmpeg
import com.yausername.youtubedl_android.YoutubeDL
import com.yausername.youtubedl_android.YoutubeDLException
import com.yausername.youtubedl_android.mapper.VideoInfo
import java.io.IOException
import java.util.Locale
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

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

    suspend fun fetchInfo(context: Context, url: String): VideoInfo = withContext(Dispatchers.IO) {
        initialize(context.applicationContext)
        YoutubeDL.getInstance().getInfo(url)
    }

    suspend fun versionName(context: Context): String? = withContext(Dispatchers.IO) {
        YoutubeDL.getInstance().versionName(context.applicationContext)
    }

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
