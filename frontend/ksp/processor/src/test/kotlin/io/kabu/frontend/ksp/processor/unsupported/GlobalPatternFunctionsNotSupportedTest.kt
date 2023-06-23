package io.kabu.frontend.ksp.processor.unsupported

import io.kabu.annotations.GlobalPattern
import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Ignore
import org.junit.Test

class GlobalPatternFunctionsNotSupportedTest : BaseKspFrontendProcessorTest() {

    private val role = GlobalPattern::class.simpleName

    @Test //todo consider support?
    fun `member function not supported`() = compileAndCheck(
        """
        class A {
        
            @GlobalPattern("!bar")
            fun foo(bar: String){
            }
        }
        """
    ) {
        assertCompilationError(17, "Member functions as $role aren't supported yet", "\"foo\"")
    }

    @Test //todo consider support?
    fun `constructor not supported`() = compileAndCheck(
        """
        class A @GlobalPattern("!bar") constructor()
        """
    ) {
        assertCompilationError(14, "Constructors as $role aren't supported yet", "\"<init>\"")
    }

    @Test //todo consider support?
    @Ignore("Member functions as GlobalPattern aren't supported yet")
    fun `inner class functions not supported`() = compileAndCheck(
        """
        class Foo {
        
            inner class Inner {
            
                @GlobalPattern("!bar")
                fun foo(bar: String) {
                }
            }
        }
        """
    ) {
        assertCompilationError(19, "Inner class functions as $role aren't supported yet", "\"foo\"")
    }

    @Test //todo consider support?
    fun `object function not supported`() = compileAndCheck(
        """
        class Foo {

            companion object {

                @GlobalPattern("!i") 
                fun func(i: Int): String { return i.toString() } 
            }
        }
        """
    ) {
        assertCompilationError(19, "Functions of objects as $role aren't supported yet", "\"func\"")
    }

    // KIND

    @Test
    @Ignore("is not detected as GlobalPattern")
    fun `lambda not supported`() = compileAndCheck(
        """
        val foo = @GlobalPattern("!bar") { "abc" } 
        """
    ) {
        assertCompilationError(0, "Lambda functions as $role aren't supported yet", "\"<init>\"")
    }

    @Test
    @Ignore("is not detected as GlobalPattern")
    fun `anonymous function not supported`() = compileAndCheck(
        """
        val foo = @GlobalPattern("!bar") fun(i: Int): String { return i.toString() } 
        """
    ) {
        assertCompilationError(0, "Anonymous functions as $role aren't supported yet", "\"<init>\"")
    }

    // VISIBILITY

    @Test
    fun `private function not supported`() = compileAndCheck(
        """
        @GlobalPattern("!bar") 
        private fun func(bar: Int) { 
        } 
        """
    ) {
        assertCompilationError(15, "Private functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `protected function not supported`() = compileAndCheck(
        """
        @GlobalPattern("!bar") 
        protected fun func(bar: Int) { 
        } 
        """
    ) {
        assertCompilationError(15, "Protected functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    @Ignore("is not detected as GlobalPattern")
    fun `local function not supported`() = compileAndCheck(
        """
        fun outerFunc() {

            @GlobalPattern("!bar") 
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
        @GlobalPattern("!bar") 
        inline fun func(bar: Int) { 
        }
        """
    ) {
        assertCompilationError(15, "Inline functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `operator function not supported`() = compileAndCheck(
        """
        @GlobalPattern("!bar - this") 
        operator fun String.rem(bar: Int) { 
        }
        """
    ) {
        assertCompilationError(15, "Operator functions as $role aren't supported yet", "\"rem\"")
    }

    @Test
    fun `abstract function not supported`() = compileAndCheck(
        """
        abstract class Foo {
        
            @GlobalPattern("!bar") 
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
        @GlobalPattern("!bar") 
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

        @GlobalPattern("!x") 
        tailrec fun findFixPoint(x: Double = 1.0): Double =
            if (abs(x - cos(x)) < eps) x else findFixPoint(cos(x))
        """
    ) {
        assertCompilationError(17, "Tailrec functions as $role aren't supported yet", "\"findFixPoint\"")
    }

    @Test
    fun `parameterized function not supported`() = compileAndCheck(
        """
        @GlobalPattern("!bar") 
        fun <T> func(bar: T) { 
        }
        """
    ) {
        assertCompilationError(15, "Functions with type parameters as $role aren't supported yet", "\"func\"")
    }
}
