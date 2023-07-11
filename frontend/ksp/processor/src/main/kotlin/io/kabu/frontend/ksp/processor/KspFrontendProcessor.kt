package io.kabu.frontend.ksp.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.KSPLogger
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.processing.SymbolProcessorEnvironment
import com.google.devtools.ksp.processing.SymbolProcessorProvider
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.kabu.annotation.Context
import io.kabu.annotation.ContextCreator
import io.kabu.annotation.Pattern
import io.kabu.annotation.LocalPattern
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.common.log.LogSink
import io.kabu.backend.diagnostic.Diagnostic
import io.kabu.backend.exception.PatternProcessingException
import io.kabu.backend.inout.input.ProcessingInput
import io.kabu.backend.inout.input.method.ContextCreatorMethod
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.inout.input.method.LocalPatternMethod
import io.kabu.backend.inout.output.ProcessingOutput
import io.kabu.backend.processor.BackendProcessor
import io.kabu.backend.processor.Options
import io.kabu.backend.processor.PartialOptions
import io.kabu.frontend.ksp.processor.Constants.OPTIONS_PREFIX
import io.kabu.frontend.ksp.processor.util.builder.ContextCreatorFunctionBuilder
import io.kabu.frontend.ksp.processor.util.builder.GlobalPatternFunctionBuilder
import io.kabu.frontend.ksp.processor.util.builder.LocalPatternFunctionBuilder

class KspFrontendProcessor(
    private val codeGenerator: CodeGenerator,
    private val options: Map<String, String>,
    private val logger: KSPLogger,
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        openLogging()
        try {
            val contextCreators = getContextCreatorMethods(resolver) +
                    getContextCreatorMethodsFromContextClasses(resolver)

            val processingInput = ProcessingInput(
                fileWriter = KspFileWriter(codeGenerator),
                globalPatterns = getGlobalPatternMethods(resolver),
                localPatterns = getLocalPatternMethods(resolver),
                contextCreators = contextCreators,
            )

            val backendProcessor = BackendProcessor(Options.fromPartial(parseOptions(options)))
            val result = backendProcessor.process(processingInput)

            result.diagnostics.forEach { logDiagnostic(it) }

            //todo check should we return true in case of success
            if (result is ProcessingOutput.Failure) return emptyList()

        } catch (e: PatternProcessingException) {
            logDiagnostic(e.diagnostic)
        } catch (e: Exception) {
            logger.error(e.localizedMessage)
            e.printStackTrace()
            return emptyList()
        }
        closeLogging()
        return emptyList()
    }

    private fun getContextCreatorMethods(resolver: Resolver): List<ContextCreatorMethod> {
        val functionDeclarations = resolver
            .getSymbolsWithAnnotation(ContextCreator::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        val result = functionDeclarations.map { function ->
            ContextCreatorFunctionBuilder().build(function)
        }

        return result
    }

    private fun getContextCreatorMethodsFromContextClasses(resolver: Resolver): List<ContextCreatorMethod> {
        val typesDeclarations = resolver
            .getSymbolsWithAnnotation(Context::class.qualifiedName!!)
            .filterIsInstance<KSClassDeclaration>()
            .toList()

        val result = typesDeclarations.map { type ->
            ContextCreatorFunctionBuilder().build(type)
        }

        return result
    }

    private fun getLocalPatternMethods(resolver: Resolver): List<LocalPatternMethod> {
        val functionDeclarations = resolver
            .getSymbolsWithAnnotation(LocalPattern::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        val result = functionDeclarations.map { functionDeclaration ->
            LocalPatternFunctionBuilder().build(functionDeclaration)
        }

        return result
    }

    private fun getGlobalPatternMethods(resolver: Resolver): List<GlobalPatternMethod> {
        val functionDeclarations = resolver
            .getSymbolsWithAnnotation(Pattern::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()
            .toList()

        val result = functionDeclarations.map { functionDeclaration ->
            GlobalPatternFunctionBuilder().build(functionDeclaration)
        }

        return result
    }

    private fun parseOptions(options: Map<String, String>) = PartialOptions(
        allowUnsafe = options["$OPTIONS_PREFIX.allowUnsafe"]?.toBoolean(),
        hideInternalProperties = options["$OPTIONS_PREFIX.hideInternalProperties"]?.toBoolean(),
        accessorObjectIsInSamePackage = options["$OPTIONS_PREFIX.accessorObjectIsInSamePackage"]?.toBoolean(),
    )

    private fun logDiagnostic(diagnostic: Diagnostic) {
        val details = diagnostic.sourceCodeDetails?.let { "\n$it" }.orEmpty()
        logger.error(diagnostic.message + details)
    }

    private fun openLogging() {
        InterceptingLogging.logSink = object : LogSink {

            override fun error(message: String) = logger.error(message)

            override fun warn(message: String) = logger.warn(message)

            override fun info(message: String) = logger.info(message)
        }
    }

    private fun closeLogging() {
        InterceptingLogging.logSink = null
    }

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}

class KspFrontendProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
//        environment.logger.warn("Creating a processor")
        return KspFrontendProcessor(environment.codeGenerator, environment.options, environment.logger)
    }
}
