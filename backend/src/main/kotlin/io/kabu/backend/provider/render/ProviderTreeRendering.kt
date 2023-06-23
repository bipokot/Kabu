package io.kabu.backend.provider.render

import io.kabu.backend.provider.provider.ArgumentProvider
import io.kabu.backend.provider.provider.AuxProvider
import io.kabu.backend.provider.provider.EmptyProvider
import io.kabu.backend.provider.provider.ExtensionLambdaProvider
import io.kabu.backend.provider.provider.HolderProvider
import io.kabu.backend.provider.provider.LambdaProvider
import io.kabu.backend.provider.provider.NoReceiverProvider
import io.kabu.backend.provider.provider.Provider
import io.kabu.backend.provider.provider.ProviderContainer
import io.kabu.backend.provider.provider.ScopingLambdaProvider
import io.kabu.backend.provider.provider.WatcherLambdaProvider
import org.barfuin.texttree.api.Node
import org.barfuin.texttree.api.TextTree
import org.barfuin.texttree.api.TreeOptions
import org.barfuin.texttree.api.color.NodeColor
import org.barfuin.texttree.api.style.TreeStyles

fun renderProviderTree(providerContainer: ProviderContainer) =
    createTextTree().render(providerContainer.childrenProviders.toNode())

private fun createTextTree(): TextTree {
    val treeOptions = TreeOptions().apply {
        setStyle(TreeStyles.UNICODE_ROUNDED)
        setEnableDefaultColoring(true)
    }
    return TextTree.newInstance(treeOptions)
}

private fun List<Provider>.toNode(): Node = object : Node {
    override fun getText() = ""
    override fun getChildren() = this@toNode.map(Provider::toNode)
}

private fun Provider.toNode(): Node = object : Node {

    override fun getColor() = when (this@toNode) {
        is HolderProvider -> NodeColor.LightBlue
        is ArgumentProvider -> NodeColor.LightGreen
        else -> null
    }

    override fun getText() = when (val provider = this@toNode) {
        is ExtensionLambdaProvider -> "ExtensionLambda"
        is HolderProvider -> "Holder"
        is NoReceiverProvider -> "NoReceiver"
        is ScopingLambdaProvider -> "ScopingLambda"
        is WatcherLambdaProvider -> "WatcherLambda"
        is LambdaProvider -> "Lambda"
        is ArgumentProvider -> "Identifier (${provider.originalName})"
        is EmptyProvider -> "Empty"
        is AuxProvider -> "Aux"
        else -> TODO()
    }

    override fun getChildren(): List<Node> = this@toNode.childrenProviders.map(Provider::toNode)
}
