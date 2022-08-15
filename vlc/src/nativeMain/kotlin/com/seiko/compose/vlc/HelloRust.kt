package com.seiko.compose.vlc

actual object HelloRust {
    init {
        loadNativeLibrary()
    }
    actual fun hello(): String {
        return ""
    }
}