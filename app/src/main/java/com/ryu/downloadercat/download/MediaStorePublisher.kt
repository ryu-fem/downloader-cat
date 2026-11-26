package com.ryu.downloadercat.download

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.webkit.MimeTypeMap
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.File
import java.io.IOException
import java.util.Locale
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class StorageException(cause: Throwable) : Exception(cause)

data class PublishedFile(
    val uri: Uri,
    val path: String,
    val mimeType: String,
)

object MediaStorePublisher {
    suspend fun publish(
        context: Context,
        source: File,
        folder: String,
    ): PublishedFile = withContext(Dispatchers.IO) {
        val relative = relativePath(folder)
        val mimeType = mimeTypeFor(source)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            publishScoped(context, source, relative, mimeType)
        } else {
            publishLegacy(context, source, relative, mimeType)
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun publishScoped(
        context: Context,
        source: File,
        relative: String,
        mimeType: String,
    ): PublishedFile {
        val resolver = context.contentResolver
        val values = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, source.name)
            put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            put(MediaStore.MediaColumns.RELATIVE_PATH, relative)
            put(MediaStore.MediaColumns.IS_PENDING, 1)
        }

        val collection = MediaStore.Downloads.EXTERNAL_CONTENT_URI
        val uri = resolver.insert(collection, values)
            ?: throw StorageException(IOException("Could not create a MediaStore item"))

        try {
            resolver.openOutputStream(uri)?.use { output ->
                source.inputStream().use { input -> input.copyTo(output) }
            } ?: throw StorageException(IOException("Could not open the MediaStore output"))

            val done = ContentValues().apply {
                put(MediaStore.MediaColumns.IS_PENDING, 0)
            }
            if (resolver.update(uri, done, null, null) <= 0) {
                throw StorageException(IOException("Could not finish the MediaStore item"))
            }
        } catch (throwable: Throwable) {
            resolver.delete(uri, null, null)
            if (throwable is StorageException) throw throwable
            throw StorageException(throwable)
        }

        return PublishedFile(
            uri = uri,
            path = pathFor(relative, source.name),
            mimeType = mimeType,
        )
    }

    @Suppress("DEPRECATION")
    private fun publishLegacy(
        context: Context,
        source: File,
        relative: String,
        mimeType: String,
    ): PublishedFile {
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            throw SecurityException("Storage permission is required")
        }

        if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
            throw StorageException(IOException("External storage is not mounted"))
        }

        val downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        val subFolder = relative.removePrefix("Download/").ifBlank { "DownloaderCat" }
        val targetDir = File(downloads, subFolder)
        if (!targetDir.exists() && !targetDir.mkdirs()) {
            throw StorageException(IOException("Could not create the download folder"))
        }

        val target = uniqueTarget(targetDir, source.name)
        try {
            source.copyTo(target, overwrite = false)
        } catch (throwable: Throwable) {
            throw StorageException(throwable)
        }

        val uri = scan(context, target, mimeType)
            ?: throw StorageException(IOException("Could not scan the saved file"))

        return PublishedFile(
            uri = uri,
            path = target.absolutePath,
            mimeType = mimeType,
        )
    }

    private fun scan(context: Context, file: File, mimeType: String): Uri? {
        val latch = CountDownLatch(1)
        val result = AtomicReference<Uri?>(null)

        MediaScannerConnection.scanFile(
            context,
            arrayOf(file.absolutePath),
            arrayOf(mimeType),
        ) { _, uri ->
            result.set(uri)
            latch.countDown()
        }

        return try {
            latch.await(10, TimeUnit.SECONDS)
            result.get()
        } catch (exception: InterruptedException) {
            Thread.currentThread().interrupt()
            throw StorageException(exception)
        }
    }

    private fun uniqueTarget(directory: File, name: String): File {
        val base = name.substringBeforeLast('.', name)
        val extension = name.substringAfterLast('.', "")
        var candidate = File(directory, name)
        var index = 1

        while (candidate.exists()) {
            val nextName = if (extension.isBlank()) {
                "$base ($index)"
            } else {
                "$base ($index).$extension"
            }
            candidate = File(directory, nextName)
            index += 1
        }

        return candidate
    }

    private fun relativePath(folder: String): String {
        val cleaned = folder
            .replace('\\', '/')
            .split('/')
            .filter { it.isNotBlank() && it != "." && it != ".." }
            .joinToString("/")
        val normalized = when {
            cleaned.isBlank() -> "Download/DownloaderCat"
            cleaned == "Download" || cleaned.startsWith("Download/") -> cleaned
            cleaned == "Downloads" -> "Download"
            cleaned.startsWith("Downloads/") -> "Download/" + cleaned.removePrefix("Downloads/")
            else -> "Download/$cleaned"
        }
        return if (normalized == "Download") "Download/DownloaderCat" else normalized
    }

    @Suppress("DEPRECATION")
    private fun pathFor(relative: String, name: String): String =
        File(Environment.getExternalStorageDirectory(), "$relative/$name").absolutePath

    private fun mimeTypeFor(file: File): String {
        val extension = file.extension.lowercase(Locale.US)
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
            ?: "application/octet-stream"
    }
}
