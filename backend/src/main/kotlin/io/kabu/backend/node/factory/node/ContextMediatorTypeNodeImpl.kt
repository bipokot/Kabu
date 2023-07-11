package io.kabu.backend.node.factory.node

import io.kabu.backend.declaration.classes.ContextMediatorClassDeclaration
import io.kabu.backend.node.ContextMediatorTypeNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parameter.Parameter

class ContextMediatorTypeNodeImpl(
    name: String,
    namespaceNode: NamespaceNode,
    private val contextProperty: Parameter,
) : ContextMediatorTypeNode(
    name = name,
    desiredName = null,
    namespaceNode = namespaceNode,
) {

    private fun getContextMediatorClassDeclaration() = ContextMediatorClassDeclaration(
        className = this.className,
        contextProperty = contextProperty,
    )

    override fun createDeclarations() = listOf(getContextMediatorClassDeclaration())

    override fun toString() = "ContextMediatorTypeNode($name)"

    init {
        updateLinks()
    }
}
