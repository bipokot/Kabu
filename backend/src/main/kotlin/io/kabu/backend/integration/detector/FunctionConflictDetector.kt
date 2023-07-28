package io.kabu.backend.integration.detector

import io.kabu.backend.integration.areParametersPlatformClashing
import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.Node


class FunctionConflictDetector {

    fun areConflicting(node: FunctionNode, other: Node): Boolean = when (other) {
        is FunctionNode -> areFunctionNodesConflicting(node, other)
        else -> false
    }

    private fun areFunctionNodesConflicting(node: FunctionNode, other: FunctionNode): Boolean {
        if (node.name != other.name) return false

        // don't take into account returning type intentionally
        return areParametersPlatformClashing(node.parameters, other.parameters)
    }
}
