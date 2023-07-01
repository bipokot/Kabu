package io.kabu.frontend.ksp.processor.simple

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test


class MultipleGlobalPatternsTest : BaseKspFrontendProcessorTest() {

    @Test
    fun `both global patterns work well`() = compileAndCheckAndRun(
        """
            @Pattern("i / b * s")
            fun foo(i: Int, b: Boolean, s: String) {
                print("foo")
            }

            @Pattern("i * b + s")
            fun bar(i: Int, b: Boolean, s: String) {
                print("bar")
            }
        """,
        sample("""
            19 / false * "abc"
        """) - Termination("foo"),
    )
}
