package io.kabu.backend.plannerx

import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.renderKotlinExpressionTree
import io.kabu.backend.exception.PatternProcessingException
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.integration.render.GraphVisualizer
import io.kabu.backend.node.Nodes
import io.kabu.backend.parser.PatternParser
import io.kabu.backend.parser.PatternString
import io.kabu.backend.pattern.PatternWithSignature
import io.kabu.backend.processor.MethodsRegistry
import io.kabu.backend.processor.Options
import org.junit.Assert


open class XTest {

    protected infix fun String.assertEq(actual: String) {
        Assert.assertEquals(this.trimIndent() + "\n", actual)
    }

    companion object {
       protected val logger = InterceptingLogging.logger {}

        fun analyzePatternWithSignature(input: String): Nodes {
            try {
                val patternWithSignature = PatternWithSignature(input)
                val expression = PatternParser(PatternString(patternWithSignature.pattern)).parse().getOrThrow()
                logger.debug("Expression: $expression")
                println("\n" + renderKotlinExpressionTree(expression))

                val (receiver, parameters, returned) = patternWithSignature.signature
                val method = GlobalPatternMethod(
                    packageName = "io.kabu.backend.plannerx",
                    name = "completion",
                    returnedType = returned,
                    receiver = receiver,
                    parameters = parameters,
                    pattern = patternWithSignature.pattern,
                    origin = Origin()
                )
                val options = Options.DEFAULT.copy(hideInternalProperties = false, accessorObjectIsInSamePackage = true)

                return AnalyzerImpl(method, MethodsRegistry(), null, options).analyze()

            } catch (e: PatternProcessingException) {
                logger.error(e) { "${e.localizedMessage}\n${e.diagnostic}" }
                throw e
            }
        }

        fun getDiagramOfNodes(nodes: Nodes): String {
            val visualizer = GraphVisualizer()
            val styledDiagram = visualizer.generateMermaidDiagramAsFlowchart(nodes, styling = true)
            val unStyledDiagram = visualizer.generateMermaidDiagramAsFlowchart(nodes, styling = false)
            println(visualizer.getMermaidRenderLink(styledDiagram))
            println(unStyledDiagram)
            return unStyledDiagram
        }
    }
}
