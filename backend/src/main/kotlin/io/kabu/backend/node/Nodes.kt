package io.kabu.backend.node

class Nodes private constructor(private val mutableSet: MutableSet<Node>) : Set<Node> by mutableSet {
    constructor() : this(mutableSetOf())

    fun add(node: Node) {
        mutableSet.add(node)
    }
}
