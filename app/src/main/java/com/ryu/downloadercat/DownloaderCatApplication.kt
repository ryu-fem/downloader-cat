package com.ryu.downloadercat

import android.app.Application
import android.util.Log
import com.ryu.downloadercat.engine.DownloaderEngine
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

class DownloaderCatApplication : Application() {
    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            try {
                DownloaderEngine.initialize(this@DownloaderCatApplication)
            } catch (throwable: Throwable) {
                Log.e(TAG, "Failed to initialize download engine", throwable)
            }
        }
    }

    private companion object {
        const val TAG = "DownloaderCatApplication"
    }
}
