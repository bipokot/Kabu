package io.kabu.backend.provider.group

import io.kabu.backend.integration.NameAndType
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.provider.Provider

// names uniqueness must be externally
class OrderedNamedProviders {
    private val map: LinkedHashMap<Provider, String> = linkedMapOf()

    val providers: List<Provider>
        get() = map.keys.toList()

    val nameAndTypes: List<NameAndType> = Wrapper(this)

    // overwrites
    fun register(provider: Provider, name: String) {
        map[provider] = name
    }

    fun unregister(provider: Provider) {
        map.remove(provider)
    }

    // throws if not found
    operator fun get(provider: Provider): String =
        map[provider]!!

    operator fun set(provider: Provider, name: String) {
        if (provider !in map) return
        map[provider] = name
    }

    fun getOrNull(provider: Provider): String? =
        map[provider]

    fun getOrNull(name: String): Provider? =
        map.entries.find { it.value == name }?.key
}

private class Wrapper(private val namedProviders: OrderedNamedProviders) : List<NameAndType> {

    private inner class InnerNameAndType(val provider: Provider) : NameAndType {
        override var name: String
            get() = namedProviders[provider]
            set(value) {
                namedProviders[provider] = value
            }
        override var typeNode: TypeNode
            get() = provider.typeNode
            set(value) {
                provider.typeNode = value
            }
    }

    override val size: Int
        get() = namedProviders.providers.size

    override fun get(index: Int): NameAndType = InnerNameAndType(namedProviders.providers.get(index))

    override fun isEmpty() = namedProviders.providers.isEmpty()

    @Suppress("IteratorNotThrowingNoSuchElementException")
    override fun iterator(): Iterator<NameAndType> {
        val iterator = namedProviders.providers.iterator()
        return object : Iterator<NameAndType> {
            override fun hasNext(): Boolean = iterator.hasNext()
            override fun next(): NameAndType = InnerNameAndType(iterator.next())
        }
    }

    override fun listIterator() = error("not implemented")
    override fun listIterator(index: Int) = error("not implemented")
    override fun subList(fromIndex: Int, toIndex: Int) = error("not implemented")
    override fun lastIndexOf(element: NameAndType) = error("not implemented")
    override fun indexOf(element: NameAndType) = error("not implemented")
    override fun containsAll(elements: Collection<NameAndType>) = error("not implemented")
    override fun contains(element: NameAndType) = error("not implemented")
}
