package io.kabu.backend.processor

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import io.kabu.annotation.LocalPattern
import io.kabu.annotation.Pattern
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.exception.PatternProcessingException
import io.kabu.backend.generator.Generator
import io.kabu.backend.inout.input.ProcessingInput
import io.kabu.backend.inout.input.method.ContextCreatorMethod
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.inout.input.method.PatternMethod
import io.kabu.backend.inout.output.ProcessingOutput
import io.kabu.backend.integration.Integrator
import io.kabu.backend.node.Node
import io.kabu.backend.node.Nodes
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.factory.node.ContextMediatorTypeNodeImpl
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parameter.Parameter
import io.kabu.backend.util.Constants

class BackendProcessor(private val options: Options = Options.DEFAULT) {

//    private val diagnostics = mutableListOf<Diagnostic>() //todo use later for warnings

    fun process(processingInput: ProcessingInput): ProcessingOutput {
        logger.debug("Using options: $options")
        return try {

            processInternally(processingInput)

            ProcessingOutput.Success()
        } catch (e: PatternProcessingException) {
            ProcessingOutput.Failure(listOf(e.diagnostic))
        }
    }

    private fun processInternally(processingInput: ProcessingInput) {
        val methodsRegistry = MethodsRegistry(processingInput)
        val integrator = Integrator()

        val nodesForExtensionContexts = analyzeContextCreators(processingInput.contextCreators, methodsRegistry)
        integrator.integrate(nodesForExtensionContexts)

        val globalPatternMethods = processingInput.globalPatterns
        globalPatternMethods.forEach {
            val nodes = analyzeGlobalPatternMethod(it, methodsRegistry)
            integrator.integrate(nodes)
        }

        val generator = Generator()
        generator.writeCode(integrator.integrated, processingInput.fileWriter)
    }

    private fun analyzeContextCreators(
        contextCreators: List<ContextCreatorMethod>,
        methodsRegistry: MethodsRegistry,
    ) : Nodes {
        val extensionContextTypes = contextCreators.map { it.returnedType }.distinct()

        val nodes = extensionContextTypes.flatMap { extensionContextTypeName ->
            analyzeContextType(extensionContextTypeName, methodsRegistry)
        }

        return Nodes().apply {
            nodes.forEach(this::add)
        }
    }

    private fun analyzeContextType(
        extensionContextTypeName: TypeName,
        methodsRegistry: MethodsRegistry,
    ): List<Node> {
        logger.info { "Handling extension context type: $extensionContextTypeName" }

        val packageName = when (extensionContextTypeName) {
            is ClassName -> extensionContextTypeName.packageName
            is ParameterizedTypeName -> extensionContextTypeName.rawType.packageName
            else -> diagnosticError("Unsupported extension context type: $extensionContextTypeName")
        }

        val packageNode = PackageNode(packageName)
        val contextMediatorClassSimpleName = packageNode.typeNameGenerator.generateNextTypeName()
        val contextMediatorTypeNode = ContextMediatorTypeNodeImpl(
            name = contextMediatorClassSimpleName,
            namespaceNode = packageNode,
            //todo Origin() && Parameter ?
            contextProperty = Parameter(Constants.EXTENSION_CONTEXT_PROPERTY_NAME, extensionContextTypeName, Origin()),
        )

        methodsRegistry.registerContextMediatorTypeNode(extensionContextTypeName, contextMediatorTypeNode)

        val localPatternMethods = methodsRegistry.getLocalPatternMethods(extensionContextTypeName)
        val nodesForLocalPatterns = localPatternMethods.flatMap {
            analyzeLocalPatternMethod(
                method = it,
                contextMediatorTypeNode = contextMediatorTypeNode,
                methodsRegistry = methodsRegistry,
            )
        }

        return listOf(packageNode, contextMediatorTypeNode) + nodesForLocalPatterns
    }

    private fun analyzeLocalPatternMethod(
        method: PatternMethod,
        contextMediatorTypeNode: NamespaceNode,
        methodsRegistry: MethodsRegistry,
    ) : Nodes {
        logger.info { "Analyzing ${LocalPattern::class.simpleName} method: $method" }
        return AnalyzerImpl(
            method = method,
            methodsRegistry = methodsRegistry,
            options = options,
            contextMediatorNamespaceNode = contextMediatorTypeNode //todo inconsistency in names
        ).analyze()
    }

    private fun analyzeGlobalPatternMethod(
        method: GlobalPatternMethod,
        methodsRegistry: MethodsRegistry
    ): Nodes {
        logger.info { "Analyzing ${Pattern::class.simpleName} method: $method" }
        return AnalyzerImpl(
            method = method,
            methodsRegistry = methodsRegistry,
            options = options
        ).analyze()
    }
}

private val logger = InterceptingLogging.logger {}
