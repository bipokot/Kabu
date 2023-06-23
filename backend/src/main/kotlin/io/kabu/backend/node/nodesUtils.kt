package io.kabu.backend.node


fun sortTopologically(graph: Set<Node>): List<Node> {
    val remainingNodes = graph.toMutableList()
    val resolvedNodes = mutableListOf<Node>()
    while (remainingNodes.isNotEmpty()) {
        val iterator = remainingNodes.iterator()
        while (iterator.hasNext()) {
            val node = iterator.next()
            val hasNoUnresolvedDependencies = node.dependencies.all { it in resolvedNodes }
            if (hasNoUnresolvedDependencies) {
                resolvedNodes += node
                iterator.remove()
            }
        }
    }
    return resolvedNodes
}

internal fun <T : Any> T.className(): String = this::class.simpleName!!

internal fun <T : Node> T.namespaceRecursiveName(): String? = namespaceNode?.recursiveName
