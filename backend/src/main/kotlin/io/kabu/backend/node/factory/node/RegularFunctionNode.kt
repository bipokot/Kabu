package io.kabu.backend.node.factory.node

import io.kabu.backend.declaration.functions.FunctionDeclaration
import io.kabu.backend.node.BaseFunctionNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.factory.node.util.RegularFunctionNodeKind
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.FunDeclarationProviders

class RegularFunctionNode(
    funDeclarationProviders: FunDeclarationProviders,
    namespaceNode: NamespaceNode,
    private val typeNode: TypeNode,
    private val operator: Operator,
    private val kind: RegularFunctionNodeKind,
) : BaseFunctionNode(funDeclarationProviders, namespaceNode) {

    override val isTerminal: Boolean = false

    override val name: String
        get() = getFunctionDeclaration().functionName

    override var returnTypeNode: TypeNode = typeNode

    private fun getFunctionDeclaration(): FunctionDeclaration {
        return FunctionDeclaration(
            operator = operator,
            funDeclarationProviders = funDeclarationProviders,
            returnTypeNode = typeNode,
            kind = kind,
            namespaceNode = namespaceNode,
        )
    }

    override fun createDeclarations() = listOf(getFunctionDeclaration())

    override fun toString() = "RegularFunctionNode($name)"

    init {
        updateLinks()
    }
}
