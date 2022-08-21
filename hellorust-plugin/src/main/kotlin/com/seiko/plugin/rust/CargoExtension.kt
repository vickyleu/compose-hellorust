package com.seiko.plugin.rust

open class CargoExtension {
    var module: String = ""
    var libName: String = ""
    var profile: String = "debug"
    var isVerbose: Boolean = false

    var jniSuffix = "-jni"
    var nativeSuffix = "-native"

    var cargoCommand: String = "cargo"
    var rustUpChannel: String = ""
    var pythonCommand: String = "python"

    var androidJniDir: String = "./src/androidMain/jniLibs"
    var jvmJniDir: String = "./src/jvmMain/resources/jni"
    val darwinDir: String = "./src/nativeInterop/cinterop"
}