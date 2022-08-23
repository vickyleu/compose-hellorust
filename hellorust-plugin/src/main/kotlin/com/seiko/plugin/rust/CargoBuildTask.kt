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
        execRustcTarget(toolchain, cargoExtension)
        when (val toolchain = toolchain) {
            is Toolchain.Android -> execRustAndroid(toolchain, cargoExtension)
            is Toolchain.Jvm -> execRustJvm(toolchain, cargoExtension)
            is Toolchain.Darwin -> execRustDarwin(toolchain, cargoExtension)
            is Toolchain.IOS -> execRustIOS(toolchain, cargoExtension)
        }
    }

    private fun Project.execRustAndroid(
        toolchain: Toolchain.Android,
        cargoExtension: CargoExtension,
    ) {
        val moduleDir = getModuleDir(toolchain, cargoExtension)
        exec {
            workingDir = moduleDir
            commandLine = buildList {
                add(cargoExtension.cargoCommand)
                add(cargoExtension.ndkCommand)
                toolchain.abiFilters.forEach { abi ->
                    add("-t")
                    add(abi)
                }
                add("-o")
                add(getDir(cargoExtension.androidJniDir).absolutePath)
                add("build")
                addCommonArgs(cargoExtension)
            }
        }
    }

    private fun Project.execRustJvm(
        toolchain: Toolchain.Jvm,
        cargoExtension: CargoExtension,
    ) {
        val target = toolchain.targets.first()
        execRustBuild(
            toolchain = toolchain,
            cargoExtension = cargoExtension,
            target = target,
        )
        execMoveLib(
            toolchain = toolchain,
            cargoExtension = cargoExtension,
            target = target,
            intoDir = File(getDir(cargoExtension.jvmJniDir), target.split('-').first()),
        )
    }

    private fun Project.execRustDarwin(
        toolchain: Toolchain.Darwin,
        cargoExtension: CargoExtension,
    ) {
        val target = toolchain.targets.first()
        execRustBuild(
            toolchain = toolchain,
            cargoExtension = cargoExtension,
            target = target,
        )
        execMoveLib(
            toolchain = toolchain,
            cargoExtension = cargoExtension,
            target = target,
            intoDir = File(getDir(cargoExtension.cinteropDir), cargoExtension.libName),
        )
    }

    private fun Project.execRustIOS(
        toolchain: Toolchain.IOS,
        cargoExtension: CargoExtension,
    ) {
        val target = toolchain.targets.first()
        execRustBuild(
            toolchain = toolchain,
            cargoExtension = cargoExtension,
            target = target,
        )
        execMoveLib(
            toolchain = toolchain,
            cargoExtension = cargoExtension,
            target = target,
            intoDir = File(getDir(cargoExtension.cinteropDir), cargoExtension.libName),
        )
    }

    private fun Project.execMoveLib(
        toolchain: Toolchain,
        cargoExtension: CargoExtension,
        target: String,
        intoDir: File,
    ) {
        val moduleDir = getModuleDir(toolchain, cargoExtension)
        val fromDir = File(moduleDir, "target/$target/${cargoExtension.profile}")
        delete {
            delete(intoDir)
        }
        copy {
            from(fromDir)
            into(intoDir)
            val libName = cargoExtension.libName
            val isStaticLib = when (toolchain) {
                is Toolchain.Android, is Toolchain.Jvm -> cargoExtension.isJniStaticLib
                is Toolchain.IOS, is Toolchain.Darwin -> cargoExtension.isNativeStaticLib
            }
            if (isStaticLib) {
                include("lib$libName.a")
                include("lib$libName.lib")
            } else {
                include("lib$libName.so")
                include("lib$libName.dylib")
                include("$libName.dll")
            }
        }
    }

    private fun Project.execRustcTarget(
        toolchain: Toolchain,
        cargoExtension: CargoExtension,
    ) {
        exec {
            commandLine = buildList {
                add(cargoExtension.rustUpCommand)
                add("target")
                add("add")
                addAll(toolchain.targets)
            }
        }
    }

    private fun Project.execRustBuild(
        toolchain: Toolchain,
        cargoExtension: CargoExtension,
        target: String,
    ) {
        exec {
            workingDir = getModuleDir(toolchain, cargoExtension)
            commandLine = buildList {
                add(cargoExtension.cargoCommand)
                add("build")
                add("--target")
                add(target)
                addCommonArgs(cargoExtension)
            }
        }
    }

    private fun getModuleDir(toolchain: Toolchain, cargoExtension: CargoExtension): File {
        val libName = cargoExtension.libName + when (toolchain) {
            is Toolchain.Android, is Toolchain.Jvm -> cargoExtension.jniSuffix
            is Toolchain.IOS, is Toolchain.Darwin -> cargoExtension.nativeSuffix
        }
        return File(getDir(cargoExtension.module), libName)
    }

    private fun getDir(path: String): File {
        val file = File(path)
        return if (file.isAbsolute) file else {
            File(project.projectDir, file.path)
        }.canonicalFile
    }

    private fun <T> buildList(block: MutableList<T>.() -> Unit) = mutableListOf<T>().apply(block)

    private fun MutableList<String>.addCommonArgs(cargoExtension: CargoExtension) {
        if (cargoExtension.isVerbose) {
            add("--verbose")
        }
        if (cargoExtension.profile != "debug") {
            add("--${cargoExtension.profile}")
        }
    }
}
