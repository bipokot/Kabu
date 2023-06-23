package io.kabu.backend.node.factory.node

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.declaration.properties.TerminalReadOnlyPropertyDeclaration
import io.kabu.backend.node.BasePropertyNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.provider.group.FunDeclarationProviders

class TerminalReadOnlyPropertyNode(
    name: String,
    typeNode: TypeNode,
    namespaceNode: NamespaceNode,
    private val funDeclarationProviders: FunDeclarationProviders,
    private val analyzer: Analyzer,
) : BasePropertyNode(
    name = name,
    receiverTypeNode = null,
    returnTypeNode = typeNode,
    namespaceNode = namespaceNode,
    isTerminal = true,
) {

    override fun createDeclarations() = listOf(getTerminalReadOnlyPropertyDeclaration())

    private fun getTerminalReadOnlyPropertyDeclaration() = TerminalReadOnlyPropertyDeclaration(
        name = name,
        propertyType = returnTypeNode.typeName,
        funDeclarationProviders = funDeclarationProviders,
        analyzer = analyzer,
    )
}
