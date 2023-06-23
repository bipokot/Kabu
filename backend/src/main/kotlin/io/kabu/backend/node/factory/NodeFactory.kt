package io.kabu.backend.node.factory

import io.kabu.backend.analyzer.Analyzer
import io.kabu.backend.node.ContextMediatorTypeNode
import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.ObjectTypeNode
import io.kabu.backend.node.PropertyNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.WatcherContextTypeNode
import io.kabu.backend.node.factory.node.AccessorObjectTypeNode
import io.kabu.backend.node.factory.node.AssignablePropertyNode
import io.kabu.backend.node.factory.node.ContextMediatorTypeNodeImpl
import io.kabu.backend.node.factory.node.HolderTypeNodeImpl
import io.kabu.backend.node.factory.node.PlaceholderPropertyNode
import io.kabu.backend.node.factory.node.RegularFunctionNode
import io.kabu.backend.node.factory.node.TerminalAssignablePropertyNode
import io.kabu.backend.node.factory.node.TerminalFunctionNode
import io.kabu.backend.node.factory.node.TerminalReadOnlyPropertyNode
import io.kabu.backend.node.factory.node.WatcherContextTypeNodeImpl
import io.kabu.backend.node.factory.node.WrapperPropertyNode
import io.kabu.backend.node.factory.node.util.RegularFunctionNodeKind
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.parameter.EntryParameter
import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.FunDeclarationProviders


@Suppress("MaxLineLength")
class NodeFactory {

    // PROPERTIES

    fun createPlaceholderPropertyNode(
        name: String,
        typeNode: HolderTypeNode,
        namespaceNode: NamespaceNode,
    ): PropertyNode = PlaceholderPropertyNode(name, typeNode, namespaceNode)

    fun createWrapperPropertyNode(
        name: String,
        typeNode: HolderTypeNode,
        namespaceNode: NamespaceNode,
        funDeclarationProviders: FunDeclarationProviders,
    ): PropertyNode = WrapperPropertyNode(name, typeNode, namespaceNode, funDeclarationProviders)

    fun createAssignablePropertyNode(
        name: String,
        typeNode: TypeNode,
        namespaceNode: NamespaceNode,
        funDeclarationProviders: FunDeclarationProviders,
    ): PropertyNode = AssignablePropertyNode(name, typeNode, namespaceNode, funDeclarationProviders)

    fun createTerminalReadOnlyPropertyNode(
        name: String,
        funDeclarationProviders: FunDeclarationProviders,
        typeNode: TypeNode,
        namespaceNode: NamespaceNode,
        analyzer: Analyzer,
    ): PropertyNode = TerminalReadOnlyPropertyNode(
        name,
        typeNode,
        namespaceNode,
        funDeclarationProviders,
        analyzer,
    )

    fun createTerminalAssignablePropertyNode(
        name: String,
        typeNode: TypeNode,
        funDeclarationProviders: FunDeclarationProviders,
        namespaceNode: NamespaceNode,
        analyzer: Analyzer,
    ): PropertyNode = TerminalAssignablePropertyNode(name, typeNode, namespaceNode, funDeclarationProviders, analyzer)

    // FUNCTIONS

    fun createFunctionNode(
        funDeclarationProviders: FunDeclarationProviders,
        operator: Operator,
        typeNode: TypeNode,
        namespaceNode: NamespaceNode,
        kind: RegularFunctionNodeKind = RegularFunctionNodeKind.Default,
    ): FunctionNode = RegularFunctionNode(funDeclarationProviders, namespaceNode, typeNode, operator, kind)

    fun createTerminalFunctionNode(
        funDeclarationProviders: FunDeclarationProviders,
        operator: Operator,
        analyzer: Analyzer,
        namespaceNode: NamespaceNode,
    ): FunctionNode = TerminalFunctionNode(funDeclarationProviders, namespaceNode, operator, analyzer)

    // TYPES

    fun createHolderTypeNode(
        name: String,
        fieldTypes: List<TypeNode>,
        namespaceNode: NamespaceNode,
    ): HolderTypeNode = HolderTypeNodeImpl(name, fieldTypes, namespaceNode)

    fun createWatcherContextTypeNode(
        name: String,
        namespaceNode: NamespaceNode,
    ): WatcherContextTypeNode = WatcherContextTypeNodeImpl(name, namespaceNode)

    fun createContextMediatorTypeNode(
        name: String,
        namespaceNode: NamespaceNode,
        contextProperty: EntryParameter,
    ): ContextMediatorTypeNode = ContextMediatorTypeNodeImpl(name, namespaceNode, contextProperty)

    fun createAccessorObjectTypeNode(
        name: String,
        namespaceNode: NamespaceNode,
    ): ObjectTypeNode = AccessorObjectTypeNode(name, namespaceNode)
}
