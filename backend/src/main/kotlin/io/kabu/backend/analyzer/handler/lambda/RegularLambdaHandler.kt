package io.kabu.backend.analyzer.handler.lambda

import com.squareup.kotlinpoet.LambdaTypeName
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.analyzer.handler.Handler
import io.kabu.backend.node.DerivativeTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.provider.provider.LambdaProvider

class RegularLambdaHandler(analyzer: AnalyzerImpl) : Handler(analyzer) {

    fun handle(expression: LambdaExpression): LambdaProvider {
        val returningProvider = providerOf(expression.expressions.first())

        val returningProviderTypeNode = returningProvider.typeNode
        val typeNode = DerivativeTypeNode(namespaceNode, mutableListOf(returningProviderTypeNode)) { deps ->
            LambdaTypeName.get(returnType = (deps.first() as TypeNode).typeName)
        }
        registerNode(typeNode)

        return LambdaProvider(typeNode, returningProvider, analyzer)
    }
}
