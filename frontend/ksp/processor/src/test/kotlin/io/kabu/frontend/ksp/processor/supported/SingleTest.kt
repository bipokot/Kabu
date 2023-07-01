package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Test


class SingleTest : BaseKspFrontendProcessorTest() {

    @Test
    fun `single test`() = compileAndCheck(
        """
        class A

//        @Pattern("!par") // temp - ok, notemp - ok
        @Pattern("foo + par") //todo temp - error, notemp - ok
        fun f(par: A){
        }

        """
    ) {
        assertOk()
    }
}
