package io.kabu.backend.analyzer

import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.parser.BinaryExpression
import io.kabu.backend.parser.Call
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.parser.NaryExpression
import io.kabu.backend.parser.UnaryExpression
import io.kabu.backend.parser.UnaryPostfix
import io.kabu.backend.parser.UnaryPrefix


/**
 * Validates a [KotlinExpression] before work with it
 */
class ExpressionValidator {

    fun validateExpression(expression: KotlinExpression) {
        validateNodeAndChildren(expression)
    }

    private fun validateNodeAndChildren(expression: KotlinExpression) {
        validateNode(expression)
        expression.children.forEach { validateNodeAndChildren(it) }
    }

    private fun validateNode(expression: KotlinExpression) {
        return when (expression) {
            is UnaryExpression -> validateUnaryExpression(expression)
            is NaryExpression -> validateNaryExpression(expression)
            is IdentifierLeaf,
            is LambdaExpression,
            is BinaryExpression,
            -> Unit
        }
    }

    private fun validateUnaryExpression(expression: UnaryExpression) {
        val operator = expression.operator
        val isIncrement = operator is UnaryPrefix.PlusPlusPrefix || operator is UnaryPostfix.PlusPlusPostfix
        val isDecrement = operator is UnaryPrefix.MinusMinusPrefix || operator is UnaryPostfix.MinusMinusPostfix
        if (isIncrement || isDecrement) {
            diagnosticError(
                "Prefix/postfix increment/decrement operators are not currently supported",
                expression
            )
        }
    }

    private fun validateNaryExpression(expression: NaryExpression) {
        // check for several lambda literals outside of parenthesis: '{ s }{ b }{ i }'
        //todo maybe change parsing logic to parse such calls as one Call operator with several parameters?
        if (expression.operator is Call) {
            val argumentIsALambda = expression.arguments.size == 1 &&
                expression.arguments.single() is LambdaExpression

            val leftSide = expression.operand
            val leftSideIsACallWithALambdaArgument = leftSide is NaryExpression
                && leftSide.operator is Call
                && leftSide.arguments.size == 1
                && leftSide.arguments.single() is LambdaExpression

            if (leftSideIsACallWithALambdaArgument && argumentIsALambda) {
                diagnosticError(
                    "Only one lambda expression is allowed outside a parenthesized argument list",
                    leftSide,
                    expression.arguments.single()
                )
            }
        }
    }
}
