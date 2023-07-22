package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.integration.render.GraphVisualizer.Companion.visualize
import io.kabu.backend.node.FixedTypeNode
import io.kabu.backend.node.GeneratedTypeNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.PropertyNode
import io.kabu.backend.node.TypeNode

/**
 * Resolves TypeNode-PropertyNode conflict (both ways)
 */
class TypeAndPropertyConflictResolver(private val integrator: Integrator) : ConflictResolver {

    override fun resolve(node1: Node, node2: Node) {
        val typeNode = node1 as TypeNode
        val propertyNode = node2 as PropertyNode

        when (typeNode) {
            is GeneratedTypeNode -> resolveForGeneratedTypeNode(typeNode, propertyNode)
            is FixedTypeNode -> integrator.unresolvedConflictError(typeNode, propertyNode)
        }
    }

    private fun resolveForGeneratedTypeNode(
        generatedTypeNode: GeneratedTypeNode,
        propertyNode: PropertyNode,
    ) {
        if (generatedTypeNode.desiredName != null) {
            integrator.unresolvedConflictError(generatedTypeNode, propertyNode)
        }

        visualize(integrator.integrated, "Before conflict resolving")

        generatedTypeNode.name = integrator.pickAvailableTypeName(generatedTypeNode.namespaceNode!!, propertyNode.name)
        integrator.integrated.add(integrator.notIntegratedOf(generatedTypeNode, propertyNode))

        visualize(integrator.integrated, "After conflict resolving")
    }
}
