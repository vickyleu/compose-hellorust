package com.seiko.compose.hellorust

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Application
import com.seiko.compose.ImageLoader
import com.seiko.compose.ImageLoaderBuilder
import com.seiko.compose.LocalImageLoader
import com.seiko.compose.cache.memory.MemoryCacheBuilder
import com.seiko.compose.hellorust.demo.App
import io.github.aakira.napier.DebugAntilog
import io.github.aakira.napier.Napier
import kotlinx.cinterop.ObjCObjectBase
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

class SkikoAppDelegate : UIResponder, UIApplicationDelegateProtocol {
    companion object : UIResponderMeta(), UIApplicationDelegateProtocolMeta

    @ObjCObjectBase.OverrideInit
    constructor() : super()

    private var _window: UIWindow? = null
    override fun window() = _window
    override fun setWindow(window: UIWindow?) {
        _window = window
    }

    override fun application(
        application: UIApplication,
        didFinishLaunchingWithOptions: Map<Any?, *>?,
    ): Boolean {
        window = UIWindow(frame = UIScreen.mainScreen.bounds)
        window!!.rootViewController = Application("Compose ImageLoader") {
            Column {
                // To skip upper part of screen.
                Spacer(modifier = Modifier.height(30.dp))
                CompositionLocalProvider(
                    LocalImageLoader provides generateImageLoader(),
                    LocalResLoader provides ResLoader(),
                ) {
                    App()
                }
            }
        }
        window!!.makeKeyAndVisible()
        return true
    }
}

private fun generateImageLoader(): ImageLoader {
    return ImageLoaderBuilder()
        .memoryCache {
            MemoryCacheBuilder()
                // Set the max size to 25% of the app's available memory.
                .maxSizePercent(0.25)
                .build()
        }
        // .diskCache {
        //     DiskCacheBuilder()
        //         .directory(getCacheDir().resolve("image_cache").toOkioPath())
        //         .maxSizeBytes(512L * 1024 * 1024) // 512MB
        //         .build()
        // }
        .build()
}
