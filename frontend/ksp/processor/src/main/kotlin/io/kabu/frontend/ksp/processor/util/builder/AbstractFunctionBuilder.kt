package io.kabu.frontend.ksp.processor.util.builder

import com.google.devtools.ksp.isLocal
import com.google.devtools.ksp.isPrivate
import com.google.devtools.ksp.isProtected
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.ksp.TypeParameterResolver
import com.squareup.kotlinpoet.ksp.toTypeVariableName
import io.kabu.backend.inout.input.method.Method
import io.kabu.backend.util.poet.TypeNameUtils.requireClassName
import io.kabu.frontend.ksp.processor.util.areNotSupported
import io.kabu.frontend.ksp.processor.util.asTypeName
import io.kabu.frontend.ksp.processor.util.builder.validator.notSupportedFunctionKinds
import io.kabu.frontend.ksp.processor.util.builder.validator.notSupportedFunctionModifiers
import io.kabu.frontend.ksp.processor.util.builder.validator.notSupportedFunctionVisibilities
import io.kabu.frontend.ksp.processor.util.validate


abstract class AbstractFunctionBuilder<out T: Method> {

    abstract fun build(function: KSFunctionDeclaration): T

    protected fun getTypeNameOfEnclosingType(
        classDeclaration: KSClassDeclaration,
        typeParameterResolver: TypeParameterResolver,
    ): TypeName {
        val declaringType = classDeclaration
            .asStarProjectedType()
            .also { it.validate() }
            .asTypeName()

        val typeVariableNames = classDeclaration.typeParameters.map { it.toTypeVariableName(typeParameterResolver) }

        return if (typeVariableNames.isNotEmpty()) {
            declaringType.requireClassName.parameterizedBy(typeVariableNames)
        } else {
            declaringType
        }
    }

    protected open fun validateFunction(function: KSFunctionDeclaration, role: String) {
        val isInObject = (function.parentDeclaration as? KSClassDeclaration)?.classKind == ClassKind.OBJECT
        areNotSupported(isInObject, function) { "Functions of objects as $role" } // tested

        notSupportedFunctionKinds(
            function,
            role,
            FunctionKind.STATIC,
            FunctionKind.ANONYMOUS, // tested
            FunctionKind.LAMBDA, // tested
        )
        //todo try to use isVisibleFrom
        notSupportedFunctionVisibilities(
            function,
            role,
            Visibility.LOCAL, // tested
            Visibility.PRIVATE, // tested
            Visibility.PROTECTED, // tested
        )
        areNotSupported(function.isLocal(), function) { "Local functions as $role" } // tested
        areNotSupported(function.isPrivate(), function) { "Private functions as $role" } // tested
        areNotSupported(function.isProtected(), function) { "Protected functions as $role" } // tested
        areNotSupported(function.isAbstract, function) { "Abstract functions as $role" } // tested
        areNotSupported(function.isActual, function) { "Functions with 'actual' keyword as $role" }
        areNotSupported(function.isExpect, function) { "Functions with 'expect' keyword as $role" }
        notSupportedFunctionModifiers(
            function,
            role,
            Modifier.INLINE, // tested
            Modifier.JAVA_NATIVE,
            Modifier.OPERATOR, // tested
            Modifier.ABSTRACT, // tested
            Modifier.ACTUAL,
            Modifier.EXPECT,
            Modifier.EXTERNAL,
            Modifier.PRIVATE, // tested
            Modifier.PROTECTED, // tested
            Modifier.SUSPEND, // tested
            Modifier.TAILREC, // tested
        )
    }
}
