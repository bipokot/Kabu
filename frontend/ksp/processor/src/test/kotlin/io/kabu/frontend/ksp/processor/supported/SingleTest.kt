package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test


@ExperimentalCompilerApi
class SingleTest : BaseKspFrontendProcessorTest() {

    @Test
    fun `single test`() = compileAndCheck(
        """
        class A

        @Pattern("foo + par")
        fun f(par: A){
        }

        """
    ) {
        assertOk()
    }
}
