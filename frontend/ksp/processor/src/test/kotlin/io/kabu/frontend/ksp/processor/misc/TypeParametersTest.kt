package io.kabu.frontend.ksp.processor.misc

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Test

class TypeParametersTest : BaseKspFrontendProcessorTest() {

    @Test
    fun `supported star projections`() = compileAndCheck(
        """
        @Pattern("list {}")
        fun f(list: List<*>){
        }
        """
    ) {
        assertOk()
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
