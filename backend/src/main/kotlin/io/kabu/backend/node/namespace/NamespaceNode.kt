package io.kabu.backend.node.namespace

import com.squareup.kotlinpoet.ClassName
import io.kabu.backend.integration.namegen.SequentialTypeNameGenerator
import io.kabu.backend.node.Node
import io.kabu.backend.node.ObjectTypeNode

interface NamespaceNode : Node {
    val recursiveName: String // FQN

    val typeNameGenerator: SequentialTypeNameGenerator
    var accessorObjectNode : ObjectTypeNode?

    /**
     * Compose a ClassName for a [name]-named class inside this NamespaceNode
     */
    fun composeClassName(name: String): ClassName

    fun getRoot(): NamespaceNode {
        val namespace = namespaceNode ?: return this

        return namespace.getRoot()
    }
}
