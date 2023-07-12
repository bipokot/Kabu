package io.kabu.backend.integration.namegen

class IndexedSequentialTypeNameGenerator(
    private val typeNameGenerator: TypeNameGenerator,
) : SequentialTypeNameGenerator {

    private var index = 1

    override fun generateNextTypeName(): String {
        return typeNameGenerator.generateTypeName(index++)
    }
}
