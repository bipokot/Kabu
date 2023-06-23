package io.kabu.backend.node.factory.node

import com.squareup.kotlinpoet.INT
import io.kabu.backend.declaration.properties.DispatcherCallsCounterPropertyDeclaration
import io.kabu.backend.node.BasePropertyNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode

class DispatcherCallsCounterPropertyNode(
    name: String,
    namespaceNode: NamespaceNode,
) : BasePropertyNode(
    name = name,
    receiverTypeNode = null,
    returnTypeNode = INT.toFixedTypeNode(),
    namespaceNode = namespaceNode,
    isTerminal = false,
) {

    override fun createDeclarations() = listOf(
        DispatcherCallsCounterPropertyDeclaration(name)
    )
}
