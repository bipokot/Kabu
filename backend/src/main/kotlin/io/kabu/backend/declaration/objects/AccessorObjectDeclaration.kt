package io.kabu.backend.declaration.objects

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec

class AccessorObjectDeclaration(
    name: String,
    className: ClassName,
) : AbstractObjectDeclaration(
    name = name,
    className = className
) {

    override fun generateTypeSpec(): TypeSpec {
        return TypeSpec.objectBuilder(className).apply {
            addKdoc(MSG_KDOC)
            addAnnotation(
                AnnotationSpec.builder(Deprecated::class)
                    .addMember(MSG_SHOULD_NOT_BE_USED)
                    .build()
            )
        }.build()
    }

    override fun toString() = "Object($name, $className)"

    private companion object {
        const val MSG_KDOC = "Used to access properties that should be hidden from library user"
        const val MSG_SHOULD_NOT_BE_USED = "\"This class is library internal class " +
            "and should be used in generated code only\", level = DeprecationLevel.WARNING"
    }
}
