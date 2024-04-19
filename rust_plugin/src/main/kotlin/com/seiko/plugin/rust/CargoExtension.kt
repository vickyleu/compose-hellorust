package com.seiko.plugin.rust

open class CargoExtension {
    var module: String = ""
    var libName: String = ""
    var profile: String = "debug"
    var isVerbose: Boolean = true
//    var isVerbose: Boolean = false

    var jniSuffix: String = "-jni"
    var nativeSuffix: String = "-native"

    var cargoCommand: String = "cargo"
    var rustUpCommand: String = "rustup"
    var pythonCommand: String = "python"
    var ndkCommand: String = "ndk"
    var lipoCommand: String = "lipo"

    var androidJniDir: String = "./src/androidMain/jniLibs"
    var jvmJniDir: String = "./src/jvmMain/resources/jni"
    val cinteropDir: String = "./src/nativeInterop/cinterop"
}
