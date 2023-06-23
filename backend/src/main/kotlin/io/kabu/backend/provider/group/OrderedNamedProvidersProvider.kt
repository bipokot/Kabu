package io.kabu.backend.provider.group

import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.provider.provider.ProviderContainer
import io.kabu.backend.util.poet.asCodeBlock

class OrderedNamedProvidersProvider(private val providers: OrderedNamedProviders) : ProviderContainer {

    override val childrenProviders: List<Provider>
        get() = providers.providers

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        val name = providers.getOrNull(provider) ?: return null
        return RetrievalWay(name.asCodeBlock(), isReentrant = true)
    }
}
