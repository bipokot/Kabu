package io.kabu.backend.declaration.properties

import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.declaration.util.PropertyAccessor
import io.kabu.backend.declaration.util.TerminalCallableBuilder
import io.kabu.backend.provider.evaluation.FunctionBlockContext
import io.kabu.backend.provider.group.FunDeclarationProviders


class TerminalAssignablePropertyDeclaration(
    val name: String,
    val propertyType: TypeName,
    val funDeclarationProviders: FunDeclarationProviders,
    val analyzer: Analyzer,
) : AbstractPropertyDeclaration() {

    private val callableBuilder = TerminalCallableBuilder()

    override fun generatePropertySpec(): PropertySpec {
        val functionBlockContext = FunctionBlockContext(funDeclarationProviders)
        functionBlockContext.doEvaluation()

        val statements = callableBuilder
            .createTerminationStatements(analyzer, functionBlockContext, requiredReturnStatement = null)

        return generatePropertySpec(
            name = name,
            propertyType = propertyType,
            receiverType = funDeclarationProviders.receiverProvider?.type,
            setterAccessor = PropertyAccessor.Existing.Provided(statements)
        )
    }

    override fun toString(): String {
        return "TerminalAssignableProperty(name=$name, providers=$funDeclarationProviders)"
    }
}
