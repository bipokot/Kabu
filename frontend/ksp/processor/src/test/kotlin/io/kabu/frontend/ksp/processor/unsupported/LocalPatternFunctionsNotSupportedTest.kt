package io.kabu.frontend.ksp.processor.unsupported

import io.kabu.annotation.LocalPattern
import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import org.junit.Ignore
import org.junit.Test

class LocalPatternFunctionsNotSupportedTest : BaseKspFrontendProcessorTest() {

    private val role = LocalPattern::class.simpleName

    @Test
    fun `top level function not supported`() = compileAndCheck(
        """
        @LocalPattern("!bar")
        fun foo(bar: String){
        }
        """
    ) {
        assertCompilationError(15, "Top level functions as $role aren't supported yet", "\"foo\"")
    }

    @Test //todo consider support? for constructors of inner classes
    fun `constructor not supported`() = compileAndCheck(
        """
        class A @LocalPattern("!bar") constructor()
        """
    ) {
        assertCompilationError(14, "Constructors as $role aren't supported yet", "\"<init>\"")
    }

    @Test
    fun `object function not supported`() = compileAndCheck(
        """
        class Foo {

            companion object {

                @LocalPattern("!bar") 
                fun func(i: Int): String { return i.toString() } 
            }
        }
        """
    ) {
        assertCompilationError(19, "Functions of objects as $role aren't supported yet", "\"func\"")
    }

    @Test //todo consider support?
    fun `inner class functions not supported`() = compileAndCheck(
        """
        class Foo {
        
            inner class Inner {
            
                @LocalPattern("!bar")
                fun foo(bar: String) {
                }
            }
        }
        """
    ) {
        assertCompilationError(19, "Inner class functions as $role aren't supported yet", "\"foo\"")
    }

    // KIND

    @Test
    @Ignore("is not detected as LocalPattern")
    fun `lambda not supported`() = compileAndCheck(
        """
        val foo = @LocalPattern("!bar") { "abc" } 
        """
    ) {
        assertCompilationError(0, "Lambda functions as $role aren't supported yet", "\"<init>\"")
    }

    @Test
    @Ignore("is not detected as LocalPattern")
    fun `anonymous function not supported`() = compileAndCheck(
        """
        val foo = @LocalPattern("!bar") fun(i: Int): String { return i.toString() } 
        """
    ) {
        assertCompilationError(0, "Anonymous functions as $role aren't supported yet", "\"<init>\"")
    }

    // VISIBILITY

    @Test
    fun `private function not supported`() = compileAndCheck(
        """
        class Foo {
        
            @LocalPattern("!bar") 
            private fun func(bar: Int) { 
            } 
        } 
        """
    ) {
        assertCompilationError(17, "Private functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `protected function not supported`() = compileAndCheck(
        """
        class Foo {

            @LocalPattern("!bar") 
            protected fun func(bar: Int) { 
            }
        }
        """
    ) {
        assertCompilationError(17, "Protected functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    @Ignore("is not detected as LocalPattern")
    fun `local function not supported`() = compileAndCheck(
        """
        fun outerFunc() {

            @LocalPattern("!bar") 
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
        class Foo {
         
            @LocalPattern("!bar") 
            inline fun func(bar: Int) { 
            }
        }
        """
    ) {
        assertCompilationError(17, "Inline functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `operator function not supported`() = compileAndCheck(
        """
        class Foo {

            @LocalPattern("!bar - this") 
            operator fun String.rem(bar: Int) { 
            }
        }
        """
    ) {
        assertCompilationError(17, "Operator functions as $role aren't supported yet", "\"rem\"")
    }

    //todo can be permitted
    // (if abstract LocalPattern function is inherited by subclasses of an enclosing abstract class/interface)
    @Test
    fun `abstract function not supported`() = compileAndCheck(
        """
        abstract class Foo {
        
            @LocalPattern("!bar") 
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
        class Foo {
         
            @LocalPattern("!bar") 
            suspend fun func(bar: Int) { 
            }
        }
        """
    ) {
        assertCompilationError(17, "Suspend functions as $role aren't supported yet", "\"func\"")
    }

    @Test
    fun `tailrec function not supported`() = compileAndCheck(
        """
        class Foo {
            val eps = 1E-10
    
            @LocalPattern("!x") 
            tailrec fun findFixPoint(x: Double = 1.0): Double =
                if (abs(x - cos(x)) < eps) x else findFixPoint(cos(x))
        }
        """
    ) {
        assertCompilationError(18, "Tailrec functions as $role aren't supported yet", "\"findFixPoint\"")
    }
}
