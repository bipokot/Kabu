package io.kabu.backend.analyzer.handler.lambda.watcher

import com.squareup.kotlinpoet.LambdaTypeName
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.analyzer.handler.Handler
import io.kabu.backend.node.DerivativeTypeNode
import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.WatcherContextTypeNode
import io.kabu.backend.node.factory.node.AssignablePropertyNode
import io.kabu.backend.node.factory.node.DispatcherCallsCounterPropertyNode
import io.kabu.backend.node.factory.node.RegularFunctionNode
import io.kabu.backend.node.factory.node.util.RegularFunctionNodeKind
import io.kabu.backend.parser.Access
import io.kabu.backend.parser.Assign
import io.kabu.backend.parser.BinaryExpression
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.Indexing
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.parser.NaryExpression
import io.kabu.backend.provider.group.FunDeclarationProvidersFactory
import io.kabu.backend.provider.provider.AbstractProvider
import io.kabu.backend.provider.provider.ScopingLambdaProvider
import io.kabu.backend.provider.provider.WatcherContextProvider
import io.kabu.backend.provider.provider.WatcherLambdaProvider

class WatcherLambdaHandler(analyzer: AnalyzerImpl) : Handler(analyzer) {

    fun handle(expression: LambdaExpression): AbstractProvider {
        val watcherLambda = analyzer.currentWatcherLambda!!

        // examining the first statement of lambda
        val returningProvider = providerOf(expression.expressions.first())

        // adding declarations for all capture types
        watcherLambda.captureTypeGroups.forEach { group ->
            handleCaptureTypeGroup(group.key, group.value)
        }

        val returningProviderTypeNode = returningProvider.typeNode
        val watcherContextTypeNode = watcherLambda.watcherContextTypeNode
        val typeNode = DerivativeTypeNode(
            namespaceNode = namespaceNode,
            generatorDependencies = mutableListOf(returningProviderTypeNode, watcherContextTypeNode)
        ) { deps ->
            LambdaTypeName.get(
                returnType = (deps[0] as TypeNode).typeName,
                receiver = (deps[1] as WatcherContextTypeNode).className,
            )
        }
        registerNode(typeNode)

        return if (watcherLambda.captureTypeGroups.isEmpty()) {
            ScopingLambdaProvider(
                typeNode = typeNode,
                returningProvider = returningProvider,
                watcherContextTypeNode = watcherContextTypeNode,
                analyzer = analyzer
            )
        } else {
            WatcherLambdaProvider(
                typeNode = typeNode,
                returnType = returningProvider,
                watcherContextProvider = WatcherContextProvider(watcherContextTypeNode, returningProvider, analyzer),
                analyzer = analyzer
            )
        }
    }

    private fun handleCaptureTypeGroup(
        group: CaptureTypeGroup,
        captureTypes: MutableList<CaptureType>,
    ) {
        val operator = group.operator
        val assignableSuffixExpression = group.assignableSuffixExpression

        when {
            operator is Assign && assignableSuffixExpression is IdentifierLeaf -> {
                // x = z
                handleBinaryAssign(captureTypes)
            }

            operator is Assign && (assignableSuffixExpression as? BinaryExpression)?.operator is Access -> {
                // x.y = z
                handleBinaryAssign(captureTypes)
            }

            operator is Assign && (assignableSuffixExpression as? NaryExpression)?.operator is Indexing -> {
                // x[y] = z
                handleRegularCapture(captureTypes, group)
            }

            else -> {
                handleRegularCapture(captureTypes, group)
            }
        }
    }

    private fun handleRegularCapture(captureTypes: List<CaptureType>, group: CaptureTypeGroup) {
        if (captureTypes.size == 1) {
            val captureType = captureTypes.single()
            val funDeclarationProviders = captureType.funDeclarationProviders
            val operator = captureType.operator
            val typeNode = captureType.returnTypeNode

            val functionNode = RegularFunctionNode(
                funDeclarationProviders = funDeclarationProviders,
                operator = operator,
                typeNode = typeNode,
                namespaceNode = namespaceNode,
                kind = RegularFunctionNodeKind.Default,
            )
            registerNode(functionNode)
        } else {
            val groupBaseName = group.getBaseNameForDeclarations()

            // counter
            val counterNode = DispatcherCallsCounterPropertyNode(groupBaseName + "_helperCounter", namespaceNode)
            registerNode(counterNode)

            // helper functions
            val helperFunctionsNodes: List<FunctionNode> = captureTypes.mapIndexed { index, captureType ->
                val funDeclarationProviders = captureType.funDeclarationProviders
                val operator = captureType.operator
                val typeNode = captureType.returnTypeNode

                val helperFunctionName = groupBaseName + "_helperFunction$index"

                val functionNode = RegularFunctionNode(
                    funDeclarationProviders = funDeclarationProviders,
                    operator = operator,
                    typeNode = typeNode,
                    namespaceNode = namespaceNode,
                    kind = RegularFunctionNodeKind.HelperFunction(helperFunctionName)
                )
                registerNode(functionNode)

                functionNode
            }

            // dispatcher function
            val funDeclarationProviders = group.funDeclarationProviders
            val operator = group.operator
            val typeNode = group.returnTypeNode

            val kind = RegularFunctionNodeKind.DispatcherFunction(counterNode, helperFunctionsNodes)
            val functionNode = RegularFunctionNode(
                funDeclarationProviders = funDeclarationProviders,
                namespaceNode = namespaceNode,
                typeNode = typeNode,
                operator = operator,
                kind = kind
            )
            registerNode(functionNode)
        }
    }

    private fun handleBinaryAssign(captureTypes: List<CaptureType>) {
        val captureType = captureTypes.single()
        val name = when (val suffix = captureType.assignableSuffixExpression) {
            is IdentifierLeaf -> suffix.name  // x = z
            is BinaryExpression -> (suffix.right as IdentifierLeaf).name // x.y = z
            else -> error("Unknown type of assignableSuffixExpression: $suffix")
        }

        val rawProviders = captureType.rawProviders!!
        val funDeclarationProviders = FunDeclarationProvidersFactory.from(
            rawProviders,
            captureType.operator.overriding.invertedArgumentOrdering,
            forSetter = true
        )

        val propertyNode = AssignablePropertyNode(
            name = name,
            returnTypeNode = rawProviders.right.single().typeNode,
            namespaceNode = namespaceNode,
            funDeclarationProviders = funDeclarationProviders,
        )
        registerNode(propertyNode)
    }
}
