package io.kabu.frontend.ksp.processor.misc

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@ExperimentalCompilerApi
class AliasesTest : BaseKspFrontendProcessorTest(){

    @Test
    fun `unsupported aliased suspending functional types`() = compileAndCheck(
        """
        typealias Alias = suspend (String) -> Unit

        @Pattern("foo + bar")
        fun func(bar: Alias) {
        }
        """
    ) {
        assertCompilationError(17, "Suspend functional types aren't supported yet", "\"bar\"")
    }
}
