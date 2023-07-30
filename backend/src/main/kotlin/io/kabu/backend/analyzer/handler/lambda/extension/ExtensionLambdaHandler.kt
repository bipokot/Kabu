package io.kabu.backend.analyzer.handler.lambda.extension

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.diagnostic.builder.extensionAnnotationMissingError
import io.kabu.backend.diagnostic.builder.unknownFunctionParameterNameError
import io.kabu.backend.node.ContextMediatorTypeNode
import io.kabu.backend.node.DerivativeTypeNode
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.provider.provider.ArgumentProvider
import io.kabu.backend.provider.provider.ExtensionLambdaProvider
import io.kabu.backend.util.Constants.EXTENSION_ANNOTATION
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode

class ExtensionLambdaHandler(
    private val analyzer: AnalyzerImpl
) : Analyzer by analyzer {

    fun handle(expression: LambdaExpression): ExtensionLambdaProvider {
        val extensionAnnotationData = expression.annotations
            .find { it.name == EXTENSION_ANNOTATION }
            ?.let { ExtensionAnnotation.from(it) }
            ?: extensionAnnotationMissingError(expression)

        // destination parameter info
        val parameterName = extensionAnnotationData.destinationParameterName
        val destinationParameterType = analyzer.parametersRegistry.parametersTypes[parameterName]
            ?: unknownFunctionParameterNameError(parameterName, analyzer.method)

        val extensionContextCreatorDefinition = extensionAnnotationData.contextCreatorDefinition

        val extensionContextTypeName =
            analyzer.methodsRegistry.getExtensionContextType(extensionContextCreatorDefinition.name)
        val extensionContextTypeNode = extensionContextTypeName.toFixedTypeNode()

        val contextMediatorTypeNode = methodsRegistry.getContextMediatorTypeNode(extensionContextTypeName).also { node ->
            registerNode(node)
            node.dependencies.forEach { registerNode(it) } //todo not recursive yet
        }

        val returningTypeNode = DerivativeTypeNode(namespaceNode, mutableListOf(contextMediatorTypeNode)) { deps ->
            LambdaTypeName.get(returnType = UNIT, receiver = (deps.first() as ContextMediatorTypeNode).typeName)
        }
        registerNode(returningTypeNode)

        return ExtensionLambdaProvider(
            typeNode = returningTypeNode,
            returningProvider = ArgumentProvider(extensionContextTypeNode, parameterName),
            contextMediatorTypeNode = contextMediatorTypeNode as ContextMediatorTypeNode, //todo fix in MethodsRegistry
            contextCreatorDefinition = extensionContextCreatorDefinition,
            destinationParameterTypeNode = destinationParameterType,
            analyzer = analyzer,
        )
    }
}
