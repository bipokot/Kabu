package io.kabu.frontend.ksp.processor.util

import com.google.devtools.ksp.symbol.KSCallableReference
import com.google.devtools.ksp.symbol.KSType
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSTypeReference
import com.google.devtools.ksp.symbol.KSValueParameter
import com.google.devtools.ksp.symbol.Variance

internal fun KSTypeReference.validate() {
    val type = resolve()
    validateType(type)

    (element as? KSCallableReference)?.let { callableReference ->
        validateCallableReference(callableReference)

        callableReference.receiverType?.validate()
        callableReference.functionParameters.forEach {
            validateValueParameter(it)
            it.type.validate()
        }
        callableReference.returnType.validate()
    }

    // assuming interface or class below
    type.validate()
}

internal fun KSType.validate() {
    validateType(this)
    arguments.forEach { typeArgument ->
        validateTypeArgument(typeArgument)
    }
}

internal fun validateTypeArgument(typeArgument: KSTypeArgument) {
//    areNotSupported(typeArgument.variance == Variance.CONTRAVARIANT, typeArgument) { // supported
//        "Contravariant type arguments"
//    } // tested
//    areNotSupported(typeArgument.variance == Variance.COVARIANT, typeArgument) { "Covariant type arguments" } // supported
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
