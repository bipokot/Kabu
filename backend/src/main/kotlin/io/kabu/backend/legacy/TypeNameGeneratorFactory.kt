package io.kabu.backend.legacy

import io.kabu.backend.integration.namegen.IndexedSequentialTypeNameGenerator
import io.kabu.backend.integration.namegen.IndexedTypeNameGenerator
import io.kabu.backend.integration.namegen.SequentialTypeNameGenerator


object TypeNameGeneratorFactory {

    fun create(): SequentialTypeNameGenerator =
        IndexedSequentialTypeNameGenerator(IndexedTypeNameGenerator("H"))
}
