package io.kabu.backend.provider.provider

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationCode
import io.kabu.backend.provider.evaluation.ProviderWithEvaluationCode
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME


class AssignProvider(
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
        val stack = if (watcherContextName.isNotEmpty()) {
            // outside of context - explicit context name required
            "$watcherContextName.$STACK_PROPERTY_NAME"
        } else {
            // inside of context - no explicit context name required
            STACK_PROPERTY_NAME
        }

        val code = buildString {
            append("$auxName.let{aux->")

            // extracting values from stack
            for (i in providers.size - 1 downTo 0) {
                val provider = providers[i]
                append("val v$i=$stack.pop() as ${provider.type};")
            }

            // constructor invocation
            append("$holderClassCanonicalName(")
            append(List(providers.size) { "v$it" }.joinToString(","))
            append(")")

            append("}")
        }

        return ProviderWithEvaluationCode(this, EvaluationCode.Code(code))
    }
}
