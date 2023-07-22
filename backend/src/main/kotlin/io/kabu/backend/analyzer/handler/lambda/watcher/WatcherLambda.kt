package io.kabu.backend.analyzer.handler.lambda.watcher

import io.kabu.backend.node.WatcherContextTypeNode

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
                captureType.leftHandSideOfAssign,
            )
            captureTypeGroups[group] = mutableListOf()
        }

        return group
    }

    private fun areCompatible(captureType: CaptureType, group: CaptureTypeGroup): Boolean {
        val captureTypes = captureType.funDeclarationProviders.providersList.map { it.type }
        val groupTypes = group.funDeclarationProviders.providersList.map { it.type }

        //todo exact types match (ignoring type parameters)!
        return captureTypes.size == groupTypes.size &&
            captureTypes.zip(groupTypes).all { it.first == it.second } &&
            captureType.operator.overriding.function == group.operator.overriding.function
    }
}
