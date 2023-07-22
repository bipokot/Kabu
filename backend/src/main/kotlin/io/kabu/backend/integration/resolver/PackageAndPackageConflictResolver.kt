package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.node.Node
import io.kabu.backend.node.PackageNode

/**
 * Resolves conflict when integrating a PackageNode
 */
@Suppress("MaxLineLength")
class PackageAndPackageConflictResolver(private val integrator: Integrator) : ConflictResolver {

    override fun resolve(node1: Node, node2: Node) {
        node1 as PackageNode; node2 as PackageNode

        resolveConflict(node1, node2)
    }

    private fun resolveConflict(node1: PackageNode, node2: PackageNode) {
        val current = integrator.notIntegratedOf(node1, node2)
        val conflicting = integrator.integratedOf(node1, node2)

        integrator.rewireNodes(current, conflicting)

        // rewiring accessor objects
        val currentAccessorObjectNode = current.accessorObjectNode
        val conflictingAccessorObjectNode = conflicting.accessorObjectNode
        if (currentAccessorObjectNode != null && conflictingAccessorObjectNode == null) {
            conflicting.accessorObjectNode = currentAccessorObjectNode
        }
    }
}
