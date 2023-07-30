package io.kabu.backend.integration.resolver.common

import io.kabu.backend.integration.Integrator
import io.kabu.backend.integration.resolver.ConflictResolver
import io.kabu.backend.node.Node

/**
 * Resolves conflict by rewiring nodes (swap current node with already integrated).
 */
open class RewiringConflictResolver(private val integrator: Integrator): ConflictResolver {

    override fun resolve(node1: Node, node2: Node) {
        val current = integrator.notIntegratedOf(node1, node2)
        val conflicting = integrator.integratedOf(node1, node2)

        integrator.rewireNodes(current, conflicting)
    }
}
