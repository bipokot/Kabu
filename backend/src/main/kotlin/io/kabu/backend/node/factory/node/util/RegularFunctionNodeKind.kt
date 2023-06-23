package io.kabu.backend.node.factory.node.util

import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.PropertyNode

sealed class RegularFunctionNodeKind {

    object Default : RegularFunctionNodeKind()

    class HelperFunction(
        val name: String,
    ) : RegularFunctionNodeKind()

    class DispatcherFunction(
        val counterPropertyNode: PropertyNode,
        val helperFunctionsNodes: List<FunctionNode>,
    ) : RegularFunctionNodeKind()
}
