package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.CodeBlock
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.decaps


class WatcherContextProvider(
    typeNode: TypeNode,
    private val childProvider: Provider,
    private val analyzer: Analyzer,
    origin: Origin? = null,
) : BaseProvider(typeNode, origin), Provider {

    override fun getProviderName(): String {
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

//        val privateFieldName = fields[provider]
//        val code = FieldAccessCodeGenerator(analyzer).generateFieldAccessorCode(selfName!!, privateFieldName)

        val code = "(${selfName!!}.stack.pop() as ${provider.type})"
        return RetrievalWay(CodeBlock.of(code), isReentrant = false)
    }

    override fun getEvaluationRequirement(): EvaluationRequirement =
        EvaluationRequirement.MANDATORY
}
