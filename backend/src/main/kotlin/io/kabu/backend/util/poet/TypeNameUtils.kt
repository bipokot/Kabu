package io.kabu.backend.util.poet

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.Dynamic
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterizedTypeName
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import com.squareup.kotlinpoet.WildcardTypeName
import io.kabu.backend.node.FixedTypeNode
import io.kabu.backend.util.poet.TypeNameUtils.requireClassName


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

    infix fun TypeName.isAssignableTo(other: TypeName): Boolean {
        return this == other // todo consider inheritance
    }

    private val packageNamesRegex = Regex("\\w+\\.")
}

fun TypeName.gatherTypeVariableNames(): MutableSet<TypeVariableName> {
    val set = mutableSetOf<TypeVariableName>()
    gatherTypeVariableNames(set)
    return set
}

fun TypeName.gatherTypeVariableNames(collector: MutableSet<TypeVariableName>) {

    fun Iterable<TypeName>.gatherTypeVariableNames(bag: MutableSet<TypeVariableName>) {
        forEach { it.gatherTypeVariableNames(bag) }
    }

    when (this) {
        is TypeVariableName -> {
            if (collector.none { it.name == this.name }) collector += this
            bounds.gatherTypeVariableNames(collector)
        }

        is ParameterizedTypeName -> typeArguments.gatherTypeVariableNames(collector)
        is WildcardTypeName -> {
            inTypes.gatherTypeVariableNames(collector)
            outTypes.gatherTypeVariableNames(collector)
        }

        is LambdaTypeName -> {
            parameters.map { it.type }.gatherTypeVariableNames(collector)
            returnType.gatherTypeVariableNames(collector)
            receiver?.gatherTypeVariableNames(collector)
        }

        is ClassName -> Unit
        Dynamic -> Unit
    }
}

fun TypeName.fullNameWithTypeArguments(): String {
    val rawClassCanonicalName = requireClassName.canonicalName

    val typeArguments = when(this) {
        is ParameterizedTypeName -> typeArguments.map { (it as TypeVariableName).name }
        is ClassName -> emptyList()
        else -> error("Unknown context mediator type: $this")
    }

    val typeArgumentsPart = if (typeArguments.isNotEmpty()) {
        typeArguments.joinToString(prefix = "<", postfix = ">", separator = ",")
    } else ""

    return rawClassCanonicalName + typeArgumentsPart
}
