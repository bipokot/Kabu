package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.node.DerivativeTypeNode
import io.kabu.backend.node.Node

/**
 * Resolves conflict when integrating a HolderTypeNode
 */
class DerivativeTypeAndDerivativeTypeNodeConflictResolver(private val integrator: Integrator): ConflictResolver {

    override fun resolve(node1: Node, node2: Node) {
        node1 as DerivativeTypeNode; node2 as DerivativeTypeNode

        resolveConflict(node1, node2)
    }

    private fun resolveConflict(node1: DerivativeTypeNode, node2: DerivativeTypeNode) {
        val current = integrator.notIntegratedOf(node1, node2)
        integrator.integrateNode(current) //todo not resolving but coexisting
    }
}
