package io.kabu.backend.provider.provider

import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ReplacementProviderWithCode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.poet.TypeNameUtils.shorten

/**
 * Represents a runtime object, which can provide some useful values.
 *
 * @property type source code type of the parameter value (think of ot as Ctrl+Shift+P in client source code)
 */
open class BaseProvider( //todo abstract?
    override var typeNode: TypeNode,
    override val origin: Origin? = null,
): Provider {

    override val isUseful = false

    override fun generateName() = "<unknown>"

    override val childrenProviders: List<Provider>
        get() = emptyList()

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        return null
    }

    override fun getReplacementWay(context: FunctionBlockContext, forName: String): ReplacementProviderWithCode? {
        if (!isReplacementRequired()) return null

        val providerToObtain = findNearestProviderRequiredForReplacement()
        val code = getChildRetrievalWay(forName, providerToObtain, context.actualProvidersProvider)!!.codeBlock.toString()
        return ReplacementProviderWithCode(providerToObtain, code)
    }

    override fun isReplacementRequired(): Boolean = false

    override fun toString() = ":${type.shorten}"
}
