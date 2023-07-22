package io.kabu.backend.declaration

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes
import io.kabu.backend.node.TypeNode
import io.kabu.backend.parser.FunctionMustReturn
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.provider.provider.WatcherLambdaProvider.Companion.STACK_PROPERTY_NAME
import io.kabu.backend.util.poet.asCodeBlock

abstract class AbstractCallableDeclaration : Declaration() {

    protected fun getCallableStatements(
        funDeclarationProviders: FunDeclarationProviders,
        returningClassName: ClassName,
    ): CodeBlock {
        return getCodeBlockForReturn(funDeclarationProviders, returningClassName)
    }

    protected fun getCallableStatements(
        operator: Operator,
        funDeclarationProviders: FunDeclarationProviders,
        returnTypeNode: TypeNode,
    ): CodeBlock {
        return if (operator.overriding.mustReturn == FunctionMustReturn.FREE) {
            codeBlockForGeneratedTypeNode(funDeclarationProviders, returnTypeNode)
        } else {
            codeBlockForFixedTypeNode(funDeclarationProviders, operator)
        }
    }

    private fun codeBlockForGeneratedTypeNode(
        funDeclarationProviders: FunDeclarationProviders,
        returnTypeNode: TypeNode,
    ): CodeBlock {
        return getCodeBlockForReturn(funDeclarationProviders, returnTypeNode.typeName)
    }

    private fun getCodeBlockForReturn(
        funDeclarationProviders: FunDeclarationProviders,
        returningTypeName: TypeName,
    ): CodeBlock {
        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        functionBlockContext.doEvaluation()

        val statements1 = functionBlockContext.joinAllStatements()
        val statements2 = run {
            val allParameters = functionBlockContext.actualProvidersProvider.childrenProviders
                .joinToString { functionBlockContext.getCodeForProvider(it) }
            "return %T($allParameters)"
        }

        return listOf(statements1, statements2)
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .asCodeBlock(returningTypeName)
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
            .map { functionBlockContext.getCodeForProvider(it) }
        val statements2 = actualValues.joinToString(";") { "$STACK_PROPERTY_NAME.push($it)" }

        // returning necessary value
        val statements3 = when (operator.overriding.mustReturn) {
            FunctionMustReturn.FREE,
            FunctionMustReturn.ASSIGNABLE,
            -> error("Incompatible FunctionMustReturn(${operator.overriding.mustReturn}) for fixed returned type")

            FunctionMustReturn.BOOLEAN -> "return true"
            FunctionMustReturn.INT -> getReturnStatementForComparison(funDeclarationProviders.operatorInfoType)
            FunctionMustReturn.UNIT -> ""
        }

        return listOf(statements1, statements2, statements3)
            .filter { it.isNotBlank() }
            .joinToString("\n")
            .asCodeBlock()
    }

    private fun getReturnStatementForComparison(operatorInfoType: TypeName?): String {
        return when (operatorInfoType) {
            OperatorInfoTypes.RANKING_COMPARISON_INFO_TYPE -> "return 42"
            OperatorInfoTypes.STRICTNESS_COMPARISON_INFO_TYPE -> "return 0"
            else -> "return 42"
        }
    }
}
