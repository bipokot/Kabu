package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes.RANKING_COMPARISON_INFO_TYPE
import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes.STRICTNESS_COMPARISON_INFO_TYPE
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME


class ComparisonProvider(
    typeNode: TypeNode,
    providers: List<Provider>,
) : AbstractWatchedProvider(typeNode, providers) {

    override fun provideCodeForConstructionFromAux(auxName: String): String {
        val holderClassCanonicalName = (typeNode as HolderTypeNode).rawClassName.canonicalName

        //todo do safeCast method to bring right exceptions to the user
        //todo make holder creation through CodeBlock(%T, className)

        val code = buildString {
            append("$auxName.let{aux->")
            val providerInfoProvider = providers.single { it is OperatorInfoProvider }
            val providerInfoProviderIndex = providers.indexOf(providerInfoProvider)
            for (i in providers.size - 1 downTo 0) {
                if (i == providerInfoProviderIndex) continue
                append("val v$i=$STACK_PROPERTY_NAME.pop() as ${providers[i].type};")
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

        return code
    }
}
