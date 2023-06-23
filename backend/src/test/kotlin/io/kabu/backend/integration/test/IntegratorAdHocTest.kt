package io.kabu.backend.integration.test

import io.kabu.backend.integration.TestFunctionNode
import io.kabu.backend.integration.booleanNat
import io.kabu.backend.integration.intNat
import io.kabu.backend.integration.kBoolean
import io.kabu.backend.integration.kInt
import io.kabu.backend.integration.kString
import io.kabu.backend.integration.kUnit
import io.kabu.backend.integration.nat
import io.kabu.backend.integration.pkgNode1
import io.kabu.backend.integration.stringNat
import io.kabu.backend.node.BasePropertyNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.Node
import org.junit.Test

@Suppress("MaxLineLength")
class IntegratorAdHocTest : IntegratorTest() {

    // x = s / b + i // s b i
    val graph1: Set<Node> = run {
        val pkgNode1 = pkgNode1()
        val holder1 = HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
        val holder2 = HolderTypeNode("H2", mutableListOf(holder1, kInt()), pkgNode1)
        val div = TestFunctionNode("div", listOf(stringNat(), booleanNat()), holder1, pkgNode1)
        val plus = TestFunctionNode("plus", listOf(nat("holder1", holder1), intNat()), holder2, pkgNode1)
        val property = BasePropertyNode("x", receiverTypeNode = null, holder2, pkgNode1, isTerminal = true)
        setOf(pkgNode1, plus, property, holder1, holder2, div)
    }

    // x * s // s
    val graph2: Set<Node> = run {
        val pkgNode1 = pkgNode1()
        val propType = HolderTypeNode("Prop1", mutableListOf(), pkgNode1)
        val property = BasePropertyNode("x", receiverTypeNode = null, propType, pkgNode1)
        val times = TestFunctionNode("times", listOf(nat("x", propType), stringNat()), kUnit(), pkgNode1, isTerminal = true)
        setOf(pkgNode1, propType, times, property)
    }

    // s / b // s b
    val graph3: Set<Node> = run {
        val pkgNode1 = pkgNode1()
        val div = TestFunctionNode("div", listOf(stringNat(), booleanNat()), kUnit(), pkgNode1, isTerminal = true)
        setOf(pkgNode1, div)
    }

    // i + s / b // s b
    val graph4: Set<Node> = run {
        val pkgNode1 = pkgNode1()
        val holder1 = HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
        val div = TestFunctionNode("div", listOf(stringNat(), booleanNat()), holder1, pkgNode1)
        val holder2 = HolderTypeNode("H2", mutableListOf(kInt(), holder1), pkgNode1)
        val plus = TestFunctionNode("plus", listOf(intNat(), nat("holder1", holder1)), kUnit(), pkgNode1, isTerminal = true)
        setOf(pkgNode1, div, plus, holder2, holder1)
    }

    @Test
    fun testAdHoc() {
//        showDiagram(graph1)
//        showDiagram(graph4)
//        integrateAndShowResult(graph1, graph4, styling = false)
    }
}
