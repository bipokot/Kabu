package io.kabu.backend.analyzer.handler.lambda.watcher

/**
 * Represents information about left hand side of assign operation
 */
sealed class LeftHandSideOfAssign {
    /**
     * @property name - "x" for assign expression "x = z"
     */
    data class StandaloneProperty(val name: String): LeftHandSideOfAssign()

    /**
     * @property name - "y" for assign expression "x.y = z"
     */
    data class ObjectProperty(val name: String): LeftHandSideOfAssign()

    /**
     * "x[y] = z"
     */
    data object Indexing : LeftHandSideOfAssign()
}
