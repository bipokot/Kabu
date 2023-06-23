package io.kabu.backend.integration.namegen


interface SequentialTypeNameGenerator {

    fun generateNextTypeName(): String
}
