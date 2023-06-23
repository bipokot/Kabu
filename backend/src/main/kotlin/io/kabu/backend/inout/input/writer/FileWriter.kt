package io.kabu.backend.inout.input.writer


interface FileWriter {

    fun writeFile(packageName: String, filename: String, contents: String)
}
