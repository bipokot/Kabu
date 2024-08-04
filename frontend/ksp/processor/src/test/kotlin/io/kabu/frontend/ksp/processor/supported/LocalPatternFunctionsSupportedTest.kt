package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@ExperimentalCompilerApi
class LocalPatternFunctionsSupportedTest : BaseKspFrontendProcessorTest() {

    // todo add tests for supported cases

    @Test
    fun `parameterized function supported`() = compileAndCheck(
        """
        class Foo {

            @LocalPattern("!bar") 
            fun <T> func(bar: T) { 
            }
        }
        """
    ) {
        assertOk()
    }
}
