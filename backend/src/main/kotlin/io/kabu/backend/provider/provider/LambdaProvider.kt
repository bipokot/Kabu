package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.poet.asCodeBlock


open class LambdaProvider(
    typeNode: TypeNode,
    val returningProvider: Provider,
    val analyzer: Analyzer,
    origin: Origin? = null,
) : BaseProvider(typeNode, origin) {

    override fun generateName(): String {
        return returningProvider.generateName() + "Lambda"
    }

    override val childrenProviders: List<Provider>
        get() = listOf(returningProvider)

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !== returningProvider) return null

        return RetrievalWay("${selfName!!}()".asCodeBlock(), isReentrant = false)
    }

    override fun isReplacementRequired(): Boolean {
        val hasChildRequiredToMandatoryEval = findProvider { it.isReplacementRequired() } != null
        return (hasChildRequiredToMandatoryEval || !analyzer.postponeLambdaExecution)
    }
}
