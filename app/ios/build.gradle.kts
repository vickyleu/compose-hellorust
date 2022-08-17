plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

kotlin {
    ios("uikit") {
        binaries {
            executable {
                entryPoint = "com.seiko.compose.hellorust.demo.main"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-linker-option", "-framework", "-linker-option", "Metal",
                )
            }
        }
    }
    sourceSets {
        val uikitMain by getting {
            dependencies {
                implementation(projects.app.common)
            }
        }
    }
}

compose.experimental {
    uikit.application {
        bundleIdPrefix = "com.seiko.compose.hellorust.demo"
        projectName = "Compose Vlc"
    }
}
