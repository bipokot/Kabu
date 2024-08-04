package io.kabu.frontend.ksp.processor.generic.test4

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test


@ExperimentalCompilerApi
class GenericContextCreationTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
            // PACKAGE test4

            @Context("ctx")
            class Ctx<T>(val t: T)

            @Pattern("b - @Extend(context = ctx(b), parameter = ctx) {}")
            fun <T> foo(ctx: Ctx<T>, b: T): T {
                return ctx.t
            }
        """,
        sample(
            """
                // PACKAGE test4
                print("abc" - {})
            """
        ) - TestCase.ScriptResult.Termination("abc"),
    )
}
