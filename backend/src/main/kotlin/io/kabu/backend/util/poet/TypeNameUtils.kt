package io.kabu.backend.util.poet

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
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

fun TypeName.gatherTypeVariableNames(): Collection<TypeVariableName> {

    fun Iterable<TypeName>.gatherTypeVariableNames(): Collection<TypeVariableName> {
        return flatMap { it.gatherTypeVariableNames() }
    }

    //todo RECURSIVE BOUNDS NOT HANDLED!!!
    return when (this) {
        is TypeVariableName -> bounds.gatherTypeVariableNames() + this
        is ParameterizedTypeName -> typeArguments.gatherTypeVariableNames()
        is WildcardTypeName -> inTypes.gatherTypeVariableNames() + outTypes.gatherTypeVariableNames()
        is LambdaTypeName -> {
            parameters.map { it.type }.gatherTypeVariableNames() +
                    returnType.gatherTypeVariableNames() +
                    receiver?.gatherTypeVariableNames().orEmpty()
        }

        is ClassName -> emptyList()
        Dynamic -> emptyList()
    }
}
