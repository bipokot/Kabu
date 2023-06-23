package io.kabu.backend.declaration.properties

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.PropertySpec
import io.kabu.backend.declaration.util.PropertyAccessor
import io.kabu.backend.node.TypeNode
import io.kabu.backend.provider.group.FunDeclarationProviders


class WrapperPropertyDeclaration(
    val name: String,
    val returnTypeNode: TypeNode,
    private val funDeclarationProviders: FunDeclarationProviders,
) : AbstractPropertyDeclaration() {

    override fun generatePropertySpec(): PropertySpec {
        val getterAccessor = getCallableStatements(funDeclarationProviders, returnTypeNode.typeName as ClassName)
        return generatePropertySpec(
            name = name,
            propertyType = returnTypeNode.typeName,
            receiverType = funDeclarationProviders.receiverProvider?.type,
            getterAccessor = PropertyAccessor.Existing.Provided(getterAccessor)
        )
    }

    override fun toString(): String {
        return "WrapperProperty(name=$name)"
    }
}
