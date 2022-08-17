package com.seiko.compose.hellorust

actual object HelloRust {
    init {
        loadNativeLibrary()
    }
    @JvmStatic
    actual external fun hello(): String
}

internal expect fun loadNativeLibrary()