package io.kabu.backend.provider.group

import io.kabu.backend.diagnostic.builder.binaryOperatorRequiredForSetterError
import io.kabu.backend.diagnostic.builder.unexpectedInvertedOrderingError
import io.kabu.backend.provider.provider.Provider

object FunDeclarationProvidersFactory {

    /**
     *      A       B   C
     *      this    B   C
     */
    fun from(
        rawProviders: RawProviders,
        invertedOrdering: Boolean,
        forSetter: Boolean = false,
    ): FunDeclarationProviders {
        val list = rawProviders.providersList
        if (list.isEmpty()) return FunDeclarationProviders(OrderedNamedProviders(), invertedOrdering)

        validate(list, invertedOrdering, forSetter)

        val orderedList = if (invertedOrdering) listOf(list[1], list[0]) else list

        val orderedNamesProviders = OrderedNamedProviders()

        orderedNamesProviders.register(orderedList.first(), RECEIVER_PARAMETER_NAME)

        val nonReceiverParameters = orderedList.drop(1).map { ProviderWithName(it, it.generateName()) }

        if (forSetter) nonReceiverParameters[0].name = SETTER_PARAMETER_NAME

        renameClashingParametersNames(nonReceiverParameters.map { it.name })
            .forEachIndexed { index, edited -> nonReceiverParameters[index].name = edited }

        nonReceiverParameters.forEach { providerWithName ->
            orderedNamesProviders.register(providerWithName.provider, providerWithName.name)
        }

        return FunDeclarationProviders(
            orderedNamesProviders,
            invertedOrdering,
            rawProviders.operatorInfoParameter?.typeNode
        )
    }

    private fun validate(
        list: List<Provider>,
        invertedOrdering: Boolean,
        forSetter: Boolean,
    ) {
        if (invertedOrdering && list.size != 2) {
            unexpectedInvertedOrderingError(list.mapNotNull { it.origin })
        }
        if (forSetter && list.size != 2) {
            binaryOperatorRequiredForSetterError(list.mapNotNull { it.origin })
        }
    }

    private data class ProviderWithName(var provider: Provider, var name: String)

    private const val RECEIVER_PARAMETER_NAME = "this"
    private const val SETTER_PARAMETER_NAME = "value"
}
