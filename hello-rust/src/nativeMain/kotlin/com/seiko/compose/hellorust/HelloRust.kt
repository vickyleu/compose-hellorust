package com.seiko.compose.hellorust

import hellorust.add_native
import hellorust.hello_native
import kotlinx.cinterop.rawValue
import kotlinx.cinterop.toKStringFromUtf8

actual object HelloRust {

    actual fun hello(): String {
        return hello_native()?.toKStringFromUtf8().orEmpty()
    }

    actual fun add(lhs: Int, rhs: Int): Int {
        return add_native(lhs, rhs)
    }
}