package io.kabu.backend.analyzer.handler.lambda.watcher

import io.kabu.backend.node.TypeNode
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.provider.group.RawProviders


class CaptureType(
    val operator: Operator,
    val funDeclarationProviders: FunDeclarationProviders,
    val returnTypeNode: TypeNode,
    // in case of operator==Assign, tells us about actual accessor expression: (Access|Index)
    val assignableSuffixExpression: KotlinExpression? = null, //todo get rid of KExpression, use class/enum instead?
    val rawProviders: RawProviders? = null
)
