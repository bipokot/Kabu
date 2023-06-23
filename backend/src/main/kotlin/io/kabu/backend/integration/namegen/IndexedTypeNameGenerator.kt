package io.kabu.backend.integration.namegen

class IndexedTypeNameGenerator(private val prefix: String) : TypeNameGenerator {

    override fun generateTypeName(attempt: Int): String {
        require(attempt >= 0)
        return if (attempt == 0) prefix
        else prefix + attempt
    }
}
