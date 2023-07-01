package io.kabu.backend.processor

import io.kabu.annotation.Pattern
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.exception.PatternProcessingException
import io.kabu.backend.generator.Generator
import io.kabu.backend.inout.input.ProcessingInput
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.inout.output.ProcessingOutput
import io.kabu.backend.integration.Integrator
import io.kabu.backend.node.Nodes

class BackendProcessor(private val options: Options = Options.DEFAULT) {

//    private val diagnostics = mutableListOf<Diagnostic>() //todo use later for warnings

    fun process(processingInput: ProcessingInput): ProcessingOutput {
        logger.debug("Using options: $options")
        return try {

            processInternally(processingInput)

            ProcessingOutput.Success()
        } catch (e: PatternProcessingException) {
            ProcessingOutput.Failure(listOf(e.diagnostic))
        }
    }

    private fun processInternally(processingInput: ProcessingInput) {
        val globalPatternMethods = processingInput.globalPatterns
        val methodsRegistry = MethodsRegistry(processingInput)

        val integrator = Integrator()
        globalPatternMethods.forEach {
            val nodes = analyzeGlobalPatternMethod(it, methodsRegistry)
            integrator.integrate(nodes)
        }

        val generator = Generator()
        generator.writeCode(integrator.integrated, processingInput.fileWriter)
    }

    private fun analyzeGlobalPatternMethod(
        method: GlobalPatternMethod,
        methodsRegistry: MethodsRegistry
    ): Nodes {
        logger.info { "Analyzing ${Pattern::class.simpleName} method: $method" }
        return AnalyzerImpl(
            method = method,
            methodsRegistry = methodsRegistry,
            contextPropertyName = null,
            options = options
        ).analyze()
    }
}

private val logger = InterceptingLogging.logger {}
