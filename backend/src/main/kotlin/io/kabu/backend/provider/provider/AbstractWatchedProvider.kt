package io.kabu.backend.provider.provider

import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode


abstract class AbstractWatchedProvider(
    typeNode: TypeNode,
    val providers: List<Provider>,
    origin: Origin? = null,
) : HolderProvider(typeNode, providers, origin) {

    //todo introduce `class VariableName(val value: String)` / `class Code(val value: String)`
    abstract fun provideCodeForConstructionFromAux(auxName: String): String
}
