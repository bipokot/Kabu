package io.kabu.frontend.ksp.processor.supported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

/**
 * Validation of context class constructor of which is marked as [ContextCreator]
 */
@ExperimentalCompilerApi
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
