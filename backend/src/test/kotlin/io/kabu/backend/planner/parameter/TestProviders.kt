package io.kabu.backend.planner.parameter

import com.squareup.kotlinpoet.CodeBlock
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ReplacementProviderWithCode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.provider.provider.ProviderContainer

interface TestProvider : Provider {

    override val origin: Origin?
        get() = null
    
    override val isUseful: Boolean
        get() = false

    override var typeNode: TypeNode
        get() = TODO("Not yet implemented")
        set(value) {}

    override fun getReplacementWay(context: FunctionBlockContext, forName: String): ReplacementProviderWithCode? {
        TODO()
    }

    override fun isReplacementRequired(): EvaluationRequirement {
        return EvaluationRequirement.NONE
    }
}


class TestHolderProvider(val name: String, val children: List<Provider>): TestProvider {

    override fun generateName() = name

    override val childrenProviders: List<Provider>
        get() = children

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        val index = children.indexOf(provider)
            .also { if (it == -1) return null }
        return RetrievalWay(CodeBlock.of("${selfName!!}.f$index"), isReentrant = true)
    }

    override fun toString() = "Holder($name)"
}

class TestLambdaProvider(val name: String, val returns: Provider): TestProvider {

    override fun generateName() = name

    override val childrenProviders: List<Provider>
        get() = listOf(returns)

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (returns != provider) return null
        return RetrievalWay(CodeBlock.of("${selfName!!}.invoke()"), isReentrant = false)
    }

    override fun toString() = "Lambda($name)"
}

class TestEmptyProvider(val name: String): TestProvider {

    override fun generateName() = name

    override val childrenProviders: List<Provider>
        get() = emptyList()

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        return null
    }

    override fun toString() = "Empty($name)"
}

class TestArgumentProvider(val originalName: String): TestProvider {

    override val isUseful: Boolean = true

    override fun generateName() = originalName

    override val childrenProviders: List<Provider>
        get() = emptyList()

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        return null
    }

    override fun toString() = "Argument($originalName)"
}
