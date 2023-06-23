package io.kabu.backend.provider.group

import io.kabu.backend.provider.provider.Provider

/** Parameters obtained from sub-expressions 'as-is' */
class RawProviders(
    val providersList: List<Provider>,
    val operatorInfoParameter: Provider?,
) {

    val left: Provider
        get() = providersList.first()

    val right: List<Provider>
        get() = providersList.drop(1)

    override fun toString() = providersList.toString()
}
