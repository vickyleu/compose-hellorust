package com.seiko.compose.hellorust

internal actual fun loadNativeLibrary() {
    System.loadLibrary("hellorust")
}
