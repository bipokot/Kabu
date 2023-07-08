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
    val watcherContextTypeNode: TypeNode, //todo watcher VS scoping
    analyzer: Analyzer,
    origin: Origin? = null,
) : LambdaProvider(typeNode, returningProvider, analyzer, origin) {

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !== returningProvider) return null

        val contextClassName = (watcherContextTypeNode.typeName as ClassName).canonicalName
        return RetrievalWay("with($contextClassName()){${selfName!!}()}".asCodeBlock(), isReentrant = false)
    }

    override fun isReplacementRequired(): Boolean {
        //todo consider nested provider and Analyzer options to decide whether to perform evaluation of this provider
        return true
    }
}
