package io.kabu.backend.analyzer.handler.lambda.watcher

import io.kabu.backend.node.TypeNode
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.util.Constants

class CaptureTypeGroup(
    val operator: Operator,
    val funDeclarationProviders: FunDeclarationProviders,
    val returnTypeNode: TypeNode, //todo revise?
    val leftHandSideOfAssign: LeftHandSideOfAssign?,
) {

    fun getBaseNameForDeclarations(): String {
        val namePart = operator.overriding.function ?: "func"
        val argsPart = buildString {
            funDeclarationProviders.providersList.forEach {
                append(it.type.toString().replace(illegalKotlinIdentifierSymbol, "_"))
                append("_")
            }
        }
        val projectPart = Constants.PROJECT_BASE_PACKAGE.replace(".", "_")
        return listOf(projectPart, namePart, argsPart).joinToString("__")
    }

    private companion object {
        val illegalKotlinIdentifierSymbol = Regex("[^A-Za-z0-9_]")
    }
}
