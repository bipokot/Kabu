package io.kabu.backend.integration

import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.exception.UnresolvedConflictException
import io.kabu.backend.integration.detector.FunctionConflictDetector
import io.kabu.backend.integration.detector.PackageConflictDetector
import io.kabu.backend.integration.detector.PropertyConflictDetector
import io.kabu.backend.integration.detector.TypeConflictDetector
import io.kabu.backend.integration.detector.UserCodeConflictDetector
import io.kabu.backend.integration.resolver.UniversalConflictResolver
import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.PropertyNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.node.sortTopologically

class Integrator {

    private val packageConflictDetector = PackageConflictDetector()
    private val userCodeConflictDetector = UserCodeConflictDetector()
    private val functionConflictDetector = FunctionConflictDetector()
    private val typeConflictDetector = TypeConflictDetector()
    private val propertyConflictDetector = PropertyConflictDetector()
    private val universalConflictResolver = UniversalConflictResolver(this)

    internal val integrated = mutableSetOf<Node>()

    fun integrate(graph: Set<Node>, removeIrrelevant: Boolean = true) {
        val sortedNodes = sortTopologically(graph)
        sortedNodes.forEach { node ->
            if (node in integrated) return@forEach // skipping already integrated nodes

            logger.debug { "Integrating: '$node'" }

            val conflictingNode = findConflictingNode(node)
            if (conflictingNode != null) {
                logger.debug { "Resolving conflict with '$conflictingNode'" }
                resolveConflict(node, conflictingNode)
            } else {
                resolvePossibleConflictWithUserCode(node)
                integrateNode(node)
            }

            validateLinks()
        }

        if (removeIrrelevant) {
            removeIrrelevantNodes()
        }
    }

    private fun resolveConflict(current: Node, conflicting: Node) {
        universalConflictResolver.resolve(current, conflicting)
    }

    private fun findConflictingNode(node: Node): Node? {
        val subspace = getNamespaceSubspaceOfNode(node)

        val conflictingNode = integrated.singleOrNull {
            it.namespaceNode == node.namespaceNode &&
                getNamespaceSubspaceOfNode(it) == subspace &&
                nodesAreConflicting(node, it)
        }

        return conflictingNode
    }

    private fun resolvePossibleConflictWithUserCode(node: Node) {
        if (!userCodeConflictDetector.hasConflicts(node)) return

        //todo conflict with user declaration sometimes may be resolved by same means as conflict with nodes!
        error("Conflicting declaration in existing code for '$node'")
    }

    fun integrateNode(node: Node) {
        integrated += node
    }

    private fun nodesAreConflicting(node: Node, other: Node): Boolean {
        return when (node) {
            is PackageNode -> packageConflictDetector.areConflicting(node, other)
            is FunctionNode -> functionConflictDetector.areConflicting(node, other)
            is PropertyNode -> propertyConflictDetector.areConflicting(node, other)
            is TypeNode -> typeConflictDetector.areConflicting(node, other)
            else -> error("Unknown node type: $node")
        }
    }

    private fun getNamespaceSubspaceOfNode(node: Node): NamespaceSubspace {
        return when (node) {
            is FunctionNode -> NamespaceSubspace.FUNCTION
            else -> NamespaceSubspace.CLASS_INTERFACE_OBJECT_PROPERTY
        }
    }

    internal fun validateLinks() {
        integrated.forEach { node ->
            node.dependencies
                .find { dependency -> node !in dependency.derivativeNodes }
                ?.let {
                    error("Node '$node' isn't in derivativeNodes of '$it'")
                }
        }
        integrated.forEach { node ->
            node.derivativeNodes
                .find { derivativeNode -> node !in derivativeNode.dependencies }
                ?.let {
                    error("Node '$node' isn't in dependencies of '$it'")
                }
        }
    }

    internal fun rewireNodes(replaced: Node, replaceBy: Node) {
        // nodes depending on 'replaced' must depend on 'replaceBy' now
        val derivativeNodes = replaced.derivativeNodes.toList()
        derivativeNodes.forEach {
            it.replaceDependency(replaced, replaceBy)
            if (replaced in it.dependencies) {
                replaced.derivativeNodes.add(it)
            } else {
                replaced.derivativeNodes.remove(it)
            }
            if (replaceBy in it.dependencies) {
                replaceBy.derivativeNodes.add(it)
            } else {
                replaceBy.derivativeNodes.remove(it)
            }
        }
        // fixing links for 'replaced' node's dependencies
        replaced.dependencies.forEach {
            it.derivativeNodes.remove(replaced)
        }
        integrated.remove(replaced)
    }

    private fun removeIrrelevantNodes() {
        val irrelevantNodes = integrated.filter { isIrrelevantNode(it) }.toSet()
        logger.debug { "Irrelevant nodes: $irrelevantNodes" }
        integrated.removeAll(irrelevantNodes)
    }

    private fun isIrrelevantNode(node: Node): Boolean {
        val unusedHolder = (node as? HolderTypeNode)?.derivativeNodes?.isEmpty() == true

        return unusedHolder
    }

    internal fun <T: Node> notIntegratedOf(node1: T, node2: T): T {
        return when {
            node1 in integrated && node2 !in integrated -> node2
            node2 in integrated && node1 !in integrated -> node1
            else -> error("Both nodes are (not-)integrated: '$node1', '$node2'")
        }
    }

    internal fun <T: Node> integratedOf(node1: T, node2: T): T {
        return when {
            node1 in integrated && node2 !in integrated -> node1
            node2 in integrated && node1 !in integrated -> node2
            else -> error("Both nodes are (not-)integrated: '$node1', '$node2'")
        }
    }

    //todo check all cases with renaming to ensure
    // that newly generated name is not in conflict with any other declaraion (user or generated)
    internal fun pickAvailableTypeName(namespaceNode: NamespaceNode, vararg forbiddenNames: String): String {
        var typeName: String
        do {
            typeName = namespaceNode.typeNameGenerator.generateNextTypeName()
        } while (isTypeNameOccupied(namespaceNode, typeName, forbiddenNames))
        return typeName
    }

    private fun isTypeNameOccupied(
        namespaceNode: NamespaceNode,
        typeName: String,
        forbiddenNames: Array<out String>,
    ): Boolean {
        return typeName in forbiddenNames ||
            isTypeNameOccupiedByGeneratedCode(namespaceNode, typeName) ||
            isTypeNameOccupiedByUser(namespaceNode, typeName)
    }

    private fun isTypeNameOccupiedByGeneratedCode(namespaceNode: NamespaceNode, typeName: String): Boolean {
        // checking names of properties as well (same name subspace)
        return integrated.asSequence()
            .filter { it.namespaceNode === namespaceNode }
            .any {
                when (it) {
                    is TypeNode -> it.name == typeName
                    is PropertyNode -> it.name == typeName
                    else -> false
                }
            }
    }

    private fun isTypeNameOccupiedByUser(namespaceNode: NamespaceNode, typeName: String): Boolean {
        return userCodeConflictDetector.isTypeNameOccupied(namespaceNode, typeName)
    }

    internal fun unresolvedConflictError(current: Node, conflicting: Node): Nothing =
        throw UnresolvedConflictException("Conflict cannot be resolved: '$current' VS '$conflicting'")

    enum class NamespaceSubspace {
        CLASS_INTERFACE_OBJECT_PROPERTY,
        FUNCTION,
    }

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}
