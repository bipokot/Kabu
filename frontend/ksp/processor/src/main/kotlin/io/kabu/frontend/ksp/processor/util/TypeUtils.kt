package io.kabu.frontend.ksp.processor.util

import com.google.devtools.ksp.symbol.KSCallableReference
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Variance
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.exception.PatternProcessingException
import io.kabu.backend.inout.input.method.Method
import io.kabu.backend.parameter.Parameter
import io.kabu.backend.util.Constants.RECEIVER_PARAMETER_NAME
import io.kabu.frontend.ksp.diagnostic.builder.parameterProcessingError


internal fun KSFunctionDeclaration.toMethod(): Method {

    fun createParameter(
        name: String,
        typeReference: KSTypeReference,
        origin: Origin,
        methodName: String,
    ) = try {
        Parameter(name, typeReference.toTypeName(), origin)
    } catch (e: PatternProcessingException) {
        parameterProcessingError(name, methodName, e, origin)
    }

    val methodOrigin = originOf(this)
    val methodName = simpleName.asString()
    val receiver = extensionReceiver
        ?.let { createParameter(RECEIVER_PARAMETER_NAME, it, originOf(it, parent = methodOrigin), methodName) }
    val returnType = returnType?.toTypeName()!!
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

internal fun KSTypeReference.toTypeName(): TypeName {
    val type = resolve()
    validateType(type)
    
    (element as? KSCallableReference)?.let { callableReference ->
        validateCallableReference(callableReference)

        val typeName = LambdaTypeName.get(
            receiver = callableReference.receiverType?.toTypeName(),
            parameters = callableReference.functionParameters.map {
                validateValueParameter(it)

                val modifiers = mutableListOf<KModifier>().apply {
                    if (it.isVararg) add(KModifier.VARARG)
                }

                ParameterSpec(name = it.name?.asString() ?: "", type = it.type.toTypeName(), modifiers)
            },
            returnType = callableReference.returnType.toTypeName()
        )

        return if (type.isMarkedNullable) {
            typeName.copy(nullable = true)
        } else typeName
    }

    // assuming interface or class below
    return type.toTypeName()
}

internal fun KSType.toTypeName(): TypeName {
    validateType(this)

    val className = ClassName(declaration.packageName.asString(), declaration.simpleName.asString())
    val typeName = if (arguments.isEmpty()) className else {
        className.parameterizedBy(
            arguments.map { typeArgument ->
                validateTypeArgument(typeArgument)
                typeArgument.type!!.toTypeName()
            }
        )
    }
    return if (isMarkedNullable) {
        typeName.copy(nullable = true)
    } else typeName
}

internal fun validateTypeArgument(typeArgument: KSTypeArgument) {
    areNotSupported(typeArgument.variance == Variance.CONTRAVARIANT, typeArgument) {
        "Contravariant type arguments"
    } // tested
    areNotSupported(typeArgument.variance == Variance.COVARIANT, typeArgument) { "Covariant type arguments" } // tested
    areNotSupported(typeArgument.variance == Variance.STAR, typeArgument) { "Star projections" } // tested
}

/**
 * Validates parameters of methods and parameters of functional parameter types (e.g. 'block: (User, Access) -> Unit')
 */
internal fun validateValueParameter(valueParameter: KSValueParameter) {
//    areNotSupported(valueParameter.isVar, valueParameter) { "Parameters with 'var' modifier" } // constructors as ContextCreator
//    areNotSupported(valueParameter.isVal, valueParameter) { "Parameters with 'val' modifier" } // constructors as ContextCreator
    areNotSupported(valueParameter.isVararg, valueParameter) { "Parameters with 'vararg' modifier" } // tested
    areNotSupported(valueParameter.isCrossInline, valueParameter) { "Parameters with 'crossinline' modifier" } // tested
    areNotSupported(valueParameter.isNoInline, valueParameter) { "Parameters with 'noinline' modifier" } // tested
    areNotSupported(valueParameter.hasDefault, valueParameter) { "Parameters with default values" } // tested
}

internal fun validateType(type: KSType) {
    areNotSupported(type.isSuspendFunctionType) { "Suspend functional types" } // tested
}

internal fun validateCallableReference(callableReference: KSCallableReference) {
    // validation is provided by validation of function constituting parts: receiver, parameters, returned type
}

internal val KSClassDeclaration.isInner get() = Modifier.INNER in modifiers
