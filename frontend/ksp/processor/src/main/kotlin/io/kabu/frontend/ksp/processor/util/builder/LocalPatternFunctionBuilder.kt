package io.kabu.frontend.ksp.processor.util.builder

import com.google.devtools.ksp.closestClassDeclaration
import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import io.kabu.annotation.LocalPattern
import io.kabu.backend.inout.input.method.LocalPatternMethod
import io.kabu.backend.inout.input.method.LocalPatternMethod.Companion.toLocalPatternMethod
import io.kabu.frontend.ksp.diagnostic.builder.unexpectedLocalPatternError
import io.kabu.frontend.ksp.processor.util.areNotSupported
import io.kabu.frontend.ksp.processor.util.builder.validator.notSupportedFunctionKinds
import io.kabu.frontend.ksp.processor.util.getAnnotation
import io.kabu.frontend.ksp.processor.util.isInner
import io.kabu.frontend.ksp.processor.util.originOf
import io.kabu.frontend.ksp.processor.util.toMethod

class LocalPatternFunctionBuilder : AbstractFunctionBuilder<LocalPatternMethod>() {

    override fun build(function: KSFunctionDeclaration): LocalPatternMethod {
        val role = LocalPattern::class.simpleName!!
        validateFunction(function, role)

        val localPatternAnnotation = function.getAnnotation<LocalPattern>()
        val pattern = localPatternAnnotation.arguments
            .single { it.name?.asString() == LocalPattern::pattern.name }
            .value as String

        val classDeclaration = function.parentDeclaration as? KSClassDeclaration
            ?: unexpectedLocalPatternError(originOf(function), function.parentDeclaration?.let { originOf(it) })

        //todo set parent resolver from enclosing class?
        val typeParameterResolver = classDeclaration.typeParameters.toTypeParameterResolver()
        val declaringType = getTypeNameOfEnclosingType(classDeclaration, typeParameterResolver)

        return function.toMethod(typeParameterResolver).toLocalPatternMethod(declaringType, pattern)
    }

    override fun validateFunction(function: KSFunctionDeclaration, role: String) {
        super.validateFunction(function, role)

        notSupportedFunctionKinds(
            function,
            role,
            FunctionKind.TOP_LEVEL, // tested
        )
        areNotSupported(function.isConstructor(), function) { "Constructors as $role" } // tested

        areNotSupported(function.closestClassDeclaration()?.isInner ?: false, function) {  // tested
            "Inner class functions as $role"
        }
    }
}
