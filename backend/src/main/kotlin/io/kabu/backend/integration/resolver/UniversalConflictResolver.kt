package io.kabu.backend.integration.resolver

import io.kabu.backend.integration.Integrator
import io.kabu.backend.node.ContextMediatorTypeNode
import io.kabu.backend.node.DerivativeTypeNode
import io.kabu.backend.node.FixedTypeNode
import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.PropertyNode
import io.kabu.backend.node.TypeNode
import io.kabu.backend.node.WatcherContextTypeNode
import io.kabu.backend.node.factory.node.AccessorObjectTypeNode
import kotlin.reflect.KClass
import kotlin.reflect.full.isSubclassOf

typealias ResolvableNodeTypes = Pair<KClass<out Node>, KClass<out Node>>
typealias ConflictResolverCreator = (Integrator) -> ConflictResolver

class UniversalConflictResolver(private val integrator: Integrator): ConflictResolver {

    @Suppress("MaxLineLength")
    private val resolvers: Map<ResolvableNodeTypes, ConflictResolverCreator> = mapOf(
        Pair(PackageNode::class, PackageNode::class) to ::PackageAndPackageConflictResolver,
        Pair(TypeNode::class, PropertyNode::class) to ::TypeAndPropertyConflictResolver,
        Pair(FunctionNode::class, FunctionNode::class) to ::FunctionAndFunctionConflictResolver,

        // generated classes
        Pair(ContextMediatorTypeNode::class, ContextMediatorTypeNode::class) to ::GeneratedTypeAndGeneratedTypeConflictResolver,
        Pair(ContextMediatorTypeNode::class, HolderTypeNode::class) to ::GeneratedTypeAndGeneratedTypeConflictResolver,
        Pair(ContextMediatorTypeNode::class, WatcherContextTypeNode::class) to ::GeneratedTypeAndGeneratedTypeConflictResolver,
        Pair(HolderTypeNode::class, HolderTypeNode::class) to ::GeneratedTypeAndGeneratedTypeConflictResolver,
        Pair(HolderTypeNode::class, WatcherContextTypeNode::class) to ::GeneratedTypeAndGeneratedTypeConflictResolver,
        Pair(WatcherContextTypeNode::class, WatcherContextTypeNode::class) to ::GeneratedTypeAndGeneratedTypeConflictResolver,

        // other
        Pair(PropertyNode::class, PropertyNode::class) to ::PropertyAndPropertyConflictResolver,
        Pair(AccessorObjectTypeNode::class, AccessorObjectTypeNode::class) to ::AccessorObjectAndAccessorObjectConflictResolver,
        Pair(FixedTypeNode::class, FixedTypeNode::class) to ::FixedTypeAndFixedTypeConflictResolver,
        Pair(DerivativeTypeNode::class, DerivativeTypeNode::class) to ::DerivativeTypeAndDerivativeTypeConflictResolver,
    )

    override fun resolve(node1: Node, node2: Node) {
        val creator1 = firstMatchingConflictResolverCreator(node1, node2)
        if (creator1 != null) {
            creator1(integrator).resolve(node1, node2)
        } else {
            val creator2 = firstMatchingConflictResolverCreator(node2, node1)
            if (creator2 != null) {
                creator2(integrator).resolve(node2, node1)
            } else {
                error("Could not find a conflict resolver for '$node1' and '$node2'")
            }
        }
    }

    private fun firstMatchingConflictResolverCreator(node1: Node, node2: Node): ConflictResolverCreator? {
        return resolvers
            .filterKeys { node1::class.isSubclassOf(it.first) && node2::class.isSubclassOf(it.second) }
            .values
            .firstOrNull()
    }
}
