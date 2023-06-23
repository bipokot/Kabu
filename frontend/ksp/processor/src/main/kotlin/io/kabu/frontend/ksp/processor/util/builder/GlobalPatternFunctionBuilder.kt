package io.kabu.frontend.ksp.processor.util.builder

import com.google.devtools.ksp.isConstructor
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import io.kabu.annotations.GlobalPattern
import io.kabu.backend.inout.input.method.GlobalPatternMethod
import io.kabu.backend.inout.input.method.GlobalPatternMethod.Companion.toGlobalPatternMethod
import io.kabu.frontend.ksp.processor.util.areNotSupported
import io.kabu.frontend.ksp.processor.util.builder.validator.notSupportedFunctionKinds
import io.kabu.frontend.ksp.processor.util.getAnnotation
import io.kabu.frontend.ksp.processor.util.toMethod

class GlobalPatternFunctionBuilder : AbstractFunctionBuilder<GlobalPatternMethod>() {

    override fun build(function: KSFunctionDeclaration): GlobalPatternMethod {
        val role = GlobalPattern::class.simpleName!!
        validateFunction(function, role)

        val globalPatternAnnotation = function.getAnnotation<GlobalPattern>()
        val pattern = globalPatternAnnotation.arguments
            .single { it.name?.asString() == GlobalPattern::pattern.name }
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
