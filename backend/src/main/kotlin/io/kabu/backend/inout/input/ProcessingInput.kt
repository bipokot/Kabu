package io.kabu.backend.inout.input

import io.kabu.backend.inout.input.method.ContextCreatorMethod
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.inout.input.method.LocalPatternMethod
import io.kabu.backend.inout.input.writer.FileWriter

/**
 * Interface between frontend and backend processors
 */
data class ProcessingInput(
    val fileWriter: FileWriter,
    val globalPatterns: List<GlobalPatternMethod>,
    val localPatterns: List<LocalPatternMethod>,
    val contextCreators: List<ContextCreatorMethod>,
)
