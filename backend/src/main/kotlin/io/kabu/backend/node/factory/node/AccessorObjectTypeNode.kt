package io.kabu.backend.node.factory.node

import io.kabu.backend.declaration.objects.AccessorObjectDeclaration
import io.kabu.backend.node.ObjectTypeNode
import io.kabu.backend.node.namespace.NamespaceNode

class AccessorObjectTypeNode(
    name: String,
    namespaceNode: NamespaceNode,
) : ObjectTypeNode(name, namespaceNode) {

    override fun createDeclarations() = listOf(
        AccessorObjectDeclaration(
            name = name,
            className = className,
        )
    )

    override fun toString() = "AccessorObjectTypeNode($name)"

    init {
        updateLinks()
    }
}
