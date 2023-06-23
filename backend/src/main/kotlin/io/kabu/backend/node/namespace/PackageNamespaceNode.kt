package io.kabu.backend.node.namespace

import com.squareup.kotlinpoet.ClassName

interface PackageNamespaceNode : NamespaceNode {

    override val recursiveName: String
        get() = name

    override fun composeClassName(name: String): ClassName =
        ClassName(this.name, name)
}
