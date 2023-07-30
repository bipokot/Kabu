package io.kabu.backend.legacy

import io.kabu.backend.integration.namegen.IndexedSequentialTypeNameGenerator
import io.kabu.backend.integration.namegen.PrefixedTypeNameGenerator
import io.kabu.backend.integration.namegen.SequentialTypeNameGenerator


object TypeNameGeneratorFactory {

    fun create(): SequentialTypeNameGenerator =
        IndexedSequentialTypeNameGenerator(PrefixedTypeNameGenerator("H"))
}
