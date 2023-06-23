package io.kabu.backend.analyzer.handler.lambda

import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.analyzer.handler.Handler
import io.kabu.backend.provider.provider.EmptyProvider
import io.kabu.backend.provider.provider.LambdaProvider
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode

class EmptyLambdaHandler(analyzer: AnalyzerImpl) : Handler(analyzer) {

    fun handle(): LambdaProvider {
        val returnTypeNode = UNIT.toFixedTypeNode()
        val returningProvider = EmptyProvider(returnTypeNode)

        val typeName = LambdaTypeName.get(returnType = UNIT)
        val typeNode = typeName.toFixedTypeNode()

        return LambdaProvider(typeNode, returningProvider, analyzer)
    }
}
