package io.kabu.frontend.ksp.processor.generic.test2

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test


@ExperimentalCompilerApi
class GenericRecursiveBoundsTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
            // PACKAGE test2
            @Pattern("a * b + c")
            fun <T : Comparable<T>, R : T> foo(a: T, b: Array<out R>, c: (T) -> R): T {
                return if (false) {
                    c(a)
                } else {
                    c(b.first())
                }
            }
        """,
        sample(
            """
                // PACKAGE test2
                print("abc" * arrayOf("def") + { it + it })
            """
        ) - TestCase.ScriptResult.Termination("defdef"),
    )
}
