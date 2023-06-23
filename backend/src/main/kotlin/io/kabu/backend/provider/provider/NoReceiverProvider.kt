package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.ANY
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode

/**
 *  Represents missing receiver (e.g. for package level property without receiver):
 *  - may be used as first provider of FunDeclarationParameters to indicate absence of function/property receiver
 *  - does not turn into 'this' by transformation to FunDeclarationParameters
 */
class NoReceiverProvider : BaseProvider(ANY.toFixedTypeNode())
