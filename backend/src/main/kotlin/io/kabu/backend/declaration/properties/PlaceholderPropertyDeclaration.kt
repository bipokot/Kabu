package io.kabu.backend.declaration.properties

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.PropertySpec
import io.kabu.backend.declaration.util.PropertyAccessor
import io.kabu.backend.node.TypeNode


class PlaceholderPropertyDeclaration(
    val name: String,
    val returnTypeNode: TypeNode,
) : AbstractPropertyDeclaration() {

    override fun generatePropertySpec(): PropertySpec {
        return generatePropertySpec(
            name = name,
            propertyType = returnTypeNode.typeName,
            receiverType = null,
            initializerBlock = CodeBlock.of("%T()", returnTypeNode.typeName as ClassName),
            getterAccessor = PropertyAccessor.Existing.Default
        )
    }

    override fun toString(): String {
        return "PlaceholderProperty(name=$name)"
    }
}
