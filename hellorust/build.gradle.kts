import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
    id("com.seiko.plugin.rust")
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = Versions.Java.jvmTarget
        }
    }
    ios()
    macosX64()
    macosArm64()
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
        val macosMain by creating {
            dependsOn(nativeMain)

        }
        val macosX64Main by getting {
            dependsOn(macosMain)
        }
        val macosArm64Main by getting {
            dependsOn(macosMain)
        }

        targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
            val main by compilations.getting
            main.defaultSourceSet.dependsOn(nativeMain)

            main.cinterops {
                val hellorust by creating {
                    defFile("${projectDir}/src/nativeInterop/cinterop/hellorust.def")
                    header(file("${projectDir}/rs/hellorust-native/hellorust.h"))
                    // includeDirs {
                    //     allHeaders("${projectDir}/src/nativeInterop/cinterop/hellorust")
                    // }


                    // linkerOpts("-L${rootDir.resolve("hellorust/src/nativeInterop/cinterop/hellorust/").absolutePath}")
                    // linkerOpts("-lhellorust")

                    // includeDirs("${projectDir}/src/nativeInterop/cinterop/hellorust")
                    // linkerOpts("-L ${projectDir.resolve("src/nativeInterop/cinterop/hellorust/").absolutePath}")
                    // includeDirs("src/nativeInterop/cinterop/hellorust")
                    // packageName("com.seiko.hellorust")
                }
            }

            binaries {
                // staticLib {
                //     linkerOpts("-v")
                //     linkerOpts("-L${rootDir.resolve("hellorust/src/nativeInterop/cinterop/hellorust/").absolutePath}")
                //     linkerOpts("-lhellorust")
                // }
                // sharedLib {
                //     linkerOpts("-L${rootDir.resolve("hellorust/src/nativeInterop/cinterop/hellorust/").absolutePath}")
                //     linkerOpts("-lhellorust")
                // }
                // framework {
                //     linkerOpts("-L${rootDir.resolve("hellorust/src/nativeInterop/cinterop/hellorust/").absolutePath}")
                //     linkerOpts("-lhellorust")
                // }

            //     executable {
            //         linkerOpts("-L${rootDir.resolve("hellorust/src/nativeInterop/cinterop/hellorust/").absolutePath}")
            //         linkerOpts("-lhellorust")
            //     }
            }

        }
    }
}

android {
    namespace = "io.github.qdsfdhvh.compose.hellorust"
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

cargo {
    module = "./rs"
    libName = "hellorust"
    profile = "release"

    jvmJniDir = "./src/desktopMain/resources/jni"
}
