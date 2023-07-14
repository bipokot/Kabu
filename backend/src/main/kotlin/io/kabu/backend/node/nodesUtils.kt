package io.kabu.backend.node


fun sortTopologically(graph: Set<Node>): List<Node> {
    var counter = 0
    val remainingNodes = graph.toMutableList()
    val resolvedNodes = mutableListOf<Node>()
    while (remainingNodes.isNotEmpty()) {
        if (counter++ > 100_000) error("Topological sort went into infinite loop, remaining nodes: $remainingNodes")
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
