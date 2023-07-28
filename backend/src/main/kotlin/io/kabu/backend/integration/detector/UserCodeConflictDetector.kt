package io.kabu.backend.integration.detector

import io.kabu.backend.integration.detector.user.StaticUserCodeConflictDetector
import io.kabu.backend.node.Node
import io.kabu.backend.node.namespace.NamespaceNode


class UserCodeConflictDetector {

    private val staticUserCodeConflictDetector = StaticUserCodeConflictDetector()

    fun hasConflicts(node: Node): Boolean {
        return hasStaticConflicts(node) || hasDynamicConflicts(node)
    }

    private fun hasStaticConflicts(node: Node): Boolean {
        return staticUserCodeConflictDetector.hasConflicts(node)
    }

    private fun hasDynamicConflicts(node: Node): Boolean {
        //todo implement via KSP lookup
        return false
    }

    //todo implement via KSP
    //todo search for conflicting element (class, interface, object or property) in designated namespace
    fun isTypeNameOccupied(namespaceNode: NamespaceNode, typeName: String): Boolean {
        return false
    }
}
