@file:Suppress("OPT_IN_USAGE")

import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}



kotlin {
    jvm {
//        compilerOptions {
//            jvmTarget.set(JvmTarget.fromTarget(libs.versions.jvmTarget.get()))
//        }
//        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(projects.app.common)
                implementation(compose.desktop.currentOs)
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "com.seiko.compose.hellorust.demo.MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "Compose Vlc"
            packageVersion = "1.0.0"
            modules("java.sql") // https://github.com/JetBrains/compose-jb/issues/381
            modules("jdk.unsupported")
            modules("jdk.unsupported.desktop")
            macOS {
                bundleID = "com.seiko.imageloader.demo"
                // iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.icns"))
            }
            linux {
                // iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.png"))
            }
            windows {
                shortcut = true
                menu = true
                // iconFile.set(project.file("src/jvmMain/resources/icon/ic_launcher.ico"))
            }
        }
    }
}
