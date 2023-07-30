package io.kabu.backend.analyzer

import io.kabu.backend.inout.input.method.PatternMethod
import io.kabu.backend.node.Node
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.OperatorExpression
import io.kabu.backend.processor.MethodsRegistry
import io.kabu.backend.provider.group.RawProviders
import io.kabu.backend.provider.provider.Provider


interface Analyzer {

    val postponeLambdaExecution: Boolean

    val method: PatternMethod

    val expression: KotlinExpression

    val methodsRegistry: MethodsRegistry

    val isLocalPattern: Boolean

    val namespaceNode: NamespaceNode

    fun <T: Node> registerNode(node: T): T

    fun providerOf(expression: KotlinExpression): Provider

    fun createRawProvidersFromChildren(expression: OperatorExpression): RawProviders

}
