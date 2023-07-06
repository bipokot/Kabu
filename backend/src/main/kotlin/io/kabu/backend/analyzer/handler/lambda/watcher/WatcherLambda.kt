package io.kabu.backend.analyzer.handler.lambda.watcher

import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.WatcherContextTypeNode
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.util.Constants

class WatcherLambda(val watcherContextTypeNode: WatcherContextTypeNode) {

    val captureTypeGroups: MutableMap<CaptureTypeGroup, MutableList<CaptureType>> = mutableMapOf()

    fun register(captureType: CaptureType) {
        val group = findGroupForCaptureType(captureType)
        captureTypeGroups[group]!! += captureType
    }

    private fun findGroupForCaptureType(captureType: CaptureType): CaptureTypeGroup {
        var group = captureTypeGroups.keys
            .find { group -> areCompatible(captureType, group) }

        if (group == null) {
            group = CaptureTypeGroup(
                captureType.operator,
                captureType.funDeclarationProviders,
                captureType.returnTypeNode,
                captureType.assignableSuffixExpression,
            )
            captureTypeGroups[group] = mutableListOf()
        }

        return group
    }

    private fun areCompatible(captureType: CaptureType, group: CaptureTypeGroup): Boolean {
        val captureTypes = captureType.funDeclarationProviders.providersList.map { it.type }
        val groupTypes = group.funDeclarationProviders.providersList.map { it.type }
        return captureTypes.size == groupTypes.size &&
            captureTypes.zip(groupTypes).all { it.first == it.second } && //todo exact types match (ignoring type parameters)!
            captureType.operator.overriding?.function == group.operator.overriding?.function
    }
}

class CaptureTypeGroup(
    val operator: Operator,
    val funDeclarationProviders: FunDeclarationProviders,
    val returnTypeNode: TypeNode, //todo revise?
//    // in case of operator==Assign, tells us about actual accessor expression: (Access|Index)
    val assignableSuffixExpression: KotlinExpression?,
//    val rawProviders: RawProviders? = null
) {

    fun getBaseNameForDeclarations(): String {
        val namePart = operator.overriding?.function ?: "func"
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
