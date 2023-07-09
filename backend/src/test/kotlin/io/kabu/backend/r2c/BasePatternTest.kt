package io.kabu.backend.r2c

import io.kabu.backend.common.log.InterceptingLogging
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import java.io.File
import java.nio.file.Files
import kotlin.streams.toList

@RunWith(Parameterized::class)
abstract class BasePatternTest(val raw: String, val sample: String, val termination: String) : Assert() {

    abstract val testedManuallyPath: String
    abstract val filepath: String

    @Test
    fun test() {
        val result = completionOutputOf(raw, sample)
        val actualTermination = completionStringOfCompletionOutput(result)
        logger.debug { "should get     : $termination" }

        assertEquals(termination, actualTermination)
    }

    companion object {

        fun load(filepath: String) = load<List<TestCase>>(filepath)
            .map { arrayOf(it.raw, it.sample, it.termination) }
    }
}

open class PatternTestManagement(val testedManuallyPath: String, val filepath: String) {

    protected fun _addManuallyTested() {
        val testedManually = Files.lines(File(testedManuallyPath).toPath()).toList()

        val newTestCases = testedManually
            .filter { it.isNotBlank() && !it.startsWith("//") }
            .windowed(size = 2, step = 2, partialWindows = false) {
                val completion = completionOutputOf(it[0], it[1])
                TestCase(it[0], it[1], completionStringOfCompletionOutput(completion))
            }

        val list = load<List<TestCase>>(filepath)
        save(list + newTestCases, filepath)
    }

    protected fun _rewriteFile() {
        val oldTestCases = load<List<TestCase>>(filepath)

        val rewrittenTestCases = oldTestCases.map { testCase ->
            logger.debug("Raw: ${testCase.raw}")
            val newCompletion = completionOutputOf(testCase.raw, testCase.sample)
            TestCase(testCase.raw, testCase.sample, completionStringOfCompletionOutput(newCompletion))
        }.toList()

        save(rewrittenTestCases, filepath)
    }
}

data class TestCase(
    val raw: String,
    val sample: String,
    val termination: String
)

internal fun completionStringOfCompletionOutput(result: String) = result.substringAfter(MARKER).trim()

private const val MARKER = "completion:"

internal fun getManualPath(testName: String) = "src/test/resources/io/kabu/backend/r2c/$testName/manual.txt"

internal fun getDataPath(testName: String) = "src/test/resources/io/kabu/backend/r2c/$testName/data.json"

private val logger = InterceptingLogging.logger {}
