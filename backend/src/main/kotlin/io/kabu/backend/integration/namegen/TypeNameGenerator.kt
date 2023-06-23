package io.kabu.backend.integration.namegen


interface TypeNameGenerator {

    fun generateTypeName(attempt: Int): String
}
