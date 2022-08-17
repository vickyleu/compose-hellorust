package com.seiko.transform.action.rust

import org.gradle.api.Plugin
import org.gradle.api.Project

class RustKotlinPlugin : Plugin<Project> {
    override fun apply(target: Project) {
        println("run in transform action!!!!!")
    }
}
