package io.kabu.backend.integration.detector

import io.kabu.backend.node.Node
import io.kabu.backend.node.PackageNode


class PackageConflictDetector {

    fun areConflicting(node: PackageNode, other: Node): Boolean = when (other) {
        is PackageNode -> isConflictingWithPackageNode(node, other)
        else -> false
    }

    private fun isConflictingWithPackageNode(node: PackageNode, other: PackageNode): Boolean {
        return node.name == other.name
    }
}
