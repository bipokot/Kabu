package io.kabu.backend.provider.group

import io.kabu.backend.diagnostic.builder.unexpectedInvertedOrderingError
import io.kabu.backend.integration.NameAndType
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.provider.NoReceiverProvider
import io.kabu.backend.provider.provider.Provider

/**
 * Parameters for signature of an operator function.
 * Appear in the list in an order corresponding to function signature
 * First provider in the list represents receiver of property/function.
 * TODO be able to compose fun/prop signature with provided FunDeclarationProviders and fun/property name?
 */
class FunDeclarationProviders(
    val providers: OrderedNamedProviders,
    val invertedOrdering: Boolean,
    val operatorInfoType: TypeNode? = null, //todo revise
) {

    init {
        if (invertedOrdering && providers.providers.size != 2) {
            unexpectedInvertedOrderingError(providers.providers.mapNotNull { it.origin })
        }
    }

    val providersList: List<Provider>
        get() = providers.providers

    val namedTypeNodes: List<NameAndType>
        get() = providers.nameAndTypes

    val left: Provider get() = providersList.first()
    val right: List<Provider> get() = providersList.drop(1)

    val leftNamedProvider: NamedProvider
        get() = NamedProvider(left, providers[left])

    val rightNamedProviders: OrderedNamedProviders
        get() = OrderedNamedProviders().apply {
            right.forEach { provider ->
                val name = this@FunDeclarationProviders.providers[provider]
                register(provider, name)
            }
        }

    val receiverProvider: Provider?
        get() = providersList.firstOrNull().takeUnless { it is NoReceiverProvider }

    override fun toString() = providersList.toString()
}
