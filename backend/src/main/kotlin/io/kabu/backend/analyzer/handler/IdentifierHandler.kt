package io.kabu.backend.analyzer.handler

import com.squareup.kotlinpoet.ANY
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.node.FixedTypeNode
import io.kabu.backend.node.factory.node.PlaceholderPropertyNode
import io.kabu.backend.node.factory.node.WrapperPropertyNode
import io.kabu.backend.parser.BinaryExpression
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.group.FunDeclarationProvidersFactory
import io.kabu.backend.provider.group.RawProviders
import io.kabu.backend.provider.provider.ArgumentProvider
import io.kabu.backend.provider.provider.BaseProvider
import io.kabu.backend.provider.provider.EmptyProvider
import io.kabu.backend.provider.provider.HolderProvider
import io.kabu.backend.provider.provider.NoReceiverProvider
import io.kabu.backend.util.Constants
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode

class IdentifierHandler(analyzer: AnalyzerImpl) : Handler(analyzer) {

    fun handle(expression: IdentifierLeaf): BaseProvider {
        val parametersRegistry = analyzer.parametersRegistry
        val typeNode: FixedTypeNode? = parametersRegistry.parametersTypes[expression.name]
        return if (typeNode != null) {
            createIdentifierParameter(expression, typeNode)
        } else {
            createProperty(expression)
        }
    }

    private fun createIdentifierParameter(
        expression: IdentifierLeaf,
        fixedTypeNode: FixedTypeNode,
    ): ArgumentProvider {
        if (expression.isTerminal) {
            diagnosticError("An expression pattern can not be form from a value alone", expression)
        }

        analyzer.checkStrictMethodParametersOrdering(expression.name, fixedTypeNode.typeName)

        registerNode(fixedTypeNode)

        return ArgumentProvider(
            typeNode = fixedTypeNode,
            originalName = expression.name
        )
    }

    private fun createProperty(expression: IdentifierLeaf): BaseProvider {
        val parametersRegistry = analyzer.parametersRegistry

        if (parametersRegistry.receiverCatchIsNecessary) {
            parametersRegistry.receiverCatchIsNecessary = false
            return createReceiverHolderParameter(expression.name)
        }

        if (expression.isTerminal) {
            val rawProviders = RawProviders(emptyList(), operatorInfoParameter = null)
            return createTerminalReadOnlyProperty(expression.name, rawProviders)
        }

        return if (expression.isPropertyNameOfAccessExpression) {
            // property for Access expression will be created in operator handler
            EmptyProvider(FixedTypeNode(ANY, namespaceNode = null))
        } else {
            if (expression.isLeftSideOfAssign) {
                if (expression.parent?.isTerminal == true) {
                    createTerminalAssignableProperty(expression)
                } else {
                    // x { y = b } // b
                    val rawProviders = RawProviders(listOf(NoReceiverProvider()), operatorInfoParameter = null)
                    createHolderTypeAndAssignablePropertyViaWatchedParameter(rawProviders, expression)
                }
            } else {
                createPlaceholderProperty(expression.name)
            }
        }
    }

    private fun createTerminalAssignableProperty(expression: IdentifierLeaf): EmptyProvider {
        val propertyName = expression.name
        val receiverProvider = NoReceiverProvider()
        val assigningProvider = providerOf((expression.parent as BinaryExpression).children[1])

        return createTerminalAssignableProperty(propertyName, receiverProvider, assigningProvider)
    }

    private fun createPlaceholderProperty(name: String): EmptyProvider {
        val typeNode = createAndRegisterHolderType(emptyList())

        val propertyNode = PlaceholderPropertyNode(
            name = name,
            typeNode = typeNode,
            namespaceNode = namespaceNode,
        )
        registerNode(propertyNode)

        return EmptyProvider(typeNode)
    }

    private fun createReceiverHolderParameter(name: String): HolderProvider {
        val method = analyzer.method
        val receiverType = method.receiver?.type //todo use receiverTypeNode
            ?: diagnosticError("Creating receiver holder type declaration but no receiver type found", method)
        val receiverTypeNode = receiverType.toFixedTypeNode()
        val receiver = ArgumentProvider(receiverTypeNode, Constants.RECEIVER_PARAMETER_NAME)

        // Type
        val rawProviders = RawProviders(listOf(receiver), null)
        val funDeclarationProviders = FunDeclarationProvidersFactory.from(rawProviders, invertedOrdering = false)
        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        val evaluatedParameters = functionBlockContext.doEvaluation()
        val fieldTypes = evaluatedParameters.map { it.typeNode }
        val holderTypeNode = createAndRegisterHolderType(fieldTypes)

        val functionNode = WrapperPropertyNode(name, holderTypeNode, namespaceNode, funDeclarationProviders)
        registerNode(functionNode)

        return HolderProvider(holderTypeNode, evaluatedParameters)
    }
}
