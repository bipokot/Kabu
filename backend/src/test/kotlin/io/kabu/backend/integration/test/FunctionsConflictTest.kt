package io.kabu.backend.integration.test

import io.kabu.backend.exception.UnresolvedConflictException
import io.kabu.backend.integration.TestFunctionNode
import io.kabu.backend.integration.booleanNat
import io.kabu.backend.integration.kBoolean
import io.kabu.backend.integration.kString
import io.kabu.backend.integration.kUnit
import io.kabu.backend.integration.nat
import io.kabu.backend.integration.pkgNode1
import io.kabu.backend.integration.stringNat
import io.kabu.backend.node.HolderTypeNode
import org.junit.Test


@Suppress("MaxLineLength")
class FunctionsConflictTest : IntegratorTest() {

    @Test
    fun `identical functions can be resolved`() {
//        s / b  // s b
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("div", mutableListOf(stringNat(), booleanNat()), holder1, pkgNode1)
        }
//        s / b  // s b
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder2 = +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("div", mutableListOf(stringNat(), booleanNat()), holder2, pkgNode1)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1["class H1\n- String\n- Boolean\n"]
            fun_p1_div(["fun div\n- String\n- Boolean\n: H1"])
            class_p1_H1 -.-> pkg__p1
            fun_p1_div --> class_p1_H1
            fun_p1_div -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2)
    }

    @Test(expected = UnresolvedConflictException::class)
    fun `terminal functions cannot be resolved`() {
//        s / b  // s b
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("div", listOf(stringNat(), booleanNat()), holder1, pkgNode1, isTerminal = true)
        }
//        s / b  // s b
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder2 = +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("div", listOf(stringNat(), booleanNat()), holder2, pkgNode1, isTerminal = true)
        }
        """
            flowchart
            class_H1[class H1\ns: String\nb: Boolean\n]
            fun_div([fun div\nstring: String\nboolean: Boolean\n: H1])
            fun_div -.-> class_H1
        """ assertEq getDiagramOfIntegrated(graph1, graph2)
    }

    @Test(expected = UnresolvedConflictException::class)
    fun `terminal functions cannot be resolved (one of them)`() {
//        s / b  // s b
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("div", listOf(stringNat(), booleanNat()), holder1, pkgNode1)
        }
//        s / b  // s b
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder2 = +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("div", listOf(stringNat(), booleanNat()), holder2, pkgNode1, isTerminal = true)
        }
        """
            flowchart
            class_H1[class H1\ns: String\nb: Boolean\n]
            fun_div([fun div\nstring: String\nboolean: Boolean\n: H1])
            fun_div -.-> class_H1
        """ assertEq getDiagramOfIntegrated(graph1, graph2)
    }

    @Test
    fun `conflict in same graph`() {
        val graph1 = buildAndShowGraph {
//            s * b + s2 * b2  // s b s2 b2
//            (s * b) + (s2 * b2)  // s b s2 b2
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("times", listOf(stringNat(), booleanNat()), holder1, pkgNode1)
            val holder2 = +HolderTypeNode("H2", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("times", listOf(stringNat("s2"), booleanNat("b2")), holder2, pkgNode1)
            +TestFunctionNode("plus", listOf(nat("holder1", holder1), nat("holder2", holder2)), kUnit(), pkgNode1, isTerminal = true)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1["class H1\n- String\n- Boolean\n"]
            fun_p1_times(["fun times\n- String\n- Boolean\n: H1"])
            fun_p1_plus(["fun plus\n- H1\n- H1\n: Unit"])
            class_p1_H1 -.-> pkg__p1
            fun_p1_times --> class_p1_H1
            fun_p1_times -.-> pkg__p1
            fun_p1_plus --> class_p1_H1
            fun_p1_plus -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1)
    }

    @Test
    fun `2 conflicts in same graph`() {
        val graph1 = buildAndShowGraph {
//            s * b + s2 * b2 + s3 * b3  // s b s2 b2 s3 b3
//            ((s * b) + (s2 * b2)) + (s3 * b3)  // s b s2 b2 s3 b3
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("times", listOf(stringNat(), booleanNat()), holder1, pkgNode1)
            val holder2 = +HolderTypeNode("H2", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("times", listOf(stringNat("s2"), booleanNat("b2")), holder2, pkgNode1)
            val holder3 = +HolderTypeNode("Holder3", mutableListOf(kString(), kBoolean()), pkgNode1)
            +TestFunctionNode("times", listOf(stringNat("s3"), booleanNat("b3")), holder3, pkgNode1)
            val holder4 = +HolderTypeNode("Holder4", mutableListOf(holder1, holder2), pkgNode1)
            +TestFunctionNode("plus", listOf(nat("holder1", holder1), nat("holder2", holder2)), holder4, pkgNode1)
            +TestFunctionNode("plus", listOf(nat("holder4", holder4), nat("holder3", holder3)), kUnit(), pkgNode1, isTerminal = true)

        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1["class H1\n- String\n- Boolean\n"]
            fun_p1_times(["fun times\n- String\n- Boolean\n: H1"])
            class_p1_Holder4["class Holder4\n- H1\n- H1\n"]
            fun_p1_plus(["fun plus\n- H1\n- H1\n: Holder4"])
            _fun_p1_plus__2(["'fun plus'-2\n- Holder4\n- H1\n: Unit"])
            class_p1_H1 -.-> pkg__p1
            fun_p1_times --> class_p1_H1
            fun_p1_times -.-> pkg__p1
            class_p1_Holder4 --> class_p1_H1
            class_p1_Holder4 -.-> pkg__p1
            fun_p1_plus --> class_p1_H1
            fun_p1_plus --> class_p1_Holder4
            fun_p1_plus -.-> pkg__p1
            _fun_p1_plus__2 --> class_p1_Holder4
            _fun_p1_plus__2 --> class_p1_H1
            _fun_p1_plus__2 -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1)
    }
}
