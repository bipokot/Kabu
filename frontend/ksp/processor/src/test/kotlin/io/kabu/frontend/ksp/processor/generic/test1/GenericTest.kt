@file:Suppress("MaxLineLength")

package io.kabu.frontend.ksp.processor.generic.test1

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test


@ExperimentalCompilerApi
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
            print("abc" * arrayOf("def") + { it + it })
        """) - Termination("defdef"),
    )
}
