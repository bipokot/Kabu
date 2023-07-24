package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.asTypeName
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME
import io.kabu.runtime.RankingComparisonInfo
import io.kabu.runtime.StrictnessComparisonInfo


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
            val operatorInfoProvider = providers.single { it is OperatorInfoProvider }
            val operatorInfoProviderIndex = providers.indexOf(operatorInfoProvider)
            for (i in providers.size - 1 downTo 0) {
                if (i == operatorInfoProviderIndex) continue
                append("val v$i=$STACK_PROPERTY_NAME.pop() as ${providers[i].type};")
            }

            append("val v$operatorInfoProviderIndex=")
            when (val type = operatorInfoProvider.type) {
                RankingComparisonInfo::class.asTypeName() -> {
                    append("if(aux)RankingComparisonInfo.GREATER else RankingComparisonInfo.LESS;")
                }

                StrictnessComparisonInfo::class.asTypeName() -> {
                    append("if(aux)StrictnessComparisonInfo.RELAXED else StrictnessComparisonInfo.STRICT;")
                }

                else -> error("Unknown operator info type: $type")
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
