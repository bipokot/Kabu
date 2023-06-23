package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.integration.areParametersEqual
import io.kabu.backend.integration.render.GraphVisualizer.Companion.visualize
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.PropertyNode

/**
 * Resolves PropertyNode-PropertyNode conflict (both ways)
 */
class PropertyAndPropertyUniversalConflictResolver(private val integrator: Integrator): ConflictResolver {

    override fun resolve(node1: Node, node2: Node) {
        node1 as PropertyNode; node2 as PropertyNode
        resolveConflict(node1, node2)
    }

    private fun resolveConflict(node1: PropertyNode, node2: PropertyNode) {
        val conflicting = integrator.integratedOf(node1, node2)
        val current = integrator.notIntegratedOf(node1, node2)
        if (!isResolvable(current, conflicting)) integrator.unresolvedConflictError(current, conflicting)

        visualize(integrator.integrated, "Before rewire")
        integrator.validateLinks()

        integrator.rewireNodes(current, conflicting)
        visualize(integrator.integrated, "After functions rewire")
        integrator.validateLinks()

        integrator.rewireNodes(current.returnTypeNode, conflicting.returnTypeNode)
        visualize(integrator.integrated, "After return types rewire")
        integrator.validateLinks()
    }

    private fun isResolvable(current: PropertyNode, conflicting: PropertyNode): Boolean {
        return bothAreNonTerminating(current, conflicting) &&
            parametersAreEqual(current, conflicting) &&
            genericPropertiesAreEqual(current, conflicting) &&
            modifiersAreEqual(current, conflicting) &&
            returnTypesAreMergeable(current, conflicting)
    }

    private fun bothAreNonTerminating(current: PropertyNode, conflicting: PropertyNode): Boolean {
        //todo case when current has terminal getter and conflicting has terminal setter
        return !current.isTerminal && !conflicting.isTerminal
    }

    private fun parametersAreEqual(current: PropertyNode, conflicting: PropertyNode): Boolean {
        return areParametersEqual(listOfNotNull(current.receiverTypeNode), listOfNotNull(conflicting.receiverTypeNode))
    }

    private fun returnTypesAreMergeable(current: PropertyNode, conflicting: PropertyNode): Boolean {
        //todo consider FixedTypes !!!
        val currentReturnHolderTypeNode = current.returnTypeNode as? HolderTypeNode
        val conflictingReturnHolderTypeNode = conflicting.returnTypeNode as? HolderTypeNode

        return if (currentReturnHolderTypeNode != null && conflictingReturnHolderTypeNode != null) {
            areParametersEqual(currentReturnHolderTypeNode.fieldTypes, conflictingReturnHolderTypeNode.fieldTypes)
        } else false
    }

    private fun genericPropertiesAreEqual(current: PropertyNode, conflicting: PropertyNode): Boolean {
        return true //todo
    }

    private fun modifiersAreEqual(current: PropertyNode, conflicting: PropertyNode): Boolean {
        return true //todo
    }
}
