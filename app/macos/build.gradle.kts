plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}


kotlin {
    applyDefaultHierarchyTemplate()
    macosX64 {
        binaries {
            executable {
                entryPoint = "com.seiko.compose.hellorust.demo.main"
                freeCompilerArgs += listOf(
                    "-linker-option",
                    "-framework",
                    "-linker-option",
                    "Metal"
                )
            }
        }
    }
    macosArm64 {
        binaries {
            executable {
                entryPoint = "com.seiko.compose.hellorust.demo.main"
                freeCompilerArgs += listOf(
                    "-linker-option",
                    "-framework",
                    "-linker-option",
                    "Metal"
                )
            }
        }
    }

    sourceSets {
        macosMain{
            dependsOn(commonMain.get())
            dependencies {
                implementation(projects.app.common)
            }
        }
        val macosX64Main by getting {
            dependsOn(macosMain.get())
        }
        val macosArm64Main by getting {
            dependsOn(macosMain.get())
        }
    }
}

compose.desktop.nativeApplication {
    targets(kotlin.targets.getByName("macosX64"), kotlin.targets.getByName("macosArm64"))
    distributions {
        targetFormats(org.jetbrains.compose.desktop.application.dsl.TargetFormat.Dmg)
        packageName = "ComposeHelloRust"
        packageVersion = "1.0.0"
    }
}

kotlin {
    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            // TODO: the current compose binary surprises LLVM, so disable checks for now.
            freeCompilerArgs += "-Xdisable-phases=VerifyBitcode"
            binaryOptions["memoryModel"] = "experimental"
        }
    }
}
