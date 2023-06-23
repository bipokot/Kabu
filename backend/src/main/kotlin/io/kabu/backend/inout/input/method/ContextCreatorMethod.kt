package io.kabu.backend.inout.input.method

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.parameter.EntryParameter
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.provider.provider.ProviderContainer

open class ContextCreatorMethod(
    packageName: String,
    name: String,
    returnedType: TypeName,
    receiverType: TypeName?,
    parameters: List<EntryParameter>,
    val contextName: String,
    origin: Origin
) : Method(
    packageName,
    name,
    returnedType,
    receiverType,
    parameters,
    origin
) {

    open fun getInvocationCode(parameters: List<Provider>, providerContainer: ProviderContainer): String {
        val invocationArgumentsString = parameters.joinToString(", ") {
            providerContainer.getChildRetrievalWay(selfName = null, it, providerContainer)!!.codeBlock.toString()
        }
        return "${packageName}.${name}($invocationArgumentsString)"
    }

    companion object {

        fun Method.toContextCreatorMethod(contextName: String) = ContextCreatorMethod(
            packageName = packageName,
            name = name,
            returnedType = returnedType,
            receiverType = receiverType,
            parameters = parameters,
            contextName = contextName,
            origin = origin
        )
    }
}
