package com.seiko.plugin.rust

import com.android.build.gradle.LibraryExtension
import org.gradle.api.DefaultTask
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinMultiplatformPlugin

class RustKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val cargoExtension = extensions.create("cargo", CargoExtension::class.java)
            afterEvaluate {
                if (plugins.hasPlugin(KotlinMultiplatformPlugin::class.java)) {
                    throw GradleException("Kotlin multiplatform plugin is not resolved.")
                }
                val kmpExtension = extensions.getByType(KotlinMultiplatformExtension::class.java)
                configurePlugin(
                    kmpExtension = kmpExtension,
                    cargoExtension = cargoExtension,
                )
            }
        }
    }

    private fun Project.configurePlugin(
        kmpExtension: KotlinMultiplatformExtension,
        cargoExtension: CargoExtension,
    ) {
        check(cargoExtension.module.isNotEmpty()) { "module cannot be empty" }
        check(cargoExtension.libName.isNotEmpty()) { "libName cannot be empty" }

        val toolchains = mutableSetOf<Toolchain>()
        kmpExtension.targets.forEach {
            when (it.name) {
                "android" -> {
                    val androidExtension = extensions.getByType(LibraryExtension::class.java)
                    val abiFilters = androidExtension.defaultConfig.ndk.abiFilters
                    if (abiFilters.isEmpty()) {
                        // if not config, support all android targets
                        abiFilters.addAll(listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
                    }
                    toolchains.add(
                        Toolchain.Android(
                            targets = abiFilters.mapTo(mutableSetOf()) { abi ->
                                when (abi) {
                                    "x86" -> "i686-linux-android"
                                    "x86_64" -> "x86_64-linux-android"
                                    "armeabi-v7a" -> " armv7-linux-androideabi"
                                    "arm64-v8a" -> "aarch64-linux-android"
                                    else -> ""
                                }
                            },
                            abiFilters = abiFilters,
                        )
                    )
                }
                "jvm", "desktop" -> {
                    toolchains.add(
                        Toolchain.Jvm(
                            targets = setOf(
                                getCurrentOsTargetTriple(),
                            ),
                        )
                    )
                }
                "macosX64", "macosArm64" -> {
                    toolchains.add(
                        Toolchain.Darwin(
                            targets = setOf(
                                getCurrentOsTargetTriple(),
                            ),
                        )
                    )
                }
                "iosX64", "iosArm64", "iosSimulatorArm64" -> {
                    toolchains.add(
                        Toolchain.IOS(
                            targets = setOf(
                                when (it.name) {
                                    "iosX64" -> "x86_64-apple-ios"
                                    "iosArm64" -> "aarch64-apple-ios"
                                    "iosSimulatorArm64" -> "aarch64-apple-ios-sim"
                                    else -> ""
                                }
                            ),
                        )
                    )
                }
            }
        }

        val buildTask = project.tasks.maybeCreate(
            "cargoBuild",
            DefaultTask::class.java
        ).apply {
            group = RUST_TASK_GROUP
            description = "Build library (all targets)"
        }

        toolchains.forEach { toolchain ->
            val targetBuildTask = project.tasks.maybeCreate(
                "cargoBuild${toolchain.name}",
                CargoBuildTask::class.java,
            ).also {
                it.group = RUST_TASK_GROUP
                it.description = "Build library (${toolchain.name})"
                it.toolchain = toolchain
            }
            when (toolchain) {
                is Toolchain.Android -> {
                    val javaPreCompileDebug by tasks.getting
                    javaPreCompileDebug.dependsOn(targetBuildTask)
                    val javaPreCompileRelease by tasks.getting
                    javaPreCompileRelease.dependsOn(targetBuildTask)
                }
                is Toolchain.Jvm -> {
                    // TODO replace desktop name
                    val compileKotlinDesktop by tasks.getting
                    compileKotlinDesktop.dependsOn(targetBuildTask)
                }
                is Toolchain.Darwin -> {
                    val compileKotlinMacosX64 by tasks.getting
                    compileKotlinMacosX64.dependsOn(targetBuildTask)
                }
                is Toolchain.IOS -> {
                    val compileKotlinIosX64 by tasks.getting
                    compileKotlinIosX64.dependsOn(targetBuildTask)
                }
            }
            buildTask.dependsOn(targetBuildTask)
        }
    }

    companion object {
        const val RUST_TASK_GROUP = "rust"
    }
}
