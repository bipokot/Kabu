package io.kabu.frontend.ksp.processor.generic.test3

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test


@ExperimentalCompilerApi
class GenericContextTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
            // PACKAGE test3

            @Context("ctx")
            class Ctx<T, R : T> {
                
                @LocalPattern("a - b (c)")
                fun <E> foo(a: T, b: Array<out R>, c: (E) -> E) = Unit
            }
        """,
        sample(
            """
                // PACKAGE test3
                print(H1::class)
            """
        ) - TestCase.ScriptResult.Termination("class test3.H1"),
    )
}
