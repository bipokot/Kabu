package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Test

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
