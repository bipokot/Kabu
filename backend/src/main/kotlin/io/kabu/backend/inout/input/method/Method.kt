package io.kabu.backend.inout.input.method

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.diagnostic.HasOrigin
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.parameter.Parameter

open class Method(
    val packageName: String,
    val name: String,
    open val returnedType: TypeName,
    val receiver: Parameter?,
    val parameters: List<Parameter>,
    override val origin: Origin,
) : HasOrigin {

    val hasReceiver get() = receiver != null

    override fun toString() = "Method $name ($origin)"
}
