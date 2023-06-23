package io.kabu.backend.integration.detector.user

import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.PropertyNode
import io.kabu.backend.node.TypeNode

class StaticUserCodeConflictDetector {
    private val functionStaticUserCodeConflictDetector = FunctionStaticUserCodeConflictDetector()
    private val propertyStaticUserCodeConflictDetector = PropertyStaticUserCodeConflictDetector()
    private val typeStaticUserCodeConflictDetector = TypeStaticUserCodeConflictDetector()

    fun hasConflicts(node: Node): Boolean {
        return when (node) {
            is FunctionNode -> functionStaticUserCodeConflictDetector.hasConflicts(node)
            is PropertyNode -> propertyStaticUserCodeConflictDetector.hasConflicts(node)
            is TypeNode -> typeStaticUserCodeConflictDetector.hasConflicts(node)
            is PackageNode -> false // packages don't conflict
            else -> error("Unknown node type: $node")
        }
    }
}
