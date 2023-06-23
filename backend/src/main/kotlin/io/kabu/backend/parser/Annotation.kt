package io.kabu.backend.parser

data class Annotation(
    val name: String,
    val arguments: List<Argument>
) {

    data class Argument(
        val name: String?,
        val expression: KotlinExpression
    ) {
        override fun toString() = "${if (name != null) "$name=" else ""}$expression"
    }

    override fun toString() = "$name(${arguments.joinToString()})"
}
