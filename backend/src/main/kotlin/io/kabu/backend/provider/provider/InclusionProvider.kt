package io.kabu.backend.provider.provider

import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME


class InclusionProvider(
    typeNode: TypeNode,
    providers: List<Provider>,
) : AbstractWatchedProvider(typeNode, providers) {

    override fun provideCodeForConstructionFromAux(auxName: String): String {
        val holderClassCanonicalName = (typeNode as HolderTypeNode).rawClassName.canonicalName

        //todo use FieldAccessCodeGenerator(analyzer).generateFieldAccessorCode(selfName!!, privateFieldName)

        val code = buildString {
            append("$auxName.let{aux->")
            val providerInfoProviderIndex = providers.indexOfFirst { it is OperatorInfoProvider }
            for (i in providers.size - 1 downTo 0) {
                if (i == providerInfoProviderIndex) continue
                append("val v$i=safeCast<${providers[i].type}>($STACK_PROPERTY_NAME.pop());")
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
