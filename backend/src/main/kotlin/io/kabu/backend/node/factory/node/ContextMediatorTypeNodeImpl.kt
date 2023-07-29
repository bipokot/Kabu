package io.kabu.backend.node.factory.node

import com.squareup.kotlinpoet.TypeVariableName
import io.kabu.backend.declaration.classes.ContextMediatorClassDeclaration
import io.kabu.backend.node.ContextMediatorTypeNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parameter.Parameter

class ContextMediatorTypeNodeImpl(
    name: String,
    typeVariableNames: List<TypeVariableName>,
    namespaceNode: NamespaceNode,
    private val contextProperty: Parameter,
) : ContextMediatorTypeNode(
    name = name,
    typeVariableNames = typeVariableNames,
    desiredName = null,
    namespaceNode = namespaceNode,
) {

    private fun getContextMediatorClassDeclaration() = ContextMediatorClassDeclaration(
        typeNode = this,
        contextProperty = contextProperty,
    )

    override fun createDeclarations() = listOf(getContextMediatorClassDeclaration())

    override fun toString() = "ContextMediatorTypeNode($name)"

    init {
        updateLinks()
    }
}
