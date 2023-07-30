package io.kabu.backend.integration.resolver

import io.kabu.backend.node.Node

interface ConflictResolver {

    //todo any conflict resolver must take user types into account
    fun resolve(node1: Node, node2: Node)
}
