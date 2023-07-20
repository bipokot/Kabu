package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.integration.areParametersEqual
import io.kabu.backend.integration.render.GraphVisualizer.Companion.visualize
import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.group.renameClashingParametersNames
import io.kabu.backend.util.decaps


/**
 * Resolves FunctionNode-FunctionNode conflict (both ways)
 */
@Suppress("UNUSED_PARAMETER")
class FunctionAndFunctionUniversalConflictResolver(private val integrator: Integrator): ConflictResolver {

    override fun resolve(node1: Node, node2: Node) {
        node1 as FunctionNode
        node2 as FunctionNode

        resolveConflictWithFunctionNode(node1, node2)
    }

    private fun resolveConflictWithFunctionNode(node1: FunctionNode, node2: FunctionNode) {
        val conflicting = integrator.integratedOf(node1, node2)
        val current = integrator.notIntegratedOf(node1, node2)
        if (!isResolvable(current, conflicting)) integrator.unresolvedConflictError(current, conflicting)

        visualize(integrator.integrated, "Before conflict resolving")

        conflicting.parameters.forEachIndexed { index, nameAndType ->
            val correspondingNameAndType = current.parameters[index]
            if (nameAndType.name != correspondingNameAndType.name) {
                nameAndType.name = generalizeName(nameAndType.typeNode)
            }
        }
        val parameterNames = renameClashingParametersNames(conflicting.parameters.map { it.name })
        conflicting.parameters.forEachIndexed { index, nameAndType -> nameAndType.name = parameterNames[index] }

        integrator.rewireNodes(current, conflicting)
        integrator.validateLinks()

        integrator.rewireNodes(current.returnTypeNode, conflicting.returnTypeNode)
        integrator.validateLinks()

        visualize(integrator.integrated, "After conflict resolving")
    }

    private fun generalizeName(typeNode: TypeNode): String {
        return typeNode.name.decaps()
    }

    private fun isResolvable(current: FunctionNode, conflicting: FunctionNode): Boolean {
        return bothAreNonTerminating(current, conflicting) &&
            modifiersAreEqual(current, conflicting) &&
            parametersAreEqual(current, conflicting) &&
            genericPropertiesAreEqual(current, conflicting) &&
            returnTypesAreMergeable(current, conflicting)
    }

    private fun returnTypesAreMergeable(current: FunctionNode, conflicting: FunctionNode): Boolean {
        val currentReturnTypeNode = current.returnTypeNode as? HolderTypeNode ?: return false
        val conflictingReturnTypeNode = conflicting.returnTypeNode as? HolderTypeNode ?: return false

        return areParametersEqual(currentReturnTypeNode.fieldTypes, conflictingReturnTypeNode.fieldTypes)
    }

    private fun bothAreNonTerminating(current: FunctionNode, conflicting: FunctionNode): Boolean {
        return !current.isTerminal && !conflicting.isTerminal
    }

    private fun modifiersAreEqual(current: FunctionNode, conflicting: FunctionNode): Boolean {
        return true
    }

    private fun parametersAreEqual(current: FunctionNode, conflicting: FunctionNode): Boolean {
        return true //todo
    }

    private fun genericPropertiesAreEqual(current: FunctionNode, conflicting: FunctionNode): Boolean {
        return true //todo
    }
}
