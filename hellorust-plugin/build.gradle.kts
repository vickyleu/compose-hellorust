plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

dependencies {
    implementation("com.android.tools.build:gradle:7.2.2")
    implementation(kotlin("gradle-plugin", version = "1.7.10"))
}

gradlePlugin {
    plugins {
            register("HelloRustPlugin") {
            id = "com.seiko.plugin.rust"
            implementationClass = "com.seiko.plugin.rust.RustKotlinPlugin"
        }
    }
}
