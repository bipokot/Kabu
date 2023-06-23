package io.kabu.backend.analyzer.handler

import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.analyzer.handler.lambda.LambdaHandler
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.parser.OperatorExpression
import io.kabu.backend.provider.provider.Provider

class TerminalHandler(
    private val analyzer: AnalyzerImpl
) {

    fun handle(expression: KotlinExpression): Provider {
        val rootProvider = when (expression) {
            is OperatorExpression -> OperatorHandler(analyzer).handle(expression)
            is LambdaExpression -> LambdaHandler(analyzer).handle(expression)
            is IdentifierLeaf -> IdentifierHandler(analyzer).handle(expression)
        }
        return rootProvider
    }
}
