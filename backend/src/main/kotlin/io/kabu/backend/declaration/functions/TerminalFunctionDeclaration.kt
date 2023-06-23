package io.kabu.backend.declaration.functions

import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.declaration.util.TerminalCallableBuilder
import io.kabu.backend.parser.Assign
import io.kabu.backend.parser.FunctionMustReturn
import io.kabu.backend.parser.InfixFunction
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.util.poet.asFixedTypeName


open class TerminalFunctionDeclaration(
    val operator: Operator,
    val funDeclarationProviders: FunDeclarationProviders,
    val analyzer: Analyzer,
) : AbstractFunctionDeclaration() {

    private val callableBuilder = TerminalCallableBuilder()

    //todo abstract in base?
    val functionName: String
        get() = operator.getFunctionNameOrThrow()

    val returnedType: TypeName
        get() = when {
            operator is Assign -> UNIT
            operator.overriding!!.mustReturn != FunctionMustReturn.FREE -> {
                operator.overriding.mustReturn.asFixedTypeName()
            }

            else -> {
                analyzer.method.returnedType
            }
        }

    override fun generateFunSpec(): FunSpec {
        val requiredReturnStatement = getRequiredByOperatorReturnStatement(operator)

        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        functionBlockContext.doEvaluation()

        val statements = callableBuilder
            .createTerminationStatements(analyzer, functionBlockContext, requiredReturnStatement)

        return generateFunSpec(
            name = functionName,
            parameters = funDeclarationProviders.rightNamedProviders,
            returnType = returnedType,
            receiverType = funDeclarationProviders.left.type,
            isInfix = operator is InfixFunction,
            codeBlock = statements
        )
    }

    private fun getRequiredByOperatorReturnStatement(operator: Operator) = when (operator.overriding?.mustReturn) {
        FunctionMustReturn.BOOLEAN -> "return true"
        FunctionMustReturn.INT -> "return 42"
        FunctionMustReturn.FREE,
        FunctionMustReturn.ASSIGNABLE,
        FunctionMustReturn.UNIT,
        -> null

        null -> null
    }

    override fun toString(): String {
        return "TerminalFunction(operator=$operator, parameters=$funDeclarationProviders)"
    }
}
