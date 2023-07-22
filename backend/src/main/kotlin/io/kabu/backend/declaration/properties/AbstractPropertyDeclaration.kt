package io.kabu.backend.declaration.properties

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.declaration.AbstractCallableDeclaration
import io.kabu.backend.declaration.util.PropertyAccessor


abstract class AbstractPropertyDeclaration : AbstractCallableDeclaration() {

    abstract fun generatePropertySpec(): PropertySpec

    @Suppress("LongParameterList")
    protected open fun generatePropertySpec(
        name: String,
        propertyType: TypeName,
        receiverType: TypeName?,
        initializerBlock: CodeBlock? = null,
        getterAccessor: PropertyAccessor = PropertyAccessor.Missing,
        setterAccessor: PropertyAccessor = PropertyAccessor.Missing,
        modifiers: List<KModifier>? = null,
    ): PropertySpec {
        return PropertySpec.builder(name, propertyType).apply {
            modifiers?.let { addModifiers(it) }
            if (receiverType != null) receiver(receiverType)
            if (initializerBlock != null) initializer(initializerBlock)

            generateGetter(getterAccessor)
            generateSetter(setterAccessor, propertyType)
        }.build()
    }

    private fun PropertySpec.Builder.generateSetter(
        setterAccessor: PropertyAccessor,
        propertyType: TypeName,
    ) {
        when (setterAccessor) {
            PropertyAccessor.Missing -> {
                // do nothing as we create a 'val' on the first hand
            }
            PropertyAccessor.Existing.Default -> {
                mutable(true) // making property a 'var'
            }
            is PropertyAccessor.Existing.Provided -> addSetterBlock(setterAccessor.codeBlock, propertyType)
        }
    }

    private fun PropertySpec.Builder.generateGetter(getterAccessor: PropertyAccessor) {
        when (getterAccessor) {
            PropertyAccessor.Missing -> forbidGetter()
            PropertyAccessor.Existing.Default -> {
                //do nothing as implicit getter is already there
            }
            is PropertyAccessor.Existing.Provided -> addGetterBlock(getterAccessor.codeBlock)
        }
    }

    private fun PropertySpec.Builder.addSetterBlock(block: CodeBlock, propertyType: TypeName) {
        mutable(true)
        setter(
            FunSpec.setterBuilder()
                .addParameter("value", propertyType)
                .addCode(block)
                .build()
        )
    }

    private fun PropertySpec.Builder.addGetterBlock(getterBlock: CodeBlock) {
        getter(
            FunSpec.getterBuilder()
                .addCode(getterBlock)
                .build()
        )
    }

    private fun PropertySpec.Builder.forbidGetter() {
        val errorMessage = "The property can be used in an assign operation only"
        getter(
            FunSpec.getterBuilder()
                .addCode(CodeBlock.of("throw %T(%S)", NotImplementedError::class, errorMessage))
                .addAnnotation(
                    AnnotationSpec.builder(Deprecated::class)
                        .addMember("\"$errorMessage\", level = DeprecationLevel.ERROR")
                        .build()
                )
                .build()
        )
    }
}
