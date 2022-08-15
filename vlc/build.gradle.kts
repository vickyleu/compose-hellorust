import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.Java.jvmTarget
        }
    }
    ios()
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.foundation)
                api("io.ktor:ktor-client-core:${Versions.ktor}")
                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.Kotlin.coroutines}")
                api("com.squareup.okio:okio:${Versions.okio}")
                implementation("io.github.aakira:napier:${Versions.napier}")
            }
        }
        val engineMain by creating {
            dependsOn(commonMain)
        }
        val jniMain by creating {
            dependsOn(engineMain)
        }
        val androidMain by getting {
            dependsOn(jniMain)
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:${Versions.ktor}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.Kotlin.coroutines}")
            }
        }
        val desktopMain by getting {
            dependsOn(jniMain)
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:${Versions.ktor}")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-swing:${Versions.Kotlin.coroutines}")
            }
        }
        val nativeMain by creating {
            dependsOn(engineMain)
        }
        val iosMain by getting {
            dependsOn(nativeMain)
            dependencies {
                implementation("io.ktor:ktor-client-darwin:${Versions.ktor}")
            }
        }
    }
}

android {
    namespace = "io.github.qdsfdhvh.compose.vlc"
    compileSdk = Versions.Android.compile
    buildToolsVersion = Versions.Android.buildTools
    defaultConfig {
        minSdk = Versions.Android.min
        targetSdk = Versions.Android.target
        ndk {
            abiFilters += listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a")
        }
    }
    compileOptions {
        sourceCompatibility = Versions.Java.java
        targetCompatibility = Versions.Java.java
    }
}

val cargoBuild by tasks.registering {
    doLast {
        exec {
            workingDir = File(project.projectDir, "rs")
            commandLine = listOf(
                "cargo", "xdk",
                "-t", "x86",
                "-o", "../src/androidMain/jniLibs",
                "build", "--release",
            )
        }
    }
}

afterEvaluate {
    val javaPreCompileDebug by tasks.getting
    javaPreCompileDebug.dependsOn(cargoBuild)
    val javaPreCompileRelease by tasks.getting
    javaPreCompileRelease.dependsOn(cargoBuild)
}
