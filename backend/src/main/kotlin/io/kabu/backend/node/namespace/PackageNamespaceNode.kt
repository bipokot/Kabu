package io.kabu.backend.node.namespace

import com.squareup.kotlinpoet.ClassName

/**
 * Defines a namespace associated with some package.
 */
interface PackageNamespaceNode : NamespaceNode {

    override val recursiveName: String
        get() = name

    override fun composeClassName(name: String): ClassName =
        ClassName(this.name, name)
}
