package io.kabu.backend.declaration.functions

import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FunSpec
import com.squareup.kotlinpoet.KModifier
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.declaration.AbstractCallableDeclaration
import io.kabu.backend.provider.group.OrderedNamedProviders
import io.kabu.backend.util.poet.gatherTypeVariableNames


abstract class AbstractFunctionDeclaration : AbstractCallableDeclaration() {

    abstract fun generateFunSpec(): FunSpec

    @Suppress("LongParameterList")
    fun generateFunSpec(
        name: String,
        providers: OrderedNamedProviders,
        returnType: TypeName,
        receiverType: TypeName? = null,
        isInfix: Boolean = false,
        codeBlock: CodeBlock,
        isHelper: Boolean = false,
    ): FunSpec {
        return FunSpec.builder(name).apply {
            val typeVariableNames = returnType.gatherTypeVariableNames() +
                    receiverType?.gatherTypeVariableNames().orEmpty() +
                    providers.providers.flatMap { it.type.gatherTypeVariableNames() }
            addTypeVariables(typeVariableNames.toSet())

            if (isHelper) addModifiers(KModifier.PRIVATE)
            when {
                isInfix -> addModifiers(KModifier.INFIX)
                !isHelper -> addModifiers(KModifier.OPERATOR)
                else -> Unit
            }
            if (receiverType != null) receiver(receiverType)
            returns(returnType)

            providers.providers.forEach {
                addParameter(providers[it], it.type)
            }

            addCode(codeBlock)

        }.build()
    }
}
