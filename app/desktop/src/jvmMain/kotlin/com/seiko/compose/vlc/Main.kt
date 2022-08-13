package com.seiko.compose.vlc

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seiko.compose.vlc.demo.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier

fun main() {
    Napier.base(DebugAntilog())
    application {
        Window(
            title = "PreCompose Sample",
            onCloseRequest = {
                exitApplication()
            },
        ) {
            App()
        }
    }
}
