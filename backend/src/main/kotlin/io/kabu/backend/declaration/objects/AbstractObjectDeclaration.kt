package io.kabu.backend.declaration.objects

import com.squareup.kotlinpoet.ClassName
import io.kabu.backend.declaration.AbstractTypeDeclaration

abstract class AbstractObjectDeclaration(
    val name: String,
    override val className: ClassName
) : AbstractTypeDeclaration()
