package io.kabu.backend.provider.provider

import com.squareup.kotlinpoet.ANY
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.node.TypeNode
import io.kabu.backend.util.decaps
import io.kabu.backend.util.poet.TypeNameUtils.toFixedTypeNode


class EmptyProvider(
    typeNode: TypeNode,
    origin: Origin? = null,
) : BaseProvider(typeNode, origin) {

    constructor() : this(ANY.toFixedTypeNode())

    override fun getProviderName(): String {
        return typeNode.name.decaps()
    }

//    override fun getEvaluationRequirement(): Provider.EvaluationRequirement =
//        Provider.EvaluationRequirement.MANDATORY //todo rethink to evaluate "empty lambdas" and not pass them
}
