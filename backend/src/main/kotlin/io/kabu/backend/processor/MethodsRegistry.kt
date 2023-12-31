package io.kabu.backend.processor

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.inout.input.ProcessingInput
import io.kabu.backend.inout.input.method.ContextCreatorMethod
import io.kabu.backend.inout.input.method.LocalPatternMethod
import io.kabu.backend.node.TypeNode
import io.kabu.backend.util.poet.TypeNameUtils.isAssignableTo

class MethodsRegistry(processingInput: ProcessingInput? = null) {

    private val localPatterns: Map<TypeName, List<LocalPatternMethod>> // registry of LocalPattern methods
    private val contextCreators: List<ContextCreatorMethod> // registry of ContextCreator

    //todo not registry responsibility
    private val map: MutableMap<TypeName, TypeNode> = mutableMapOf()

    fun registerContextMediatorTypeNode(extensionContextTypeName: TypeName, contextMediatorTypeNode: TypeNode) {
        if (extensionContextTypeName in map) {
            error("Duplicate context mediator registration for: $extensionContextTypeName")
        }
        map[extensionContextTypeName] = contextMediatorTypeNode
    }

    fun getContextMediatorTypeNode(extensionContextTypeName: TypeName): TypeNode {
        return map[extensionContextTypeName]!!
    }

    init {
        if (processingInput != null) {
            contextCreators = validateContextCreators(processingInput.contextCreators)
            localPatterns = validateAndMapLocalPatternMethods(processingInput.localPatterns)
        } else {
            localPatterns = emptyMap()
            contextCreators = emptyList()
        }
    }

    fun getExtensionContextType(contextName: String): TypeName {
        val methods = contextCreators.filter { it.contextName == contextName }
        val groupedByExtensionContextType = methods.groupBy { it.returnedType }

        return when {
            groupedByExtensionContextType.isEmpty() ->
                diagnosticError("Context creator for context name '$contextName' not found.")

            groupedByExtensionContextType.size > 1 -> diagnosticError(
                "Context creators for one context name '$contextName' must have the same return type\n " +
                    "$groupedByExtensionContextType", *methods.toTypedArray()
            )

            else -> groupedByExtensionContextType.keys.first()
        }
    }

    fun findCreatorMethod(
        contextName: String,
        argumentTypes: List<TypeName>,
        contextType: TypeName,
    ): ContextCreatorMethod {
        val compatibleMethods = contextCreators.filter { candidate ->
            candidate.returnedType isAssignableTo contextType &&
                candidate.contextName == contextName &&
                candidate.parameters.size == argumentTypes.size &&
                candidate.parameters.zip(argumentTypes).all { (candidateMethodParameter, argumentType) ->
                    argumentType isAssignableTo candidateMethodParameter.type
                }
        }

        if (compatibleMethods.isEmpty()) {
            diagnosticError("No compatible context creator method found for context name '$contextName' " +
                "(type: '$contextType') and parameters $argumentTypes")
        }
        if (compatibleMethods.size > 1) {
            //todo extra = it.toString()
            val origins = compatibleMethods.map { Origin(extra = "${it.packageName} ${it.name}") }
            diagnosticError("Context creator method ambiguity for context name '$contextName' " +
                "(type: '$contextType') and parameters: $argumentTypes", origins)
        }

        return compatibleMethods.first()
    }

    fun getLocalPatternMethods(typeName: TypeName): List<LocalPatternMethod> {
        return localPatterns[typeName] ?: emptyList()
    }

    private fun validateContextCreators(contextCreators: List<ContextCreatorMethod>): List<ContextCreatorMethod> {
        //todo check for absence of receiver
        return contextCreators
    }

    private fun validateAndMapLocalPatternMethods(
        localPatternMethods: List<LocalPatternMethod>,
    ): Map<TypeName, List<LocalPatternMethod>> {
        return localPatternMethods.groupBy { it.declaringType }
    }
}
