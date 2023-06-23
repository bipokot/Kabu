package io.kabu.backend.parser

import io.kabu.backend.diagnostic.Origin


data class PatternString( //todo HasOrigin ?
    val pattern: String,
    val origin: Origin? = null
)
