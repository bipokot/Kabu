package io.kabu.backend.util.poet

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.node.FixedTypeNode


object TypeNameUtils {

    val TypeName.requireClassName: ClassName
        get() = (this as? ParameterizedTypeName)?.rawType
            ?: this as? ClassName
            ?: error("Unknown class name for type $this")

    fun TypeName.toFixedTypeNode(): FixedTypeNode = FixedTypeNode(
        typeName = this,
        namespaceNode = null,
    )

    val TypeName.shorten: String
        get() {
            val base = if (this is LambdaTypeName) "($this)" else "$this"
            return base.replace(packageNamesRegex, "")
        }

    private val packageNamesRegex = Regex("\\w+\\.")
}
