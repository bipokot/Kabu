@file:Suppress("MaxLineLength")
package io.kabu.backend.node

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.ParameterizedTypeName.Companion.parameterizedBy
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.TypeVariableName
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.declaration.Declaration
import io.kabu.backend.integration.NameAndType
import io.kabu.backend.legacy.TypeNameGeneratorFactory
import io.kabu.backend.node.namespace.ClassNamespaceNode
import io.kabu.backend.node.namespace.NamespaceNode
import io.kabu.backend.node.namespace.PackageNamespaceNode
import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.util.poet.gatherTypeVariableNames

/**
 * Integrated node
 *
 * @property namespaceNode namespace node which encloses the node
 * @property name short name associated with the node (class/function/property name etc.), may not be unique
 * @property dependencies dependencies of the node, must be composed dynamically of the node's properties
 * @property derivativeNodes nodes which depend on this node
 */
interface Node {
    var namespaceNode: NamespaceNode? //todo not all node types are enclosed into some namespace
    val name: String
    val dependencies: Iterable<Node> //todo make dependencies a set
    val derivativeNodes: MutableSet<Node>
    fun createDeclarations(): List<Declaration>
    fun replaceDependency(replaced: Node, replaceBy: Node)
}

interface AbstractNode : Node {

    // marks this node as 'derivative node' in its 'dependency node'-s
    //todo must be called in leaf-classes, consider refactoring
    fun updateLinks() {
        try {
            dependencies.forEach {
                it.derivativeNodes.add(this)
            }
        } catch (e: Exception) {
            logger.error("Links inconsistency in node '$this'", e)
        }
    }

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}

//==========

class PackageNode(
    override var name: String,
) : AbstractNode, PackageNamespaceNode {
    override val typeNameGenerator = TypeNameGeneratorFactory.create()
    override var accessorObjectNode: ObjectTypeNode? = null
    override var namespaceNode: NamespaceNode? = null
    override val dependencies: Iterable<Node> = emptyList()
    override val derivativeNodes = mutableSetOf<Node>()
    override fun createDeclarations(): List<Declaration> = emptyList()

    override fun replaceDependency(replaced: Node, replaceBy: Node) = Unit

    override fun toString() = "PackageNode($name)"

    init {
        updateLinks()
    }
}

// TypeNode ===============================================================

abstract class TypeNode : AbstractNode {
    override val derivativeNodes = mutableSetOf<Node>()
    abstract val typeName: TypeName
    override fun toString() = className()
}

// for given types (not generated ones: user types or Kotlin types)
class FixedTypeNode(
    override val typeName: TypeName, // unchanged
    override var namespaceNode: NamespaceNode?,
) : TypeNode() {
    override val name: String
        get() = (typeName as? ClassName)?.simpleName ?: typeName.toString()
    override val dependencies: Iterable<Node>
        get() = mutableListOf<Node>()
            .apply { namespaceNode?.let { add(it) } }

    override val derivativeNodes = mutableSetOf<Node>()
    override fun createDeclarations() = emptyList<Declaration>()
    override fun replaceDependency(replaced: Node, replaceBy: Node) {
        if (namespaceNode === replaced) namespaceNode = replaceBy as NamespaceNode
    }

    override fun toString(): String {
        val namespacePart = namespaceRecursiveName()?.let { "$it/" }.orEmpty()
        return "${className()}: $namespacePart$name"
    }

    init {
        updateLinks()
    }
}

class DerivativeTypeNode(
    override var namespaceNode: NamespaceNode?,
    private val generatorDependencies: MutableList<Node>,
    private val generator: (generatorDependencies: List<Node>) -> TypeName,
) : TypeNode() {
    override val name: String
        get() = (typeName as? ClassName)?.simpleName ?: typeName.toString()
    override val dependencies: Iterable<Node>
        get() = generatorDependencies.toMutableList()
            .apply { namespaceNode?.let { add(it) } }
            .filterNot { it is FixedTypeNode } //todo attention

    override val derivativeNodes = mutableSetOf<Node>()
    override val typeName: TypeName
        get() = generator(generatorDependencies)

    override fun createDeclarations() = emptyList<Declaration>()
    override fun replaceDependency(replaced: Node, replaceBy: Node) {
        for (i in generatorDependencies.indices) {
            if (generatorDependencies[i] === replaced) {
                generatorDependencies[i] = replaceBy
            }
        }
        if (namespaceNode === replaced) namespaceNode = replaceBy as NamespaceNode
    }

    override fun toString(): String {
        return "${className()}: ${namespaceRecursiveName()}/$name" //todo revise
    }

    init {
        updateLinks()
    }
}


abstract class GeneratedTypeNode(
    override var name: String, // mutable as it isn't defined or fixed by pattern
) : TypeNode() {
    override val derivativeNodes = mutableSetOf<Node>()
    open val desiredName: String? get() = null //todo optional user given name for that type
}

open class ObjectTypeNode(
    name: String,
    override var namespaceNode: NamespaceNode?, //todo consider non-nullable for majority of node types
) : GeneratedTypeNode(name) {
    override val derivativeNodes = mutableSetOf<Node>()
    override val dependencies: Iterable<Node>
        get() = listOfNotNull(namespaceNode)
    override fun replaceDependency(replaced: Node, replaceBy: Node) {
        if (namespaceNode === replaced) namespaceNode = replaceBy as NamespaceNode
    }

    override fun createDeclarations() = emptyList<Declaration>()

    val className: ClassName
        get() = namespaceNode!!.composeClassName(name)

    override val typeName: TypeName
        get() = className

    init {
        updateLinks()
    }
}

open class HolderTypeNode(
    name: String,
    val fieldTypes: MutableList<TypeNode>,
    override var namespaceNode: NamespaceNode?,
    override val desiredName: String? = null,
) : GeneratedTypeNode(name) {
    override fun createDeclarations() = emptyList<Declaration>()
    override fun replaceDependency(replaced: Node, replaceBy: Node) {
        for (i in fieldTypes.indices) {
            if (fieldTypes[i] === replaced) {
                fieldTypes[i] = replaceBy as TypeNode
            }
        }
        if (namespaceNode === replaced) namespaceNode = replaceBy as NamespaceNode
    }

    override val derivativeNodes = mutableSetOf<Node>()
    override val dependencies: Iterable<Node>
        get() = fieldTypes
            .filterNot { it is FixedTypeNode } //todo attention
            .mapTo(mutableListOf<Node>()) { it }
            .apply { namespaceNode?.let { add(it) } }

    val rawClassName: ClassName
        get() = namespaceNode!!.composeClassName(name)

    override val typeName: TypeName
        get() = composeTypeName()

    private fun composeTypeName(): TypeName {
        val typeVariableNames = gatherTypeVariableNames()
        return if (typeVariableNames.isEmpty()) rawClassName else {
            rawClassName.parameterizedBy(typeVariableNames)
        }
    }

    fun gatherTypeVariableNames(): List<TypeVariableName> {
        return fieldTypes.flatMap {
            it.typeName.gatherTypeVariableNames()
        }.toSet().toList()
    }

    init {
        updateLinks()
    }
}

open class WatcherContextTypeNode(
    name: String,
    override val desiredName: String? = null,
    override var namespaceNode: NamespaceNode?,
) : GeneratedTypeNode(name), ClassNamespaceNode {
    override val typeNameGenerator = TypeNameGeneratorFactory.create()
    override var accessorObjectNode: ObjectTypeNode? // delegating all work to enclosing namespaceNode
        get() = namespaceNode?.accessorObjectNode
        set(value) { namespaceNode?.accessorObjectNode = value }

    override fun createDeclarations() = emptyList<Declaration>()
    override fun replaceDependency(replaced: Node, replaceBy: Node) {
        if (namespaceNode === replaced) namespaceNode = replaceBy as NamespaceNode
    }

    override val derivativeNodes = mutableSetOf<Node>()

    override val dependencies: Iterable<Node>
        get() = mutableListOf<Node>()
            .apply { namespaceNode?.let { add(it) } }

    val className: ClassName
        get() = namespaceNode!!.composeClassName(name)

    //todo className vs typeName
    override val typeName: TypeName
        get() = className

    init {
        updateLinks()
    }
}

open class ContextMediatorTypeNode(
    name: String,
    val typeVariableNames: List<TypeVariableName>,
    override val desiredName: String? = null,
    override var namespaceNode: NamespaceNode?,
) : GeneratedTypeNode(name), ClassNamespaceNode {
    override val typeNameGenerator = TypeNameGeneratorFactory.create()
    override var accessorObjectNode: ObjectTypeNode? // delegating all work to enclosing namespaceNode
        get() = namespaceNode?.accessorObjectNode
        set(value) { namespaceNode?.accessorObjectNode = value }

    override fun createDeclarations() = emptyList<Declaration>()
    override fun replaceDependency(replaced: Node, replaceBy: Node) {
        if (namespaceNode === replaced) namespaceNode = replaceBy as NamespaceNode
    }

    override val derivativeNodes = mutableSetOf<Node>()

    override val dependencies: Iterable<Node>
        get() = mutableListOf<Node>()
            .apply { namespaceNode?.let { add(it) } }

    val rawClassName: ClassName
        get() = namespaceNode!!.composeClassName(name)

    override val typeName: TypeName
        get() = composeTypeName()

    private fun composeTypeName(): TypeName {
        return if (typeVariableNames.isEmpty()) rawClassName else {
            rawClassName.parameterizedBy(typeVariableNames)
        }
    }

    fun gatherTypeVariableNames(): List<TypeVariableName> {
        return typeVariableNames
    }

    init {
        updateLinks()
    }
}

// Functions / Properties ========================================================

interface FunctionNode : AbstractNode {
    val parameters: List<NameAndType>
    var returnTypeNode: TypeNode
    val isTerminal: Boolean

    override fun replaceDependency(replaced: Node, replaceBy: Node) {
        parameters.filter { it.typeNode === replaced }
            .forEach { it.typeNode = replaceBy as TypeNode }
        if (returnTypeNode === replaced) returnTypeNode = replaceBy as TypeNode
        if (namespaceNode === replaced) namespaceNode = replaceBy as NamespaceNode
    }

    override val dependencies: Iterable<Node>
        get() = (parameters.map { it.typeNode } + returnTypeNode).toMutableList<Node>()
            .apply { namespaceNode?.let { add(it) } }
            .filterNot { it is FixedTypeNode } //todo attention
}

abstract class BaseFunctionNode(
    protected val funDeclarationProviders: FunDeclarationProviders,
    override var namespaceNode: NamespaceNode?,
) : FunctionNode {

    override val parameters: List<NameAndType>
        get() = funDeclarationProviders.namedTypeNodes

    override val derivativeNodes = mutableSetOf<Node>()

    override fun toString() = "${className()}: fun ${namespaceRecursiveName()}/$name(): ${returnTypeNode.name}"
}

interface PropertyNode : AbstractNode {
    var receiverTypeNode: TypeNode?
    var returnTypeNode: TypeNode
    val isTerminal: Boolean //todo isTerminal is property of getter or setter, not the property itself

    override fun replaceDependency(replaced: Node, replaceBy: Node) {
        if (receiverTypeNode === replaced) receiverTypeNode = replaceBy as TypeNode
        if (returnTypeNode === replaced) returnTypeNode = replaceBy as TypeNode
        if (namespaceNode === replaced) namespaceNode = replaceBy as NamespaceNode
    }

    override val dependencies: Iterable<Node>
        get() = listOfNotNull(receiverTypeNode, returnTypeNode, namespaceNode)
            .filterNot { it is FixedTypeNode } //todo attention
}

open class BasePropertyNode(
    override val name: String,
    override var receiverTypeNode: TypeNode?,
    override var returnTypeNode: TypeNode,
    override var namespaceNode: NamespaceNode?,
    override val isTerminal: Boolean = false,
) : PropertyNode {
    override fun createDeclarations() = emptyList<Declaration>()

    override fun toString(): String {
        val receiverPart = receiverTypeNode?.name?.let { "$it." }.orEmpty()
        return "${className()}: val ${namespaceRecursiveName()}/$receiverPart$name: ${returnTypeNode.name}"
    }
    override val derivativeNodes = mutableSetOf<Node>()

    init {
        updateLinks()
    }
}
