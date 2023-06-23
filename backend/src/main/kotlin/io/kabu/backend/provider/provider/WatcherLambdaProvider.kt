package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.ClassName
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.EvaluationCode
import io.kabu.backend.provider.evaluation.EvaluationRequirement
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.evaluation.ProviderWithEvaluationCode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.poet.asCodeBlock


class WatcherLambdaProvider(
    typeNode: TypeNode,
    val returnType: Provider, //todo unused?
    val watcherContextProvider: WatcherContextProvider,
    val analyzer: Analyzer,
    origin: Origin? = null,
) : BaseProvider(typeNode, origin) {

    override fun getProviderName(): String {
        return watcherContextProvider.getProviderName() + "Lambda"
    }

    override val childrenProviders: List<Provider>
        get() = listOf(watcherContextProvider)

    override fun getEvaluationWay(context: FunctionBlockContext, forName: String): ProviderWithEvaluationCode {
        // watcher context
        val watcherContextCode = getChildRetrievalWay(forName, watcherContextProvider, context.actualProvidersProvider)!!
            .codeBlock.toString()
        val watcherContextName = context.nextVarName()
        val watcherContextCodeStatement = "val $watcherContextName=$watcherContextCode"
        context.addStatements(listOf(watcherContextCodeStatement))

        // payload
        val payloadProvider = watcherContextProvider.findNearestNotEvaluatedProvider()
        val payloadName = context.nextVarName()
        val payloadCode = watcherContextProvider
            .getChildRetrievalWay(watcherContextName, payloadProvider, context.actualProvidersProvider)!!
            .codeBlock.toString()

        context.addStatements(listOf("val $payloadName=$watcherContextName.run{"))
        context.addStatements(listOf(payloadCode))
        context.addStatements(listOf("}"))

        return ProviderWithEvaluationCode(payloadProvider, EvaluationCode.Code(payloadName))
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

    override fun getEvaluationRequirement(): EvaluationRequirement =
        EvaluationRequirement.MANDATORY

    companion object {
        const val STACK_PROPERTY_NAME = "stack"
    }
}
