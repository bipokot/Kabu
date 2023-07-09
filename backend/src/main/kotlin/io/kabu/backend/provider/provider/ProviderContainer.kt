package io.kabu.backend.provider.provider

import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.poet.asCodeBlock

/**
 * Container for providers (may not be a runtime object).
 * Knows how to access other providers.
 */
interface ProviderContainer {

    /**
     * Providers which this provider container can access to.
     * Providers are in natural pattern order.
     */
    val childrenProviders: List<Provider>

    fun hasProviderRecursively(provider: Provider): Boolean {
        return childrenProviders.any {
            it === provider || it.hasProviderRecursively(provider)
        }
    }

    /**
     * Returns first provider which satisfies the condition
     */
    fun findProvider(condition: (Provider) -> Boolean): Provider? {
        childrenProviders.forEach { child ->
            if (condition(child)) return child
            val found = child.findProvider(condition)
            if (found != null) return found
        }

        return null
    }

    /**
     * Returns all providers which satisfy the condition in following order: "parent first" (Parent -> Left -> Right)
     */
    fun findProviders(condition: (Provider) -> Boolean): List<Provider> {
        return childrenProviders.flatMap { child ->
            val result: MutableList<Provider> = if (condition(child)) mutableListOf(child) else mutableListOf()
            result += child.findProviders(condition)
            result
        }
    }

    /**
     * Returns retrieval way for immediate child.
     * Returns null if passed [provider] is not an immediate child of this provider
     *
     * @param selfName name under which *this* container is accessible (null - if it is not accessible) //todo container?
     * @param provider target immediate child provider
     * @param providerContainer accessible container (e.g. function block context)
     */
    fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer? = null,
    ): RetrievalWay?

    /**
     * Returns retrieval way for child (including non-immediate children).
     * Returns null if passed [provider] can not be retrieved by this provider
     *
     * @param selfName name under which *this* container is accessible (null - if it is not accessible) //todo container?
     * @param provider target child provider
     * @param providerContainer accessible container (e.g. function block context)
     */
    fun getChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer? = null,
    ): RetrievalWay? {
        val path = getPathToProvider(provider) ?: return null

        var allLinksAreReentrant = true
        var currentRetrievalCode = selfName

        // performing "THIS -> path[0]" step
        val firstStep = getImmediateChildRetrievalWay(currentRetrievalCode, path[0], providerContainer)!!
        currentRetrievalCode = firstStep.codeBlock.toString()
        allLinksAreReentrant = allLinksAreReentrant && firstStep.isReentrant

        // performing "path[i] -> path[i+1]" steps
        for (i in 0 until path.size - 1) {
            val current = path[i]
            val next = path[i + 1]
            val step = current.getImmediateChildRetrievalWay(currentRetrievalCode, next, providerContainer)!!
            currentRetrievalCode = step.codeBlock.toString()
            allLinksAreReentrant = allLinksAreReentrant && step.isReentrant
        }

        return RetrievalWay(currentRetrievalCode!!.asCodeBlock(), allLinksAreReentrant)
    }

    /**
     * Returns a path - navigation chain of providers which provides retrieval of target provider.
     * Returns null if navigation is not possible.
     *
     * First element - child provider of this container.
     * Last element - target provider - [provider]
     */
    fun getPathToProvider(provider: Provider): List<Provider>? {
        val emptyList = mutableListOf<Provider>()

        fun Provider.reversedPart(provider: Provider) : MutableList<Provider> {
            // reaching searched element first, then composing resulting list "from the end"
            if (this === provider) return mutableListOf(provider)

            childrenProviders.forEach {
                val partPath = it.reversedPart(provider)
                if (partPath.isNotEmpty()) {
                    partPath.add(this)
                    return partPath
                }
            }

            return emptyList
        }

        val reversedPath = childrenProviders.asSequence()
            .map { child -> child.reversedPart(provider) }
            .filter { it.isNotEmpty() }
            .first()

        return if (reversedPath.isNotEmpty()) reversedPath.reversed()
        else null
    }
}
