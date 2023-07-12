package io.kabu.backend.integration.namegen

class PrefixedTypeNameGenerator(private val prefix: String) : TypeNameGenerator {

    override fun generateTypeName(counter: Int): String {
        require(counter >= 0)
        return if (counter == 0) prefix
        else prefix + counter
    }
}
