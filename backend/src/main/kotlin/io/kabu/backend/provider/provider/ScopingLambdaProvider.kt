package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.ClassName
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.poet.asCodeBlock


class ScopingLambdaProvider(
    typeNode: TypeNode,
    returningProvider: Provider,
    val contextTypeNode: TypeNode,
    analyzer: Analyzer,
    origin: Origin? = null,
) : LambdaProvider(typeNode, returningProvider, analyzer, origin) {

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !== returningProvider) return null

        val contextClassName = (contextTypeNode.typeName as ClassName).canonicalName
        return RetrievalWay("with($contextClassName()){${selfName!!}()}".asCodeBlock(), isReentrant = false)
    }
}
