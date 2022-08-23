package com.seiko.plugin.rust

sealed class Toolchain(
    val name: String,
    val targets: Set<String>
) {

    class Android(
        name: String,
        targets: Set<String>,
        val abiFilters: Set<String>,
    ) : Toolchain(
        name = name,
        targets = targets,
    )

    class IOS(
        name: String,
        targets: Set<String>,
    ) : Toolchain(
        name = name,
        targets = targets,
    )

    class Jvm(
        name: String,
        targets: Set<String>,
    ) : Toolchain(
        name = name,
        targets = targets,
    )

    class Darwin(
        name: String,
        targets: Set<String>,
    ) : Toolchain(
        name = name,
        targets = targets,
    )
}
