package io.kabu.backend.declaration.functions

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.factory.node.util.RegularFunctionNodeKind
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parser.InfixFunction
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.util.poet.asCodeBlock
import io.kabu.runtime.exception.PatternEvaluationException


class FunctionDeclaration(
    val operator: Operator,
    val funDeclarationProviders: FunDeclarationProviders,
    val returnTypeNode: TypeNode,
    val kind: RegularFunctionNodeKind,
    namespaceNode: NamespaceNode?,
) : AbstractFunctionDeclaration(namespaceNode) {

    val functionName: String
        get() = when (kind) {
            is RegularFunctionNodeKind.HelperFunction -> kind.name
            else -> operator.overriding.function!!
        }

    override fun generateFunSpec(): FunSpec {
        val statements = when (kind) {
            is RegularFunctionNodeKind.DispatcherFunction -> getCallableStatementsForDispatcher(kind)
            else -> getCallableStatements(operator, funDeclarationProviders, returnTypeNode)
        }

        return generateFunSpec(
            name = functionName,
            providers = funDeclarationProviders.rightNamedProviders,
            receiverType = funDeclarationProviders.left.type,
            returnType = returnTypeNode.typeName,
            isInfix = operator is InfixFunction,
            codeBlock = statements,
            isHelper = kind is RegularFunctionNodeKind.HelperFunction,
        )
    }

    private fun getCallableStatementsForDispatcher(kind: RegularFunctionNodeKind.DispatcherFunction): CodeBlock {
        val counterName = kind.counterPropertyNode.name

        return buildString {
            appendLine("return when ($counterName++) {")

            kind.helperFunctionsNodes.forEachIndexed { index, functionNode ->
                val receiver = funDeclarationProviders.leftNamedProvider.name
                val rightParameters = funDeclarationProviders.rightNamedProviders.providers
                    .joinToString(",") { funDeclarationProviders.rightNamedProviders[it] }
                val callCode = "$receiver.${functionNode.name}($rightParameters)"
                appendLine("    $index -> $callCode")
            }

            appendLine("    else -> throw %T(%S)")
            appendLine("}")
        }.asCodeBlock(PatternEvaluationException::class, "Wrong counter index ($$counterName)")
    }

    override fun toString(): String {
        return "Function(name=$functionName, providers=$funDeclarationProviders, returns=$returnTypeNode)"
    }
}
