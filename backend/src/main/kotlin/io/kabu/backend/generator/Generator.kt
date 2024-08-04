package io.kabu.backend.generator

import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.declaration.Declaration
import io.kabu.backend.declaration.classes.ContextMediatorClassDeclaration
import io.kabu.backend.declaration.classes.WatcherContextClassDeclaration
import io.kabu.backend.inout.input.writer.FileWriter
import io.kabu.backend.node.ContextMediatorTypeNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.WatcherContextTypeNode
import io.kabu.backend.node.sortTopologically


class Generator(private val testMode: Boolean = false) {
    private val filename: String = "Generated"

    fun writeCode(nodes: Set<Node>, writer: FileWriter) {
        val nodesList = sortTopologically(nodes)

        val packageNodes = findPackageNodes(nodesList)

        packageNodes.forEach { packageNode ->
            val packageCode = getCodeForPackage(packageNode)
            logger.debug { "Code for package '${packageNode.name}':\n$packageCode" }
            writer.writeFile(packageNode.name, filename, packageCode)
        }
    }

    fun getCodeForPackage(nodes: Set<Node>, packageName: String): String {
        val nodesList = sortTopologically(nodes)

        val packageNode = findPackageNodes(nodesList)
            .single { it.name == packageName }

        return getCodeForPackage(packageNode)
    }

    private fun findPackageNodes(nodes: Iterable<Node>): List<PackageNode> {
        return nodes.filterIsInstance<PackageNode>()
    }

    private fun getCodeForPackage(packageNode: PackageNode): String {
        val nodesInPackage = packageNode.derivativeNodes
        val writtenNodes = mutableSetOf<Node>(packageNode) // starting with "writing" node itself
        val unwrittenNodes = nodesInPackage.toMutableList()

        val gatheredDeclarations = gatherDeclarationsForNodes(unwrittenNodes, writtenNodes)
        val declarations = gatheredDeclarations.declarations

        return Writer(testMode).composeFileForDeclarations(packageNode.name, declarations)
    }

    private fun getDeclarationsForNode(node: Node): GatheredDeclarations {
        return when (node) {
            is WatcherContextTypeNode -> gatherWatcherContextTypeNodeDeclarations(node)
            is ContextMediatorTypeNode -> gatherContextMediatorTypeNodeDeclarations(node)
            else -> gatherNonContainerNodeDeclarations(node)
        }
    }

    private fun gatherWatcherContextTypeNodeDeclarations(node: WatcherContextTypeNode): GatheredDeclarations {
        val watcherContextDeclaration = (node.createDeclarations().single() as WatcherContextClassDeclaration)
        val gatheredDeclarations = gatherDeclarationsForContainerNode(node)
        watcherContextDeclaration.innerDeclarations.addAll(gatheredDeclarations.declarations)
        return GatheredDeclarations(listOf(watcherContextDeclaration), gatheredDeclarations.handledNodes + node)
    }

    private fun gatherContextMediatorTypeNodeDeclarations(node: ContextMediatorTypeNode): GatheredDeclarations {
        val contextMediatorDeclaration = (node.createDeclarations().single() as ContextMediatorClassDeclaration)
        val gatheredDeclarations = gatherDeclarationsForContainerNode(node)
        contextMediatorDeclaration.innerDeclarations.addAll(gatheredDeclarations.declarations)
        return GatheredDeclarations(listOf(contextMediatorDeclaration), gatheredDeclarations.handledNodes + node)
    }

    private fun gatherDeclarationsForContainerNode(node: Node): GatheredDeclarations {
        val innerNodes = node.derivativeNodes.filter { it.namespaceNode == node }

        val unwrittenNodes = innerNodes.toMutableList()
        val writtenNodes = mutableSetOf(node) // starting with "writing" node itself
        return gatherDeclarationsForNodes(unwrittenNodes, writtenNodes)
    }

    private fun gatherNonContainerNodeDeclarations(node: Node): GatheredDeclarations {
        return GatheredDeclarations(node.createDeclarations(), setOf(node))
    }

    private fun gatherDeclarationsForNodes(
        unwrittenNodes: MutableList<Node>,
        writtenNodes: MutableSet<Node>,
    ): GatheredDeclarations {
        val declarations = mutableListOf<Declaration>()
        val handledNodes = mutableSetOf<Node>()

        val toRemoveFromUnwritten = mutableSetOf<Node>()
        while (unwrittenNodes.isNotEmpty()) {
            val unwrittenIterator = unwrittenNodes.iterator()
            while (unwrittenIterator.hasNext()) {
                val node = unwrittenIterator.next()
                logger.trace { "Checking node: $node" }
                val hasNoUnwrittenDependencies = node.dependencies.none { it in unwrittenNodes }
                if (hasNoUnwrittenDependencies) {
                    val gatheredDeclarations = getDeclarationsForNode(node)
                    declarations.addAll(gatheredDeclarations.declarations)
                    handledNodes.addAll(gatheredDeclarations.handledNodes)
                    writtenNodes.addAll(gatheredDeclarations.handledNodes)
                    toRemoveFromUnwritten.addAll(gatheredDeclarations.handledNodes)
                }
            }
            unwrittenNodes.removeAll(toRemoveFromUnwritten)
            toRemoveFromUnwritten.clear()
        }

        return GatheredDeclarations(declarations, handledNodes)
    }

    private data class GatheredDeclarations(
        val declarations: List<Declaration>,
        val handledNodes: Set<Node>,
    )

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}
