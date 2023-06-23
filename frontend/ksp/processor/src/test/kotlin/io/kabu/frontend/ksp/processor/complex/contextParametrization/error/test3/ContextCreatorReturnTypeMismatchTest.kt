@file:Suppress("UNUSED_PARAMETER")

package io.kabu.frontend.ksp.processor.complex.contextParametrization.error.test3

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Test


class ContextCreatorReturnTypeMismatchTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheck(
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
            fun someClassCreator(i: Int, b: Boolean): String = "some string"
            
            @GlobalPattern("{xxx[b..{i}]} @Extend(context = someClass(i, b), parameter = arg) {}")
            fun bar(i: Int, b: Boolean, arg: SomeClass) {
                println(arg)
            }

        """,
    ) {
        assertCompilationError("No compatible context creator method found for context name 'someClass'")
    }
}
