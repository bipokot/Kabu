package io.kabu.backend.analyzer.handler.lambda.extension

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.builder.extensionAnnotationMissingError
import io.kabu.backend.diagnostic.builder.unknownFunctionParameterNameError
import io.kabu.backend.inout.input.method.PatternMethod
import io.kabu.backend.node.ContextMediatorTypeNode
import io.kabu.backend.node.DerivativeTypeNode
import io.kabu.backend.node.factory.node.ContextMediatorTypeNodeImpl
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parameter.Parameter
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.processor.MethodsRegistry
import io.kabu.backend.provider.provider.ArgumentProvider
import io.kabu.backend.provider.provider.ExtensionLambdaProvider
import io.kabu.backend.util.Constants.EXTENSION_ANNOTATION
import io.kabu.backend.util.Constants.EXTENSION_CONTEXT_PROPERTY_NAME
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

        val contextMediatorNamespaceNode = namespaceNode.getRoot()

        val contextMediatorClassSimpleName = contextMediatorNamespaceNode.typeNameGenerator.generateNextTypeName()
        val contextMediatorTypeNode = ContextMediatorTypeNodeImpl(
            name = contextMediatorClassSimpleName,
            namespaceNode = contextMediatorNamespaceNode,
            //todo Origin() && Parameter ?
            contextProperty = Parameter(EXTENSION_CONTEXT_PROPERTY_NAME, extensionContextTypeName, Origin()),
        )
        registerNode(contextMediatorTypeNode)

        val localPatternMethods = analyzer.methodsRegistry.getLocalPatternMethods(extensionContextTypeName)
        localPatternMethods.forEach {
            analyzeMethod(it, contextMediatorTypeNode, analyzer.methodsRegistry, EXTENSION_CONTEXT_PROPERTY_NAME)
        }

        val returningTypeNode = DerivativeTypeNode(namespaceNode, mutableListOf(contextMediatorTypeNode)) { deps ->
            LambdaTypeName.get(returnType = UNIT, receiver = (deps.first() as ContextMediatorTypeNode).className)
        }
        registerNode(returningTypeNode)

        return ExtensionLambdaProvider(
            typeNode = returningTypeNode,
            returningProvider = ArgumentProvider(extensionContextTypeNode, parameterName),
            contextMediatorTypeNode = contextMediatorTypeNode,
            contextCreatorDefinition = extensionContextCreatorDefinition,
            destinationParameterTypeNode = destinationParameterType,
            analyzer = analyzer,
        )
    }

    private fun analyzeMethod(
        method: PatternMethod,
        contextMediatorNamespaceNode: NamespaceNode,
        methodsRegistry: MethodsRegistry,
        contextPropertyName: String?
    ) {
        val nodes = AnalyzerImpl(
            method = method,
            methodsRegistry = methodsRegistry,
            contextPropertyName = contextPropertyName,
            options = analyzer.options,
            contextMediatorNamespaceNode = contextMediatorNamespaceNode
        ).analyze()
        nodes.forEach {
            registerNode(it)
        }
    }
}
