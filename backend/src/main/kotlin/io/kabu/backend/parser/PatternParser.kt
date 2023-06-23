package io.kabu.backend.parser

import io.kabu.backend.antlr.grammar.MGrammar
import io.kabu.backend.antlr.grammar.MGrammar.ADD
import io.kabu.backend.antlr.grammar.MGrammar.ADD_ASSIGNMENT
import io.kabu.backend.antlr.grammar.MGrammar.ASSIGNMENT
import io.kabu.backend.antlr.grammar.MGrammar.AdditiveExpressionContext
import io.kabu.backend.antlr.grammar.MGrammar.AnnotatedLambdaContext
import io.kabu.backend.antlr.grammar.MGrammar.AnnotationContext
import io.kabu.backend.antlr.grammar.MGrammar.AssignmentContext
import io.kabu.backend.antlr.grammar.MGrammar.CallSuffixContext
import io.kabu.backend.antlr.grammar.MGrammar.ComparisonContext
import io.kabu.backend.antlr.grammar.MGrammar.DECR
import io.kabu.backend.antlr.grammar.MGrammar.DIV
import io.kabu.backend.antlr.grammar.MGrammar.DIV_ASSIGNMENT
import io.kabu.backend.antlr.grammar.MGrammar.DirectlyAssignableExpressionContext
import io.kabu.backend.antlr.grammar.MGrammar.EQEQ
import io.kabu.backend.antlr.grammar.MGrammar.EXCL_EQ
import io.kabu.backend.antlr.grammar.MGrammar.EqualityContext
import io.kabu.backend.antlr.grammar.MGrammar.ExclContext
import io.kabu.backend.antlr.grammar.MGrammar.GE
import io.kabu.backend.antlr.grammar.MGrammar.IN
import io.kabu.backend.antlr.grammar.MGrammar.INCR
import io.kabu.backend.antlr.grammar.MGrammar.IndexingSuffixContext
import io.kabu.backend.antlr.grammar.MGrammar.InfixFunctionCallContext
import io.kabu.backend.antlr.grammar.MGrammar.InfixOperationContext
import io.kabu.backend.antlr.grammar.MGrammar.LANGLE
import io.kabu.backend.antlr.grammar.MGrammar.LE
import io.kabu.backend.antlr.grammar.MGrammar.LambdaLiteralContext
import io.kabu.backend.antlr.grammar.MGrammar.MOD
import io.kabu.backend.antlr.grammar.MGrammar.MOD_ASSIGNMENT
import io.kabu.backend.antlr.grammar.MGrammar.MULT
import io.kabu.backend.antlr.grammar.MGrammar.MULT_ASSIGNMENT
import io.kabu.backend.antlr.grammar.MGrammar.MultiplicativeExpressionContext
import io.kabu.backend.antlr.grammar.MGrammar.NOT_IN
import io.kabu.backend.antlr.grammar.MGrammar.NavigationSuffixContext
import io.kabu.backend.antlr.grammar.MGrammar.ParenthesizedExpressionContext
import io.kabu.backend.antlr.grammar.MGrammar.PatternContext
import io.kabu.backend.antlr.grammar.MGrammar.PostfixUnaryExpressionContext
import io.kabu.backend.antlr.grammar.MGrammar.PrefixUnaryExpressionContext
import io.kabu.backend.antlr.grammar.MGrammar.RANGE
import io.kabu.backend.antlr.grammar.MGrammar.RANGE_UNTIL
import io.kabu.backend.antlr.grammar.MGrammar.RANGLE
import io.kabu.backend.antlr.grammar.MGrammar.RangeExpressionContext
import io.kabu.backend.antlr.grammar.MGrammar.SUB
import io.kabu.backend.antlr.grammar.MGrammar.SUB_ASSIGNMENT
import io.kabu.backend.antlr.grammar.MGrammar.SimpleIdentifierContext
import io.kabu.backend.antlr.grammar.MGrammar.StatementsContext
import io.kabu.backend.antlr.grammar.MGrammar.UnescapedAnnotationContext
import io.kabu.backend.antlr.grammar.MGrammar.UserTypeContext
import io.kabu.backend.antlr.grammar.MLexer
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.diagnostic.InlineSourceLocation
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.exception.PatternParsingException
import io.kabu.backend.parser.PatternParser.PostfixUnarySuffixAnalysis.AccessOperatorPostfix
import io.kabu.backend.parser.PatternParser.PostfixUnarySuffixAnalysis.CallPostfix
import io.kabu.backend.parser.PatternParser.PostfixUnarySuffixAnalysis.IndexingPostfix
import io.kabu.backend.parser.PatternParser.PostfixUnarySuffixAnalysis.UnaryOperatorPostfix
import org.antlr.v4.runtime.BaseErrorListener
import org.antlr.v4.runtime.CharStreams
import org.antlr.v4.runtime.CommonTokenStream
import org.antlr.v4.runtime.ParserRuleContext
import org.antlr.v4.runtime.RecognitionException
import org.antlr.v4.runtime.Recognizer
import org.antlr.v4.runtime.tree.ParseTree
import org.antlr.v4.runtime.tree.TerminalNode

@Suppress("TooManyFunctions")
class PatternParser(
    private val patternString: PatternString
) {

    fun parse(): Result<KotlinExpression> {
        logger.debug { "Parsing: $patternString" }
        val lexer = MLexer(CharStreams.fromString(patternString.pattern))
        val tokens = CommonTokenStream(lexer)
        val grammar = MGrammar(tokens)

        return try {
            grammar.addErrorListener(ErrorListener())
            val tree = grammar.pattern()
            Result.success(visit(tree))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun visit(tree: ParseTree): KotlinExpression {
        if (tree.childCount == 1) {
            return visit(tree.getChild(0))
        }
        return when (tree) {
            is PatternContext -> {
                visit(tree.statement())
            }
            is PostfixUnaryExpressionContext -> {
                getTreeOfPostfixOperationList(tree, { primaryExpression() }, { postfixUnarySuffix(it) })
            }
            is PrefixUnaryExpressionContext -> {
                getTreeOfPrefixOperationList(tree, { postfixUnaryExpression() }, { unaryPrefix(it) })
            }
            is MultiplicativeExpressionContext -> {
                getLeftAssociativeTreeOfOperationList(tree, { asExpression(it) }, { multiplicativeOperator(it) })
            }
            is AdditiveExpressionContext -> {
                getLeftAssociativeTreeOfOperationList(tree,
                    { multiplicativeExpression(it) }, { additiveOperator(it) })
            }
            is RangeExpressionContext -> {
                getLeftAssociativeTreeOfOperationList(tree,
                    { additiveExpression(it) }, { children[it * 2 + 1] })
            }
            is InfixFunctionCallContext -> { // infix
                getLeftAssociativeTreeOfOperationList(tree, { rangeExpression(it) }, { simpleIdentifier(it) })
            }
            is InfixOperationContext -> { // in/!in
                getLeftAssociativeTreeOfOperationList(tree, { elvisExpression(it) }, { inOperator(it) })
            }
            is ComparisonContext -> {
                getLeftAssociativeTreeOfOperationList(tree,
                    { genericCallLikeComparison(it) }, { comparisonOperator(it) })
            }
            is EqualityContext -> {
                getLeftAssociativeTreeOfOperationList(tree, { comparison(it) }, { equalityOperator(it) })
            }
            is AssignmentContext -> {
                require(tree.childCount == 3) //todo can be many of them
                val operator = visitBinaryOperator(tree.getChild(1))
                val left = visit(tree.assignableExpression() ?: tree.directlyAssignableExpression())
                val right = visit(tree.expression())
                BinaryExpression(operator, left, right, originOf(tree))
            }
            is DirectlyAssignableExpressionContext -> {
                getTreeOfPostfixOperationList(tree, { postfixUnaryExpression() }, { assignableSuffix() })
            }
            is AnnotatedLambdaContext -> {
                val annotations = gatherAnnotations(tree.annotation())
                val lambdaLiteral = tree.lambdaLiteral()
                val expressions = visitStatements(lambdaLiteral.statements())
                LambdaExpression(expressions, annotations, originOf(tree))
            }
            is LambdaLiteralContext -> {
                val expressions = visitStatements(tree.statements())
                LambdaExpression(expressions, emptyList(), originOf(tree))
            }
            is ParenthesizedExpressionContext -> {
                visit(tree.expression())
            }
            else -> {
                if (tree.childCount == 0)
                    visitTerminal(tree)
                else unknownContextError(tree)
            }
        }
    }

    private fun gatherAnnotations(annotations: List<AnnotationContext>): List<Annotation> {

        fun identifierTextOf(userTypeContext: UserTypeContext): String =
            userTypeContext.simpleUserType().simpleIdentifier().Identifier().text


        return annotations.flatMap {
            buildList<UnescapedAnnotationContext> {
                it.singleAnnotation()?.unescapedAnnotation()?.let { add(it) }
                it.multiAnnotation()?.unescapedAnnotation()?.let { addAll(it) }
            }
        }.map {
            val userType = it.userType()
            val constructorInvocation = it.constructorInvocation()
            when {
                userType != null -> {
                    val name = identifierTextOf(userType)
                    Annotation(name, listOf())
                }
                constructorInvocation != null -> {
                    val name = identifierTextOf(constructorInvocation.userType())
                    val arguments = constructorInvocation.valueArguments().valueArgument().map { argument ->
                        val argumentName = argument.simpleIdentifier()?.let {
                            it.Identifier()?.text
                                ?: error("Annotation argument '${it.text}' is not an identifier")
                        }
                        val value = visit(argument.expression())
                        Annotation.Argument(name = argumentName, expression = value)
                    }
                    Annotation(name, arguments)
                }
                else -> error("Annotation has invalid format: ${it.text}")
            }
        }
    }


    private fun <T : ParseTree> getLeftAssociativeTreeOfOperationList(
        tree: T,
        expressionConstructor: T.(Int) -> ParseTree,
        operatorConstructor: T.(Int) -> ParseTree,
    ): BinaryExpression {
        // compute the initial expression

        // left part
        val initialLeftTree = tree.expressionConstructor(0)
        val initialLeftExpr = visit(initialLeftTree)

        // right part
        val initialRightTree = tree.expressionConstructor(1)
        val initialRightExpr = visit(initialRightTree)

        // operator
        val initialOperator = visitBinaryOperator(tree.operatorConstructor(0))

        // expression
        val expressionOrigin = createOrigin(initialLeftExpr.requireStartIndex, initialRightExpr.requireEndIndex)
        var expression = BinaryExpression(initialOperator, initialLeftExpr, initialRightExpr, expressionOrigin)

        // construct expression tree from the list
        require((tree.childCount - 1) % 2 == 0) // require exact calculation
        val operationsCount = (tree.childCount - 1) / 2
        for (i in 1 until operationsCount) {
            val operator = visitBinaryOperator(tree.operatorConstructor(i))

            val rightTree = tree.expressionConstructor(i + 1)
            val right = visit(rightTree)
            val origin = createOrigin(initialLeftExpr.requireStartIndex, right.requireEndIndex)
            expression = BinaryExpression(operator, expression, right, origin)
        }
        return expression
    }

    private fun <T : ParseTree> getTreeOfPostfixOperationList(
        tree: T,
        baseExpressionAccessor: T.() -> ParseTree,
        operatorAccessor: T.(Int) -> ParseTree,
    ): KotlinExpression {
        // compute the initial expression
        var expression = visit(tree.baseExpressionAccessor())
        val startIndex = expression.requireStartIndex

        // construct expression tree from the list
        val operationsCount = tree.childCount - 1
        for (i in 0 until operationsCount) {
            val analysis = postfixUnarySuffixAnalysis(tree.operatorAccessor(i))
            expression = when (analysis) {
                is CallPostfix -> {
                    val origin = createOrigin(startIndex, analysis.operator.origin.sourceLocation?.endIndex!!)
                    NaryExpression(analysis.operator, expression, analysis.arguments, origin)
                }
                is UnaryOperatorPostfix -> {
                    val origin = createOrigin(startIndex, analysis.operator.origin.sourceLocation?.endIndex!!)
                    UnaryExpression(analysis.operator, expression, origin)
                }
                is IndexingPostfix -> {
                    val origin = createOrigin(startIndex, analysis.operator.origin.sourceLocation?.endIndex!!)
                    NaryExpression(analysis.operator, expression, analysis.indices, origin)
                }
                is AccessOperatorPostfix -> {
                    val origin = createOrigin(startIndex, analysis.argument.requireEndIndex)
                    BinaryExpression(analysis.operator, expression, analysis.argument, origin)
                }
            }
        }
        return expression
    }

    private fun <T : ParseTree> getTreeOfPrefixOperationList(
        tree: T,
        baseExpressionAccessor: T.() -> ParseTree,
        operatorAccessor: T.(Int) -> ParseTree,
    ): KotlinExpression {
        // compute the initial expression
        var expression = visit(tree.baseExpressionAccessor())
        val endIndex = expression.requireEndIndex

        // construct expression tree from the list
        val operationsCount = tree.childCount - 1
        for (i in 0 until operationsCount) {
            val operator = visitPrefixOperator(tree.operatorAccessor(operationsCount - i - 1))
            val origin = createOrigin(operator.origin.sourceLocation?.startIndex!!, endIndex)
            expression = UnaryExpression(operator, expression, origin)
        }
        return expression
    }

    private fun visitBinaryOperator(context: ParseTree): BinaryOperator {
        return when (context) {
            is SimpleIdentifierContext -> InfixFunction(context.text, originOf(context))
            is TerminalNode -> {
                val origin = originOf(context)
                when (context.symbol.type) {
                    ADD -> Additive.BinaryPlus(origin)
                    SUB -> Additive.BinaryMinus(origin)
                    MULT -> Multiplicative.Multiply(origin)
                    DIV -> Multiplicative.Divide(origin)
                    MOD -> Multiplicative.Remainder(origin)
                    RANGE -> RangeTo(origin)
                    RANGE_UNTIL -> RangeUntil(origin)
                    IN -> InclusionCheck.In(origin)
                    NOT_IN -> {
                        // trailing space fixing
                        InclusionCheck.NotIn(createOrigin(context.symbol.startIndex, context.symbol.stopIndex - 1))
                    }
                    LANGLE -> Comparison.Less(origin)
                    RANGLE -> Comparison.Greater(origin)
                    LE -> Comparison.LessOrEqual(origin)
                    GE -> Comparison.GreaterOrEqual(origin)
                    EQEQ -> Equality.EqualTo(origin)
                    EXCL_EQ -> Equality.NotEqualTo(origin)
                    ADD_ASSIGNMENT -> ModAssign.PlusAssign(origin)
                    SUB_ASSIGNMENT -> ModAssign.MinusAssign(origin)
                    MULT_ASSIGNMENT -> ModAssign.MultiplyAssign(origin)
                    DIV_ASSIGNMENT -> ModAssign.DivideAssign(origin)
                    MOD_ASSIGNMENT -> ModAssign.RemainderAssign(origin)
                    ASSIGNMENT -> Assign(origin)
                    else -> unknownContextError(context)
                }
            }
            else -> visitBinaryOperator(context.getChild(0))
        }
    }

    private fun visitPrefixOperator(context: ParseTree): UnaryOperator {
        return when (context) {
            is ExclContext -> UnaryPrefix.Not(originOf(context))
            is TerminalNode -> {
                val origin = originOf(context)
                when (context.symbol.type) {
                    INCR -> UnaryPrefix.PlusPlusPrefix(origin)
                    DECR -> UnaryPrefix.MinusMinusPrefix(origin)
                    ADD -> UnaryPrefix.UnaryPlus(origin)
                    SUB -> UnaryPrefix.UnaryMinus(origin)
                    else -> unknownContextError(context)
                }
            }
            else -> visitPrefixOperator(context.getChild(0))
        }
    }

    private fun postfixUnarySuffixAnalysis(context: ParseTree): PostfixUnarySuffixAnalysis {
        return when (context) {
            is CallSuffixContext -> {
                val operatorOrigin = originOf(context)
                val arguments = retrieveCallArguments(context)
                CallPostfix(Call(operatorOrigin), arguments)
            }
            is IndexingSuffixContext -> {
                val operatorOrigin = originOf(context)
                val indices = context.expression().map { visit(it) }
                IndexingPostfix(Indexing(operatorOrigin), indices)
            }
            is NavigationSuffixContext -> {
                val operatorOrigin = originOf(context.memberAccessOperator())
                val name = context.simpleIdentifier().Identifier().text
                val identifierLeaf = IdentifierLeaf(name, originOf(context.simpleIdentifier()))
                AccessOperatorPostfix(Access(operatorOrigin), identifierLeaf)
            }
            is TerminalNode -> {
                val origin = originOf(context)
                when (context.symbol.type) {
                    INCR -> UnaryOperatorPostfix(UnaryPostfix.PlusPlusPostfix(origin))
                    DECR -> UnaryOperatorPostfix(UnaryPostfix.MinusMinusPostfix(origin))
                    else -> unknownContextError(context)
                }
            }
            else -> postfixUnarySuffixAnalysis(context.getChild(0))
        }
    }

    private fun retrieveCallArguments(context: CallSuffixContext): List<KotlinExpression> {
        val arguments = context.valueArguments()?.valueArgument()?.map {
            visit(it.expression())
        }?.toMutableList() ?: mutableListOf()
        context.annotatedLambda()?.let {
            arguments.add(visit(it))
        }
        return arguments
    }

    private fun visitTerminal(context: ParseTree): KotlinExpression {
        return when (context) {
            is TerminalNode -> IdentifierLeaf(context.text, originOf(context))
            else -> unknownContextError(context)
        }
    }

    private fun visitStatements(context: StatementsContext): List<KotlinExpression> {
        val statement = context.statement() // assuming there is only one statement possible
        return if (statement != null) {
            listOf(visit(statement))
        } else emptyList()
    }

    private fun unknownContextError(context: ParseTree): Nothing =
        error("Unknown parsing context: $context, '${context.text}'")

    private fun originOf(tree: ParserRuleContext): Origin = createOrigin(tree.start.startIndex, tree.stop.stopIndex)

    private fun originOf(terminalNode: TerminalNode) =
        createOrigin(terminalNode.symbol.startIndex, terminalNode.symbol.stopIndex)

    private fun createOrigin(startIndex: Int, endIndex: Int) = Origin(
        excerpt = patternString.pattern.substring(startIndex, endIndex + 1),
        sourceLocation = InlineSourceLocation(
            startIndex = startIndex,
            endIndex = endIndex
        ),
        parent = patternString.origin
    )

    private val KotlinExpression.requireEndIndex: Int
        get() = origin.sourceLocation?.endIndex!!

    private val KotlinExpression.requireStartIndex: Int
        get() = origin.sourceLocation?.startIndex!!

    private sealed class PostfixUnarySuffixAnalysis {
        class CallPostfix(val operator: Call, val arguments: List<KotlinExpression>) : PostfixUnarySuffixAnalysis()
        class IndexingPostfix(
            val operator: Indexing,
            val indices: List<KotlinExpression>,
        ) : PostfixUnarySuffixAnalysis()
        class AccessOperatorPostfix(val operator: Access, val argument: IdentifierLeaf) : PostfixUnarySuffixAnalysis()
        class UnaryOperatorPostfix(val operator: UnaryOperator) : PostfixUnarySuffixAnalysis()
    }

    private inner class ErrorListener : BaseErrorListener() {

        override fun syntaxError(
            recognizer: Recognizer<*, *>?,
            offendingSymbol: Any?,
            line: Int,
            charPositionInLine: Int,
            msg: String?,
            e: RecognitionException?,
        ) {
            // don't set 'line' parameter of SourceLocation if there is only one line in the pattern
            val overriddenLine = if (line == 1) {
                val hasSeveralLines = patternString.pattern.any { it == '\n' }
                line.takeIf { hasSeveralLines }
            } else line

            // extracting an excerpt
            val linePart = patternString.pattern.lines().getOrNull(line - 1)
            val excerpt = if (linePart != null && charPositionInLine < linePart.length) {
                linePart.substring(charPositionInLine)
            } else null

            val sourceLocation = InlineSourceLocation(line = overriddenLine, startIndex = charPositionInLine)
            val origin = Origin(excerpt = excerpt, sourceLocation = sourceLocation, parent = patternString.origin)
            val message = if (msg != null) {
                "Pattern parsing failed: \"$msg\"."
            } else "Pattern parsing failed."
            throw PatternParsingException(message, e, origin)
        }
    }

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}


