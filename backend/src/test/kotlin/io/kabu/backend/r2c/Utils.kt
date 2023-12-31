package io.kabu.backend.r2c

import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.generator.Generator
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.integration.Integrator
import io.kabu.backend.pattern.PatternWithSignature
import io.kabu.backend.processor.MethodsRegistry
import io.kabu.backend.processor.Options
import io.kabu.backend.processor.PartialOptions
import io.kabu.backend.r2c.scripting.evalFile
import io.kabu.backend.util.Constants.BACKEND_PACKAGE
import org.junit.Assert
import java.io.ByteArrayOutputStream
import java.io.PrintStream
import kotlin.script.experimental.api.ResultWithDiagnostics

fun completion(vararg parameters: Any?) {
    println("completion: ${parameters.joinToString()}") // println must be used
}

fun Any?.completionWithReceiver(vararg parameters: Any?) {
    println("completion: $this; ${parameters.joinToString()}") // println must be used
}

fun completionOutputOf(raw: String, sample: String): String {
    logger.debug("Raw: $raw")

    val patternWithSignature = PatternWithSignature(raw)
    val (receiver, parameters, returnedType) = patternWithSignature.signature
    val method = GlobalPatternMethod(
        packageName = TARGET_PACKAGE,
        name = if (receiver != null) CLIENT_METHOD_WITH_RECEIVER else CLIENT_METHOD,
        returnedType = returnedType,
        receiver = receiver,
        parameters = parameters,
        pattern = patternWithSignature.pattern,
        origin = unknownOrigin
    )
    val options = Options.fromPartial(
        PartialOptions(
            hideInternalProperties = true,
            accessorObjectIsInSamePackage = true
        )
    )
    val nodes = AnalyzerImpl(method, MethodsRegistry(), options).analyze()
    val integrator = Integrator()
    integrator.integrate(nodes, removeIrrelevant = false)
    val scriptGenerated = Generator(testMode = true).getCodeForPackage(integrator.integrated, method.packageName)
    // ---

    val scriptString = scriptGenerated +
        """
            
        run {
            $sample
        }
        """.trimIndent()
    println("\n$DELIMITER\n${scriptString}\n$DELIMITER")

    // FQCNs don't work in test scripts properly
    val fixedGeneratedScript = scriptGenerated
        .replace("io.kabu.backend.r2c.", "io.kabu.backend.r2c.Script_simplescript.")
    val fixedScriptString = fixedGeneratedScript +
        """
            
        run {
            $sample
        }
        """.trimIndent()

    val out: String = captureOut {
        val res = evalFile(fixedScriptString)
        (res as? ResultWithDiagnostics.Failure)?.let {
            System.err.println(it)
            it.reports.forEach {
                System.err.println("\nProblem: $it")
            }
        }
        Assert.assertTrue(res is ResultWithDiagnostics.Success)
    }
    logger.debug(out)
    return out
}

private const val CLIENT_METHOD = "completion"
private const val CLIENT_METHOD_WITH_RECEIVER = "completionWithReceiver"
private const val TARGET_PACKAGE = "$BACKEND_PACKAGE.r2c"
private const val DELIMITER = "================================================="
val unknownOrigin = Origin()
private val logger = InterceptingLogging.logger {}

internal fun captureOut(body: () -> Unit): String {
    val outStream = ByteArrayOutputStream()
    val prevOut = System.out
    System.setOut(PrintStream(outStream))
    try {
        body()
    } finally {
        System.out.flush()
        System.setOut(prevOut)
    }
    return outStream.toString().trim()
}
