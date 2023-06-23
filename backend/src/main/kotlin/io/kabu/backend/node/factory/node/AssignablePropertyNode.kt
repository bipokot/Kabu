package io.kabu.backend.node.factory.node

import io.kabu.backend.declaration.properties.AssignablePropertyDeclaration
import io.kabu.backend.node.BasePropertyNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.provider.group.FunDeclarationProviders

class AssignablePropertyNode(
    name: String,
    returnTypeNode: TypeNode,
    namespaceNode: NamespaceNode,
    private val funDeclarationProviders: FunDeclarationProviders,
) : BasePropertyNode(
    name = name,
    receiverTypeNode = null,
    returnTypeNode = returnTypeNode,
    namespaceNode = namespaceNode,
    isTerminal = false,
) {

    override fun createDeclarations() = listOf(getAssignablePropertyDeclaration())

    private fun getAssignablePropertyDeclaration() = AssignablePropertyDeclaration(
        name = name,
        typeNode = returnTypeNode,
        funDeclarationProviders = funDeclarationProviders,
    )
}
