package io.kabu.backend.inout.input.writer

import java.io.File


open class SimpleFileWriter(private val rootPath: String) : FileWriter {

    override fun writeFile(packageName: String, filename: String, contents: String) {
        val packagesPath = packageName.replace('.', '/')
        val newFilename = "$filename.kt"

        val directory = File("$rootPath/$packagesPath")
        directory.mkdirs()

        File(directory, newFilename).writeText(contents)
    }
}
