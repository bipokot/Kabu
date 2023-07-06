package io.kabu.backend.analyzer

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.analyzer.handler.IdentifierHandler
import io.kabu.backend.analyzer.handler.OperatorHandler
import io.kabu.backend.analyzer.handler.TerminalHandler
import io.kabu.backend.analyzer.handler.lambda.LambdaHandler
import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes.isOperatorInfoType
import io.kabu.backend.analyzer.handler.lambda.watcher.WatcherLambda
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.builder.patternParsingError
import io.kabu.backend.diagnostic.builder.strictParametersOrderingError
import io.kabu.backend.exception.PatternParsingException
import io.kabu.backend.inout.input.method.PatternMethod
import io.kabu.backend.legacy.AccessorObjectNameGenerator
import io.kabu.backend.node.Node
import io.kabu.backend.node.Nodes
import io.kabu.backend.node.ObjectTypeNode
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.factory.node.AccessorObjectTypeNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.parser.OperatorExpression
import io.kabu.backend.parser.PatternParser
import io.kabu.backend.parser.PatternString
import io.kabu.backend.processor.MethodsRegistry
import io.kabu.backend.processor.Options
import io.kabu.backend.provider.group.RawProviders
import io.kabu.backend.provider.provider.BaseProvider
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.util.Constants
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode


class AnalyzerImpl(
    override val method: PatternMethod,
    override val methodsRegistry: MethodsRegistry,
    override val contextPropertyName: String?,
    val options: Options,
    private val contextMediatorNamespaceNode: NamespaceNode? = null,
) : Analyzer {

    init {
        logger.debug("Analyzing method: $method")
    }

    private val nodes = Nodes()

    override fun <T: Node> registerNode(node: T): T =
        node.also { nodes.add(it) }

    override val expression = try {
        val patternOrigin = Origin(excerpt = method.pattern, parent = method.origin)
        PatternParser(PatternString(method.pattern, patternOrigin)).parse().getOrThrow()
    } catch (e: PatternParsingException) {
        patternParsingError(e)
    }

    val parametersRegistry = ParametersRegistry(method, expression)

    var currentWatcherLambda: WatcherLambda? = null

    private val methodPackageNode: NamespaceNode by lazy {
        PackageNode(method.packageName).also {
            registerNode(it)
            ensureAccessorObjectIsCreated(it)
        }
    }

    override val namespaceNode: NamespaceNode
        get() = currentWatcherLambda?.watcherContextTypeNode
            ?: contextMediatorNamespaceNode
            ?: methodPackageNode

    private var nextExpectedParameterIndex: Int = 0

    override val postponeLambdaExecution: Boolean = false

    init {
        ExpressionValidator().validateExpression(expression)
    }

    fun analyze(): Nodes {
        TerminalHandler(this)
            .handle(expression)

        return nodes
    }

    private fun ensureAccessorObjectIsCreated(packageNode: PackageNode) {
        if (!(options.hideInternalProperties && packageNode.accessorObjectNode == null)) return

        val accessorNode = createAccessorObjectForPackageNode(packageNode)
        registerNode(accessorNode)

        packageNode.accessorObjectNode = accessorNode
    }

    private fun createAccessorObjectForPackageNode(packageNode: PackageNode): ObjectTypeNode {
        val targetPackageNode = if (options.accessorObjectIsInSamePackage) {
            packageNode
        } else {
            PackageNode("${Constants.RUNTIME_PACKAGE}.generated.${packageNode.name}") //todo check work in tests
        }

        val accessorName = AccessorObjectNameGenerator.generateName()

        return AccessorObjectTypeNode(accessorName, targetPackageNode)
    }

    fun <R> withWatcherLambda(watcherLambda: WatcherLambda, block: () -> R): R {
        val oldWatcherLambda = currentWatcherLambda
        currentWatcherLambda = watcherLambda
        val result = block()
        currentWatcherLambda = oldWatcherLambda
        return result
    }

    override fun providerOf(expression: KotlinExpression): Provider {
        return when (expression) {
            is OperatorExpression -> OperatorHandler(this).handle(expression)
            is LambdaExpression -> LambdaHandler(this).handle(expression)
            is IdentifierLeaf -> IdentifierHandler(this).handle(expression)
        }
    }

    override fun createRawProvidersFromChildren(expression: OperatorExpression): RawProviders {
        val expressions = expression.children
        val left: List<Provider> = expressions.take(1)
            .map { providerOf(it) }

        // must be in between left and right parameters obtaining
        val operatorInfoParameter = method.parameters.getOrNull(nextExpectedParameterIndex)
            ?.takeIf { it.type.isOperatorInfoType }
            ?.let { BaseProvider(it.type.toFixedTypeNode(), it.origin) } //todo check: creating different objects for same method parameter

        val right: List<Provider> = expressions.drop(1)
            .map { providerOf(it) }

        return RawProviders(left + right, operatorInfoParameter)
    }

    internal fun checkStrictMethodParametersOrdering(expressionName: String, typeName: TypeName?) {
        if (!parametersRegistry.strictMethodParametersOrdering) return

        var expectedParameter = method.parameters[nextExpectedParameterIndex]
        if (expectedParameter.type.isOperatorInfoType)
            expectedParameter = method.parameters[++nextExpectedParameterIndex]
        if (!(expectedParameter.name == expressionName && expectedParameter.type == typeName)) {
            strictParametersOrderingError(expressionName, expectedParameter)
        }
        nextExpectedParameterIndex++
    }

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}

