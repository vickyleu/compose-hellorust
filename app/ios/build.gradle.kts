plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.jetbrains.compose)
}


kotlin {
    applyDefaultHierarchyTemplate()
    listOf(
        iosX64("uikitX64"),
        iosArm64("uikitArm64"),
        iosSimulatorArm64("uikitSimulator")
    ).forEach {
        it. binaries {
            executable {
                entryPoint = "com.seiko.compose.hellorust.demo.main"
                freeCompilerArgs = freeCompilerArgs + listOf(
                    "-linker-option",
                    "-framework",
                    "-linker-option",
                    "Metal",
                    "-linker-option",
                    "-lresolv"
                )
            }
        }
    }

    sourceSets {
        val uikitMain by creating{
            dependsOn(commonMain.get())
            dependencies {
                implementation(projects.app.common)
            }
        }

        val uikitX64Main by getting {
            dependsOn(uikitMain)
        }
        val uikitArm64Main by getting {
            dependsOn(uikitMain)
        }
        val uikitSimulatorMain by getting {
            dependsOn(uikitMain)
        }
    }
}

compose.experimental {

    /*uikit.application {
        bundleIdPrefix = "com.seiko.compose.hellorust.demo"
        projectName = "ComposeHelloRust"
        deployConfigurations {
            simulator("Simulator") {
                device = org.jetbrains.compose.experimental.dsl.IOSDevices.IPHONE_13_MINI
            }
        }
    }*/
}

val runIos by tasks.registering {
    group = "run"
    dependsOn("iosDeploySimulatorDebug")
}
