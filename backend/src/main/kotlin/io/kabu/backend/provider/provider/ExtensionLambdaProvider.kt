package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.ClassName
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.analyzer.handler.lambda.extension.ContextCreatorDefinition
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.builder.parameterIsNotEvaluatedYetError
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.evaluation.RetrievalWay
import io.kabu.backend.util.poet.asCodeBlock


class ExtensionLambdaProvider(
    typeNode: TypeNode,
    returningProvider: BaseProvider, //todo don't need? //todo rename
    val contextMediatorTypeNode: TypeNode,
    val contextCreatorDefinition: ContextCreatorDefinition,
    val destinationParameterTypeNode: TypeNode,
    analyzer: Analyzer,
    origin: Origin? = null,
) : LambdaProvider(typeNode, returningProvider, analyzer, origin) {

    private fun extractProvidersForRequiredArgumentNames(
        requiredArguments: List<String>,
        providerContainer: ProviderContainer,
    ): List<Provider> {
        return requiredArguments.map { argumentName ->
            // finding an already evaluated provider which corresponds to particular argument name
            val argumentProvider = providerContainer
                .findProvider { it is ArgumentProvider && it.originalName == argumentName }

            if (argumentProvider == null) {
                // provider not found
                val availableProviders = providerContainer
                    .findProviders { it is ArgumentProvider }
                    .filterIsInstance<ArgumentProvider>()
                    .map { it.originalName }
                parameterIsNotEvaluatedYetError(argumentName, availableProviders, analyzer.expression)
            }

            argumentProvider
        }
    }

    override fun getImmediateChildRetrievalWay(
        selfName: String?,
        provider: Provider,
        providerContainer: ProviderContainer?,
    ): RetrievalWay? {
        if (provider !== returningProvider) return null

        // finding providers for required names
        val creatorParameterProviders =
            extractProvidersForRequiredArgumentNames(contextCreatorDefinition.arguments, providerContainer!!)
        val creatorParametersTypes = creatorParameterProviders.map { it.type }

        // finding compatible creator method
        val creatorMethod = analyzer.methodsRegistry.findCreatorMethod(
            contextName = contextCreatorDefinition.name,
            argumentTypes = creatorParametersTypes,
            contextType = destinationParameterTypeNode.typeName
        )

        // constructing evaluation code
        val contextCreatorInvocation = creatorMethod.getInvocationCode(creatorParameterProviders, providerContainer)
        val contextMediatorClassName = (contextMediatorTypeNode.typeName as ClassName).canonicalName

        val code = "($contextCreatorInvocation).also{with($contextMediatorClassName(it)){${selfName!!}()}}"
        return RetrievalWay(code.asCodeBlock(), isReentrant = false)
    }

    override fun isReplacementRequired() = true
}
