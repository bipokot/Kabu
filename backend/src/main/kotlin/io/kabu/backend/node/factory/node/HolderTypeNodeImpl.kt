package io.kabu.backend.node.factory.node

import io.kabu.backend.declaration.classes.HolderClassDeclaration
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.namespace.NamespaceNode

class HolderTypeNodeImpl(
    name: String,
    fieldTypes: List<TypeNode>,
    namespaceNode: NamespaceNode,
) : HolderTypeNode(
    name = name,
    fieldTypes = fieldTypes.toMutableList(),
    namespaceNode = namespaceNode,
    desiredName = null,
) {

    @Suppress("UNCHECKED_CAST")
    private fun getHolderClassDeclaration() = HolderClassDeclaration(
        className = className,
        fieldTypes = fieldTypes,
        parentTypeName = null,
        namespaceNode = namespaceNode!!,
    )

    override fun createDeclarations() = listOf(getHolderClassDeclaration())

    override fun toString() = "HolderTypeNode($name)"

    init {
        updateLinks()
    }
}
