package io.kabu.backend.node.namespace

import com.squareup.kotlinpoet.ClassName

/**
 * Defines a namespace associated with some class.
 */
interface ClassNamespaceNode : NamespaceNode {

    override val recursiveName: String
        get() = "${namespaceNode?.recursiveName?.let { "$it." }.orEmpty()}$name"

    override fun composeClassName(name: String): ClassName {
        val names = mutableListOf(name)
        var current: NamespaceNode? = this
        while (current is ClassNamespaceNode) {
            names.add(current.name)
            current = current.namespaceNode
        }
        current as PackageNamespaceNode
        return ClassName(current.name, names.reversed())
    }
}
