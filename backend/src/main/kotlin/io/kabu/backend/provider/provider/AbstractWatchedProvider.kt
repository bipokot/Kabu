package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode


abstract class AbstractWatchedProvider(
    typeNode: TypeNode,
    val providers: List<Provider>,
    val analyzer: Analyzer,
    origin: Origin? = null,
) : HolderProvider(typeNode, providers, analyzer, origin)
