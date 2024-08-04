@file:Suppress("UNUSED_PARAMETER")

package io.kabu.frontend.ksp.processor.complex.contextParametrization.success.test3

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test


@ExperimentalCompilerApi
class ContextConstructorTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
        
            class SomeClass @ContextCreator("someClass") constructor(val i: Int, val b: Boolean) {
    
                private val pairs = mutableListOf<Pair<String, Int>>()
            
                @LocalPattern("name - number")
                fun foo(name: String, number: Int) {
                    pairs.add("${'$'}name(${'$'}b)" to number + i)
                }
            
                override fun toString() = "SomeClass(pairs=${'$'}pairs)"
            }
            
            @ContextCreator("someClass")
            fun someClassCreator(i: Int) = SomeClass(i, false)
            
            @Pattern("{xxx[b..{i}]} @Extend(context = someClass(i, b), parameter = arg) {}")
            fun bar(i: Int, b: Boolean, arg: SomeClass) {
                print(arg)
            }

        """,
        sample("""
            { xxx[true..{ 200 }] } {
                "aaa" - 1
                "bbb" - 2
                "ccc" - 3
            }            
        """) - Termination("SomeClass(pairs=[(aaa(true), 201), (bbb(true), 202), (ccc(true), 203)])")
    )
}
