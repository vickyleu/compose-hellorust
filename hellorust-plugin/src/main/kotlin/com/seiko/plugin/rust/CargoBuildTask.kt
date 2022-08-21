package com.seiko.plugin.rust

import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.TaskAction
import java.io.File

open class CargoBuildTask : DefaultTask() {

    @Input
    lateinit var toolchain: Toolchain

    @TaskAction
    fun build() = with(project) {
        val cargoExtension = extensions.getByType(CargoExtension::class.java)
        when (toolchain.type) {
            ToolchainType.Android -> generateAndroid(cargoExtension)
            ToolchainType.Jvm -> generateJvm(cargoExtension)
            ToolchainType.Darwin -> generateDarwin(cargoExtension)
            else -> Unit
        }
    }

    private fun Project.generateAndroid(
        cargoExtension: CargoExtension,
    ) {
        val jniFileName = cargoExtension.libName + cargoExtension.jniSuffix
        val moduleDir = File(getDir(cargoExtension.module), jniFileName)
        exec {
            standardOutput = System.out
            workingDir = moduleDir
            commandLine = mutableListOf<String>().apply {
                add(cargoExtension.cargoCommand)
                add("ndk")
                toolchain.targets.forEach { target ->
                    add("-t")
                    add(target)
                }
                add("-o")
                add(getDir(cargoExtension.androidJniDir).absolutePath)
                add("build")
                configCommonArgs(cargoExtension)
            }
        }
    }

    private fun Project.generateJvm(
        cargoExtension: CargoExtension,
    ) {
        val jniFileName = cargoExtension.libName + cargoExtension.jniSuffix
        val moduleDir = File(getDir(cargoExtension.module), jniFileName)
        val targetBuildDir = File(moduleDir, "target")
        val targetIntoDir = getDir(cargoExtension.jvmJniDir)

        toolchain.targets.forEach { target ->
            exec {
                // standardOutput = System.out
                workingDir = moduleDir
                commandLine = mutableListOf<String>().apply {
                    add(cargoExtension.cargoCommand)
                    add("build")
                    add("--target")
                    add(target)
                    configCommonArgs(cargoExtension)
                }
            }
            copy {
                from(File(targetBuildDir, "$target/${cargoExtension.profile}"))
                into(File(targetIntoDir, target.split('-').first()))
                val libName = cargoExtension.libName
                include("lib${libName}.so")
                include("lib${libName}.dylib")
                include("${libName}.dll")
            }
        }
    }

    private fun Project.generateDarwin(
        cargoExtension: CargoExtension,
    ) {
        val nativeFileName = cargoExtension.libName + cargoExtension.nativeSuffix
        val moduleDir = File(getDir(cargoExtension.module), nativeFileName)
        val targetBuildDir = File(moduleDir, "target")
        val targetIntoDir = getDir(cargoExtension.darwinDir)

        val target = toolchain.targets.first()
        val libName = cargoExtension.libName
        exec {
            workingDir = moduleDir
            commandLine = mutableListOf<String>().apply {
                add(cargoExtension.cargoCommand)
                add("build")
                add("--target")
                add(target)
                configCommonArgs(cargoExtension)
            }
        }
        copy {
            from(File(targetBuildDir, "$target/${cargoExtension.profile}"))
            into(File(targetIntoDir, libName))
            include("lib${libName}.so")
            include("lib${libName}.dylib")
            include("${libName}.dll")
        }
    }

    private fun MutableList<String>.configCommonArgs(cargoExtension: CargoExtension) {
        if (cargoExtension.isVerbose) {
            add("--verbose")
        }
        if (cargoExtension.profile != "debug") {
            add("--${cargoExtension.profile}")
        }
    }

    private fun getDir(path: String): File {
        val file = File(path)
        return if (file.isAbsolute) file else {
            File(project.projectDir, file.path)
        }.canonicalFile
    }
}