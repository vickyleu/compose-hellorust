package com.seiko.compose.hellorust.demo

import androidx.compose.ui.window.Window
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import platform.AppKit.NSApp
import platform.AppKit.NSApplication

fun main() {
    NSApplication.sharedApplication()
    Napier.base(DebugAntilog())
    Window("ComposeHelloRust") {
        App()
    }
    NSApp?.run()
}
