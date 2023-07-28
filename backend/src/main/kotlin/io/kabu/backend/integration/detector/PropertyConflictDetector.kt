package io.kabu.backend.integration.detector

import io.kabu.backend.integration.areParametersPlatformClashing
import io.kabu.backend.node.Node
import io.kabu.backend.node.PropertyNode
import io.kabu.backend.node.TypeNode


class PropertyConflictDetector {

    fun areConflicting(node: PropertyNode, other: Node): Boolean = when (other) {
        is PropertyNode -> isConflictingWithPropertyNode(node, other)
        is TypeNode -> isConflictingWithTypeNode(node, other)
        else -> false
    }

    private fun isConflictingWithPropertyNode(node: PropertyNode, other: PropertyNode): Boolean {
        if (node.name != other.name) return false

        // don't take into account returning type intentionally
        return areParametersPlatformClashing(
            listOfNotNull(node.receiverTypeNode),
            listOfNotNull(other.receiverTypeNode),
        )
    }

    private fun isConflictingWithTypeNode(node: PropertyNode, other: TypeNode): Boolean {
        if (node.name != other.name) return false

        // a property can conflict with type/object only if the property has not a receiver
        return node.receiverTypeNode == null
    }
}
