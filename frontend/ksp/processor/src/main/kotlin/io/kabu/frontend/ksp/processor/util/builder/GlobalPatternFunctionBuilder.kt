package io.kabu.frontend.ksp.processor.util.builder

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.kabu.annotations.Pattern
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.inout.input.method.GlobalPatternMethod.Companion.toGlobalPatternMethod
import io.kabu.frontend.ksp.processor.util.areNotSupported
import io.kabu.frontend.ksp.processor.util.builder.validator.notSupportedFunctionKinds
import io.kabu.frontend.ksp.processor.util.getAnnotation
import io.kabu.frontend.ksp.processor.util.toMethod

class GlobalPatternFunctionBuilder : AbstractFunctionBuilder<GlobalPatternMethod>() {

    override fun build(function: KSFunctionDeclaration): GlobalPatternMethod {
        val role = Pattern::class.simpleName!!
        validateFunction(function, role)

        val patternAnnotation = function.getAnnotation<Pattern>()
        val pattern = patternAnnotation.arguments
            .single { it.name?.asString() == Pattern::pattern.name }
            .value as String

        return function.toMethod().toGlobalPatternMethod(pattern)
    }

    override fun validateFunction(function: KSFunctionDeclaration, role: String) {
        super.validateFunction(function, role)

        areNotSupported(function.isConstructor(), function) { "Constructors as $role" }  // tested

        notSupportedFunctionKinds(
            function,
            role,
            FunctionKind.MEMBER, // tested
        )
    }
}
