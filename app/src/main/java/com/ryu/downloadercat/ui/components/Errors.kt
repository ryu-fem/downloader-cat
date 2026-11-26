package com.ryu.downloadercat.ui.components

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.ryu.downloadercat.R
import com.ryu.downloadercat.engine.EngineError

@StringRes
fun EngineError.messageRes(): Int = when (this) {
    EngineError.INVALID_URL -> R.string.error_invalid_url
    EngineError.UNSUPPORTED_URL -> R.string.error_unsupported_url
    EngineError.NETWORK -> R.string.error_network
    EngineError.LOGIN_REQUIRED -> R.string.error_login_required
    EngineError.UNAVAILABLE -> R.string.error_unavailable
    EngineError.DRM -> R.string.error_drm
    EngineError.EXTRACTOR -> R.string.error_extractor
    EngineError.PARSE -> R.string.error_parse
    EngineError.ENGINE_INIT -> R.string.error_engine_init
    EngineError.NO_FORMATS -> R.string.format_no_formats
    EngineError.CANCELED -> R.string.error_canceled
    EngineError.UNKNOWN -> R.string.error_unknown
}

@Composable
fun EngineError.localizedMessage(): String = stringResource(messageRes())
