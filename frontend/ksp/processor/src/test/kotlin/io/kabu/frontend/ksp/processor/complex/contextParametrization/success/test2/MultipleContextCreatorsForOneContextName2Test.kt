@file:Suppress("UNUSED_PARAMETER")

package io.kabu.frontend.ksp.processor.complex.contextParametrization.success.test2

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test


class MultipleContextCreatorsForOneContextName2Test : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
        
            class SomeClass(val i: Int, val b: Boolean) {
    
                private val pairs = mutableListOf<Pair<String, Int>>()
            
                @LocalPattern("name - number")
                fun foo(name: String, number: Int) {
                    pairs.add("${'$'}name(${'$'}b)" to number + i)
                }
            
                override fun toString() = "SomeClass(pairs=${'$'}pairs)"
            }
            
            @ContextCreator("someClass")
            fun someClassCreator(i: Int, b: Boolean) = SomeClass(i, b)
            
            @ContextCreator("someClass")
            fun someClassCreator2(i: Int) = SomeClass(i * 10, false)
            
            @Pattern("{xxx[b..{i}]} @Extend(context = someClass(i), parameter = arg) {}")
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
        """) - Termination("SomeClass(pairs=[(aaa(false), 2001), (bbb(false), 2002), (ccc(false), 2003)])")
    )
}
