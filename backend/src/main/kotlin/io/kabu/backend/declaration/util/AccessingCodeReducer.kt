package io.kabu.backend.declaration.util

import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.provider.provider.ProviderContainer


class AccessingCodeReducer {

    fun reduceAccessingCode(functionBlockContext: FunctionBlockContext) {
        val rootProvider = functionBlockContext.actualProvidersProvider

        val providerReplacementsProviders: Map<Provider, List<Provider>> =
            rootProvider.childrenProviders.associateWith { findReplacementsForProvider(it, rootProvider) }

        println("BEFORE REDUCE:")
        println(functionBlockContext.joinAllStatements())

        providerReplacementsProviders.forEach { (provider,  replacements) ->
            functionBlockContext.replaceActualProvider(provider, replacements)
        }

        println("AFTER REDUCE:")
        println(functionBlockContext.joinAllStatements())
    }

    private fun findReplacementsForProvider(provider: Provider, providerContainer: ProviderContainer): List<Provider> {
        val size = provider.childrenProviders.size
        if (size != 1) return listOf(provider)

        val childProvider: Provider = provider.childrenProviders.single()
        val selfName = "<dummy>"
        val retrievalWay = provider.getChildRetrievalWay(selfName, childProvider, providerContainer)!!
        return if (retrievalWay.isReentrant) {
            findReplacementsForProvider(childProvider, providerContainer)
        } else {
            listOf(childProvider)
        }
    }

    fun reduceAccessingCodeNew(functionBlockContext: FunctionBlockContext) {
        val rootProvider = functionBlockContext.actualProvidersProvider

        do {
            val providerReplacementsProviders: Map<Provider, List<Provider>> =
                rootProvider.childrenProviders.associateWith { findReplacementsForProviderNew(it, rootProvider) }

            var replacementWasPerformed = false
            providerReplacementsProviders.forEach { (provider, replacements) ->
                functionBlockContext.replaceActualProvider(provider, replacements)
                if (listOf(provider) != replacements) replacementWasPerformed = true
            }
            
            println(functionBlockContext.joinAllStatements())

        } while (replacementWasPerformed)
    }

    private fun findReplacementsForProviderNew(provider: Provider, providerContainer: ProviderContainer): List<Provider> {
        val size = provider.childrenProviders.size
        return if (size != 1) listOf(provider) else provider.childrenProviders
    }
}
