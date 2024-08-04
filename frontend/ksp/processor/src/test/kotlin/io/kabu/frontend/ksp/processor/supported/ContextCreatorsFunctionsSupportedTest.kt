package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@ExperimentalCompilerApi
class ContextCreatorsFunctionsSupportedTest : BaseKspFrontendProcessorTest() {

    // todo add tests for supported cases

    @Test
    fun `constructor supported`() = compileAndCheck(
        """
        class A @ContextCreator("ctx") constructor() {
        }
        """
    ) {
        assertOk()
    }

    @Test
    fun `context creator can create instances of generic types`() = compileAndCheck(
        """
        class A<T>
        
        @ContextCreator(contextName = "bar")
        fun aaa() = A<String>()
        
        @Pattern("foo @Extend(context = bar(), parameter = par) {}")
        fun f(par: A<String>){
        }
        """
    ) {
        assertOk()
    }

    @Test
    fun `parameterized function supported`() = compileAndCheck(
        """
        @ContextCreator("ctx")
        fun <T> func(bar: T) { 
        }
        """
    ) {
        assertOk()
    }
}
