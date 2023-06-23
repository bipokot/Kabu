package io.kabu.backend.integration.detector

import io.kabu.backend.node.Node
import io.kabu.backend.node.ObjectTypeNode
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.PropertyNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.factory.node.AccessorObjectTypeNode


class TypeConflictDetector {

    fun areConflicting(node: TypeNode, other: Node): Boolean = when (other) {
        is PropertyNode -> isConflictingWithPropertyNode(node, other)
        is ObjectTypeNode -> isConflictingWithObjectNode(node, other)
        is TypeNode -> isConflictingWithTypeNode(node, other)
        else -> false
    }

    private fun isConflictingWithObjectNode(node: TypeNode, other: ObjectTypeNode): Boolean {
        return if (node is AccessorObjectTypeNode && other is AccessorObjectTypeNode) {
            val nodePackage = node.dependencies.find { it is PackageNode }
            val otherPackage = node.dependencies.find { it is PackageNode }
            nodePackage != null && otherPackage != null && nodePackage.name == otherPackage.name
        } else false
    }

    private fun isConflictingWithTypeNode(node: TypeNode, other: TypeNode): Boolean {
        return node.name == other.name
    }

    private fun isConflictingWithPropertyNode(node: TypeNode, other: PropertyNode): Boolean {
        if (node.name != other.name) return false

        return other.receiverTypeNode == null
    }
}
