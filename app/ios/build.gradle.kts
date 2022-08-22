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
                    "-linker-option", "-lresolv",
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
        projectName = "ComposeHelloRust"
        deployConfigurations {
            simulator("Simulator") {
                device = org.jetbrains.compose.experimental.dsl.IOSDevices.IPHONE_13_MINI
            }
        }
    }
}

val runIos by tasks.registering {
    group = "run"
    dependsOn("iosDeploySimulatorDebug")
}
