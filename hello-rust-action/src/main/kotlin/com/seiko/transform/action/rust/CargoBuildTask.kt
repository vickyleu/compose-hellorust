package com.seiko.transform.action.rust

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
        val moduleDir = getDir(cargoExtension.module)
        when (toolchain.type) {
            ToolchainType.Android -> generateAndroid(cargoExtension, moduleDir)
            ToolchainType.Jvm -> generateJvm(cargoExtension, moduleDir)
            else -> Unit
        }
    }

    private fun Project.generateAndroid(
        cargoExtension: CargoExtension,
        moduleDir: File,
    ) {
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
        moduleDir: File,
    ) {
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
                into(File(targetIntoDir, target.split('-').first()).also { if (!it.exists()) it.mkdirs() })

                val libName = cargoExtension.libName
                include("lib${libName}.so")
                include("lib${libName}.dylib")
                include("${libName}.dll")
            }
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