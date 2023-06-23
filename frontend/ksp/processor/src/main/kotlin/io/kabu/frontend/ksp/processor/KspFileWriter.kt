package io.kabu.frontend.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import io.kabu.backend.inout.input.writer.FileWriter
import java.io.OutputStream


class KspFileWriter(private val codeGenerator: CodeGenerator) : FileWriter {

    override fun writeFile(packageName: String, filename: String, contents: String) {
        val dependencies = Dependencies(false) // todo revise dependencies
        codeGenerator.createNewFile(dependencies, packageName, filename, extensionName = "kt").use { stream ->
            stream.appendText(contents)
        }
    }

    private fun OutputStream.appendText(str: String) {
        this.write(str.toByteArray())
    }
}
