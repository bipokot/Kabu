package io.kabu.frontend.ksp.testing

import io.kabu.annotations.ContextCreator
import io.kabu.annotations.GlobalPattern
import io.kabu.annotations.LocalPattern

@GlobalPattern("i % { @Extend(context = ctx(), parameter = context) {} }")
fun foo(i: Int, context: Context) {
    print("$i, ${context.result}")
}

class Context @ContextCreator("ctx") constructor() {
    var result: String? = null

    @LocalPattern("a / b + c / d")
    fun foo(a: Int, b: Boolean, c: Int, d: Boolean) {
        result += "${'$'}a, ${'$'}b, ${'$'}c, ${'$'}d"
    }

    @LocalPattern("a / b - c / d")
    fun bar(a: Int, b: Boolean, c: Int, d: Boolean) {
        result += "${'$'}a; ${'$'}b; ${'$'}c; ${'$'}d"
    }
}

fun main() {
    123 % {{
        1 / true + 2 / false
        3 / false + 4 / true
    }}
}
