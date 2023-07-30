package io.kabu.frontend.ksp.processor

import com.tschuchort.compiletesting.KotlinCompilation
import kotlin.test.assertEquals
import kotlin.test.assertTrue

open class KotlinCompilationResultAssert {

    protected fun KotlinCompilation.Result.assertCompilationError(lineNumber: Int, vararg expectedMessages: String) {
        assertCompilationError()
        assertExpectedLineNumber(lineNumber)

        expectedMessages.forEach {
            assertExpectedMessage(it)
        }
    }

    protected fun KotlinCompilation.Result.assertCompilationError() {
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, exitCode)
    }

    protected fun KotlinCompilation.Result.assertCompilationError(errorMessage: String) {
        assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, exitCode)
        assertTrue(errorMessage in messages)
    }

    protected fun KotlinCompilation.Result.assertKspError(message: String) {
        val messageExists = messages.splitToSequence(newline)
            .any { it.startsWith("${KSP_ERROR_PREFIX}$message") }
        assertTrue(messageExists)
    }

    protected fun KotlinCompilation.Result.assertOk() {
        assertEquals(KotlinCompilation.ExitCode.OK, exitCode)
    }

    protected fun KotlinCompilation.Result.assertExpectedMessage(expectedMessage: String) {
        assertTrue("Expected message containing text '$expectedMessage' but got: '$messages'") {
            messages.contains(expectedMessage)
        }
    }

    protected fun KotlinCompilation.Result.assertExpectedLineNumber(lineNumber: Int) {
        assertExpectedMessage("testfile.kt:$lineNumber")
    }

    private companion object {
        private const val KSP_ERROR_PREFIX = "e: [ksp] "
        private val newline = Regex("\n")
    }
}
