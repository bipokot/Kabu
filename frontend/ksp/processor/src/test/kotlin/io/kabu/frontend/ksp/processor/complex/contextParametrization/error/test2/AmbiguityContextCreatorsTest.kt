@file:Suppress("UNUSED_PARAMETER")

package io.kabu.frontend.ksp.processor.complex.contextParametrization.error.test2

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test


@ExperimentalCompilerApi
class AmbiguityContextCreatorsTest : BaseKspFrontendProcessorTest() {

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
            fun someClassCreator(i: Int, b: Boolean) = SomeClass(i, b)
            
            @ContextCreator("someClass")
            fun someClassCreator2(i: Int) = SomeClass(i * 10, false)
            
            @ContextCreator("someClass")
            fun someClassCreator3(i: Int, b: Boolean) = SomeClass(i, b)
            
            @Pattern("{xxx[b..{i}]} @Extend(context = someClass(i, b), parameter = arg) {}")
            fun bar(i: Int, b: Boolean, arg: SomeClass) {
                println(arg)
            }

        """,
    ) {
        assertCompilationError("Context creator method ambiguity for context name 'someClass'")
    }
}
