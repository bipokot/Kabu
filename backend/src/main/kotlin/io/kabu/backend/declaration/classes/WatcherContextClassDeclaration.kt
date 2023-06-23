package io.kabu.backend.declaration.classes

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeSpec
import io.kabu.runtime.WatcherContext
import io.kabu.backend.declaration.AbstractTypeDeclaration
import io.kabu.backend.declaration.Declaration
import io.kabu.backend.generator.addDeclarations

class WatcherContextClassDeclaration(
    override val className: ClassName,
) : AbstractTypeDeclaration() {
    
    val innerDeclarations: MutableList<Declaration> = mutableListOf()

    override fun generateTypeSpec(): TypeSpec = TypeSpec.classBuilder(className).apply {
        superclass(WatcherContext::class)
        addModifiers(KModifier.PUBLIC)

        addDeclarations(innerDeclarations)

    }.build()

    override fun toString() = "WatcherContext($className, $innerDeclarations)"
}
