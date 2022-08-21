package com.seiko.plugin.rust

enum class ToolchainType {
    Android, IOS, Jvm, Darwin;
}

data class Toolchain(
    val type: ToolchainType,
    val targets: Set<String>,
)
