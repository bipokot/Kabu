package io.kabu.frontend.ksp.processor

import org.intellij.lang.annotations.Language

class TestCase(
    @Language("kotlin", prefix = KOTLIN_TEST_SCRIPT_FILE_PREFIX, suffix = KOTLIN_TEST_SCRIPT_FILE_SUFFIX)
    val sampleScript: String,
    val expectedResult: ScriptResult,
) {

    sealed class ScriptResult {

        class Termination(val string: String) : ScriptResult()

        sealed class Error : ScriptResult() {
            abstract val message: String

            class Exception(override val message: String) : Error()
            class KspError(override val message: String) : Error()
        }
    }
}

fun sample(
    @Language("kotlin", prefix = KOTLIN_TEST_SCRIPT_FILE_PREFIX, suffix = KOTLIN_TEST_SCRIPT_FILE_SUFFIX)
    sample: String,
) = sample

@Language("kotlin", prefix = KOTLIN_TEST_FILE_PREFIX, suffix = KOTLIN_TEST_FILE_SUFFIX)
operator fun String.minus(result: TestCase.ScriptResult) = TestCase(this.trimIndent(), result)
