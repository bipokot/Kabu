package io.kabu.backend.integration.test

import io.kabu.backend.common.log.InterceptingLogging
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
        logger.debug { visualizer.getMermaidRenderLink(styledDiagram) }
        logger.debug { unStyledDiagram }
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
        logger.debug { visualizer.getMermaidRenderLink(diagram) }
        logger.debug { diagram }
    }

    protected infix fun String.assertEq(actual: String) {
        assertEquals(this.trimIndent() + "\n", actual)
    }

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}

class GraphBuilder {
    val nodes = mutableSetOf<Node>()

    operator fun <T : Node> T.unaryPlus(): T = also { nodes.add(it) }
}
