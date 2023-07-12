package io.kabu.backend.analyzer

import io.kabu.backend.analyzer.handler.lambda.watcher.OperatorInfoTypes.isOperatorInfoType
import io.kabu.backend.diagnostic.builder.sameNamedParametersError
import io.kabu.backend.inout.input.method.PatternMethod
import io.kabu.backend.node.FixedTypeNode
import io.kabu.backend.parameter.Parameter
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.util.Constants.RECEIVER_PARAMETER_NAME
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode

class ParametersRegistry(
    val method: PatternMethod,
    val expression: KotlinExpression
) {

    val parameters: List<Parameter> = composeParameters()
        .also { validateEntryParameters(it) }
    val parametersTypes: Map<String, FixedTypeNode> = parameters
        .groupBy({ it.name }, { it.type.toFixedTypeNode() })
        .mapValues { it.value.single() }

    var receiverCatchIsNecessary = false
    private var operatorInfoParametersUsed = false
    val strictMethodParametersOrdering get() = operatorInfoParametersUsed

    //todo simplify
    private fun composeParameters(): List<Parameter> {
        val receiver = method.receiver
        val parameterList = method.parameters.map { //todo unnecessary map?
            Parameter(it.name, it.type, it.origin)
        }
        return listOfNotNull(receiver) + parameterList
    }

    private fun validateEntryParameters(parameters: List<Parameter>) {
        checkStrictParametersOrdering(parameters)
        checkReceiverParameterPresence()
        checkForSameNamedParameters(parameters)
    }

    private fun checkForSameNamedParameters(methodParameters: List<Parameter>) {
        val sameNamedParameters = methodParameters
            .groupBy { it.name }
            .filter { it.value.size > 1 }
        sameNamedParameters.entries.firstOrNull()
            ?.let { (name, parameters) -> sameNamedParametersError(name, parameters) }
    }

    private fun checkStrictParametersOrdering(methodParameters: List<Parameter>) {
        operatorInfoParametersUsed = methodParameters.any { it.type.isOperatorInfoType }
    }

    private fun checkReceiverParameterPresence() {

        fun KotlinExpression.hasReceiverParameter(): Boolean {
            if (this is IdentifierLeaf && name == RECEIVER_PARAMETER_NAME) return true
            return children.any { it.hasReceiverParameter() }
        }

        receiverCatchIsNecessary = method.hasReceiver && !expression.hasReceiverParameter()
    }
}
