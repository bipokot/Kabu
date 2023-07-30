package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.CodeBlock
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.decaps


class WatcherContextProvider(
    typeNode: TypeNode,
    private val childProvider: Provider,
    private val analyzer: Analyzer,
    origin: Origin? = null,
) : AbstractProvider(typeNode, origin), Provider {

    override fun generateName(): String {
        return typeNode.name.decaps()
    }

    override val childrenProviders: List<Provider>
        get() = listOf(childProvider)

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !== childProvider) return null

        val code = "(${selfName!!}.stack.pop() as ${provider.type})"
        return RetrievalWay(CodeBlock.of(code), isReentrant = false)
    }

    override fun isReplacementRequired() = true
}
