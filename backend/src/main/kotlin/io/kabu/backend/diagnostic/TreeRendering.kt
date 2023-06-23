package io.kabu.backend.diagnostic

import io.kabu.backend.parser.BinaryExpression
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.parser.NaryExpression
import io.kabu.backend.parser.UnaryExpression
import org.barfuin.texttree.api.Node
import org.barfuin.texttree.api.TextTree
import org.barfuin.texttree.api.TreeOptions
import org.barfuin.texttree.api.color.NodeColor
import org.barfuin.texttree.api.style.TreeStyles


fun renderKotlinExpressionTree(expression: KotlinExpression): String {
    val treeOptions = TreeOptions().apply {
        setStyle(TreeStyles.UNICODE_ROUNDED)
        setEnableDefaultColoring(true)
    }
    return TextTree.newInstance(treeOptions).render(expression.toNode())
}


private fun KotlinExpression.toNode(): Node = object : Node {

    override fun getColor() = when (this@toNode) {
        is IdentifierLeaf -> NodeColor.LightBlue
        is LambdaExpression -> NodeColor.DarkGray
        is BinaryExpression -> null
        is NaryExpression -> NodeColor.DarkGray
        is UnaryExpression -> null
    }

    override fun getText() = when (this@toNode) {
        is IdentifierLeaf -> name
        is LambdaExpression -> "lambda"
        is BinaryExpression -> operator.symbol
        is NaryExpression -> operator.symbol
        is UnaryExpression -> operator.symbol
    }

    override fun getChildren() = when (this@toNode) {
        is IdentifierLeaf -> emptyList()
        is LambdaExpression -> expressions.map { it.toNode() }
        is BinaryExpression -> listOf(left, right).map { it.toNode() }
        is NaryExpression -> (listOf(operand) + arguments).map { it.toNode() }
        is UnaryExpression -> listOf(operand.toNode())
    }
}
