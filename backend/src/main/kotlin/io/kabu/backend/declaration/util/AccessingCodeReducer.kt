package io.kabu.backend.declaration.util

import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.provider.Provider


class AccessingCodeReducer {

    fun reduceAccessingCode(functionBlockContext: FunctionBlockContext) {
        val rootProvider = functionBlockContext.actualProvidersProvider

        do {
            // for each provider find a list of providers to replace
            // the list may consist of the same examined provider if optimisation is impossible
            val providerReplacementsProviders: Map<Provider, List<Provider>> =
                rootProvider.childrenProviders.associateWith { findReplacementsForProvider(it) }

            var replacementWasPerformed = false
            providerReplacementsProviders.forEach { (provider, replacements) ->
                functionBlockContext.replaceActualProvider(provider, replacements)
                if (listOf(provider) != replacements) replacementWasPerformed = true
            }

        } while (replacementWasPerformed)
    }

    private fun findReplacementsForProvider(provider: Provider): List<Provider> {
        val size = provider.childrenProviders.size
        return if (size != 1) listOf(provider) else provider.childrenProviders
    }
}
