package io.kabu.frontend.ksp.processor.unsupported

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Ignore
import org.junit.Test

/**
 * Validation of context class constructor of which is marked as [ContextCreator]
 */
class ContextClassFoundByAnnotatedConstructorNotSupportedTest : BaseKspFrontendProcessorTest() {

    private val role = "Context" //todo role = "Context created by constructor"

    // KIND

    @Test
    fun `enum class not supported`() = compileAndCheck(
        """
        enum class Foo @ContextCreator("ctx") constructor(){
        }
        """
    ) {
        assertCompilationError(14, "Private functions as ContextCreator aren't supported yet")
    }

    @Test
    @Ignore("Functions of objects as ContextCreator aren't supported yet")
    fun `object not supported`() = compileAndCheck(
        """
        object Foo @ContextCreator("ctx") constructor() {
        } 
        """
    ) {
        assertCompilationError(14, "Object as $role isn't supported yet", "\"Foo\"")
    }

    @Test
    fun `annotation class not supported`() = compileAndCheck(
        """
        annotation class Foo @ContextCreator("ctx") constructor() {
        }
        """
    ) {
        assertCompilationError(14, "Annotation class as $role isn't supported yet", "\"Foo\"")
    }

    // VISIBILITY

    @Test
    @Ignore("is not detected as ContextCreator")
    fun `local class not supported`() = compileAndCheck(
        """
        fun foo(){

            class Bar @ContextCreator("ctx") constructor() {
            }
            val b = Bar()
        }
        """
    ) {
        assertCompilationError(15, "Local classes as $role isn't supported yet", "\"func\"")
    }

    @Test
    fun `private class not supported`() = compileAndCheck(
        """
        open class Foo {
            
            private class Bar @ContextCreator("ctx") constructor() {
            }
        }
        """
    ) {
        assertCompilationError(16, "Private classes as $role aren't supported yet", "\"Bar\"")
    }

    @Test
    fun `protected class not supported`() = compileAndCheck(
        """
        open class Foo {
            
            protected class Bar @ContextCreator("ctx") constructor() {
            }
        }
        """
    ) {
        assertCompilationError(16, "Protected classes as $role aren't supported yet", "\"Bar\"")
    }

    // MODIFIERS

    @Test
    fun `abstract class not supported`() = compileAndCheck(
        """
        abstract class Foo @ContextCreator("ctx") constructor() {
        }
        """
    ) {
        assertCompilationError(14, "Abstract classes as $role aren't supported yet", "\"Foo\"")
    }

    @Test
    fun `inline class not supported`() = compileAndCheck(
        """
        inline class Foo @ContextCreator("ctx") constructor() {
        }
        """
    ) {
        assertCompilationError(14, "Inline classes as $role aren't supported yet", "\"Foo\"")
    }

    @Test
    fun `inner class not supported`() = compileAndCheck(
        """
        class Foo {
        
            inner class Bar @ContextCreator("ctx") constructor() {
            } 
        }
        """
    ) {
        assertCompilationError(16, "Inner classes as $role aren't supported yet", "\"Bar\"")
    }

    @Test
    fun `sealed class not supported`() = compileAndCheck(
        """
        sealed class Foo @ContextCreator("ctx") constructor() {
            object Bar : Foo()
        }
        """
    ) {
        assertCompilationError(14, "Sealed classes as $role aren't supported yet", "\"Foo\"")
    }

    @Test
    fun `value class not supported`() = compileAndCheck(
        """
        value class Foo @ContextCreator("ctx") constructor() {
        }
        """
    ) {
        assertCompilationError(14, "Value classes as $role aren't supported yet", "\"Foo\"")
    }

    // OTHER

    @Test
    @Ignore("Functions of objects as ContextCreator aren't supported yet")
    fun `companion object not supported`() = compileAndCheck(
        """
        class Bar {

            object Foo @ContextCreator("ctx") constructor() {
            }
        }
        """
    ) {
        assertCompilationError(16, "Object as $role isn't supported yet", "\"Foo\"")
    }

    @Test
    fun `parameterized class not supported`() = compileAndCheck(
        """
        class Foo<T> @ContextCreator("ctx") constructor() {
        }
        """
    ) {
        assertCompilationError(14, "Parameterized classes as $role aren't supported yet", "\"Foo\"")
    }
}
