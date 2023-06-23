package io.kabu.backend.analyzer.handler

import io.kabu.backend.parser.Access
import io.kabu.backend.parser.Assign
import io.kabu.backend.parser.BinaryExpression
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.parser.OperatorExpression

internal val KotlinExpression.isTerminal: Boolean
    get() = parent == null

internal val LambdaExpression.canBeLambdaWithReceiver: Boolean
    get() {
        val parentExpressionHasInvertedArgumentOrdering = (parent as? OperatorExpression)
            ?.operator?.invertedArgumentOrdering
            ?: false
        val lambdaIsTheFirstChild = indexInParent == 0

        return lambdaIsTheFirstChild == parentExpressionHasInvertedArgumentOrdering
    }

internal val IdentifierLeaf.isPropertyNameOfAccessExpression: Boolean
    get() {
        val isPartOfAccessExpression = (parent as? BinaryExpression)?.operator is Access
        val isRightExpressionOfParent = indexInParent != 0

        return isPartOfAccessExpression && isRightExpressionOfParent
    }

internal val KotlinExpression.isLeftSideOfAssign: Boolean
    get() {
        val parentOperatorIsAssign = (parent as? BinaryExpression)?.operator is Assign
        val thisExpressionIsLeftSide = indexInParent == 0

        return parentOperatorIsAssign && thisExpressionIsLeftSide
    }

internal val KotlinExpression.indexInParent get() = parent?.children?.indexOf(this)
