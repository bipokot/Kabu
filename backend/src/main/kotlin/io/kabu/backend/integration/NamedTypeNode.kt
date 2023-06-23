package io.kabu.backend.integration

import io.kabu.backend.node.TypeNode

data class NamedTypeNode(
    override var name: String,
    override var typeNode: TypeNode,
) : NameAndType
