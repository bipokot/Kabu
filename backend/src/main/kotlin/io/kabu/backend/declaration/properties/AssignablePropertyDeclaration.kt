package io.kabu.backend.declaration.properties

import com.squareup.kotlinpoet.PropertySpec
import io.kabu.backend.declaration.util.PropertyAccessor
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.parser.Assign
import io.kabu.backend.provider.group.FunDeclarationProviders


class AssignablePropertyDeclaration(
    val name: String,
    val typeNode: TypeNode,
    val funDeclarationProviders: FunDeclarationProviders,
) : AbstractPropertyDeclaration() {

    override fun generatePropertySpec(): PropertySpec {
        val operator = Assign(Origin()) //todo get actual operator (for actual origin)
        val setterCodeBlock = getCallableStatements(operator, funDeclarationProviders, typeNode)

        return generatePropertySpec(
            name = name,
            propertyType = typeNode.typeName,
            receiverType = funDeclarationProviders.receiverProvider?.type,
            getterAccessor = PropertyAccessor.Missing,
            setterAccessor = PropertyAccessor.Existing.Provided(setterCodeBlock),
        )
    }

    override fun toString(): String {
        return "AssignableProperty(name=$name, providers=$funDeclarationProviders)"
    }
}
