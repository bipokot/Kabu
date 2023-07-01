package io.kabu.backend.analyzer.handler

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asClassName
import io.kabu.runtime.InclusionInfo
import io.kabu.runtime.RankingComparisonInfo
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.analyzer.handler.lambda.watcher.CaptureType
import io.kabu.backend.diagnostic.builder.watcherLambdaMissingError
import io.kabu.backend.node.FixedTypeNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.parser.Assign
import io.kabu.backend.parser.BinaryExpression
import io.kabu.backend.parser.Comparison
import io.kabu.backend.parser.EvaluatedExpressionType
import io.kabu.backend.parser.InclusionCheck
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.ModAssign
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.group.FunDeclarationProvidersFactory
import io.kabu.backend.provider.group.RawProviders
import io.kabu.backend.provider.provider.AssignProvider
import io.kabu.backend.provider.provider.AuxProvider
import io.kabu.backend.provider.provider.BaseProvider
import io.kabu.backend.provider.provider.ComparisonProvider
import io.kabu.backend.provider.provider.EmptyProvider
import io.kabu.backend.provider.provider.InclusionProvider
import io.kabu.backend.provider.provider.OperatorInfoProvider
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode
import io.kabu.backend.util.poet.asFixedTypeName


open class Handler(
    protected val analyzer: AnalyzerImpl
) : Analyzer by analyzer {

    protected fun createHolderTypeAndAssignablePropertyViaWatchedParameter(
        rawProviders: RawProviders,
        expression: KotlinExpression
    ): BaseProvider {
        val assignExpression = expression.parent as BinaryExpression
        val assignOperator = assignExpression.operator as Assign

        val assigningProvider = providerOf(assignExpression.children[1])

        // pretend that only property receiver and assigning value take part in the operation
        val rawProvidersOfAssign =
            RawProviders(listOf(rawProviders.providersList[0], assigningProvider), operatorInfoParameter = null)

        return createWatchedParameter(rawProvidersOfAssign, assignOperator, expression)
    }

    protected fun createWatchedParameter(
        rawProviders: RawProviders,
        operator: Operator,
        assignableSuffixExpression: KotlinExpression?
    ): BaseProvider {
        val funDeclarationProviders = FunDeclarationProvidersFactory
            .from(rawProviders, operator.invertedArgumentOrdering)

        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        var evaluatedParameters = functionBlockContext.doEvaluation()

        val operatorInfoType = rawProviders.operatorInfoParameter?.type
            ?: run {
                when(operator) {
                    is Comparison -> RankingComparisonInfo::class.asClassName()
                    is InclusionCheck -> InclusionInfo::class.asClassName()
                    else -> null
                }
            }
        if (operatorInfoType != null) {
            val operatorInfoProvider =
                OperatorInfoProvider(operatorInfoType.toFixedTypeNode(), "operatorInfo") //todo constant name
            evaluatedParameters = evaluatedParameters.toMutableList()

            val positionOfOperatorInfoProvider = run { //1
                // after first raw provider AND before second raw provider
                val firstIndex = evaluatedParameters.indexOf(rawProviders.left).takeIf { it != -1 }
                val secondIndex = evaluatedParameters.indexOf(rawProviders.right.single()).takeIf { it != -1 }
                when {
                    firstIndex != null && secondIndex != null -> secondIndex // 1 - for binary operations
                    firstIndex != null -> firstIndex + 1 // after first
                    secondIndex != null -> secondIndex // before second
                    else -> 0 // firstIndex == null && secondIndex == null
                }
            }
            evaluatedParameters.add(positionOfOperatorInfoProvider, operatorInfoProvider)
        }

        val fieldTypes = evaluatedParameters.map { it.typeNode }
        val holderTypeNode = createAndRegisterHolderType(fieldTypes)

        val returningTypeNode = expressionReturnedTypeOf(operator.expressionType)!!.toFixedTypeNode()
        val translationReturnedTypeNode =
            (if (operator is Assign) UNIT else operator.overriding!!.mustReturn.asFixedTypeName()).toFixedTypeNode()
        val captureType = CaptureType(
            operator = operator,
            funDeclarationProviders = funDeclarationProviders,
            returnTypeNode = translationReturnedTypeNode,
            assignableSuffixExpression = assignableSuffixExpression,
            rawProviders = rawProviders,
        )
        val watcherLambda = analyzer.currentWatcherLambda ?: watcherLambdaMissingError(operator)
        watcherLambda.register(captureType)

        val watchedProvider = when(operator) {
            is Comparison -> ComparisonProvider(holderTypeNode, evaluatedParameters, analyzer)
            is InclusionCheck -> InclusionProvider(holderTypeNode, evaluatedParameters, analyzer)
            is ModAssign -> AssignProvider(holderTypeNode, evaluatedParameters, analyzer)
            else -> AssignProvider(holderTypeNode, evaluatedParameters, analyzer)
        }

        return AuxProvider(returningTypeNode, watchedProvider)
    }

    private fun expressionReturnedTypeOf(expressionReturnedType: EvaluatedExpressionType): ClassName? {
        return when (expressionReturnedType) {
            EvaluatedExpressionType.FREE -> null
            EvaluatedExpressionType.BOOLEAN -> BOOLEAN
            EvaluatedExpressionType.NONE -> UNIT
        }
    }

    protected fun createTerminalReadOnlyProperty(
        name: String,
        rawProviders: RawProviders,
    ): EmptyProvider {
        val propertyType: TypeNode = when (analyzer.method.returnedType) {
            UNIT -> createAndRegisterHolderType(emptyList())
            else -> FixedTypeNode(
                typeName = analyzer.method.returnedType,
                namespaceNode = null,
            )
        }

        val funDeclarationProviders = FunDeclarationProvidersFactory.from(rawProviders, invertedOrdering = false)

        val propertyNode = nodeFactory.createTerminalReadOnlyPropertyNode(
            name = name,
            funDeclarationProviders = funDeclarationProviders,
            typeNode = propertyType,
            namespaceNode = namespaceNode,
            analyzer = analyzer,
        )
        registerNode(propertyNode)

        return EmptyProvider(propertyType)
    }

    protected fun createTerminalAssignableProperty(
        propertyName: String,
        receiverProvider: Provider,
        assigningProvider: Provider,
    ): EmptyProvider {
        // pretend that only property receiver and assigning value take part in the operation
        val rawProvidersOfAssign = RawProviders(
            listOf(receiverProvider, assigningProvider),
            operatorInfoParameter = null
        )
        val propertyTypeNode = rawProvidersOfAssign.providersList[1].typeNode

        val funDeclarationProviders =
            FunDeclarationProvidersFactory.from(rawProvidersOfAssign, invertedOrdering = false, forSetter = true)

        val propertyNode = nodeFactory.createTerminalAssignablePropertyNode(
            name = propertyName,
            typeNode = propertyTypeNode,
            funDeclarationProviders = funDeclarationProviders,
            namespaceNode = namespaceNode,
            analyzer = analyzer
        )
        registerNode(propertyNode)

        return EmptyProvider()
    }

    protected fun createAndRegisterHolderType(fieldTypes: List<TypeNode>): HolderTypeNode {
        val typeNode = nodeFactory.createHolderTypeNode(
            name = namespaceNode.typeNameGenerator.generateNextTypeName(),
            fieldTypes = fieldTypes,
            namespaceNode = namespaceNode,
        )
        registerNode(typeNode)
        return typeNode
    }
}
