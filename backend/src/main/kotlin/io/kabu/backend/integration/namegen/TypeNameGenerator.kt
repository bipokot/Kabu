package io.kabu.backend.integration.namegen


interface TypeNameGenerator {

    fun generateTypeName(counter: Int): String
}
