package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ReplacementProviderWithCode

/**
 * Defines a runtime object (which can be saved into variable, passed as parameter, etc.).
 * May represent some useful value.
 * May have access to other providers (children).
 */
interface Provider : ProviderContainer {

    val origin: Origin? //todo make HasOrigin ?

    var typeNode: TypeNode

    val type: TypeName
        get() = typeNode.typeName

    /**
     * Does this provider represent useful value (passed into termination by itself).
     * If this provider only represents a way to retrieve useful data, then [isUseful] is false.
     */
    val isUseful: Boolean

    /**
     * Whether this provider can access (incl. transitively) other "useful providers" or not.
     */
    fun isUsefulTransitively(): Boolean {
        return isUseful || childrenProviders.any { it.isUsefulTransitively() }
    }

    /**
     * Auto-generated (e.g. by this provider's type) name of the provider.
     * May be used to form functions' parameters names.
     * Does NOT represent a "variable name" under which this provider is accessible.
     */
    fun generateName(): String

    fun getReplacementWay(context: FunctionBlockContext, forName: String): ReplacementProviderWithCode?

    fun isReplacementRequired(): EvaluationRequirement

    fun findNearestProviderRequiredForReplacement(): Provider {
        if (childrenProviders.size != 1) return this

        val child = childrenProviders.single()
        return when (child.isReplacementRequired()) {
            EvaluationRequirement.NONE -> child
            else -> child.findNearestProviderRequiredForReplacement()
        }
    }
}
