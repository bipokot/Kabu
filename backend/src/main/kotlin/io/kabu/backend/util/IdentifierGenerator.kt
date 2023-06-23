package io.kabu.backend.util

object IdentifierGenerator {

    private val startingSymbolAlphabet = listOf('A'..'Z')
        .map { it.joinToString(separator = "") }.reduce(String::plus)
    
    private val followingSymbolsAlphabet = listOf('A'..'Z', 'a'..'z', '0'..'9')
        .map { it.joinToString(separator = "") }.reduce(String::plus)

    /** Generates an identifier name with at least one symbol length */
    fun generateRandomIdentifierString(length: Int): String = buildString {
        append(startingSymbolAlphabet.random())
        repeat(length - 1) {
            append(followingSymbolsAlphabet.random())
        }
    }
}
