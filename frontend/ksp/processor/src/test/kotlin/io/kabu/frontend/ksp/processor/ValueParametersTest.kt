package io.kabu.frontend.ksp.processor

import org.junit.Test

class ValueParametersTest : BaseKspFrontendProcessorTest(){

    @Test
    fun `unsupported vararg parameters`() = compileAndCheck(
        """
        @Pattern("foo bar")
        fun func(bar: (vararg Boolean) -> Unit){
        }
        """
    ) {
        assertCompilationError(15, "Parameters with 'vararg' modifier aren't supported yet", "\"bar\"")
    }

    @Test
    fun `unsupported crossinline parameters`() = compileAndCheck(
        """
        @Pattern("foo bar")
        fun func(crossinline bar: (Boolean) -> Unit){
        }
        """
    ) {
        assertCompilationError(15, "Parameters with 'crossinline' modifier aren't supported yet", "\"bar\"")
    }

    @Test
    fun `unsupported noinline parameters`() = compileAndCheck(
        """
        @Pattern("foo bar")
        fun func(noinline bar: (Boolean) -> Unit){
        }
        """
    ) {
        assertCompilationError(15, "Parameters with 'noinline' modifier aren't supported yet", "\"bar\"")
    }

    @Test
    fun `unsupported parameters with defaults`() = compileAndCheck(
        """
        @Pattern("foo bar")
        fun func(bar: String = "abc"){
        }
        """
    ) {
        assertCompilationError(15, "Parameters with default values aren't supported yet", "\"bar\"")
    }

    @Test
    fun `unsupported suspending functional types`() = compileAndCheck(
        """
        @Pattern("foo bar")
        fun func(bar: suspend (String) -> Unit) {
        }
        """
    ) {
        assertCompilationError(15, "Suspend functional types aren't supported yet", "\"bar\"")
    }
}
