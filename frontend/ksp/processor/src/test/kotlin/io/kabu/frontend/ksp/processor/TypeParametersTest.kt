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
    fun `unsupported contravariance`() = compileAndCheck(
        """
        class A<T>
        
        @Pattern("foo @Extend(context = bar(), parameter = par) {}")
        fun f(par: A<in String>){
        }
        """
    ) {
        assertCompilationError()
        assertExpectedMessage("Error while processing parameter 'par' of function 'f': " +
                "Contravariant type arguments aren't supported yet")
        assertExpectedLineNumber(17)
        assertExpectedMessage("\"par\"")
    }

    @Test
    fun `unsupported covariance`() = compileAndCheck(
        """
        class A<T>
        
        @Pattern("foo @Extend(context = bar(), parameter = par) {}")
        fun f(par: A<out String>){
        }
        """
    ) {
        assertCompilationError()
        assertExpectedMessage("Error while processing parameter 'par' of function 'f': " +
                "Covariant type arguments aren't supported yet")
        assertExpectedLineNumber(17)
        assertExpectedMessage("\"par\"")
    }
}
