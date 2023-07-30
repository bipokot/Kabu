package io.kabu.backend.declaration.classes

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeSpec
import io.kabu.backend.declaration.AbstractTypeDeclaration
import io.kabu.backend.declaration.Declaration
import io.kabu.backend.generator.addDeclarations
import io.kabu.backend.node.ContextMediatorTypeNode
import io.kabu.backend.parameter.Parameter


class ContextMediatorClassDeclaration(
    val typeNode: ContextMediatorTypeNode,
    val contextProperty: Parameter,
) : AbstractTypeDeclaration() {

    override val className: ClassName
        get() = typeNode.rawClassName

    // for adding declarations on generation code phase
    val innerDeclarations: MutableList<Declaration> = mutableListOf()

    override fun generateTypeSpec(): TypeSpec {
        val constructor = FunSpec.constructorBuilder().apply {
            contextProperty.let {
                addParameter(it.name, it.type)
            }
        }.build()

        return TypeSpec.classBuilder(typeNode.rawClassName).apply {
            addTypeVariables(typeNode.gatherTypeVariableNames())
            addModifiers(KModifier.PUBLIC)
            primaryConstructor(constructor).apply {
                contextProperty.let {
                    addProperty(PropertySpec.builder(it.name, it.type).initializer(it.name).build())
                }
            }

            addDeclarations(innerDeclarations)
        }.build()
    }

    override fun toString() = "ContextMediator($className, $contextProperty)"
}
