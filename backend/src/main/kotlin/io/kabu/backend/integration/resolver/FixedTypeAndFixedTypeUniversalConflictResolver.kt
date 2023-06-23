package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.node.FixedTypeNode
import io.kabu.backend.node.Node

/**
 * Resolves conflict when integrating a HolderTypeNode
 */
class FixedTypeAndFixedTypeUniversalConflictResolver(private val integrator: Integrator): ConflictResolver {

    override fun resolve(node1: Node, node2: Node) {
        node1 as FixedTypeNode; node2 as FixedTypeNode

        resolveConflict(node1, node2)
    }

    private fun resolveConflict(node1: FixedTypeNode, node2: FixedTypeNode) {
        val current = integrator.notIntegratedOf(node1, node2)
        val conflicting = integrator.integratedOf(node1, node2)

        integrator.rewireNodes(current, conflicting)
    }
}
