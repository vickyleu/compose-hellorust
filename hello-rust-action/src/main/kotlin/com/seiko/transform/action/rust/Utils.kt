package com.seiko.transform.action.rust

import org.gradle.api.GradleException
import org.gradle.api.Project
import java.io.ByteArrayOutputStream
// import java.util.Locale

// internal enum class OperatingSystem {
//     Windows, Linux, MacOS, Unknown;
// }
//
// internal val currentOperatingSystem: OperatingSystem
//     get() {
//         val osName = System.getProperty("os.name").toLowerCase(Locale.US)
//         return when {
//             osName.contains("win") -> {
//                 OperatingSystem.Windows
//             }
//             osName.contains("nix")
//                     || osName.contains("nux")
//                     || osName.contains("aix") -> {
//                 OperatingSystem.Linux
//             }
//             osName.contains("mac") -> {
//                 OperatingSystem.MacOS
//             }
//             else -> {
//                 OperatingSystem.Unknown
//             }
//         }
//     }

internal fun Project.getCurrentOsTargetTriple(): String {
    val stdout = ByteArrayOutputStream()
    val result = exec {
        standardOutput = stdout
        commandLine = listOf("rustc", "--version", "--verbose")
    }
    if (result.exitValue != 0) {
        throw GradleException("Failed to get default target triple from rustc (exit code: ${result.exitValue})")
    }
    val output = stdout.toString()

    // The `rustc --version --verbose` output contains a number of lines like `key: value`.
    // We're only interested in `host: `, which corresponds to the default target triple.
    val triplePrefix = "host: "

    val triple = output.split("\n")
        .find { it.startsWith(triplePrefix) }
        ?.substring(triplePrefix.length)
        ?.trim()

    if (triple == null) {
        throw GradleException("Failed to parse `rustc -Vv` output! (Please report a rust-android-gradle bug)")
    } else {
        logger.info("Default rust target triple: $triple")
    }
    return triple
}