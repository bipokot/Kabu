package io.kabu.backend.declaration

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes
import io.kabu.backend.node.TypeNode
import io.kabu.backend.parser.FunctionMustReturn
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME
import io.kabu.backend.util.poet.asCodeBlock

//todo DRY
abstract class AbstractCallableDeclaration : Declaration() {

    protected fun getCallableStatements(
        funDeclarationProviders: FunDeclarationProviders,
        returningClassName: ClassName,
    ): CodeBlock {
        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        functionBlockContext.doEvaluation()

        val statements1 = functionBlockContext.joinAllStatements()
        val statements2 = run {
            val allParameters = functionBlockContext.actualProvidersProvider.childrenProviders
                .joinToString { functionBlockContext.getCodeForActualProvider(it) }
            "return %T($allParameters)"
        }

        return listOf(statements1, statements2)
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .asCodeBlock(returningClassName)
    }

    protected fun getCallableStatements(
        operator: Operator,
        funDeclarationProviders: FunDeclarationProviders,
        returnTypeNode: TypeNode,
    ): CodeBlock {
        return if (operator.overriding?.mustReturn == FunctionMustReturn.FREE) {
            codeBlockForGeneratedTypeNode(funDeclarationProviders, returnTypeNode)
        } else {
            codeBlockForFixedTypeNode(funDeclarationProviders, operator)
        }
    }

    private fun codeBlockForGeneratedTypeNode(
        funDeclarationProviders: FunDeclarationProviders,
        returnTypeNode: TypeNode,
    ): CodeBlock {
        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        functionBlockContext.doEvaluation()

        val statements1 = functionBlockContext.joinAllStatements()
        val statements2 = run {
            val allParameters = functionBlockContext.actualProvidersProvider.childrenProviders
                .joinToString { functionBlockContext.getCodeForActualProvider(it) }
            "return %T($allParameters)"
        }

        return listOf(statements1, statements2)
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .asCodeBlock(returnTypeNode.typeName)
    }

    private fun codeBlockForFixedTypeNode(
        funDeclarationProviders: FunDeclarationProviders,
        operator: Operator,
    ): CodeBlock {
        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)

        // evaluating
        functionBlockContext.doEvaluation()
        val statements1 = functionBlockContext.joinAllStatements()

        // saving
        val actualValues = functionBlockContext.actualProvidersProvider.childrenProviders
            .map { functionBlockContext.getCodeForActualProvider(it) }
        val statements2 = actualValues.joinToString(";") { "$STACK_PROPERTY_NAME.push($it)" }

        // returning necessary value
        val statements3 = when (operator.overriding?.mustReturn) {
            FunctionMustReturn.FREE -> TODO()
            FunctionMustReturn.ASSIGNABLE -> TODO()
            FunctionMustReturn.BOOLEAN -> "return true"
            FunctionMustReturn.INT -> getReturnStatementForComparison(funDeclarationProviders.operatorInfoType)
            FunctionMustReturn.UNIT -> ""
            null -> TODO()
        }

        return listOf(statements1, statements2, statements3)
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .asCodeBlock()
    }

    private fun getReturnStatementForComparison(operatorInfoType: TypeNode?): String {
        return when (operatorInfoType?.typeName) {
            OperatorInfoTypes.RANKING_COMPARISON_INFO_TYPE -> "return 42"
            OperatorInfoTypes.STRICTNESS_COMPARISON_INFO_TYPE -> "return 0"
            else -> "return 42"
        }
    }
}
