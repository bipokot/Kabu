package io.kabu.backend.analyzer.handler

import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.parser.Access
import io.kabu.backend.parser.Assign
import io.kabu.backend.parser.BinaryExpression
import io.kabu.backend.parser.EvaluatedExpressionType
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.Indexing
import io.kabu.backend.parser.NaryExpression
import io.kabu.backend.parser.Operator
import io.kabu.backend.parser.OperatorExpression
import io.kabu.backend.parser.UnaryExpression
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.group.FunDeclarationProvidersFactory
import io.kabu.backend.provider.group.RawProviders
import io.kabu.backend.provider.provider.BaseProvider
import io.kabu.backend.provider.provider.EmptyProvider
import io.kabu.backend.provider.provider.HolderProvider
import io.kabu.backend.provider.provider.Provider

class OperatorHandler(analyzer: AnalyzerImpl) : Handler(analyzer) {

    fun handle(expression: OperatorExpression): Provider {
        if (expression.operator is Assign) {
            // delegating creation of parameter for this expression to left parameter (to create setter/set functions)
            return providerOf(expression.children[0])
        }

        val rawParameters = createRawProvidersFromChildren(expression)
        val operatorCanReturnAnything = expression.operator.expressionType == EvaluatedExpressionType.FREE
        return when (expression) {
            is NaryExpression -> {
                if (expression.isTerminal) {
                    return createTerminalFunctionDeclaration(rawParameters, expression)
                }
                if (expression.operator is Indexing && expression.isLeftSideOfAssign) {
                    if (expression.parent?.isTerminal == true) {
                        createTerminalIndexedAssignOperator(rawParameters, expression)
                    } else {
                        // x[s] = b // non-terminal
                        createHolderTypeAndIndexedAssignOperator(rawParameters, expression)
                    }
                } else {
                    createHolderTypeAndOperator(rawParameters, expression)
                }
            }
            is BinaryExpression -> {
                if (expression.isTerminal) {
                    return if (expression.operator is Access) {
                        createTerminalReadOnlyProperty((expression.right as IdentifierLeaf).name, rawParameters)
                    } else {
                        createTerminalFunctionDeclaration(rawParameters, expression)
                    }
                }

                if (!operatorCanReturnAnything) {
                    return createWatchedParameter(rawParameters, expression.operator, assignableSuffixExpression = null)
                }

                if (expression.operator !is Access) {
                    return createHolderTypeAndOperator(rawParameters, expression)
                }

                // Access
                return if (expression.isLeftSideOfAssign) {
                    if (expression.parent?.isTerminal == true) {
                        createTerminalAssignableProperty(rawParameters, expression)
                    } else {
                        // b.x = s // b s // non-terminal
                        createHolderTypeAndAssignablePropertyViaWatchedParameter(rawParameters, expression)
                    }
                } else {
                    createHolderTypeAndProperty(rawParameters, expression)
                }
            }
            is UnaryExpression -> {
                if (!operatorCanReturnAnything) {
                    //todo extract all diagnostic messages in one place
                    diagnosticError("Unary operators with restrictions on returned type are not supported yet",
                        expression.operator)
                }
                if (expression.isTerminal) {
                    return createTerminalFunctionDeclaration(rawParameters, expression)
                }
                createHolderTypeAndOperator(rawParameters, expression)
            }
        }
    }

    /** Creates function that terminates the syntax tree */
    private fun createTerminalFunctionDeclaration(
        rawProviders: RawProviders,
        expression: OperatorExpression,
    ): EmptyProvider {

        fun validateApplicability(
            operator: Operator,
            analyzer: Analyzer,
            rawProviders: RawProviders,
        ) {
            if (operator.expressionType != EvaluatedExpressionType.FREE) {
                if (analyzer.method.returnedType != UNIT)
                    diagnosticError("Function must return Unit when terminal operator is $operator", operator)
            }
            if (rawProviders.operatorInfoParameter != null)
                diagnosticError("Operator info parameter is not accessible for terminal operator $operator", operator)
        }

        validateApplicability(expression.operator, analyzer, rawProviders)

        val funDeclarationProviders = FunDeclarationProvidersFactory
            .from(rawProviders, expression.operator.invertedArgumentOrdering)

        nodeFactory.createTerminalFunctionNode(funDeclarationProviders, expression.operator, analyzer, namespaceNode)
            .also { registerNode(it) }

        return EmptyProvider()
    }

    private fun createTerminalIndexedAssignOperator(
        rawProviders: RawProviders,
        expression: NaryExpression
    ): BaseProvider {
        val assigningParameter = providerOf((expression.parent as BinaryExpression).children[1])

        // combining parameters for "set" operator
        val rawProvidersOfAssign = RawProviders(rawProviders.providersList + assigningParameter, operatorInfoParameter = null)
        val assignOperator = (expression.parent as BinaryExpression).operator as Assign

        val funDeclarationProviders = FunDeclarationProvidersFactory.from(
            rawProvidersOfAssign,
            assignOperator.invertedArgumentOrdering
        )

        val functionNode = nodeFactory.createTerminalFunctionNode(
            funDeclarationProviders = funDeclarationProviders,
            operator = assignOperator,
            analyzer = analyzer,
            namespaceNode = namespaceNode,
        )
        registerNode(functionNode)

        return EmptyProvider()
    }

    private fun createTerminalAssignableProperty(
        rawProviders: RawProviders,
        expression: BinaryExpression,
    ): BaseProvider {
        val propertyName = (expression.right as IdentifierLeaf).name
        val receiverProvider = rawProviders.providersList[0]
        val assigningProvider = providerOf((expression.parent as BinaryExpression).children[1])

        return createTerminalAssignableProperty(propertyName, receiverProvider, assigningProvider)
    }

    private fun createHolderTypeAndOperator(
        rawProviders: RawProviders,
        expression: OperatorExpression,
    ): HolderProvider {
        val funDeclarationProviders = FunDeclarationProvidersFactory.from(
            rawProviders,
            expression.operator.invertedArgumentOrdering
        )

        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        val evaluatedParameters = functionBlockContext.doEvaluation()
        val fieldTypes = evaluatedParameters.map { it.typeNode }
        val holderTypeNode = createAndRegisterHolderType(fieldTypes)

        val functionNode = nodeFactory
            .createFunctionNode(funDeclarationProviders, expression.operator, holderTypeNode, namespaceNode)
        registerNode(functionNode)

        return HolderProvider(holderTypeNode, evaluatedParameters, analyzer)
    }

    private fun createHolderTypeAndProperty(
        rawProviders: RawProviders,
        expression: BinaryExpression,
    ): HolderProvider {
        val propertyName = (expression.right as IdentifierLeaf).name
        val wrappedProvider = rawProviders.left

        val funDeclarationProviders =
            FunDeclarationProvidersFactory.from(RawProviders(listOf(wrappedProvider), null), invertedOrdering = false)
        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        val evaluatedParameters = functionBlockContext.doEvaluation()
        val fieldTypes = evaluatedParameters.map { it.typeNode }
        val holderTypeNode = createAndRegisterHolderType(fieldTypes)

        val propertyNode = nodeFactory.createWrapperPropertyNode(
            name = propertyName,
            typeNode = holderTypeNode,
            namespaceNode = namespaceNode,
            funDeclarationProviders = funDeclarationProviders,
        )
        registerNode(propertyNode)

        return HolderProvider(holderTypeNode, evaluatedParameters, analyzer)
    }

    private fun createHolderTypeAndIndexedAssignOperator(
        rawProviders: RawProviders,
        expression: NaryExpression
    ): BaseProvider {
        val assigningParameter = providerOf((expression.parent as BinaryExpression).children[1])

        // combining parameters for "set" operator
        val rawProvidersOfAssign = RawProviders(rawProviders.providersList + assigningParameter, operatorInfoParameter = null)
        val assignOperator = (expression.parent as BinaryExpression).operator as Assign

        return createWatchedParameter(rawProvidersOfAssign, assignOperator, expression)
    }
}
