@file:Suppress("UNUSED_PARAMETER")

package io.kabu.frontend.ksp.processor.complex.contextParametrization.contextAnnotation.test2

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test


@ExperimentalCompilerApi
class ContextAnnotationAndContextCreatorConflictTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheck(
        """
            @Context("someClass")
            class SomeClass @ContextCreator("someClass") constructor(val i: Int) {
    
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

        """) {
            assertCompilationError(15, "Primary constructor of", "can not have @ContextCreator annotation")
        }
}
