package io.kabu.backend.integration.test

import io.kabu.backend.exception.UnresolvedConflictException
import io.kabu.backend.integration.fixed
import io.kabu.backend.integration.pkgNode1
import io.kabu.backend.node.BasePropertyNode
import io.kabu.backend.node.HolderTypeNode
import org.junit.Test


class PropertyVsTypeConflictTest : IntegratorTest() {
    //todo test conflicts with object declarations

    // no desired names

    @Test
    fun `property VS type`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(), pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1Type", mutableListOf(), pkgNode1)
            +BasePropertyNode("H1", receiverTypeNode = null, holder1, pkgNode1)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H2["class H2\n"]
            class_p1_H1Type["class H1Type\n"]
            val_p1_H1("val H1\n: H1Type")
            class_p1_H2 -.-> pkg__p1
            class_p1_H1Type -.-> pkg__p1
            val_p1_H1 --> class_p1_H1Type
            val_p1_H1 -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    @Test
    fun `type VS property`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1Type", mutableListOf(), pkgNode1)
            +BasePropertyNode("H1", receiverTypeNode = null, holder1, pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(), pkgNode1)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_H1Type["class H1Type\n"]
            val_p1_H1("val H1\n: H1Type")
            class_p1_H2["class H2\n"]
            class_p1_H1Type -.-> pkg__p1
            val_p1_H1 --> class_p1_H1Type
            val_p1_H1 -.-> pkg__p1
            class_p1_H2 -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    // desired names

    @Test(expected = UnresolvedConflictException::class)
    fun `property VS type with desired name`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(), pkgNode1, desiredName = "H1")
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1Type", mutableListOf(), pkgNode1)
            +BasePropertyNode("H1", receiverTypeNode = null, holder1, pkgNode1)
        }
        """
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    @Test(expected = UnresolvedConflictException::class)
    fun `type with desired name VS property`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("H1Type", mutableListOf(), pkgNode1)
            +BasePropertyNode("H1", receiverTypeNode = null, holder1, pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +HolderTypeNode("H1", mutableListOf(), pkgNode1, desiredName = "H1")
        }
        """
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    // fixed types

    @Test(expected = UnresolvedConflictException::class)
    fun `property VS fixed type`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +fixed("UserType", pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("PropertyType", mutableListOf(), pkgNode1)
            +BasePropertyNode("UserType", receiverTypeNode = null, holder1, pkgNode1)
        }
        """
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    @Test(expected = UnresolvedConflictException::class)
    fun `fixed type VS property`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val holder1 = +HolderTypeNode("PropertyType", mutableListOf(), pkgNode1)
            +BasePropertyNode("UserType", receiverTypeNode = null, holder1, pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +fixed("UserType", pkgNode1)
        }
        """
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    // properties with receivers
    
    @Test
    fun `property with receiver VS fixed type`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +fixed("UserType", pkgNode1) //todo fix test
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val propType = +HolderTypeNode("PropertyType", mutableListOf(), pkgNode1)
            val receiver = +HolderTypeNode("ReceiverType", mutableListOf(), pkgNode1)
            +BasePropertyNode("UserType", receiver, propType, pkgNode1)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_UserType["class UserType\n"]
            class_p1_PropertyType["class PropertyType\n"]
            class_p1_ReceiverType["class ReceiverType\n"]
            val_p1_UserType("val UserType\nRECEIVER: ReceiverType\n: PropertyType")
            class_p1_UserType -.-> pkg__p1
            class_p1_PropertyType -.-> pkg__p1
            class_p1_ReceiverType -.-> pkg__p1
            val_p1_UserType --> class_p1_ReceiverType
            val_p1_UserType --> class_p1_PropertyType
            val_p1_UserType -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }

    @Test
    fun `fixed type VS property with receiver`() {
        val graph1 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            val propType = +HolderTypeNode("PropertyType", mutableListOf(), pkgNode1)
            val receiver = +HolderTypeNode("ReceiverType", mutableListOf(), pkgNode1)
            +BasePropertyNode("UserType", receiver, propType, pkgNode1)
        }
        val graph2 = buildAndShowGraph {
            val pkgNode1 = +pkgNode1()
            +fixed("UserType", pkgNode1)
        }
        """
            flowchart
            pkg__p1["pkg p1\n"]
            class_p1_PropertyType["class PropertyType\n"]
            class_p1_ReceiverType["class ReceiverType\n"]
            val_p1_UserType("val UserType\nRECEIVER: ReceiverType\n: PropertyType")
            class_p1_UserType["class UserType\n"]
            class_p1_PropertyType -.-> pkg__p1
            class_p1_ReceiverType -.-> pkg__p1
            val_p1_UserType --> class_p1_ReceiverType
            val_p1_UserType --> class_p1_PropertyType
            val_p1_UserType -.-> pkg__p1
            class_p1_UserType -.-> pkg__p1
        """ assertEq getDiagramOfIntegrated(graph1, graph2, removeIrrelevant = false)
    }
}
