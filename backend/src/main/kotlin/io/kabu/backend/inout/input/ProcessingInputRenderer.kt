package io.kabu.backend.inout.input

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.inout.input.method.ContextConstructorMethod
import io.kabu.backend.inout.input.method.ContextCreatorMethod
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.inout.input.method.LocalPatternMethod
import io.kabu.backend.inout.input.method.Method
import io.kabu.backend.parameter.EntryParameter


val ProcessingInput.dslDefinitionString: String
    get() = buildString {
        appendLine("""
                 override val processingInput = ProcessingInput(
                 ${IND1}generatedSourcesRoot = "", // can be anything
                 """.trimIndent())
        appendLine("${IND1}globalPatterns = listOf(")
        globalPatterns
            .joinToString("$IND2,\n") { it.dslString() }
            .let { append(it) }
        appendLine("$IND1),")

        appendLine("${IND1}localPatterns = listOf(")
        localPatterns
            .joinToString("$IND2,\n") { it.dslString() }
            .let { append(it) }
        appendLine("$IND1),")

        appendLine("${IND1}contextCreators = listOf(")
        contextCreators
            .joinToString("$IND2,\n") { it.dslString() }
            .let { append(it) }
        appendLine("$IND1)")

        appendLine(")")
    }

private fun Method.buildForMethodOfType(type: String, typeSpecificStrings: StringBuilder.()->Unit) = buildString {
    appendLine("""${IND2}"$name" - $type(basePackage) {""")
    typeSpecificStrings()
    appendLine("""${IND3}returns = ${returnedType.simplify()} """)

    appendParameters(parameters)
    appendLine("${IND2}}")
}

private fun GlobalPatternMethod.dslString() = buildForMethodOfType("globalPattern") {
    appendLine("""${IND3}pattern = "$pattern" """)
}

private fun LocalPatternMethod.dslString() = buildForMethodOfType("localPattern") {
    appendLine("""${IND3}pattern = "$pattern" """)
    appendLine("""${IND3}declaringType = ${declaringType.simplify()} """)
}

private fun ContextCreatorMethod.dslString() = when (this) {
    is ContextConstructorMethod -> buildForMethodOfType("contextConstructor") {
        appendLine("""${IND3}contextName = "$contextName" """)
        appendLine("""${IND3}declaringType = ${declaringType.simplify()} """)
    }
    else -> buildForMethodOfType("contextCreator") {
        appendLine("""${IND3}contextName = "$contextName" """)
    }
}

private fun StringBuilder.appendParameters(parameters: List<EntryParameter>) {
    if (parameters.isNotEmpty()) {
        appendLine("${IND3}parameters(")
        parameters.forEach {
            appendLine("""$IND4"${it.name}" / ${it.type.simplify()},""")
        }
        appendLine("$IND3)")
    }
}

private fun TypeName.simplify(): String {
    if (this !is ClassName) return "<UNKNOWN TYPE>"
    return when (this) {
        UNIT -> "UNIT"
        INT -> "INT"
        BOOLEAN -> "BOOLEAN"
        STRING -> "STRING"
        else -> "!\"$simpleName\""
    }
}

private fun ind(i: Int) = buildString { repeat(i) { append("    ") } }

private val IND1 = ind(1)
private val IND2 = ind(2)
private val IND3 = ind(3)
private val IND4 = ind(4)
