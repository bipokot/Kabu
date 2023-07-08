package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.CodeBlock
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.RetrievalWay


/**
 * Value to compose a [watchedProvider].
 * It should have access to [WatcherContextProvider] to compose code for [watchedProvider].
 */
class AuxProvider(
    typeNode: TypeNode,
    private val watchedProvider: AbstractWatchedProvider,
    origin: Origin? = null,
) : BaseProvider(typeNode, origin) {

    override val childrenProviders: List<Provider> = listOf(watchedProvider)

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !== watchedProvider) return null

        val watcherContextName = ""

        val providerWithEvaluationCode =
            watchedProvider.provideCodeForConstructionFromAux(selfName!!, watcherContextName)!!
        val code = providerWithEvaluationCode.code
        return RetrievalWay(CodeBlock.of(code), isReentrant = false)
    }

    override fun isReplacementRequired(): EvaluationRequirement =
        EvaluationRequirement.MANDATORY

    override fun generateName(): String {
        return watchedProvider.generateName() + "Aux"
    }
}
