package io.kabu.frontend.ksp.processor.complex.nested.ifelse

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.junit.Test

class IfElseTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
        class Context @ContextCreator("context") constructor() {
            val trueActions = mutableListOf<() -> Unit>()
            val falseActions = mutableListOf<() -> Unit>()
        
            @LocalPattern("+action")
            fun addTrueAction(action: () -> Unit) {
                trueActions += action
            }
        
            @LocalPattern("-action")
            fun addFalseAction(action: () -> Unit) {
                falseActions += action
            }
        
            @LocalPattern("the great truth in action")
            fun addTrueActionYoda(action: () -> Unit) = addTrueAction(action)
        
            @LocalPattern("the miserable lie in action")
            fun addFalseActionYoda(action: () -> Unit) = addFalseAction(action)
        
            @LocalPattern("case of action")
            fun createAction(action: () -> Unit) = action
        }
        
        @Pattern("condition @Extend(context = context(), parameter = context) {}")
        fun ifElse(condition: Boolean, context: Context) {
            (if (condition) context.trueActions else context.falseActions).forEach { it() }
        }
        
        @Pattern("condition Yoda said @Extend(context = context(), parameter = context) {}")
        fun yodaIfElse(condition: Boolean, context: Context) = ifElse(condition, context)

        """,
        sample("""
            val theEarthIsFlat = true
            theEarthIsFlat {
                + { println("statement is true") }
                + { println("Of course it is flat! Look around, don't be fooled by government!") }
        
                - { println("statement is false") }
            }
        
            theEarthIsFlat Yoda said {
                the great truth in case of { println("statement is true") }
                the miserable lie in case of { println("statement is false") }
            }       
        """) - Termination("statement is true\n" +
            "Of course it is flat! Look around, don't be fooled by government!\n" +
            "statement is true\n")
    )
}
