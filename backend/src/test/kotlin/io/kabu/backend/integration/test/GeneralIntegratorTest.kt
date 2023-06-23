package io.kabu.backend.integration.test

import org.junit.Test


class GeneralIntegratorTest : IntegratorTest() {

    @Test
    fun testEmptyGraph() {
        val graph1 = buildAndShowGraph {
        }
        val graph2 = buildAndShowGraph {
        }
        """
            flowchart
        """ assertEq getDiagramOfIntegrated(graph1, graph2)
    }
}
