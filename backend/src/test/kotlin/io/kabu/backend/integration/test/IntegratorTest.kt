package io.kabu.backend.integration.test

import io.kabu.backend.integration.Integrator
import io.kabu.backend.integration.render.GraphVisualizer
import io.kabu.backend.node.Node
import org.junit.Assert.assertEquals


open class IntegratorTest {

    protected fun getDiagramOfIntegrated(vararg graphs: Set<Node>, removeIrrelevant: Boolean = true): String {
        val integrator = Integrator()
        graphs.forEach {
            integrator.integrate(it, removeIrrelevant)
        }
        val visualizer = GraphVisualizer()
        val styledDiagram = visualizer.generateMermaidDiagramAsFlowchart(integrator.integrated, styling = true)
        val unStyledDiagram = visualizer.generateMermaidDiagramAsFlowchart(integrator.integrated, styling = false)
        println(visualizer.getMermaidRenderLink(styledDiagram))
        println(unStyledDiagram)
        return unStyledDiagram
    }

    protected fun buildAndShowGraph(graphCreator: GraphBuilder.() -> Unit): Set<Node> {
        val graphBuilder = GraphBuilder()
        graphBuilder.graphCreator()
        return graphBuilder.nodes
            .also { showDiagram(it) }
    }

    private fun showDiagram(nodes: Set<Node>, styling: Boolean = true) {
        val visualizer = GraphVisualizer()
        val diagram = visualizer.generateMermaidDiagramAsFlowchart(nodes, styling)
        println(visualizer.getMermaidRenderLink(diagram))
        println(diagram)
    }

    protected infix fun String.assertEq(actual: String) {
        assertEquals(this.trimIndent() + "\n", actual)
    }
}

class GraphBuilder {
    val nodes = mutableSetOf<Node>()

    operator fun <T : Node> T.unaryPlus(): T = also { nodes.add(it) }
}
