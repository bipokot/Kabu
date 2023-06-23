@file:Suppress("UNUSED_PARAMETER")

package io.kabu.frontend.ksp.processor.complex.contextParametrization.success.test4

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test


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
            
            @GlobalPattern("{xxx[b..{i}]} @Extend(context = someClass(i, b), parameter = arg) {}")
            fun bar(i: Int, b: Boolean, arg: SomeClass) {
                print(arg)
            }

        """,
        sample("""
            { xxx[true..{ 200 }] } {
                "aaa" - 1
            }           
        """) - Termination("SomeClass(pairs=[(aaa(true), 201)])")
    )
}
