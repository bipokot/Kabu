package io.kabu.backend.node.factory.node

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.declaration.functions.TerminalFunctionDeclaration
import io.kabu.backend.node.BaseFunctionNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode

class TerminalFunctionNode(
    funDeclarationProviders: FunDeclarationProviders,
    namespaceNode: NamespaceNode,
    private val operator: Operator,
    private val analyzer: Analyzer,
) : BaseFunctionNode(funDeclarationProviders, namespaceNode) {

    override val isTerminal: Boolean = true

    override val name: String =
        getTerminalFunctionDeclaration().functionName

    override var returnTypeNode: TypeNode =
        getTerminalFunctionDeclaration().returnedType.toFixedTypeNode()

    private fun getTerminalFunctionDeclaration() = TerminalFunctionDeclaration(
        operator = operator,
        funDeclarationProviders = funDeclarationProviders,
        analyzer = analyzer,
        namespaceNode = namespaceNode,
    )

    override fun createDeclarations() = listOf(getTerminalFunctionDeclaration())

    override fun toString() = "TerminalFunctionNode($name)"

    init {
        updateLinks()
    }
}
