package io.kabu.backend.declaration.properties

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import io.kabu.backend.declaration.util.PropertyAccessor

class DispatcherCallsCounterPropertyDeclaration(
    val name: String,
) : AbstractPropertyDeclaration() {

    override fun generatePropertySpec(): PropertySpec {
        return generatePropertySpec(
            name = name,
            propertyType = INT,
            receiverType = null,
            getterAccessor = PropertyAccessor.Existing.Default,
            setterAccessor = PropertyAccessor.Existing.Default,
            initializerBlock = CodeBlock.of("0"),
            modifiers = listOf(KModifier.PRIVATE),
        )
    }

    override fun toString(): String {
        return "DispatcherCallsCounterProperty(name=$name)"
    }
}
