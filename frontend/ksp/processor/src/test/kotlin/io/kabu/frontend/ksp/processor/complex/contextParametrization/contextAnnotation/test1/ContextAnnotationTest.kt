@file:Suppress("UNUSED_PARAMETER")

package io.kabu.frontend.ksp.processor.complex.contextParametrization.contextAnnotation.test1

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test


class ContextAnnotationTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
        
            @Context("someClass")
            class SomeClass(val i: Int) {
    
                private val pairs = mutableListOf<Pair<String, Int>>()
            
                @LocalPattern("name - number")
                fun foo(name: String, number: Int) {
                    pairs.add("${'$'}name" to number + i)
                }
            
                override fun toString() = "SomeClass(pairs=${'$'}pairs)"
            }
            
            @Pattern("i @Extend(context = someClass(i), parameter = arg) {}")
            fun bar(i: Int, arg: SomeClass) {
                print(arg)
            }

        """,
        sample("""
            2 {
                "aaa" - 3
            }           
        """) - Termination("SomeClass(pairs=[(aaa, 5)])")
    )
}
