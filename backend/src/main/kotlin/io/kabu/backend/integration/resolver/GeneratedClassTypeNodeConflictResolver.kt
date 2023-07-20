package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.integration.render.GraphVisualizer.Companion.visualize
import io.kabu.backend.node.GeneratedTypeNode
import io.kabu.backend.node.Node

/**
 * Resolves GeneratedTypeNode-GeneratedTypeNode conflicts for classes
 */
//todo introduce GeneratedClassTypeNode and remove children
open class GeneratedClassTypeNodeConflictResolver(
    private val integrator: Integrator,
) : ConflictResolver {

    override fun resolve(node1: Node, node2: Node) {
        node1 as GeneratedTypeNode; node2 as GeneratedTypeNode

        resolveConflict(node1, node2)
    }

    private fun resolveConflict(node1: GeneratedTypeNode, node2: GeneratedTypeNode) {
        val current = integrator.notIntegratedOf(node1, node2)
        val conflicting = integrator.integratedOf(node1, node2)

        visualize(integrator.integrated, "Before conflict resolving")

        when {
            current.desiredName == null -> {
                current.name = integrator.pickAvailableTypeName(current.namespaceNode!!, conflicting.name)
                integrator.integrated.add(current)
            }

            conflicting.desiredName == null -> {
                conflicting.name = integrator.pickAvailableTypeName(current.namespaceNode!!, current.name)
                integrator.integrated.add(current)
            }

            else -> integrator.unresolvedConflictError(current, conflicting)
        }

        visualize(integrator.integrated, "After conflict resolving")
    }
}
