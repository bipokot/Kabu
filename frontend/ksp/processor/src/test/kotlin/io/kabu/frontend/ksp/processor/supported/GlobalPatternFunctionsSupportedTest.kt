package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Test

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
}
