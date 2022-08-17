package com.seiko.compose.hellorust

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.seiko.compose.hellorust.demo.App
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
