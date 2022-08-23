package com.seiko.compose.hellorust

expect object HelloRust {
    fun hello(): String

    fun add(lhs: Int, rhs: Int): Int
}
