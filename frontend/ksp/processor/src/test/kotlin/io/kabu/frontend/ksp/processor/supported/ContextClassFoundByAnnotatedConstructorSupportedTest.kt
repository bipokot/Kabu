package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Test

/**
 * Validation of context class constructor of which is marked as [ContextCreator]
 */
class ContextClassFoundByAnnotatedConstructorSupportedTest : BaseKspFrontendProcessorTest() {

    @Test
    fun `parameterized class supported`() = compileAndCheck(
        """
        class Foo<T> @ContextCreator("ctx") constructor() {
        }
        """
    ) {
        assertOk()
    }
}
