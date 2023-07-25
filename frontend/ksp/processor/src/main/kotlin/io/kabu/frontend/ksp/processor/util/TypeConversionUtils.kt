package io.kabu.frontend.ksp.processor.util

import com.google.devtools.ksp.symbol.KSCallableReference
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeReference
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeName

internal fun KSTypeReference.asTypeName(
    typeParamResolver: TypeParameterResolver = TypeParameterResolver.EMPTY,
): TypeName {
    val type = resolve()

    (element as? KSCallableReference)?.let { callableReference ->
        validateCallableReference(callableReference)

        val typeName = LambdaTypeName.get(
            receiver = callableReference.receiverType?.asTypeName(typeParamResolver),
            parameters = callableReference.functionParameters.map {
                validateValueParameter(it)

                val modifiers = mutableListOf<KModifier>().apply {
                    if (it.isVararg) add(KModifier.VARARG)
                }

                ParameterSpec(name = it.name?.asString() ?: "", type = it.type.asTypeName(typeParamResolver), modifiers)
            },
            returnType = callableReference.returnType.asTypeName(typeParamResolver)
        )

        return typeName.copy(nullable = type.isMarkedNullable)
    }

    return toTypeName(typeParamResolver)
}

internal fun KSType.asTypeName(
    typeParamResolver: TypeParameterResolver = TypeParameterResolver.EMPTY,
): TypeName = toTypeName(typeParamResolver)
