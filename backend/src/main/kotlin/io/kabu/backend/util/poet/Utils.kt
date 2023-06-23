package io.kabu.backend.util.poet

import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import io.kabu.backend.parser.FunctionMustReturn


fun String.asCodeBlock(vararg args: Any?) = CodeBlock.of(this, *args)

//todo doc
internal fun FunctionMustReturn.asFixedTypeName(): TypeName = when (this) {
    FunctionMustReturn.BOOLEAN -> BOOLEAN
    FunctionMustReturn.INT -> INT
    FunctionMustReturn.UNIT -> UNIT
    FunctionMustReturn.FREE, FunctionMustReturn.ASSIGNABLE -> error("There is no fixed TypeName for $this")
}
