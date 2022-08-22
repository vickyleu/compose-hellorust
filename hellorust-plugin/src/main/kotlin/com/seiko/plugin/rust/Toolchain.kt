package com.seiko.plugin.rust

sealed class Toolchain(
    val name: String,
    val targets: Set<String>
) {

    class Android(
        targets: Set<String>,
        val abiFilters: Set<String>,
    ) : Toolchain(
        name = "Android",
        targets = targets,
    )

    class IOS(
        targets: Set<String>,
    ) : Toolchain(
        name = "IOS",
        targets = targets,
    )

    class Jvm(
        targets: Set<String>,
    ) : Toolchain(
        name = "Jvm",
        targets = targets,
    )

    class Darwin(
        targets: Set<String>,
    ) : Toolchain(
        name = "Darwin",
        targets = targets,
    )
}
