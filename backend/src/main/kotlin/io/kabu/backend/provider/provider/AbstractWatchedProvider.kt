package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.ReplacementProviderWithCode


abstract class AbstractWatchedProvider(
    typeNode: TypeNode,
    val providers: List<Provider>,
    val analyzer: Analyzer,
    origin: Origin? = null,
) : HolderProvider(typeNode, providers, analyzer, origin) {

    //todo introduce `class VariableName(val value: String)` / `class Code(val value: String)`
    abstract fun provideCodeForConstructionFromAux(auxName: String): String
}
