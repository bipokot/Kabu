package io.kabu.backend.planner

import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.renderKotlinExpressionTree
import io.kabu.backend.exception.PatternProcessingException
import io.kabu.backend.generator.Generator
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.inout.input.writer.SimpleFileWriter
import io.kabu.backend.integration.Integrator
import io.kabu.backend.parser.PatternParser
import io.kabu.backend.parser.PatternString
import io.kabu.backend.pattern.PatternWithSignature
import io.kabu.backend.planner.AnalyzerTestUtils.TEST_GENERATED_DIR
import io.kabu.backend.planner.AnalyzerTestUtils.clientMethod
import io.kabu.backend.planner.AnalyzerTestUtils.filename
import io.kabu.backend.planner.AnalyzerTestUtils.logger
import io.kabu.backend.planner.AnalyzerTestUtils.targetPackage
import io.kabu.backend.processor.MethodsRegistry
import io.kabu.backend.processor.Options
import org.junit.Assert
import org.junit.Test

class AnalyzerAdHocTest : Assert() {

    @Test
    @Suppress("MaxLineLength")
    fun testAdHoc() {
        val input = """
            x
                    """.trimIndent();

        try {
            val patternWithSignature = PatternWithSignature(input)
            printExpression(patternWithSignature.pattern)

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
            val options = Options.DEFAULT.copy(hideInternalProperties = false, accessorObjectIsInSamePackage = true)
            val nodes = AnalyzerImpl(method, MethodsRegistry(), null, options).analyze()
            val integrated = Integrator().apply { integrate(nodes) }.integrated
            val codeForPackage = Generator(testMode = true).getCodeForPackage(integrated, method.packageName)
            SimpleFileWriter(TEST_GENERATED_DIR).writeFile(method.packageName, filename, codeForPackage)
        } catch (e: PatternProcessingException) {
            logger.error(e) { "${e.localizedMessage}\n${e.diagnostic}" }
            throw e
        }
    }

    private fun printExpression(pattern: String) {
        val expression = PatternParser(PatternString(pattern)).parse().getOrThrow()
        logger.debug("Expression: $expression")
        println("\n" + renderKotlinExpressionTree(expression))
    }
}
