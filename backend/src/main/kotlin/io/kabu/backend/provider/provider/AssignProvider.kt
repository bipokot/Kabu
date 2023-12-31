package io.kabu.backend.provider.provider

import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME


class AssignProvider(
    typeNode: TypeNode,
    providers: List<Provider>,
) : AbstractWatchedProvider(typeNode, providers) {

    override fun provideCodeForConstructionFromAux(auxName: String): String {
        val holderClassCanonicalName = (typeNode as HolderTypeNode).rawClassName.canonicalName

        //todo use FieldAccessCodeGenerator(analyzer).generateFieldAccessorCode(selfName!!, privateFieldName)

        val code = buildString {
            append("$auxName.let{aux->")

            // extracting values from stack
            for (i in providers.size - 1 downTo 0) {
                val provider = providers[i]
                append("val v$i=safeCast<${provider.type}>($STACK_PROPERTY_NAME.pop());")
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
