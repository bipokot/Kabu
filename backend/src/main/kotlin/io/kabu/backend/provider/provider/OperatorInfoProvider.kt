package io.kabu.backend.provider.provider

import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode


class OperatorInfoProvider(
    typeNode: TypeNode,
    originalName: String,
    origin: Origin? = null,
) : ArgumentProvider(typeNode, originalName, origin) {

    override val isUseful: Boolean = true

    override fun generateName(): String {
        return originalName
    }
}
