package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.CodeBlock
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.legacy.planner.FieldAccessCodeGenerator
import io.kabu.backend.legacy.planner.HolderFieldsNamesGenerator.getNameForIndex
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.provider.group.OrderedNamedProviders
import io.kabu.backend.util.decaps


open class HolderProvider(
    typeNode: TypeNode,
    private val providers: List<Provider>,
    private val analyzer: Analyzer, //todo remove property
    origin: Origin? = null,
) : BaseProvider(typeNode, origin), Provider {

    private val fields: OrderedNamedProviders = OrderedNamedProviders().apply {
        this@HolderProvider.providers.forEachIndexed { index, provider -> register(provider, getNameForIndex(index)) }
    }

    override fun getProviderName(): String {
        return typeNode.name.decaps()
    }

    override val childrenProviders: List<Provider>
        get() = fields.providers

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !in childrenProviders) return null

        val privateFieldName = fields[provider]
        val code = FieldAccessCodeGenerator(typeNode.namespaceNode!!)
            .generateFieldAccessorCode(selfName!!, privateFieldName)

        return RetrievalWay(CodeBlock.of(code), isReentrant = true)
    }
}
