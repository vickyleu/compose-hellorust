package com.seiko.compose.vlc

internal actual fun loadNativeLibrary() {
    System.loadLibrary("hellorust")
}
