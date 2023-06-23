package io.kabu.frontend.ksp.processor.util.builder

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.kabu.annotations.ContextCreator
import io.kabu.backend.inout.input.method.ContextConstructorMethod.Companion.toContextConstructorMethod
import io.kabu.backend.inout.input.method.ContextCreatorMethod
import io.kabu.backend.inout.input.method.ContextCreatorMethod.Companion.toContextCreatorMethod
import io.kabu.frontend.ksp.processor.util.areNotSupported
import io.kabu.frontend.ksp.processor.util.builder.validator.ContextClassFoundByAnnotatedConstructorValidator
import io.kabu.frontend.ksp.processor.util.getAnnotation
import io.kabu.frontend.ksp.processor.util.toMethod
import io.kabu.frontend.ksp.processor.util.toTypeName

class ContextCreatorFunctionBuilder : AbstractFunctionBuilder<ContextCreatorMethod>() {

    override fun build(function: KSFunctionDeclaration): ContextCreatorMethod {
        val role = ContextCreator::class.simpleName!!
        validateFunction(function, role)

        val contextCreatorAnnotation = function.getAnnotation<ContextCreator>()
        val contextName = contextCreatorAnnotation.arguments
            .single { it.name?.asString() == ContextCreator::contextName.name }//todo error if 'name' is missing
            .value as String

        return if (function.isConstructor()) {
            val classDeclaration = function.parentDeclaration as KSClassDeclaration

            //todo don't validate here?
            // b/c Context class validation must be done after context class resolution by context name anyway
            validateContextClassFoundByAnnotatedConstructor(classDeclaration)

            val enclosingTypeName = classDeclaration
                .asStarProjectedType()  //todo generics
                .toTypeName()

            function.toMethod().toContextConstructorMethod(contextName, enclosingTypeName)

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
