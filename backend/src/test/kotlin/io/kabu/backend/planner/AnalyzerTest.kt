package io.kabu.backend.planner

import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.integration.render.GraphVisualizer
import io.kabu.backend.node.Nodes
import io.kabu.backend.pattern.PatternWithSignature
import io.kabu.backend.planner.AnalyzerTestUtils.FILEPATH
import io.kabu.backend.planner.AnalyzerTestUtils.clientMethod
import io.kabu.backend.planner.AnalyzerTestUtils.logger
import io.kabu.backend.planner.AnalyzerTestUtils.targetPackage
import io.kabu.backend.processor.MethodsRegistry
import io.kabu.backend.processor.Options
import io.kabu.backend.util.load
import io.kabu.backend.util.save
import org.junit.Assert
import org.junit.Ignore
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.nio.file.Files
import kotlin.streams.toList

@RunWith(Parameterized::class)
class AnalyzerTestParameterized(val raw: String, val diagram: String) : Assert() {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data(): List<Array<String>> {
            return load<List<TestCase>>(FILEPATH)
                .map { arrayOf(it.raw, it.diagram) }
        }
    }

    @Test
    fun testParsingAndTreeTransform_BlackBox() {
        logger.debug("Raw: $raw")

        val patternWithSignature = PatternWithSignature(raw)
        val nodes = getPlanForPatternWithSignature(patternWithSignature)
        val actualDiagram = GraphVisualizer().generateMermaidDiagramAsFlowchart(nodes, styling = false)
        assertEquals(diagram, actualDiagram)
    }
}

class AnalyzerTestManagement : Assert() {
    private val testedManuallyPath = "src/test/resources/io/kabu/backend/planner/plannerSuccessTestedManually.txt"

    @[Ignore Test]
    fun addManuallyTested() {
        val testedManually = Files.lines(File(testedManuallyPath).toPath())

        val newTestCases = testedManually.filter { it.isNotBlank() }.map { raw ->
            logger.debug("Raw: $raw")

            val patternWithSignature = PatternWithSignature(raw)
            val nodes = getPlanForPatternWithSignature(patternWithSignature)
            val actualDiagram = GraphVisualizer().generateMermaidDiagramAsFlowchart(nodes, styling = false)
            TestCase(raw, actualDiagram)
        }.toList()

        val list = load<List<TestCase>>(FILEPATH)
        save(list + newTestCases, FILEPATH)
    }

    @[Ignore Test]
    fun rewriteFile() {
        val oldTestCases = load<List<TestCase>>(FILEPATH)

        val rewrittenTestCases = oldTestCases.map { testCase ->
            logger.debug("Raw: ${testCase.raw}")

            val patternWithSignature = PatternWithSignature(testCase.raw)
            val nodes = getPlanForPatternWithSignature(patternWithSignature)
            val actualDiagram = GraphVisualizer().generateMermaidDiagramAsFlowchart(nodes, styling = false)
            TestCase(testCase.raw, actualDiagram)
        }.toList()

        save(rewrittenTestCases, FILEPATH)
    }
}

data class TestCase(
    val raw: String,
    val diagram: String,
)

private fun getPlanForPatternWithSignature(patternWithSignature: PatternWithSignature): Nodes {
    val (receiver, parameters, returned) = patternWithSignature.signature
    val method = GlobalPatternMethod(
        packageName = targetPackage,
        name = clientMethod,
        returnedType = returned,
        receiver = receiver,
        parameters = parameters,
        pattern = patternWithSignature.pattern,
        origin = Origin()
    )
    val options = Options(
        allowUnsafe = true,
        hideInternalProperties = false,
        accessorObjectIsInSamePackage = true,
    )
    return AnalyzerImpl(method, MethodsRegistry(), null, options).analyze()
}
