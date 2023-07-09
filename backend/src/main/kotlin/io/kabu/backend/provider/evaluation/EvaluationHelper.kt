package io.kabu.backend.provider.evaluation

import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.provider.group.NamedProvider
import io.kabu.backend.provider.provider.Provider

object EvaluationHelper {

    fun doEvaluation(
        funDeclarationProviders: FunDeclarationProviders,
        codeBlockContext: FunctionBlockContext,
    ) {
        val evaluationOrderedList = funDeclarationProviders.providersList.reversed()
        val invertedOrdering = funDeclarationProviders.invertedOrdering

        renameThisProvider(codeBlockContext)

        // determining list of evaluated providers and their evaluation statements
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
                provider = namedProvider.provider,
                name = namedProvider.name,
                statements = emptyList(), // statements have already been added
            )
        }
    }

    private fun renameThisProvider(codeBlockContext: FunctionBlockContext) {
        codeBlockContext.actualProviders.getOrNull(THIS)?.let { thisProvider ->
            val newName = codeBlockContext.nextVarName()
            codeBlockContext.registerLocalVar(newName)
            codeBlockContext.addStatements(listOf("val $newName = $THIS"))
            codeBlockContext.actualProviders[thisProvider] = newName
        }
    }

    private fun getReplacementProviderInfo(
        functionBlockContext: FunctionBlockContext,
        provider: Provider,
    ): ProviderInfo? {
        var evaluationStatements: String? = null
        var providerName = functionBlockContext.getCodeForActualProvider(provider)
        var replacementProvider = provider

        val replacementWay = provider.getReplacementWay(context = functionBlockContext, providerName)
        if (replacementWay != null) {
            val replacement = replacementWay.provider
            if (replacement.isUsefulTransitively()) {
                providerName = functionBlockContext.getUnoccupiedLocalVarName(replacement.generateName())
                    .also { functionBlockContext.registerLocalVar(it) }
                evaluationStatements = "val $providerName = ${replacementWay.code}"
                replacementProvider = replacement
            }
        }

        return replacementProvider
            .takeIf { it.isUsefulTransitively() }
            ?.let { ProviderInfo(replacementProvider, providerName, evaluationStatements) }
    }

    private data class ProviderInfo(
        val provider: Provider,
        val name: String,
        val statements: String?,
    )

    private const val THIS = "this"
}
