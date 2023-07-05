package io.kabu.backend.analyzer.handler.lambda

import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.analyzer.handler.Handler
import io.kabu.backend.analyzer.handler.canBeLambdaWithReceiver
import io.kabu.backend.analyzer.handler.isTerminal
import io.kabu.backend.analyzer.handler.lambda.extension.ExtensionLambdaHandler
import io.kabu.backend.analyzer.handler.lambda.watcher.WatcherLambda
import io.kabu.backend.analyzer.handler.lambda.watcher.WatcherLambdaHandler
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.node.factory.node.WatcherContextTypeNodeImpl
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.util.Constants.EXTENSION_ANNOTATION

class LambdaHandler(analyzer: AnalyzerImpl) : Handler(analyzer) {

    fun handle(expression: LambdaExpression): Provider {
        if (expression.isTerminal) {
            diagnosticError("An expression pattern can not be form from a lambda literal alone", expression)
        }
        if (expression.expressions.size > 1) {
            diagnosticError("At most one statement allowed in a lambda", expression)
        }

        //todo make search method by annotation data class (encapsulate a name of annotation)
        val hasExtensionAnnotation = expression.annotations.any { it.name == EXTENSION_ANNOTATION }
        if (hasExtensionAnnotation) {
            return ExtensionLambdaHandler(analyzer).handle(expression)
        }

        if (expression.expressions.isEmpty()) {
            // returns Unit
            return EmptyLambdaHandler(analyzer).handle()
        }

        if (expression.canBeLambdaWithReceiver) {
            // creating WatcherContextTypeNode
            val watcherContextName = namespaceNode.typeNameGenerator.generateNextTypeName()
            val watcherContextTypeNode = WatcherContextTypeNodeImpl(watcherContextName, namespaceNode)
            registerNode(watcherContextTypeNode)

            // creating WatcherLambda
            val watcherLambda = WatcherLambda(watcherContextTypeNode)

            val watcherLambdaParameter = analyzer.withWatcherLambda(watcherLambda) {
                WatcherLambdaHandler(analyzer).handle(expression)
            }

            return watcherLambdaParameter
        }

        // lambda cannot be with receiver
        return RegularLambdaHandler(analyzer).handle(expression)
    }
}
