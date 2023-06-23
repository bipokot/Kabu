package io.kabu.backend.legacy.planner

import io.kabu.backend.node.namespace.NamespaceNode


class FieldAccessCodeGenerator(val namespaceNode: NamespaceNode) {

    private val accessorName = namespaceNode.accessorObjectNode?.name

    fun generateFieldAccessorCode(parentName: String, childName: String): String {
        return if (accessorName != null) {
            "$parentName.run{$accessorName.$childName}"
        } else {
            "$parentName.$childName"
        }
    }
}
