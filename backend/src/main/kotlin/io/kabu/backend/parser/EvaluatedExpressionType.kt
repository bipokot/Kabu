package io.kabu.backend.parser

/**
 * Defines a type which a translation expression must have
 */
enum class EvaluatedExpressionType {

    /**
     * No restrictions on expression type
     */
    FREE,

    /**
     * Expression will be of Boolean type
     */
    BOOLEAN,

    /**
     * Not an expression but a statement, so it has no return type
     */
    NONE,
}
