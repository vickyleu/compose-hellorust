package com.seiko.compose.hellorust.demo

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.ComposeUIViewController
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.cinterop.BetaInteropApi
import kotlinx.cinterop.ExperimentalForeignApi
import kotlinx.cinterop.autoreleasepool
import kotlinx.cinterop.cstr
import kotlinx.cinterop.memScoped
import kotlinx.cinterop.toCValues
import platform.Foundation.NSStringFromClass
import platform.UIKit.UIApplication
import platform.UIKit.UIApplicationDelegateProtocol
import platform.UIKit.UIApplicationDelegateProtocolMeta
import platform.UIKit.UIApplicationMain
import platform.UIKit.UIResponder
import platform.UIKit.UIResponderMeta
import platform.UIKit.UIScreen
import platform.UIKit.UIWindow

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
fun main() {
    Napier.base(DebugAntilog())

    val args = emptyArray<String>()
    memScoped {
        val argc = args.size + 1
        val argv = (arrayOf("skikoApp") + args).map { it.cstr.ptr }.toCValues()
        autoreleasepool {
            UIApplicationMain(argc, argv, null, NSStringFromClass(SkikoAppDelegate))
        }
    }
}

class SkikoAppDelegate : UIResponder(), UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    @OptIn(ExperimentalForeignApi::class)
    override fun applicationDidFinishLaunching(application: UIApplication) {
//        super.applicationDidFinishLaunching(application)
        window = UIWindow(frame = UIScreen.mainScreen.bounds).apply {
            rootViewController = ComposeUIViewController() {
                Column {
                    // To skip upper part of screen.
                    Spacer(modifier = Modifier.height(30.dp))
                    App()
                }
            }
            makeKeyAndVisible()
        }
//        return true
    }

//    override fun application(
//        application: UIApplication,
//        didFinishLaunchingWithOptions: Map<Any?, *>?,
//    ): Boolean {
//
//    }
}
