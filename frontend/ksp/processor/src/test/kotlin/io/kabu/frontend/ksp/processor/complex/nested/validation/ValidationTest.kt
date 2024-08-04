package io.kabu.frontend.ksp.processor.complex.nested.validation

import io.kabu.frontend.ksp.processor.BaseKspFrontendProcessorTest
import io.kabu.frontend.ksp.processor.TestCase.ScriptResult.Termination
import io.kabu.frontend.ksp.processor.minus
import io.kabu.frontend.ksp.processor.sample
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.Test

@ExperimentalCompilerApi
class ValidationTest : BaseKspFrontendProcessorTest() {

    @Test
    fun test() = compileAndCheckAndRun(
        """
            class User(
                val name: String,
                val age: Int,
            )
            
            class ValidationCondition(
                val condition: User.() -> Boolean,
                val messageSupplier: ((User) -> String)?,
            )
            
            class ValidationConditions(val conditions: List<ValidationCondition>)
            
            @Pattern("conditions @Extend(context = builder(), parameter = context) {}")
            fun aaa(context: Builder) = ValidationConditions(context.list)
            
            @Pattern("failed rule { user > conditions }")
            fun bbb(conditions: ValidationConditions, user: User): String? {
                return conditions.conditions
                    .firstOrNull {
                        val condition = it.condition
                        !user.condition()
                    }
                    ?.messageSupplier
                    ?.invoke(user)
            }
            
            @Pattern("validate { user > conditions }")
            fun ccc(conditions: ValidationConditions, user: User) {
                conditions.conditions
                    .firstOrNull { !it.condition(user) }
                    ?.let {
                        throw RuntimeException(it.messageSupplier?.invoke(user))
                    }
            }
            
            class Builder @ContextCreator("builder") constructor() {
                val list = mutableListOf<ValidationCondition>()
            
                @LocalPattern("check (condition) - (message)")
                fun add(condition: User.() -> Boolean, message: ((User) -> String)?) {
                    list.add(ValidationCondition(condition, message))
                }
            }
        """,
        sample("""
            val conditions = conditions {
                check { name.startsWith("A") } - { "User with names starting with A are allowed only: ${'$'}{it.name}" }
                check { name.length > 1 } - { "User with short names aren't allowed: ${'$'}{it.name}" }
                check { age >= 18 } - null
            }
        
            val max = User("Max", 17)
            val a = User("A", 31)
        
            print(failed rule { max > conditions })
        """) - Termination("User with names starting with A are allowed only: Max")
    )
}
