package io.kabu.backend.planner.namespace.conflict.detector

import io.kabu.backend.pattern.PatternWithSignature
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.exception.PatternProcessingException
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.integration.detector.user.legacy.detector.KotlinLangConflictDetector
import io.kabu.backend.processor.MethodsRegistry
import io.kabu.backend.processor.Options
import io.kabu.backend.util.Constants.BACKEND_PACKAGE
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
abstract class ConflictDetector_Base(val raw: String, val outcome: String, val origin: String) : Assert() {

    abstract val testedManuallyPath: String
    abstract val filepath: String

    @Test
    fun test() {
        val (actualOutcome, actualOrigin) = getOutcomeAndOriginString(raw)

        assertEquals(outcome, actualOutcome)
        assertTrue(origin in actualOrigin)
    }

    companion object {

        fun load(filepath: String) = load<List<TestCase>>(filepath)
            .map { arrayOf(it.raw, it.outcome, it.origin) }
    }
}

open class ConflictDetector_Management(val testedManuallyPath: String, val filepath: String) {

    protected fun _addManuallyTested() {
        val testedManually = Files.lines(File(testedManuallyPath).toPath()).toList()

        val newTestCases = testedManually
            .filter { it.isNotBlank() && !it.startsWith("//") }
            .map {
                val (outcome, origin) = getOutcomeAndOriginString(it)
                TestCase(it, outcome, origin)
            }

        val list = load<List<TestCase>>(filepath)
        save(list + newTestCases, filepath)
    }

    protected fun _rewriteFile() {
        val oldTestCases = load<List<TestCase>>(filepath)

        val rewrittenTestCases = oldTestCases.map { testCase ->
            logger.debug("Raw: ${testCase.raw}")
            val (outcome, origin) = getOutcomeAndOriginString(testCase.raw)
            TestCase(testCase.raw, outcome, origin)
        }.toList()

        save(rewrittenTestCases, filepath)
    }
}

class CacheManagement {

    @[Ignore Test]
    fun refreshDetectorsCache() {
        KotlinLangConflictDetector.cache()
    }
}

data class TestCase(
    val raw: String,
    val outcome: String,
    val origin: String,
)

private fun getOutcomeAndOriginString(raw: String): Pair<String, String> {
    val patternWithSignature = PatternWithSignature(raw)
    val (receiver, parameters, returned) = patternWithSignature.signature
    val method = GlobalPatternMethod(
        packageName = targetPackage,
        name = clientMethod,
        returnedType = returned,
        receiverType = receiver,
        parameters = parameters,
        pattern = patternWithSignature.pattern,
        origin = Origin()
    )

    return try {
        AnalyzerImpl(method, MethodsRegistry(), null, Options.DEFAULT).analyze()
        "ok" to ""
    } catch (e: PatternProcessingException) {
        "fail" to (e.diagnostic.sourceCodeDetails?.trim() ?: "")
    }
}

private const val clientMethod = "completion"
private const val targetPackage = "$BACKEND_PACKAGE.planner"
private const val TEST_DATA_PATH = "src/test/resources/io/kabu/backend/planner/namespace/conflict/detector/tests"

internal fun getManualPath(testName: String) = "$TEST_DATA_PATH/$testName/manual.txt"

internal fun getDataPath(testName: String) = "$TEST_DATA_PATH/$testName/data.json"

private val logger = InterceptingLogging.logger {}
