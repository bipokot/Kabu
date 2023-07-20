@file:Suppress("MaxLineLength")

package io.kabu.frontend.ksp.processor.generic

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test


class GenericTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
            @Pattern("a * b + c")
            fun <T, R : T> foo(a: T, b: Array<out R>, c: (T) -> R): T {
                return if (false) {
                    c(a)
                } else {
                    c(b.first())
                }
            }
        """,
        sample("""
            "abc" * arrayOf("def") + { it + it }
        """) - Termination("defdef"),
    )
}
