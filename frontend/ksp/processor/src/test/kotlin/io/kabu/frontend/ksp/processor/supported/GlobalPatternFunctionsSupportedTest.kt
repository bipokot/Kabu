package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@ExperimentalCompilerApi
class GlobalPatternFunctionsSupportedTest : BaseKspFrontendProcessorTest() {

    // todo add tests for supported cases

    @Test
    fun `public global pattern function`() = compileAndCheck(
        """
        @Pattern("!bar")
        fun foo(bar: String) {
        }
        """
    ) {
        assertOk()
    }

    @Test
    fun `parameterized function`() = compileAndCheck(
        """
        @Pattern("!bar") 
        fun <T> func(bar: T) { 
        }
        """
    ) {
        assertOk()
    }
}
