package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ProviderWithEvaluationCode

interface Provider : ProviderContainer {

    val origin: Origin? //todo make HasOrigin ?

    var typeNode: TypeNode

    val type: TypeName
        get() = typeNode.typeName

    val isUseful: Boolean

    fun isUsefulTransitively(): Boolean {
        return isUseful || childrenProviders.any { it.isUsefulTransitively() }
    }

    fun getProviderName(): String

    fun getEvaluationWay(context: FunctionBlockContext, forName: String): ProviderWithEvaluationCode

    fun getEvaluationRequirement(): EvaluationRequirement

    fun provideCodeForConstructionFromAux(auxName: String, watcherContextName: String): ProviderWithEvaluationCode?

    fun findNearestNotEvaluatedProvider(): Provider {
        if (childrenProviders.size != 1) return this

        val child = childrenProviders.single()
        return when (child.getEvaluationRequirement()) {
            EvaluationRequirement.NONE -> child
            else -> child.findNearestNotEvaluatedProvider()
        }
    }

}
