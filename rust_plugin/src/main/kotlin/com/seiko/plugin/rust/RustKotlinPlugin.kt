package com.seiko.plugin.rust

import com.android.build.gradle.LibraryExtension
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.configurationcache.extensions.capitalized
import org.gradle.kotlin.dsl.getValue
import org.gradle.kotlin.dsl.getting
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

@Suppress("Unused")
class RustKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        with(target) {
            val cargoExtension = extensions.create("cargo", CargoExtension::class.java)
            afterEvaluate {
                if (!project.pluginManager.hasPlugin("org.jetbrains.kotlin.multiplatform")) {
                    throw GradleException("Completing a library with a multiplatform library requires Kotlin Multiplatform")
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

        val toolchains = mutableSetOf<Pair<Toolchain, KotlinTarget>>()
        kmpExtension.targets.forEach { target: KotlinTarget ->
            val name = target.targetName.capitalized()
            when (target.targetName) {
                "android" -> {
                    val androidExtension = extensions.getByType(LibraryExtension::class.java)
                    val abiFilters = androidExtension.defaultConfig.ndk.abiFilters
                    if (abiFilters.isEmpty()) {
                        // if not config, support all android targets
                        abiFilters.addAll(listOf("x86", "x86_64", "armeabi-v7a", "arm64-v8a"))
                    }
                    toolchains.add(
                        Toolchain.Android(
                            name = name,
                            targets = abiFilters.mapTo(mutableSetOf()) { abi ->
                                when (abi) {
                                    "x86" -> "i686-linux-android"
                                    "x86_64" -> "x86_64-linux-android"
                                    "armeabi-v7a" -> "armv7-linux-androideabi"
                                    "arm64-v8a" -> "aarch64-linux-android"
                                    else -> ""
                                }
                            },
                            abiFilters = abiFilters,
                        ) to target
                    )
                }

                "jvm", "desktop" -> {
                    toolchains.add(
                        Toolchain.Jvm(
                            name = name,
                            targets = setOf(
                                getCurrentOsTargetTriple(),
                            ),
                        ) to target
                    )
                }

                "macosX64", "macosArm64" -> {
                    toolchains.add(
                        Toolchain.Darwin(
                            name = name,
                            targets = setOf(
                                when (target.targetName) {
                                    "macosX64" -> "x86_64-apple-darwin"
                                    "macosArm64" -> "aarch64-apple-darwin"
                                    else -> ""
                                }
                            ),
                        ) to target
                    )
                }

                "iosX64", "iosArm64", "iosSimulatorArm64" -> {
                    toolchains.add(
                        Toolchain.IOS(
                            name = name,
                            targets = setOf(
                                when (target.targetName) {
                                    "iosX64" -> "x86_64-apple-ios"
                                    "iosArm64" -> "aarch64-apple-ios"
                                    "iosSimulatorArm64" -> "aarch64-apple-ios-sim"
                                    else -> ""
                                }
                            ),
                        ) to target
                    )
                }

                else -> {
                }
            }
        }
        toolchains.forEach { toolchain ->
            dependTask(toolchain)
        }
       /* toolchains.forEach { toolchain ->
            val targetBuildTask = project.tasks.maybeCreate(
                "cargoBuild${toolchain.first.name}",
                CargoBuildTask::class.java,
            ).also {
                it.group = RUST_TASK_GROUP
                it.description = "Build library (${toolchain.first.name})"
                it.toolchain = toolchain
            }
            when (toolchain.first) {
                is Toolchain.Android -> {
                    val javaPreCompileDebug by tasks.getting
                    javaPreCompileDebug.dependsOn(targetBuildTask)
                    val javaPreCompileRelease by tasks.getting
                    javaPreCompileRelease.dependsOn(targetBuildTask)
                }

                is Toolchain.Jvm, is Toolchain.Darwin, is Toolchain.IOS -> {
                    val task = tasks.getByName("compileKotlin${toolchain.first.name}")
                    task.dependsOn(targetBuildTask)
                }
            }
        }*/
    }
    private fun Project.dependTask(toolchain: Pair<Toolchain, KotlinTarget>) {
        val targetBuildTask = project.tasks.maybeCreate(
            "cargoBuild${toolchain.first.name}",
            CargoBuildTask::class.java,
        ).also {
            it.group = RUST_TASK_GROUP
            it.description = "Build library (${toolchain.first.name})"
            it.toolchain = toolchain
        }

        when (toolchain.first) {
            is Toolchain.Android -> {
                try {
                    val javaPreCompileDebug by tasks.getting
                    javaPreCompileDebug.dependsOn(targetBuildTask)
                    val javaPreCompileRelease by tasks.getting
                    javaPreCompileRelease.dependsOn(targetBuildTask)
                } catch (e: Exception) {
                    logger.warn("Android plugin not found ${e.message}")
                }
            }

            is Toolchain.Jvm, is Toolchain.Darwin, is Toolchain.IOS -> {
                try {
                    if (toolchain.second is KotlinNativeTarget) {
                        val cinterops =
                            (toolchain.second as KotlinNativeTarget).compilations.getByName("main").cinterops
                        val interopList = cinterops.mapNotNull {
                            val interopProcessingTaskName = it.interopProcessingTaskName
                            if (interopProcessingTaskName.contains(toolchain.second.targetName.capitalized())) {
                                interopProcessingTaskName
                            } else null
                        }
                        interopList.forEach {
                            val task = tasks.getByPath(it)
                            task.dependsOn(targetBuildTask)
                            task.setMustRunAfter(listOf(targetBuildTask))
                        }
                    }
                } catch (e: Exception) {
                    val task = tasks.getByName("compileKotlin${toolchain.first.name}")
                    task.dependsOn(targetBuildTask)
                    task.setMustRunAfter(listOf(targetBuildTask))
                }

            }
        }
    }

    companion object {
        const val RUST_TASK_GROUP = "rust"
    }
}
