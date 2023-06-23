package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes.RANKING_COMPARISON_INFO_TYPE
import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes.STRICTNESS_COMPARISON_INFO_TYPE
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationCode
import io.kabu.backend.provider.evaluation.ProviderWithEvaluationCode
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME


class ComparisonProvider(
    typeNode: TypeNode,
    providers: List<Provider>,
    analyzer: Analyzer,
) : AbstractWatchedProvider(typeNode, providers, analyzer) {

    override fun provideCodeForConstructionFromAux(
        auxName: String,
        watcherContextName: String,
    ): ProviderWithEvaluationCode {
        val holderClassCanonicalName = (typeNode as HolderTypeNode).className.canonicalName

        //todo use FieldAccessCodeGenerator(analyzer).generateFieldAccessorCode(selfName!!, privateFieldName)
        //todo watcherContextName is unused
        val stack = if (watcherContextName.isNotEmpty()) {
            // outside of context - explicit context name required
            "$watcherContextName.$STACK_PROPERTY_NAME"
        } else {
            // inside of context - no explicit context name required
            STACK_PROPERTY_NAME
        }

        //todo do safeCast method to bring right exceptions to the user

        val code = buildString {
            append("$auxName.let{aux->")
            val providerInfoProvider = providers.single { it is OperatorInfoProvider }
            val providerInfoProviderIndex = providers.indexOf(providerInfoProvider)
            for (i in providers.size - 1 downTo 0) {
                if (i == providerInfoProviderIndex) continue
                append("val v$i=$stack.pop() as ${providers[i].type};")
            }

            append("val v$providerInfoProviderIndex=")
            when (providerInfoProvider.type) {
                RANKING_COMPARISON_INFO_TYPE -> {
                    append("if(aux)RankingComparisonInfo.GREATER else RankingComparisonInfo.LESS;")
                }

                STRICTNESS_COMPARISON_INFO_TYPE -> {
                    append("if(aux)StrictnessComparisonInfo.RELAXED else StrictnessComparisonInfo.STRICT;")
                }

                else -> TODO()
            }
            append("$holderClassCanonicalName(")
            val holderArguments = List(providers.size) { index -> "v$index" }.joinToString(",")
            append(holderArguments)
            append(")")
            append("}")
        }

        return ProviderWithEvaluationCode(this, EvaluationCode.Code(code))
    }
}
