package io.kabu.backend.analyzer.handler.lambda.watcher

import io.kabu.backend.node.TypeNode
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.provider.group.RawProviders


class CaptureType(
    val operator: Operator,
    val funDeclarationProviders: FunDeclarationProviders,
    val returnTypeNode: TypeNode,
    val leftHandSideOfAssign: LeftHandSideOfAssign? = null,
    val rawProviders: RawProviders? = null
)

