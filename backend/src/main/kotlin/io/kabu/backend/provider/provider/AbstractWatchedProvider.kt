package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.ProviderWithEvaluationCode


abstract class AbstractWatchedProvider(
    typeNode: TypeNode,
    val providers: List<Provider>,
    val analyzer: Analyzer,
    origin: Origin? = null,
) : HolderProvider(typeNode, providers, analyzer, origin) {

    abstract fun provideCodeForConstructionFromAux(
        auxName: String, //todo introduce `class VariableName(val value: String)` / `class Code(val value: String)`
        watcherContextName: String,
    ): ProviderWithEvaluationCode?
}
