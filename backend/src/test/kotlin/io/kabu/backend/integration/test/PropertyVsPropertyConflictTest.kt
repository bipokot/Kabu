package io.kabu.backend.integration.test

import io.kabu.backend.integration.pkgNode1
import io.kabu.backend.node.BasePropertyNode
import io.kabu.backend.node.HolderTypeNode
import org.junit.Test


class PropertyVsPropertyConflictTest : IntegratorTest() {

    @Test
    fun `no conflicts`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1", mutableListOf(), pkgNode1)
            +BasePropertyNode("prop1", receiverTypeNode = null, holder1, pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder2 = +HolderTypeNode("H2", mutableListOf(), pkgNode1)
            +BasePropertyNode("prop2", receiverTypeNode = null, holder2, pkgNode1)

        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1["class H1\n"]
            val_p1_prop1("val prop1\n: H1")
            class_p1_H2["class H2\n"]
            val_p1_prop2("val prop2\n: H2")
            class_p1_H1 -.-> pkg__p1
            val_p1_prop1 --> class_p1_H1
            val_p1_prop1 -.-> pkg__p1
            class_p1_H2 -.-> pkg__p1
            val_p1_prop2 --> class_p1_H2
            val_p1_prop2 -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }
}
