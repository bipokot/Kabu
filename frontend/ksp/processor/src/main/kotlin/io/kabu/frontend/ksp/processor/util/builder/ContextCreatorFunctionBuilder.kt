package io.kabu.frontend.ksp.processor.util.builder

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.squareup.kotlinpoet.ksp.toTypeParameterResolver
import io.kabu.annotation.Context
import io.kabu.annotation.ContextCreator
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.inout.input.method.ContextConstructorMethod.Companion.toContextConstructorMethod
import io.kabu.backend.inout.input.method.ContextCreatorMethod
import io.kabu.backend.inout.input.method.ContextCreatorMethod.Companion.toContextCreatorMethod
import io.kabu.frontend.ksp.processor.util.areNotSupported
import io.kabu.frontend.ksp.processor.util.builder.validator.ContextClassFoundByAnnotatedConstructorValidator
import io.kabu.frontend.ksp.processor.util.getAnnotation
import io.kabu.frontend.ksp.processor.util.getAnnotationOrNull
import io.kabu.frontend.ksp.processor.util.originOf
import io.kabu.frontend.ksp.processor.util.toMethod

class ContextCreatorFunctionBuilder : AbstractFunctionBuilder<ContextCreatorMethod>() {

    fun build(classDeclaration: KSClassDeclaration): ContextCreatorMethod {
        val primaryConstructor = classDeclaration.primaryConstructor
            ?: diagnosticError("Type '$classDeclaration' does not have primary constructor", originOf(classDeclaration))

        if (primaryConstructor.getAnnotationOrNull<ContextCreator>() != null) {
            diagnosticError(
                "Primary constructor of @${Context::class.java.simpleName}-annotated type '$classDeclaration' " +
                        "can not have @${ContextCreator::class.java.simpleName} annotation", originOf(classDeclaration)
            )
        }

        val contextAnnotation = classDeclaration.getAnnotation<Context>()
        val contextName = contextAnnotation.arguments
            .single { it.name?.asString() == Context::contextName.name } //todo error if 'name' is missing
            .value as String

        return buildContextCreatorMethod(primaryConstructor, contextName)
    }

    override fun build(function: KSFunctionDeclaration): ContextCreatorMethod {
        val contextCreatorAnnotation = function.getAnnotation<ContextCreator>()
        val contextName = contextCreatorAnnotation.arguments
            .single { it.name?.asString() == ContextCreator::contextName.name } //todo error if 'name' is missing
            .value as String

        return buildContextCreatorMethod(function, contextName)
    }

    private fun buildContextCreatorMethod(
        function: KSFunctionDeclaration,
        contextName: String,
    ): ContextCreatorMethod {
        val role = ContextCreator::class.simpleName!!
        validateFunction(function, role)

        return if (function.isConstructor()) {
            val classDeclaration = function.parentDeclaration as KSClassDeclaration

            //todo don't validate here?
            // b/c Context class validation must be done after context class resolution by context name anyway
            validateContextClassFoundByAnnotatedConstructor(classDeclaration)

            //todo set parent resolver from enclosing class?
            val typeParameterResolver = classDeclaration.typeParameters.toTypeParameterResolver()
            val declaringType = getTypeNameOfEnclosingType(classDeclaration, typeParameterResolver)

            function.toMethod(typeParameterResolver).toContextConstructorMethod(contextName, declaringType)

        } else {
            function.toMethod().toContextCreatorMethod(contextName)
        }
    }

    override fun validateFunction(function: KSFunctionDeclaration, role: String) {
        super.validateFunction(function, role)

        val isRegularMember = function.functionKind == FunctionKind.MEMBER && !function.isConstructor()
        areNotSupported(isRegularMember, function) { "Regular member functions as $role" } // tested
    }

    private fun validateContextClassFoundByAnnotatedConstructor(classDeclaration: KSClassDeclaration) {
        ContextClassFoundByAnnotatedConstructorValidator().validate(classDeclaration, EXTENSION_CONTEXT_CLASS_ROLE)
    }

    private companion object {
        const val EXTENSION_CONTEXT_CLASS_ROLE = "Context"
    }
}
