package com.seiko.compose.vlc

actual object HelloRust {
    init {
        loadNativeLibrary()
    }
    @JvmStatic
    actual external fun hello(): String
}

internal expect fun loadNativeLibrary()