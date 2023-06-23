package io.kabu.backend.node.factory.node

import io.kabu.backend.declaration.properties.WrapperPropertyDeclaration
import io.kabu.backend.node.BasePropertyNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.provider.group.FunDeclarationProviders

class WrapperPropertyNode(
    name: String,
    returnTypeNode: HolderTypeNode,
    namespaceNode: NamespaceNode,
    private val funDeclarationProviders: FunDeclarationProviders,
) : BasePropertyNode(
    name = name,
    receiverTypeNode = null,
    returnTypeNode = returnTypeNode,
    namespaceNode = namespaceNode,
    isTerminal = false,
) {

    override fun createDeclarations() = listOf(getWrapperPropertyDeclaration())

    private fun getWrapperPropertyDeclaration() = WrapperPropertyDeclaration(
        name = name,
        returnTypeNode = returnTypeNode,
        funDeclarationProviders = funDeclarationProviders,
    )
}
