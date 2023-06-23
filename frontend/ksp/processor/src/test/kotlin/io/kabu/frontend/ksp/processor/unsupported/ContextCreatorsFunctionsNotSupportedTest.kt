package io.kabu.frontend.ksp.processor.unsupported

import io.kabu.annotations.ContextCreator
import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Assume
import org.junit.Ignore
import org.junit.Test

class ContextCreatorsFunctionsNotSupportedTest : BaseKspFrontendProcessorTest() {

    private val role = ContextCreator::class.simpleName

    @Test
    @Ignore("is not detected as ContextCreator")
    fun `instance initializer not supported`() {
        Assume.assumeTrue(false)
        compileAndCheck(
            """
            class Foo {
    
                @ContextCreator("ctx")
                init {
                    1 + 2
                }
            }
            """
        ) {
            assertCompilationError(14, "Instance initializers as $role aren't supported yet", "\"<init>\"")
        }
    }

    @Test
    @Ignore("is not detected as ContextCreator")
    fun `object initializer not supported`() = compileAndCheck(
        """
        class Foo {

            companion object {

                @ContextCreator("ctx")
                init {
                    1 + 2
                }
            }
        }
        """
    ) {
        assertCompilationError(14, "Object initializers as $role aren't supported yet", "\"<init>\"")
    }

    @Test
    fun `regular member function not supported`() = compileAndCheck(
        """
        class A {
        
            @ContextCreator("ctx")
            fun foo(bar: String) {
            }
        }
        """
    ) {
        assertCompilationError(17, "Regular member functions as $role aren't supported yet", "\"foo\"")
    }

    @Test
    @Ignore("Regular member functions as ContextCreator aren't supported yet")
    fun `inner class functions not supported`() = compileAndCheck(
        """
        class Foo {
        
            inner class Inner {
            
                @ContextCreator("ctx")
                fun foo() {
                }
            }
        }
        """
    ) {
        assertCompilationError(19, "Inner class functions as $role aren't supported yet", "\"foo\"")
    }

    @Test
    fun `object function not supported`() = compileAndCheck(
        """
        //todo make java class with static member?
        class Foo {

            companion object {

                @ContextCreator("ctx")
                fun func(i: Int): String { return i.toString() } 
            }
        }
        """
    ) {
        assertCompilationError(20, "Functions of objects as $role aren't supported yet", "\"func\"")
    }

    // KIND

    @Test
    @Ignore("is not detected as ContextCreator")
    fun `lambda not supported`() = compileAndCheck(
        """
        val foo = @ContextCreator("ctx") { "abc" } 
        """
    ) {
        assertCompilationError(0, "Lambda functions as $role aren't supported yet", "\"<init>\"")
    }

    @Test
    @Ignore("is not detected as ContextCreator")
    fun `anonymous function not supported`() = compileAndCheck(
        """
        val foo = @ContextCreator("ctx") fun(i: Int): String { return i.toString() } 
        """
    ) {
        assertCompilationError(0, "Anonymous functions as $role aren't supported yet", "\"<init>\"")
    }

    // VISIBILITY

    @Test
    fun `private function not supported`() = compileAndCheck(
        """
        @ContextCreator("ctx") 
        private fun func(bar: Int) { 
        } 
        """
    ) {
        assertCompilationError(15, "Private functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `protected function not supported`() = compileAndCheck(
        """
        @ContextCreator("ctx")
        protected fun func(bar: Int) { 
        } 
        """
    ) {
        assertCompilationError(15, "Protected functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    @Ignore("is not detected as ContextCreator")
    fun `local function not supported`() = compileAndCheck(
        """
        fun outerFunc() {

            @ContextCreator("ctx") 
            fun func(bar: Int) { 
            }

            func(1)
        }
        """
    ) {
        assertCompilationError(15, "Local functions as $role aren't supported yet", "\"func\"")
    }

    // MODIFIERS

    @Test
    fun `inline function not supported`() = compileAndCheck(
        """
        @ContextCreator("ctx") 
        inline fun func(bar: Int) { 
        }
        """
    ) {
        assertCompilationError(15, "Inline functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `operator function not supported`() = compileAndCheck(
        """
        @ContextCreator("ctx")
        operator fun String.not() { 
        }
        """
    ) {
        assertCompilationError(15, "Operator functions as $role aren't supported yet", "\"not\"")
    }

    @Test
    fun `abstract function not supported`() = compileAndCheck(
        """
        abstract class Foo {
        
            @ContextCreator("ctx") 
            abstract fun func(bar: Int) { 
            }
        }
        """
    ) {
        assertCompilationError(17, "Abstract functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `suspend function not supported`() = compileAndCheck(
        """
        @ContextCreator("ctx")
        suspend fun func(bar: Int) { 
        }
        """
    ) {
        assertCompilationError(15, "Suspend functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `tailrec function not supported`() = compileAndCheck(
        """
        val eps = 1E-10

        @ContextCreator("ctx")
        tailrec fun findFixPoint(x: Double = 1.0): Double =
            if (abs(x - cos(x)) < eps) x else findFixPoint(cos(x))
        """
    ) {
        assertCompilationError(17, "Tailrec functions as $role aren't supported yet", "\"findFixPoint\"")
    }

    @Test
    fun `parameterized function not supported`() = compileAndCheck(
        """
        @ContextCreator("ctx")
        fun <T> func(bar: T) { 
        }
        """
    ) {
        assertCompilationError(15, "Functions with type parameters as $role aren't supported yet", "\"func\"")
    }
}
