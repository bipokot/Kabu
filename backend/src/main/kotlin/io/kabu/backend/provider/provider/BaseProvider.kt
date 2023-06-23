package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationCode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ProviderWithEvaluationCode
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

    override fun getProviderName() = "<unknown>"

    override val childrenProviders: List<Provider>
        get() = emptyList()

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        return null
    }

    override fun getEvaluationWay(context: FunctionBlockContext, forName: String): ProviderWithEvaluationCode =
        ProviderWithEvaluationCode(this, EvaluationCode.MatchingIdentifier)

    override fun getEvaluationRequirement(): EvaluationRequirement =
        EvaluationRequirement.NONE

    override fun provideCodeForConstructionFromAux(
        auxName: String, //todo introduce `class VariableName(val value: String)` / `class Code(val value: String)`
        watcherContextName: String,
    ): ProviderWithEvaluationCode? = null

    /** Declared return type of translation (e.g. Int for comparison checks), can differ from [type]. */
    open fun translationReturnedType(): TypeName = type

    override fun toString() = ":${type.shorten}"
}
