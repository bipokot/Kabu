package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.ReplacementProviderWithCode
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME


class AssignProvider(
    typeNode: TypeNode,
    providers: List<Provider>,
    analyzer: Analyzer,
) : AbstractWatchedProvider(typeNode, providers, analyzer) {

    override fun provideCodeForConstructionFromAux(auxName: String): String {
        val holderClassCanonicalName = (typeNode as HolderTypeNode).className.canonicalName

        //todo use FieldAccessCodeGenerator(analyzer).generateFieldAccessorCode(selfName!!, privateFieldName)

        val code = buildString {
            append("$auxName.let{aux->")

            // extracting values from stack
            for (i in providers.size - 1 downTo 0) {
                val provider = providers[i]
                append("val v$i=$STACK_PROPERTY_NAME.pop() as ${provider.type};")
            }

            // constructor invocation
            append("$holderClassCanonicalName(")
            append(List(providers.size) { "v$it" }.joinToString(","))
            append(")")

            append("}")
        }

        return code
    }
}
