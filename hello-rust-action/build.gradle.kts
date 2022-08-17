plugins {
    `java-gradle-plugin`
    `kotlin-dsl`
}

repositories {
    mavenCentral()
    google()
}

gradlePlugin {
    plugins {
        register("com.seiko.transform.action.rust") {
            id = "com.seiko.transform.action.rust"
            implementationClass = "com.seiko.transform.action.rust.RustKotlinPlugin"
        }
    }
}
