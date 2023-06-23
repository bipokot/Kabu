package io.kabu.backend.declaration.properties

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.declaration.util.PropertyAccessor
import io.kabu.backend.declaration.util.TerminalCallableBuilder
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.util.ifTake
import io.kabu.backend.util.poet.TypeNameUtils.requireClassName


class TerminalReadOnlyPropertyDeclaration(
    val name: String,
    val propertyType: TypeName,
    val funDeclarationProviders: FunDeclarationProviders,
    val analyzer: Analyzer,
) : AbstractPropertyDeclaration() {

    private val callableBuilder = TerminalCallableBuilder()

    override fun generatePropertySpec(): PropertySpec {
        val requiredReturnStatement = ifTake(analyzer.method.returnedType == UNIT) {
            "return ${propertyType.requireClassName.simpleName}()"
        }

        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        functionBlockContext.doEvaluation()

        val statements = callableBuilder
            .createTerminationStatements(analyzer, functionBlockContext, requiredReturnStatement)

        return generatePropertySpec(
            name = name,
            propertyType = propertyType,
            receiverType = funDeclarationProviders.receiverProvider?.type,
            getterAccessor = PropertyAccessor.Existing.Provided(statements)
        )
    }

    override fun toString(): String {
        return "TerminalReadOnlyProperty(name=$name)"
    }
}
