package io.kabu.backend.declaration

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeSpec

abstract class AbstractTypeDeclaration : Declaration() {

    abstract val className: ClassName

    abstract fun generateTypeSpec(): TypeSpec
}
