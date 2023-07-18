package io.kabu.backend.declaration.classes

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.PropertySpec
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeSpec
import io.kabu.backend.declaration.AbstractTypeDeclaration
import io.kabu.backend.integration.NameAndType
import io.kabu.backend.integration.NamedTypeNode
import io.kabu.backend.legacy.planner.HolderFieldsNamesGenerator.rename
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.ObjectTypeNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.namespace.NamespaceNode

class HolderClassDeclaration(
    val typeNode: HolderTypeNode,
    fieldTypes: List<TypeNode>,
    val parentTypeName: TypeName? = null,
    private val namespaceNode: NamespaceNode
) : AbstractTypeDeclaration() {

    override val className: ClassName
        get() = typeNode.className

    private val fields: List<NameAndType> = run {
        val nameAndTypes: List<NameAndType> = fieldTypes.map { NamedTypeNode("<unknown>", it) }

        rename(nameAndTypes)
    }

    private fun getAccessorObjectNode(): ObjectTypeNode? {
        //todo ugly and unsound method of accessor object retrieving
        return namespaceNode.getRoot().derivativeNodes.find { it is ObjectTypeNode } as ObjectTypeNode?
    }

    @Suppress("DEPRECATION")
    override fun generateTypeSpec(): TypeSpec {
        val accessorClassName = getAccessorObjectNode()?.className
        val fieldsHidingEnabled = accessorClassName != null

        val constructor = FunSpec.constructorBuilder().apply {
            fields.forEach {
                val name = propertyNameInRegardToFieldHiding(it.name, fieldsHidingEnabled)
                addParameter(name, it.typeNode.typeName)
            }
        }.build()

        return TypeSpec.classBuilder(typeNode.className).apply {
            addTypeVariables(typeNode.gatherTypeVariableNames())
            parentTypeName?.let { superclass(it) }
            addModifiers(KModifier.PUBLIC)
            primaryConstructor(constructor).apply {
                fields.forEach {
                    val adjustedName = propertyNameInRegardToFieldHiding(it.name, fieldsHidingEnabled)
                    addProperty(
                        PropertySpec.builder(adjustedName, it.typeNode.typeName).apply {
                            if (fieldsHidingEnabled) {
                                addModifiers(KModifier.PRIVATE)
                            } else {
                                addAnnotation(JvmField::class)
                            }
                            initializer(adjustedName)
                        }.build()
                    )
                }
            }

            if (accessorClassName != null) {
                fields.forEach {
                    addProperty(
                        PropertySpec.builder(it.name, it.typeNode.typeName).apply {
                            receiver(accessorClassName)
                            getter(FunSpec.getterBuilder().apply {
                                val adjustedName = propertyNameInRegardToFieldHiding(it.name, fieldsHidingEnabled)
                                addCode(CodeBlock.of("return $adjustedName"))
                            }.build())
                        }.build()
                    )
                }
            }
        }.build()
    }

    override fun toString() = "Type(${className.simpleName}, $fields)"

}

internal fun propertyNameInRegardToFieldHiding(propertyName: String, fieldsHidingEnabled: Boolean) =
    if (fieldsHidingEnabled) "_$propertyName" else propertyName
