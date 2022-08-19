package com.seiko.compose.hellorust

actual object HelloRust {
    init {
        loadNativeLibrary()
    }
    @JvmStatic
    actual external fun hello(): String

    @JvmStatic
    actual external fun add(lhs: Int, rhs: Int): Int
}

internal expect fun loadNativeLibrary()