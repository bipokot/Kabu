package io.kabu.backend.inout.input.method

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.parameter.EntryParameter
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.provider.provider.ProviderContainer

class ContextConstructorMethod(
    packageName: String,
    name: String,
    returnedType: TypeName,
    receiverType: TypeName?,
    parameters: List<EntryParameter>,
    contextName: String,
    val declaringType: TypeName,
    origin: Origin
) : ContextCreatorMethod(
    packageName = packageName,
    name = name,
    returnedType = returnedType,
    receiverType = receiverType,
    parameters = parameters,
    contextName = contextName,
    origin = origin
) {

    override val returnedType = declaringType

    override fun getInvocationCode(providers: List<Provider>, providerContainer: ProviderContainer): String {
        val invocationArgumentsString = providers.joinToString(", ") {
            providerContainer.getChildRetrievalWay(selfName = null, it, providerContainer)!!.codeBlock.toString()
        }
        return "${declaringType}($invocationArgumentsString)"
    }

    companion object {

        fun Method.toContextConstructorMethod(contextName: String, enclosingTypeName: TypeName) =
            ContextConstructorMethod(
                packageName = packageName,
                name = name,
                returnedType = returnedType,
                receiverType = receiverType,
                parameters = parameters,
                contextName = contextName,
                declaringType = enclosingTypeName,
                origin = origin
            )
    }
}
