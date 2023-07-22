package io.kabu.frontend.ksp.processor

import org.junit.Test

class TypeParametersTest : BaseKspFrontendProcessorTest() {

    @Test
    fun `unsupported star projections`() = compileAndCheck(
        """
        class A<T>
        
        @Pattern("foo @Extend(context = bar(), parameter = par) {}")
        fun f(par: A<*>){
        }
        """
    ) {
        assertCompilationError()
        assertExpectedMessage("Error while processing parameter 'par' of function 'f': " +
                "Star projections aren't supported yet")
        assertExpectedLineNumber(17)
        assertExpectedMessage("\"par\"")
    }

    @Test
    fun `supported contravariance`() = compileAndCheck(
        """
        @Pattern("foo + par")
        fun f(par: List<in String>){
        }
        """
    ) {
        assertOk()
    }

    @Test
    fun `supported covariance`() = compileAndCheck(
        """
        @Pattern("foo + par")
        fun f(par: List<out String>){
        }
        """
    ) {
        assertOk()
    }
}
