@file:Suppress("OPT_IN_USAGE")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
    alias(libs.plugins.android.library)
    alias(libs.plugins.rust)
}



kotlin {
    androidTarget()
    jvm("desktop") {
//        compilerOptions {
//            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
//        }
    }
    applyDefaultHierarchyTemplate()

    iosX64()
    iosArm64()
    iosSimulatorArm64()

    macosX64()
    macosArm64()


    sourceSets {
        all {
            languageSettings.apply {
                optIn("kotlin.RequiresOptIn")
                // Required for CPointer etc. since Kotlin 1.9.
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
            }
        }
        commonMain{
            dependencies {
                api(compose.foundation)
                api(libs.ktor.client.core)
                api(libs.kotlinx.coroutines.core)
                api(libs.okio)
                implementation(libs.napier)
            }
        }

        val engineMain by creating {
            dependsOn(commonMain.get())
        }
        val jniMain by creating {
            dependsOn(engineMain)
        }
        androidMain{
            dependsOn(jniMain)
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlinx.coroutines.android)
            }
        }

        val desktopMain by getting {
            dependsOn(jniMain)
            dependencies {
                implementation(libs.ktor.client.okhttp)
                implementation(libs.kotlinx.coroutines.swing)
            }
        }

        nativeMain{
            dependsOn(engineMain)
        }

        iosMain{
            dependsOn(nativeMain.get())
            dependencies {
                implementation(libs.ktor.client.ios)
            }
        }
        val iosX64Main by getting {
            dependsOn(iosMain.get())
        }
        val iosArm64Main by getting {
            dependsOn(iosMain.get())
        }
        val iosSimulatorArm64Main by getting {
            dependsOn(iosMain.get())
        }

        macosMain{
            dependsOn(nativeMain.get())
        }
        val macosX64Main by getting {
            dependsOn(macosMain.get())
        }
        val macosArm64Main by getting {
            dependsOn(macosMain.get())
        }
        targets.withType<KotlinNativeTarget> {
            val targetName = this.name
//            binaries {
//                sharedLib {
//                    baseName = "kmmRust"
////                    linkerOpts("-L${file("src/nativeInterop/cinterop/hellorust/$targetName/").absolutePath}")
//                }
//            }
            val main by compilations.getting {
                cinterops {
                    val hellorust by creating {

                        defFile(file("src/nativeInterop/cinterop/hellorust.def"))
                        header(file("rs/hellorust-native/hellorust.h"))
                        this.packageName = "hellorust"
//                        this.linkerOpts("-L${file("src/nativeInterop/cinterop/hellorust/$targetName/").absolutePath}")
                        extraOpts(
                            "-libraryPath",file("src/nativeInterop/cinterop/hellorust/$targetName/").absolutePath
                        )
                    }
                }
            }
        }
    }
}

android {
    namespace = "io.github.qdsfdhvh.compose.hellorust"
    compileSdk = 34
    ndkVersion = "23.0.7599858"

    defaultConfig {
        minSdk = 24
        ndk {
            abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
        }
    }
    lint {
        targetSdk = 34
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
}

cargo {
    module = "./rs"
    libName = "hellorust"
    profile = "release"
    jvmJniDir = "./src/desktopMain/resources/jni"
}
