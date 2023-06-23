package io.kabu.backend.parser

import io.kabu.backend.diagnostic.HasOrigin
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.diagnostic.Origin


sealed class KotlinExpression : HasOrigin {
    abstract val children: List<KotlinExpression>
    abstract var parent: KotlinExpression?
        internal set
    abstract override val origin: Origin
    abstract fun toExcerptsString() : String

    fun validateParentsInAllChildrenArePresent() {
        children.forEach { it.validateParentsArePresent() }
    }

    private fun validateParentsArePresent() {
        if (parent == null) diagnosticError("Parent of expression is absent", this)
        children.forEach {
            it.validateParentsArePresent()
        }
    }

    protected fun setChildrenParent() {
        children.forEach { it.parent = this }
    }
}

class IdentifierLeaf(
    val name: String,
    override val origin: Origin,
) : KotlinExpression() {
    override var parent: KotlinExpression? = null
    override val children = emptyList<KotlinExpression>()
    override fun toString() = name
    override fun toExcerptsString() = quotedExcerpt
}

class LambdaExpression(
    val expressions: List<KotlinExpression>,
    val annotations: List<Annotation>,
    override val origin: Origin
) : KotlinExpression() {
    override var parent: KotlinExpression? = null
    override val children = expressions
    override fun toString() = "(${annotations.let { if (it.isNotEmpty()) "@$it " else "" }}lambda $expressions)"
    override fun toExcerptsString(): String {
        val annotationsPart = annotations.let { if (it.isNotEmpty()) "@$it " else "" } //todo annotations excerpts
        val expressionsPart = expressions.map { it.toExcerptsString() }
        return "$quotedExcerpt(${annotationsPart}lambda $expressionsPart)"
    }

    init {
        setChildrenParent()
    }
}

sealed class OperatorExpression(
    open val operator: Operator,
    override val origin: Origin
) : KotlinExpression() {
    override var parent: KotlinExpression? = null
}

class UnaryExpression(
    override val operator: UnaryOperator,
    val operand: KotlinExpression,
    origin: Origin
) : OperatorExpression(operator, origin) {
    override val children = listOf(operand)
    override fun toString() = when (operator) {
        is UnaryPrefix -> "(${operator.symbol} $operand)"
        is UnaryPostfix -> "($operand ${operator.symbol})"
    }
    override fun toExcerptsString() = when (operator) {
        is UnaryPrefix -> "$quotedExcerpt(${operator.quotedExcerpt} ${operand.toExcerptsString()})"
        is UnaryPostfix -> "$quotedExcerpt(${operand.toExcerptsString()} ${operator.quotedExcerpt})"
    }

    init {
        setChildrenParent()
    }
}

class BinaryExpression(
    override val operator: BinaryOperator,
    val left: KotlinExpression,
    val right: KotlinExpression,
    origin: Origin
) : OperatorExpression(operator, origin) {
    override val children = listOf(left, right)
    override fun toString() = when (operator) {
        is InfixFunction -> "($left `${operator.symbol}` $right)"
        else -> "($left ${operator.symbol} $right)"
    }
    override fun toExcerptsString() =
        "$quotedExcerpt(${left.toExcerptsString()} ${operator.quotedExcerpt} ${right.toExcerptsString()})"

    init {
        setChildrenParent()
    }
}

class NaryExpression(
    override val operator: NaryOperator,
    val operand: KotlinExpression,
    val arguments: List<KotlinExpression>,
    origin: Origin
) : OperatorExpression(operator, origin) {
    override val children = listOf(operand) + arguments
    override fun toString() = "($operand ${operator.symbol} $arguments)"
    override fun toExcerptsString(): String {
        val argumentsPart = arguments.map { it.toExcerptsString() }
        return "$quotedExcerpt(${operand.toExcerptsString()} ${operator.quotedExcerpt} $argumentsPart)"
    }

    init {
        setChildrenParent()
    }
}

private val Operator.quotedExcerpt: String get() = "'${origin.excerpt!!}'"
private val KotlinExpression.quotedExcerpt: String get() = "'${origin.excerpt!!}'"
