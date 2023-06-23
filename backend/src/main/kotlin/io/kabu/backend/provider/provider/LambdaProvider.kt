package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationCode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ProviderWithEvaluationCode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.poet.asCodeBlock


open class LambdaProvider(
    typeNode: TypeNode,
    val returningProvider: Provider,
    val analyzer: Analyzer,
    origin: Origin? = null,
) : BaseProvider(typeNode, origin) {

    override fun getEvaluationWay(context: FunctionBlockContext, forName: String): ProviderWithEvaluationCode {
        if (analyzer.postponeLambdaExecution) return super.getEvaluationWay(context, forName)

        val providerToObtain = findNearestNotEvaluatedProvider()
        val code = getChildRetrievalWay(forName, providerToObtain, context.actualProvidersProvider)!!.codeBlock.toString()
        return ProviderWithEvaluationCode(providerToObtain, EvaluationCode.Code(code))
    }

    override fun getProviderName(): String {
        return returningProvider.getProviderName() + "Lambda"
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

    override fun getEvaluationRequirement(): EvaluationRequirement {
        val hasChildRequiredToMandatoryEval =
            findProvider { it.getEvaluationRequirement() == EvaluationRequirement.MANDATORY } != null
        return if (hasChildRequiredToMandatoryEval) {
            EvaluationRequirement.MANDATORY
        } else {
            EvaluationRequirement.NONE
        }
    }
}
