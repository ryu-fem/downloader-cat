package com.ryu.downloadercat

import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val viewModel: AppViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        if (savedInstanceState == null) {
            handleShareIntent(intent)
        }

        setContent {
            DownloaderCatApp(viewModel)
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleShareIntent(intent)
    }

    private fun handleShareIntent(intent: Intent?) {
        val shareIntent = intent ?: return
        if (shareIntent.action != Intent.ACTION_SEND) return

        val type = shareIntent.type
        if (!type.isNullOrBlank() && !type.startsWith("text/")) return

        val text = shareIntent.getCharSequenceExtra(Intent.EXTRA_TEXT)?.toString()
            ?: shareIntent.clipData
                ?.takeIf { it.itemCount > 0 }
                ?.getItemAt(0)
                ?.coerceToText(this)
                ?.toString()

        if (text.isNullOrBlank()) return

        shareIntent.removeExtra(Intent.EXTRA_TEXT)
        viewModel.onSharedText(text)
    }
}
