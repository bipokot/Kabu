package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.ReplacementProviderWithCode
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME


class InclusionProvider(
    typeNode: TypeNode,
    providers: List<Provider>,
    analyzer: Analyzer,
) : AbstractWatchedProvider(typeNode, providers, analyzer) {

    override fun provideCodeForConstructionFromAux(auxName: String): String {
        val holderClassCanonicalName = (typeNode as HolderTypeNode).className.canonicalName

        //todo use FieldAccessCodeGenerator(analyzer).generateFieldAccessorCode(selfName!!, privateFieldName)

        val code = buildString {
            append("$auxName.let{aux->")
            val providerInfoProviderIndex = providers.indexOfFirst { it is OperatorInfoProvider }
            for (i in providers.size - 1 downTo 0) {
                if (i == providerInfoProviderIndex) continue
                append("val v$i=$STACK_PROPERTY_NAME.pop() as ${providers[i].type};")
            }
            append("val v$providerInfoProviderIndex=if(aux)InclusionInfo.IN else InclusionInfo.NOT_IN;")
            append("$holderClassCanonicalName(")
            val holderArguments = List(providers.size) { index -> "v$index" }.joinToString(",")
            append(holderArguments)
            append(")")
            append("}")
        }

        return code
    }
}
