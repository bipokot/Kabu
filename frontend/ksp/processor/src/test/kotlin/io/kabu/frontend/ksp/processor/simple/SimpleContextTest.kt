package io.kabu.frontend.ksp.processor.simple

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test


class SimpleContextTest : BaseKspFrontendProcessorTest() {

    @Test
    fun `pattern extension does not fail`() = compileAndCheckAndRun(
        """
           @GlobalPattern("i % @Extend(context = ctx(), parameter = context) {}")
            fun foo(i: Int, context: Context) {
                print(i)
            }

            class Context @ContextCreator("ctx") constructor() {

                @LocalPattern("s * count")
                fun str(count: Int, s: String): String {
                    return s + count
                }
            }
        """,
        sample("""
            123 % { "abc" * 5 }
        """) - Termination("123"),
    )

    @Test
    fun `123 % { "a" * 2 }`() = compileAndCheckAndRun(
        """
            @GlobalPattern("i % @Extend(context = ctx(), parameter = context) {}")
            fun foo(i: Int, context: Context) {
                print("${'$'}i, ${'$'}{context.result}")
            }
            
            class Context @ContextCreator("ctx") constructor() {
                var result: String? = null
            
                @LocalPattern("s * count")
                fun str(count: Int, s: String) {
                    result = buildString { repeat(count) { append(s) }}
                }
            }
        """,
        sample("""
            123 % { "a" * 2 }           
        """) - Termination("123, aa"),
    )

    @Test
    fun `extension point inside watcher lambda`() = compileAndCheckAndRun(
        """
            @GlobalPattern("i % { b - @Extend(context = ctx(), parameter = context) {} }")
            fun foo(i: Int, b: Boolean, context: Context) {
                print("${'$'}i, ${'$'}b, ${'$'}{context.result}")
            }
            
            class Context @ContextCreator("ctx") constructor() {
                var result: String? = null
            
                @LocalPattern("s * count")
                fun str(count: Int, s: String) {
                    result = buildString { repeat(count) { append(s) }}
                }
            }
        """,
        sample("""
            123 % { false - { "a" * 2 } }           
        """) - Termination("123, false, aa"),
    )

    @Test
    fun `conflicts inside context mediator, context mediator inside watcher lambda`() = compileAndCheckAndRun(
        """
            @GlobalPattern("i % { @Extend(context = ctx(), parameter = context) {} }")
            fun foo(i: Int, context: Context) {
                print("${'$'}i, ${'$'}{context.result}")
            }
            
            class Context @ContextCreator("ctx") constructor() {
                var result: String = ""

                @LocalPattern("a / b + c / d")
                fun foo(a: Int, b: Boolean, c: Int, d: Boolean) {
                    result += "${'$'}a + ${'$'}b + ${'$'}c + ${'$'}d. "
                }
            
                @LocalPattern("a / b - c / d")
                fun bar(a: Int, b: Boolean, c: Int, d: Boolean) {
                    result += "${'$'}a - ${'$'}b - ${'$'}c - ${'$'}d. "
                }
            }
        """,
        sample("""
            123 % {{ 
                1 / true + 2 / false              
                3 / false + 4 / true              
            }}           
        """) - Termination("123, 1 + true + 2 + false. 3 + false + 4 + true. "),
    )
}
