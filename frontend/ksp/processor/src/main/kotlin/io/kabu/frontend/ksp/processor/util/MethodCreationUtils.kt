package io.kabu.frontend.ksp.processor.util

import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.exception.PatternProcessingException
import io.kabu.backend.inout.input.method.Method
import io.kabu.backend.parameter.Parameter
import io.kabu.backend.util.Constants.RECEIVER_PARAMETER_NAME
import io.kabu.frontend.ksp.diagnostic.builder.parameterProcessingError


internal fun KSFunctionDeclaration.toMethod(parentTypeParametersResolver: TypeParameterResolver? = null): Method {
    val typeParameterResolver = typeParameters
        .toTypeParameterResolver(parentTypeParametersResolver, "'$this, ${this.location}'")
    val methodOrigin = originOf(this)
    val methodName = simpleName.asString()

    fun createParameter(
        name: String,
        typeReference: KSTypeReference,
        origin: Origin,
        methodName: String,
    ) = try {
        typeReference.validate()
        Parameter(name, typeReference.asTypeName(typeParameterResolver), origin)
    } catch (e: PatternProcessingException) {
        parameterProcessingError(name, methodName, e, origin)
    }

    val receiver = extensionReceiver
        ?.let { createParameter(RECEIVER_PARAMETER_NAME, it, originOf(it, parent = methodOrigin), methodName) }
    val returnType = returnType?.asTypeName(typeParameterResolver)!!
        .also { returnType?.validate() }
    val parameters = parameters.map { parameter ->
        validateValueParameter(parameter)
        val parameterOrigin = originOf(parameter, parent = methodOrigin)
        val name = parameter.name!!.asString()

        createParameter(name, parameter.type, parameterOrigin, methodName)
    }

    return Method(
        packageName = packageName.asString(),
        name = methodName,
        returnedType = returnType,
        receiver = receiver,
        parameters = parameters,
        origin = methodOrigin,
    )
}
