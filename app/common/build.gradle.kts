@file:Suppress("OPT_IN_USAGE")

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
    id("com.android.library")
}



kotlin {
    androidTarget()
    jvm("desktop") {
//        compilerOptions {
//            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
//        }
    }

    applyDefaultHierarchyTemplate()
    listOf(iosArm64(), iosX64(), iosSimulatorArm64())
    macosX64()
    macosArm64()
    sourceSets {
        commonMain{
            dependencies {
                api(projects.hellorust)
                api(compose.ui)
                api(compose.foundation)
                api(compose.material)
                api(compose.runtime)
                api("io.github.aakira:napier:2.6.1")
                api("dev.icerock.moko:resources:0.20.1")
            }
        }
        val desktopMain by getting
        androidMain{
            dependsOn(commonMain.get())
        }
        iosMain{
            dependsOn(commonMain.get())
        }
        macosMain{
            dependsOn(commonMain.get())
        }
        val macosX64Main by getting {
            dependsOn(macosMain.get())
        }
        val macosArm64Main by getting {
            dependsOn(macosMain.get())
        }
    }
}

android {
    namespace = "com.seiko.compose.hellorust.demo"
    compileSdk = 34
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
        targetCompatibility = JavaVersion.toVersion(libs.versions.jvmTarget.get())
    }
}
