package io.kabu.backend.node.factory.node

import io.kabu.backend.declaration.properties.PlaceholderPropertyDeclaration
import io.kabu.backend.node.BasePropertyNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.namespace.NamespaceNode

class PlaceholderPropertyNode(
    name: String,
    typeNode: HolderTypeNode,
    namespaceNode: NamespaceNode,
) : BasePropertyNode(
    name = name,
    receiverTypeNode = null,
    returnTypeNode = typeNode,
    namespaceNode = namespaceNode,
    isTerminal = false,
) {

    override fun createDeclarations() = listOf(getPlaceholderPropertyDeclaration())

    private fun getPlaceholderPropertyDeclaration() = PlaceholderPropertyDeclaration(
        name = name,
        returnTypeNode = returnTypeNode,
    )
}
