package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.CodeBlock
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.RetrievalWay


/**
 * Value to compose a [watchedProvider].
 * It should have access to [WatcherContextProvider] to compose code for [watchedProvider].
 */
class AuxProvider(
    typeNode: TypeNode,
    private val watchedProvider: AbstractWatchedProvider,
    origin: Origin? = null,
) : BaseProvider(typeNode, origin) {

    override val childrenProviders: List<Provider> = listOf(watchedProvider)

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !== watchedProvider) return null

        val watchedProviderCreationCode = watchedProvider.provideCodeForConstructionFromAux(selfName!!)
        return RetrievalWay(CodeBlock.of(watchedProviderCreationCode), isReentrant = false)
    }

    override fun isReplacementRequired() = true

    override fun generateName(): String {
        return watchedProvider.generateName() + "Aux"
    }
}
