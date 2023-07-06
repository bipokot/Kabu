package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.CodeBlock
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationCode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ProviderWithEvaluationCode
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
        val code = providerWithEvaluationCode.code as EvaluationCode.Code
        return RetrievalWay(CodeBlock.of(code.code), isReentrant = false)
    }

    override fun getEvaluationRequirement(): EvaluationRequirement =
        EvaluationRequirement.MANDATORY

    override fun getEvaluationWay(context: FunctionBlockContext, forName: String): ProviderWithEvaluationCode {
        val code = getChildRetrievalWay(forName, watchedProvider, context.actualProvidersProvider)!!.codeBlock.toString()
        return ProviderWithEvaluationCode(watchedProvider, EvaluationCode.Code(code))
    }

    override fun generateName(): String {
        return watchedProvider.generateName() + "Aux"
    }
}
