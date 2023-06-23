// Generated from java-escape by ANTLR 4.11.1
package io.kabu.backend.antlr.grammar;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link MGrammar}.
 */
public interface MGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link MGrammar#pattern}.
	 * @param ctx the parse tree
	 */
	void enterPattern(MGrammar.PatternContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#pattern}.
	 * @param ctx the parse tree
	 */
	void exitPattern(MGrammar.PatternContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#constructorInvocation}.
	 * @param ctx the parse tree
	 */
	void enterConstructorInvocation(MGrammar.ConstructorInvocationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#constructorInvocation}.
	 * @param ctx the parse tree
	 */
	void exitConstructorInvocation(MGrammar.ConstructorInvocationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#userType}.
	 * @param ctx the parse tree
	 */
	void enterUserType(MGrammar.UserTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#userType}.
	 * @param ctx the parse tree
	 */
	void exitUserType(MGrammar.UserTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#simpleUserType}.
	 * @param ctx the parse tree
	 */
	void enterSimpleUserType(MGrammar.SimpleUserTypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#simpleUserType}.
	 * @param ctx the parse tree
	 */
	void exitSimpleUserType(MGrammar.SimpleUserTypeContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#statements}.
	 * @param ctx the parse tree
	 */
	void enterStatements(MGrammar.StatementsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#statements}.
	 * @param ctx the parse tree
	 */
	void exitStatements(MGrammar.StatementsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#statement}.
	 * @param ctx the parse tree
	 */
	void enterStatement(MGrammar.StatementContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#statement}.
	 * @param ctx the parse tree
	 */
	void exitStatement(MGrammar.StatementContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#label}.
	 * @param ctx the parse tree
	 */
	void enterLabel(MGrammar.LabelContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#label}.
	 * @param ctx the parse tree
	 */
	void exitLabel(MGrammar.LabelContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#assignment}.
	 * @param ctx the parse tree
	 */
	void enterAssignment(MGrammar.AssignmentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#assignment}.
	 * @param ctx the parse tree
	 */
	void exitAssignment(MGrammar.AssignmentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#expression}.
	 * @param ctx the parse tree
	 */
	void enterExpression(MGrammar.ExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#expression}.
	 * @param ctx the parse tree
	 */
	void exitExpression(MGrammar.ExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#disjunction}.
	 * @param ctx the parse tree
	 */
	void enterDisjunction(MGrammar.DisjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#disjunction}.
	 * @param ctx the parse tree
	 */
	void exitDisjunction(MGrammar.DisjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#conjunction}.
	 * @param ctx the parse tree
	 */
	void enterConjunction(MGrammar.ConjunctionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#conjunction}.
	 * @param ctx the parse tree
	 */
	void exitConjunction(MGrammar.ConjunctionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#equality}.
	 * @param ctx the parse tree
	 */
	void enterEquality(MGrammar.EqualityContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#equality}.
	 * @param ctx the parse tree
	 */
	void exitEquality(MGrammar.EqualityContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#comparison}.
	 * @param ctx the parse tree
	 */
	void enterComparison(MGrammar.ComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#comparison}.
	 * @param ctx the parse tree
	 */
	void exitComparison(MGrammar.ComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#genericCallLikeComparison}.
	 * @param ctx the parse tree
	 */
	void enterGenericCallLikeComparison(MGrammar.GenericCallLikeComparisonContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#genericCallLikeComparison}.
	 * @param ctx the parse tree
	 */
	void exitGenericCallLikeComparison(MGrammar.GenericCallLikeComparisonContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#infixOperation}.
	 * @param ctx the parse tree
	 */
	void enterInfixOperation(MGrammar.InfixOperationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#infixOperation}.
	 * @param ctx the parse tree
	 */
	void exitInfixOperation(MGrammar.InfixOperationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#elvisExpression}.
	 * @param ctx the parse tree
	 */
	void enterElvisExpression(MGrammar.ElvisExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#elvisExpression}.
	 * @param ctx the parse tree
	 */
	void exitElvisExpression(MGrammar.ElvisExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#elvis}.
	 * @param ctx the parse tree
	 */
	void enterElvis(MGrammar.ElvisContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#elvis}.
	 * @param ctx the parse tree
	 */
	void exitElvis(MGrammar.ElvisContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#infixFunctionCall}.
	 * @param ctx the parse tree
	 */
	void enterInfixFunctionCall(MGrammar.InfixFunctionCallContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#infixFunctionCall}.
	 * @param ctx the parse tree
	 */
	void exitInfixFunctionCall(MGrammar.InfixFunctionCallContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#rangeExpression}.
	 * @param ctx the parse tree
	 */
	void enterRangeExpression(MGrammar.RangeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#rangeExpression}.
	 * @param ctx the parse tree
	 */
	void exitRangeExpression(MGrammar.RangeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveExpression(MGrammar.AdditiveExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#additiveExpression}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveExpression(MGrammar.AdditiveExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeExpression(MGrammar.MultiplicativeExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#multiplicativeExpression}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeExpression(MGrammar.MultiplicativeExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#asExpression}.
	 * @param ctx the parse tree
	 */
	void enterAsExpression(MGrammar.AsExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#asExpression}.
	 * @param ctx the parse tree
	 */
	void exitAsExpression(MGrammar.AsExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#prefixUnaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPrefixUnaryExpression(MGrammar.PrefixUnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#prefixUnaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPrefixUnaryExpression(MGrammar.PrefixUnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#unaryPrefix}.
	 * @param ctx the parse tree
	 */
	void enterUnaryPrefix(MGrammar.UnaryPrefixContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#unaryPrefix}.
	 * @param ctx the parse tree
	 */
	void exitUnaryPrefix(MGrammar.UnaryPrefixContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#postfixUnaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPostfixUnaryExpression(MGrammar.PostfixUnaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#postfixUnaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPostfixUnaryExpression(MGrammar.PostfixUnaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#postfixUnarySuffix}.
	 * @param ctx the parse tree
	 */
	void enterPostfixUnarySuffix(MGrammar.PostfixUnarySuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#postfixUnarySuffix}.
	 * @param ctx the parse tree
	 */
	void exitPostfixUnarySuffix(MGrammar.PostfixUnarySuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#directlyAssignableExpression}.
	 * @param ctx the parse tree
	 */
	void enterDirectlyAssignableExpression(MGrammar.DirectlyAssignableExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#directlyAssignableExpression}.
	 * @param ctx the parse tree
	 */
	void exitDirectlyAssignableExpression(MGrammar.DirectlyAssignableExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#parenthesizedDirectlyAssignableExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedDirectlyAssignableExpression(MGrammar.ParenthesizedDirectlyAssignableExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#parenthesizedDirectlyAssignableExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedDirectlyAssignableExpression(MGrammar.ParenthesizedDirectlyAssignableExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#assignableExpression}.
	 * @param ctx the parse tree
	 */
	void enterAssignableExpression(MGrammar.AssignableExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#assignableExpression}.
	 * @param ctx the parse tree
	 */
	void exitAssignableExpression(MGrammar.AssignableExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#parenthesizedAssignableExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedAssignableExpression(MGrammar.ParenthesizedAssignableExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#parenthesizedAssignableExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedAssignableExpression(MGrammar.ParenthesizedAssignableExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#assignableSuffix}.
	 * @param ctx the parse tree
	 */
	void enterAssignableSuffix(MGrammar.AssignableSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#assignableSuffix}.
	 * @param ctx the parse tree
	 */
	void exitAssignableSuffix(MGrammar.AssignableSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#indexingSuffix}.
	 * @param ctx the parse tree
	 */
	void enterIndexingSuffix(MGrammar.IndexingSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#indexingSuffix}.
	 * @param ctx the parse tree
	 */
	void exitIndexingSuffix(MGrammar.IndexingSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#navigationSuffix}.
	 * @param ctx the parse tree
	 */
	void enterNavigationSuffix(MGrammar.NavigationSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#navigationSuffix}.
	 * @param ctx the parse tree
	 */
	void exitNavigationSuffix(MGrammar.NavigationSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#callSuffix}.
	 * @param ctx the parse tree
	 */
	void enterCallSuffix(MGrammar.CallSuffixContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#callSuffix}.
	 * @param ctx the parse tree
	 */
	void exitCallSuffix(MGrammar.CallSuffixContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#annotatedLambda}.
	 * @param ctx the parse tree
	 */
	void enterAnnotatedLambda(MGrammar.AnnotatedLambdaContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#annotatedLambda}.
	 * @param ctx the parse tree
	 */
	void exitAnnotatedLambda(MGrammar.AnnotatedLambdaContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#valueArguments}.
	 * @param ctx the parse tree
	 */
	void enterValueArguments(MGrammar.ValueArgumentsContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#valueArguments}.
	 * @param ctx the parse tree
	 */
	void exitValueArguments(MGrammar.ValueArgumentsContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#valueArgument}.
	 * @param ctx the parse tree
	 */
	void enterValueArgument(MGrammar.ValueArgumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#valueArgument}.
	 * @param ctx the parse tree
	 */
	void exitValueArgument(MGrammar.ValueArgumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void enterPrimaryExpression(MGrammar.PrimaryExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#primaryExpression}.
	 * @param ctx the parse tree
	 */
	void exitPrimaryExpression(MGrammar.PrimaryExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#parenthesizedExpression}.
	 * @param ctx the parse tree
	 */
	void enterParenthesizedExpression(MGrammar.ParenthesizedExpressionContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#parenthesizedExpression}.
	 * @param ctx the parse tree
	 */
	void exitParenthesizedExpression(MGrammar.ParenthesizedExpressionContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#literalConstant}.
	 * @param ctx the parse tree
	 */
	void enterLiteralConstant(MGrammar.LiteralConstantContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#literalConstant}.
	 * @param ctx the parse tree
	 */
	void exitLiteralConstant(MGrammar.LiteralConstantContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#lambdaLiteral}.
	 * @param ctx the parse tree
	 */
	void enterLambdaLiteral(MGrammar.LambdaLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#lambdaLiteral}.
	 * @param ctx the parse tree
	 */
	void exitLambdaLiteral(MGrammar.LambdaLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#functionLiteral}.
	 * @param ctx the parse tree
	 */
	void enterFunctionLiteral(MGrammar.FunctionLiteralContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#functionLiteral}.
	 * @param ctx the parse tree
	 */
	void exitFunctionLiteral(MGrammar.FunctionLiteralContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#assignmentAndOperator}.
	 * @param ctx the parse tree
	 */
	void enterAssignmentAndOperator(MGrammar.AssignmentAndOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#assignmentAndOperator}.
	 * @param ctx the parse tree
	 */
	void exitAssignmentAndOperator(MGrammar.AssignmentAndOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#equalityOperator}.
	 * @param ctx the parse tree
	 */
	void enterEqualityOperator(MGrammar.EqualityOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#equalityOperator}.
	 * @param ctx the parse tree
	 */
	void exitEqualityOperator(MGrammar.EqualityOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void enterComparisonOperator(MGrammar.ComparisonOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#comparisonOperator}.
	 * @param ctx the parse tree
	 */
	void exitComparisonOperator(MGrammar.ComparisonOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#inOperator}.
	 * @param ctx the parse tree
	 */
	void enterInOperator(MGrammar.InOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#inOperator}.
	 * @param ctx the parse tree
	 */
	void exitInOperator(MGrammar.InOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#additiveOperator}.
	 * @param ctx the parse tree
	 */
	void enterAdditiveOperator(MGrammar.AdditiveOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#additiveOperator}.
	 * @param ctx the parse tree
	 */
	void exitAdditiveOperator(MGrammar.AdditiveOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#multiplicativeOperator}.
	 * @param ctx the parse tree
	 */
	void enterMultiplicativeOperator(MGrammar.MultiplicativeOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#multiplicativeOperator}.
	 * @param ctx the parse tree
	 */
	void exitMultiplicativeOperator(MGrammar.MultiplicativeOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#prefixUnaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterPrefixUnaryOperator(MGrammar.PrefixUnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#prefixUnaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitPrefixUnaryOperator(MGrammar.PrefixUnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#postfixUnaryOperator}.
	 * @param ctx the parse tree
	 */
	void enterPostfixUnaryOperator(MGrammar.PostfixUnaryOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#postfixUnaryOperator}.
	 * @param ctx the parse tree
	 */
	void exitPostfixUnaryOperator(MGrammar.PostfixUnaryOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#excl}.
	 * @param ctx the parse tree
	 */
	void enterExcl(MGrammar.ExclContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#excl}.
	 * @param ctx the parse tree
	 */
	void exitExcl(MGrammar.ExclContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#memberAccessOperator}.
	 * @param ctx the parse tree
	 */
	void enterMemberAccessOperator(MGrammar.MemberAccessOperatorContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#memberAccessOperator}.
	 * @param ctx the parse tree
	 */
	void exitMemberAccessOperator(MGrammar.MemberAccessOperatorContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#safeNav}.
	 * @param ctx the parse tree
	 */
	void enterSafeNav(MGrammar.SafeNavContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#safeNav}.
	 * @param ctx the parse tree
	 */
	void exitSafeNav(MGrammar.SafeNavContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#annotation}.
	 * @param ctx the parse tree
	 */
	void enterAnnotation(MGrammar.AnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#annotation}.
	 * @param ctx the parse tree
	 */
	void exitAnnotation(MGrammar.AnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#singleAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterSingleAnnotation(MGrammar.SingleAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#singleAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitSingleAnnotation(MGrammar.SingleAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#multiAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterMultiAnnotation(MGrammar.MultiAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#multiAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitMultiAnnotation(MGrammar.MultiAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#annotationUseSiteTarget}.
	 * @param ctx the parse tree
	 */
	void enterAnnotationUseSiteTarget(MGrammar.AnnotationUseSiteTargetContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#annotationUseSiteTarget}.
	 * @param ctx the parse tree
	 */
	void exitAnnotationUseSiteTarget(MGrammar.AnnotationUseSiteTargetContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#unescapedAnnotation}.
	 * @param ctx the parse tree
	 */
	void enterUnescapedAnnotation(MGrammar.UnescapedAnnotationContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#unescapedAnnotation}.
	 * @param ctx the parse tree
	 */
	void exitUnescapedAnnotation(MGrammar.UnescapedAnnotationContext ctx);
	/**
	 * Enter a parse tree produced by {@link MGrammar#simpleIdentifier}.
	 * @param ctx the parse tree
	 */
	void enterSimpleIdentifier(MGrammar.SimpleIdentifierContext ctx);
	/**
	 * Exit a parse tree produced by {@link MGrammar#simpleIdentifier}.
	 * @param ctx the parse tree
	 */
	void exitSimpleIdentifier(MGrammar.SimpleIdentifierContext ctx);
}
