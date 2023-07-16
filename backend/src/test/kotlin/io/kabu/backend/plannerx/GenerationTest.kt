package io.kabu.backend.plannerx

import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.generator.Generator
import io.kabu.backend.inout.input.writer.SimpleFileWriter
import io.kabu.backend.integration.Integrator
import org.junit.Test


class GenerationTest : XTest() {

    private val generator = Generator(testMode = true)

    @Test
    fun test() {
        testPattern("s * i // s i")
    }

    private fun testPattern(patternWithSignature: String) {
        // analyze method + pattern
        val nodes = analyzePatternWithSignature(patternWithSignature)
        getDiagramOfNodes(nodes)

        // integrate nodes
        val integrator = Integrator()
        integrator.integrate(nodes)

        // generate code for integrated nodes
        generator.writeCode(integrator.integrated, SimpleFileWriter("src/test/kotlin"))
    }
}

private val logger = InterceptingLogging.logger {}

fun completion(vararg parameters: Any?) {
    logger.info { "completion: ${parameters.joinToString()}" }
}

fun Any?.completion(vararg parameters: Any?) {
    logger.info { "completion: $this; ${parameters.joinToString()}" }
}
