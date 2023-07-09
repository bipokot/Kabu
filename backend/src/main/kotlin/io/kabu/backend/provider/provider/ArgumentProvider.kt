package io.kabu.backend.provider.provider

import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode


open class ArgumentProvider(
    typeNode: TypeNode,
    val originalName: String, // name defined by user
    origin: Origin? = null,
) : BaseProvider(typeNode, origin) {

    override val isUseful: Boolean = true

    override fun generateName(): String {
        return originalName
    }
}
