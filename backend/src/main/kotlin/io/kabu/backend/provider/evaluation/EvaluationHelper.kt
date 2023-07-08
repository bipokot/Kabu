package io.kabu.backend.provider.evaluation

import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.provider.group.NamedProvider
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.util.ifTake

/** Evaluated parameters inside operator function body */
object EvaluationHelper {

    /**
     * Returns evaluated parameters and statements in their conventional ordering,
     * independent of operator's argument ordering
     */
    fun doEvaluation(
        funDeclarationProviders: FunDeclarationProviders,
        codeBlockContext: FunctionBlockContext,
    ) {
        val evaluationOrderedList = funDeclarationProviders.providersList.reversed()
        val invertedOrdering = funDeclarationProviders.invertedOrdering

        // determining list of evaluated parameters and their evaluation statements
        val evaluatedParametersWithStatements = evaluationOrderedList.mapNotNull {
            getReplacementProviderInfo(codeBlockContext, it)
        }

        // separating evaluation code from resulting order of providers
        val namedProviders = mutableListOf<NamedProvider>()
        val statementsLists = mutableListOf<List<String>>()
        evaluatedParametersWithStatements.forEach { providerInfo ->
            namedProviders += NamedProvider(providerInfo.provider, providerInfo.name)
            statementsLists += listOfNotNull(providerInfo.statements)
        }

        // adding statements of evaluation
        statementsLists.forEach {
            codeBlockContext.addStatements(it)
        }

        // obtaining "conventional ordering" from "evaluation ordering"
        if (!invertedOrdering) namedProviders.reverse()

        // making all existing providers obsolete
        evaluationOrderedList.forEach {
            codeBlockContext.unregisterActualProvider(it)
        }

        // adding current actual providers
        namedProviders.forEach { namedProvider ->
            codeBlockContext.registerActualProvider(
                parameter = namedProvider.provider,
                name = namedProvider.name,
                statements = emptyList(), // statements have already been added
            )
        }
    }

    private fun getReplacementProviderInfo(
        functionBlockContext: FunctionBlockContext,
        provider: Provider,
    ): ProviderInfo? {
        var evaluationStatements: String? = null
        var providerName = functionBlockContext.getCodeForActualProvider(provider)

        val renamingStatement = ifTake(providerName == THIS) {
            providerName = functionBlockContext.nextVarName()
            "val $providerName = $THIS"
        }

        val replacementWay = provider.getReplacementWay(context = functionBlockContext, providerName)
        val replacementProvider = if (replacementWay != null) {
            val replacement = replacementWay.provider
            if (replacement.isUsefulTransitively()) {
                providerName = functionBlockContext.getUnoccupiedLocalVarName(replacement.generateName())
                    .also { functionBlockContext.registerLocalVar(it) }
                val renamingStatementString = if (renamingStatement != null) "$renamingStatement; " else ""
                evaluationStatements = "${renamingStatementString}val $providerName = ${replacementWay.code}"

                replacement
            } else null
        } else {
            if (renamingStatement != null) evaluationStatements = renamingStatement
            provider.takeIf { it.isUsefulTransitively() }
        }

        return replacementProvider
            ?.let { ProviderInfo(replacementProvider, providerName, evaluationStatements) }
    }

    private data class ProviderInfo(
        val provider: Provider,
        val name: String,
        val statements: String?,
    )

    private const val THIS = "this"
}
