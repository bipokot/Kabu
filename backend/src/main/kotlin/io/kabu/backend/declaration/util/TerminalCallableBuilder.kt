package io.kabu.backend.declaration.util

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.analyzer.AnalyzerImpl
import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes.isOperatorInfoType
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.diagnostic.builder.couldNotRetrieveReceiverValueError
import io.kabu.backend.diagnostic.builder.signatureParameterMissingError
import io.kabu.backend.diagnostic.builder.signatureParameterMissingInPatternError
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.provider.ArgumentProvider
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.provider.provider.ProviderContainer
import io.kabu.backend.provider.render.renderProviderTree
import io.kabu.backend.util.Constants.EXTENSION_CONTEXT_PROPERTY_NAME
import io.kabu.backend.util.poet.asCodeBlock


class TerminalCallableBuilder {

    fun createTerminationStatements(
        analyzer: Analyzer,
        functionBlockContext: FunctionBlockContext,
        requiredReturnStatement: String?,
    ): CodeBlock {
        logger.debug { renderProviderTree(functionBlockContext.actualProvidersProvider) }

        val providers: List<Provider> =
            gatherRequiredProviders(analyzer, functionBlockContext.actualProvidersProvider)

        AccessingCodeReducer().reduceAccessingCode(functionBlockContext)

        return generateCode(analyzer, providers, requiredReturnStatement, functionBlockContext)
    }

    private fun gatherRequiredProviders(
        analyzer: Analyzer,
        providerContainer: ProviderContainer,
    ): List<Provider> {
        analyzer as AnalyzerImpl
        val parametersRegistry = analyzer.parametersRegistry
        if (parametersRegistry.receiverCatchIsNecessary) couldNotRetrieveReceiverValueError(analyzer.method)

        val patternOrderedEnumeration = providerContainer
            .findProviders { it is ArgumentProvider }

        var latestIdentifierParameterIndex = -1
        val providers: List<Provider> = parametersRegistry.parameters.map { signatureParameter ->
            if (signatureParameter.type.isOperatorInfoType) {
                patternOrderedEnumeration.elementAtOrNull(latestIdentifierParameterIndex + 1)
                    ?.takeIf { it.type.isOperatorInfoType }
                    ?: signatureParameterMissingError(signatureParameter)
            } else {
                latestIdentifierParameterIndex = patternOrderedEnumeration
                    .indexOfFirst { it is ArgumentProvider && it.originalName == signatureParameter.name }
                patternOrderedEnumeration.elementAtOrNull(latestIdentifierParameterIndex)
                    ?: signatureParameterMissingInPatternError(signatureParameter)
            }
        }
        return providers
    }

    private fun generateCode(
        analyzer: Analyzer,
        providers: List<Provider>,
        requiredReturnStatement: String?,
        functionBlockContext: FunctionBlockContext,
    ): CodeBlock {
        val evaluatedProvidersProvider = functionBlockContext.actualProvidersProvider
        val codes = providers.map {
            val retrievalWay = evaluatedProvidersProvider
                .getChildRetrievalWay(selfName = null, it, evaluatedProvidersProvider)!!
            retrievalWay.codeBlock.toString() //todo ignoring reentrant!!!
        }

        val evaluationStatements = functionBlockContext.joinAllStatements()

        val method = analyzer.method
        val methodName = method.name
        val receiverExists = method.hasReceiver
        val returningUnit = method.returnedType == UNIT
        val terminatingLocalPatternMethod = analyzer.isLocalPattern
        if (receiverExists || terminatingLocalPatternMethod) {
            if (receiverExists == terminatingLocalPatternMethod) {
                diagnosticError("Member functions with receiver aren't supported", method)
            }
            val (receiver, callParameters) = if (receiverExists) {
                codes.first() to codes.drop(1)
            } else {
                EXTENSION_CONTEXT_PROPERTY_NAME to codes
            }
            val names = callParameters.joinToString()
            return if (returningUnit) {
                joinStatements(evaluationStatements, "$receiver.$methodName($names)", requiredReturnStatement)
            } else {
                joinStatements(evaluationStatements, "return $receiver.$methodName($names)")
            }
        } else {
            val names = codes.joinToString()
            return if (returningUnit) {
                joinStatements(evaluationStatements, "$methodName($names)", requiredReturnStatement)
            } else {
                joinStatements(evaluationStatements, "return $methodName($names)")
            }
        }
    }

    private fun joinStatements(vararg statements: String?): CodeBlock {
        return statements.asSequence()
            .filterNotNull()
            .filterNot { it.isBlank() }
            .joinToString(separator = "\n")
            .asCodeBlock()
    }

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}
