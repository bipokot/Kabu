package io.kabu.backend.integration.test

import io.kabu.backend.integration.kBoolean
import io.kabu.backend.integration.kInt
import io.kabu.backend.integration.kString
import io.kabu.backend.integration.pkgNode1
import io.kabu.backend.integration.pkgNode2
import io.kabu.backend.node.HolderTypeNode
import org.junit.Test


class HoldersConflictTest : IntegratorTest() {

    @Test
    fun `no holder conflict`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H2", mutableListOf(kString(), kBoolean()), pkgNode1)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1["class H1\n- String\n- Boolean\n"]
            class_p1_H2["class H2\n- String\n- Boolean\n"]
            class_p1_H1 -.-> pkg__p1
            class_p1_H2 -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    @Test
    fun `holder resolving`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1["class H1\n- String\n- Boolean\n"]
            class_p1_H2["class H2\n- String\n- Boolean\n"]
            class_p1_H1 -.-> pkg__p1
            class_p1_H2 -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    @Test
    fun `holder VS holder with desired name`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(kInt()), pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(kString()), pkgNode1, desiredName = "H1")
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H2["class H2\n- Int\n"]
            class_p1_H1["class H1\n- String\n"]
            class_p1_H2 -.-> pkg__p1
            class_p1_H1 -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    @Test
    fun `holder with desired name VS holder`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(kInt()), pkgNode1, desiredName = "H1")
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(kString()), pkgNode1)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1["class H1\n- Int\n"]
            class_p1_H2["class H2\n- String\n"]
            class_p1_H1 -.-> pkg__p1
            class_p1_H2 -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    // namespaces

    @Test
    fun `holders in different namespaces`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode2 = +pkgNode2()
            +HolderTypeNode("H1", mutableListOf(kString(), kBoolean()), pkgNode2)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1["class H1\n- String\n- Boolean\n"]
            pkg__p2["pkg p2\n"]
            class_p2_H1["class H1\n- String\n- Boolean\n"]
            class_p1_H1 -.-> pkg__p1
            class_p2_H1 -.-> pkg__p2
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }
}
