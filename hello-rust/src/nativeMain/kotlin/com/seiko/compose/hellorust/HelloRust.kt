package com.seiko.compose.hellorust

actual object HelloRust {
    init {
        loadNativeLibrary()
    }
    actual fun hello(): String {
        return ""
    }
}