package com.seiko.transform.action.rust

enum class ToolchainType {
    Android, IOS, Jvm, Darwin;
}

data class Toolchain(
    val type: ToolchainType,
    val targets: Set<String>,
)
