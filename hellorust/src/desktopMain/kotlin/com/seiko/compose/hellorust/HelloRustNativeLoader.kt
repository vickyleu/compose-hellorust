package com.seiko.compose.hellorust

import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import java.util.Locale

@Suppress("UnsafeDynamicallyLoadedCode") // Only loading from our own JAR contents.
internal actual fun loadNativeLibrary() {
    val osName = System.getProperty("os.name").lowercase(Locale.US)
    val osArch = System.getProperty("os.arch").lowercase(Locale.US)
    val nativeLibraryJarPath = when {
        osName.contains("linux") -> "/jni/$osArch/libhellorust.so"
        osName.contains("mac") -> "/jni/$osArch/libhellorust.dylib"
        else -> throw IllegalStateException("Unsupported OS: $osName")
    }

    val nativeLibraryUrl = HelloRust::class.java.getResource(nativeLibraryJarPath)
        ?: throw IllegalStateException("Unable to read $nativeLibraryJarPath from JAR")

    val nativeLibraryFile: Path
    try {
        nativeLibraryFile = Files.createTempFile("hellorust", null)

        // File-based deleteOnExit() uses a special internal shutdown hook that always runs last.
        nativeLibraryFile.toFile().deleteOnExit()
        nativeLibraryUrl.openStream().use { nativeLibrary ->
            Files.copy(nativeLibrary, nativeLibraryFile, StandardCopyOption.REPLACE_EXISTING)
        }
    } catch (e: IOException) {
        throw RuntimeException("Unable to extract native library from JAR", e)
    }
    System.load(nativeLibraryFile.toAbsolutePath().toString())
}
