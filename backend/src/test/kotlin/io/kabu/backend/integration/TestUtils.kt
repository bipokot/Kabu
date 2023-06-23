package io.kabu.backend.integration

import io.kabu.backend.declaration.Declaration
import io.kabu.backend.integration.NameAndType
import io.kabu.backend.integration.NamedTypeNode
import io.kabu.backend.node.FixedTypeNode
import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.className
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.node.namespaceRecursiveName

fun pkgNode1() = PackageNode("p1")
fun pkgNode2() = PackageNode("p2")

fun stringNat(name: String = "s") = nat(name, kString())
fun intNat(name: String = "i") = nat(name, kInt())
fun booleanNat(name: String = "b") = nat(name, kBoolean())

fun kString(): FixedTypeNode = fixedTypeNode("kotlin", "String")
fun kInt(): FixedTypeNode = fixedTypeNode("kotlin", "Int")
fun kBoolean(): FixedTypeNode = fixedTypeNode("kotlin", "Boolean")
fun kUnit(): FixedTypeNode = fixedTypeNode("kotlin", "Unit")

fun nat(name: String, typeNode: TypeNode) = NaT(name, typeNode)

fun fixed(name: String, namespaceNode: NamespaceNode? = null) =
    FixedTypeNode(namespaceNode!!.composeClassName(name), namespaceNode)

private fun fixedTypeNode(packageName: String, name: String): FixedTypeNode {
    val packageNode = PackageNode(packageName)
    return FixedTypeNode(packageNode.composeClassName(name), packageNode)
}

typealias NaT = NamedTypeNode

//todo remove
class TestFunctionNode(
    override val name: String,
    override val parameters: List<NameAndType>,
    override var returnTypeNode: TypeNode,
    override var namespaceNode: NamespaceNode?,
    override val isTerminal: Boolean = false,
) : FunctionNode {

    override fun createDeclarations() = emptyList<Declaration>()

    override fun toString() = "${className()}: fun ${namespaceRecursiveName()}/$name(): ${returnTypeNode.name}"
    override val derivativeNodes = mutableSetOf<Node>()

    init {
        updateLinks()
    }
}
