package io.kabu.backend.util.poet

import com.fasterxml.jackson.annotation.JsonTypeInfo
import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.ParameterSpec
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName


@JsonTypeInfo(use = JsonTypeInfo.Id.NAME)
sealed class SerializedType {

    abstract fun toTypeName(): TypeName
}

data class RegularSerializedType(
    val packageName: String,
    val className: String,
    val typeParameters: List<SerializedTypeParameter>,
    val nullable: Boolean = false,
) : SerializedType() {

    override fun toTypeName(): TypeName {
        val className = ClassName(packageName, className)
        return if (typeParameters.isEmpty()) {
            if (nullable) className.copy(nullable = true) else className
        } else {
            className.parameterizedBy(typeParameters.map {
                //todo variance is ignored
                it.type.toTypeName()
            })
        }
    }
}

data class FunctionalSerializedType(
    val receiver: SerializedType?,
    val parameters: List<SerializedType>,
    val returns: SerializedType,
    val nullable: Boolean = false,
) : SerializedType() {

    override fun toTypeName(): TypeName {
        val parameters = parameters.map {
            ParameterSpec(name = "", type = it.toTypeName())
        }
        return LambdaTypeName.get(
            receiver = receiver?.toTypeName(),
            parameters = parameters,
            returnType = returns.toTypeName()
        )
    }
}

data class SerializedTypeParameter(
    val type: SerializedType,
    val variance: SerializedVariance,
)

enum class SerializedVariance {
    INVARIANT,
    IN,
    OUT,
    STAR
}
