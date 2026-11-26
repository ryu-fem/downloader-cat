package com.ryu.downloadercat.engine

enum class EngineError {
    INVALID_URL,
    UNSUPPORTED_URL,
    NETWORK,
    LOGIN_REQUIRED,
    UNAVAILABLE,
    DRM,
    EXTRACTOR,
    PARSE,
    ENGINE_INIT,
    NO_FORMATS,
    CANCELED,
    UNKNOWN,
}

enum class EngineUpdateState {
    IDLE,
    UPDATING,
    UPDATED,
    UP_TO_DATE,
    FAILED,
}
