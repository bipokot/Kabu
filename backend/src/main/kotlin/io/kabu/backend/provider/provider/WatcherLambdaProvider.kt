package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.ClassName
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ReplacementProviderWithCode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.poet.asCodeBlock


class WatcherLambdaProvider(
    typeNode: TypeNode,
    val watcherContextProvider: WatcherContextProvider,
    val analyzer: Analyzer,
    origin: Origin? = null,
) : AbstractProvider(typeNode, origin) {

    override fun generateName(): String {
        return watcherContextProvider.generateName() + "Lambda"
    }

    override val childrenProviders: List<Provider>
        get() = listOf(watcherContextProvider)

    override fun getReplacementWay(context: FunctionBlockContext, forName: String): ReplacementProviderWithCode? {
        if (!isReplacementRequired()) return null
        
        // watcher context
        val watcherContextCode =
            getChildRetrievalWay(forName, watcherContextProvider, context.actualProvidersProvider)!!
            .codeBlock.toString()
        val watcherContextName = context.nextVarName()
        val watcherContextCodeStatement = "val $watcherContextName=$watcherContextCode"
        context.addStatements(listOf(watcherContextCodeStatement))

        // payload
        val payloadProvider = watcherContextProvider.findNearestProviderRequiredForReplacement()
        val payloadName = context.nextVarName()
        val payloadCode = watcherContextProvider
            .getChildRetrievalWay(watcherContextName, payloadProvider, context.actualProvidersProvider)!!
            .codeBlock.toString()

        context.addStatements(listOf("val $payloadName=$watcherContextName.run{"))
        context.addStatements(listOf(payloadCode))
        context.addStatements(listOf("}"))

        return ReplacementProviderWithCode(payloadProvider, payloadName)
    }

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !== watcherContextProvider) return null

        val contextClassName = (watcherContextProvider.typeNode.typeName as ClassName).canonicalName
        return RetrievalWay(
            "$contextClassName().apply{$STACK_PROPERTY_NAME.push(${selfName!!}())}".asCodeBlock(),
            isReentrant = false
        )
    }

    override fun isReplacementRequired() = true

    companion object {
        const val STACK_PROPERTY_NAME = "stack"
    }
}
