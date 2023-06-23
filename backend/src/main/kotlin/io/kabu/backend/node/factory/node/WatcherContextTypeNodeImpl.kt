package io.kabu.backend.node.factory.node

import io.kabu.backend.declaration.classes.WatcherContextClassDeclaration
import io.kabu.backend.node.WatcherContextTypeNode
import io.kabu.backend.node.namespace.NamespaceNode

class WatcherContextTypeNodeImpl(
    name: String,
    namespaceNode: NamespaceNode,
) : WatcherContextTypeNode(
    name = name,
    desiredName = null,
    namespaceNode = namespaceNode,
) {

    private fun getWatcherContextClassDeclaration() = WatcherContextClassDeclaration(className)

    override fun createDeclarations() = listOf(getWatcherContextClassDeclaration())

    override fun toString() = "WatcherContextTypeNode($name)"

    init {
        updateLinks()
    }
}
