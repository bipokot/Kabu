package io.kabu.backend.integration.resolver

import io.kabu.backend.node.Node

interface ConflictResolver {

    fun resolve(node1: Node, node2: Node)
}
