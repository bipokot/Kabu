// Generated from java-escape by ANTLR 4.11.1
package io.kabu.backend.antlr.grammar;
import org.antlr.v4.runtime.tree.ParseTreeVisitor;

/**
 * This interface defines a complete generic visitor for a parse tree produced
 * by {@link MGrammar}.
 *
 * @param <T> The return type of the visit operation. Use {@link Void} for
 * operations with no return type.
 */
public interface MGrammarVisitor<T> extends ParseTreeVisitor<T> {
	/**
	 * Visit a parse tree produced by {@link MGrammar#pattern}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPattern(MGrammar.PatternContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#constructorInvocation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConstructorInvocation(MGrammar.ConstructorInvocationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#userType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUserType(MGrammar.UserTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#simpleUserType}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleUserType(MGrammar.SimpleUserTypeContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#statements}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatements(MGrammar.StatementsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#statement}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitStatement(MGrammar.StatementContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#label}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLabel(MGrammar.LabelContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#assignment}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignment(MGrammar.AssignmentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#expression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExpression(MGrammar.ExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#disjunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDisjunction(MGrammar.DisjunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#conjunction}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitConjunction(MGrammar.ConjunctionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#equality}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEquality(MGrammar.EqualityContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#comparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparison(MGrammar.ComparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#genericCallLikeComparison}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitGenericCallLikeComparison(MGrammar.GenericCallLikeComparisonContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#infixOperation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInfixOperation(MGrammar.InfixOperationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#elvisExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElvisExpression(MGrammar.ElvisExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#elvis}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitElvis(MGrammar.ElvisContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#infixFunctionCall}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInfixFunctionCall(MGrammar.InfixFunctionCallContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#rangeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitRangeExpression(MGrammar.RangeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#additiveExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveExpression(MGrammar.AdditiveExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#multiplicativeExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeExpression(MGrammar.MultiplicativeExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#asExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAsExpression(MGrammar.AsExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#prefixUnaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixUnaryExpression(MGrammar.PrefixUnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#unaryPrefix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnaryPrefix(MGrammar.UnaryPrefixContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#postfixUnaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixUnaryExpression(MGrammar.PostfixUnaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#postfixUnarySuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixUnarySuffix(MGrammar.PostfixUnarySuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#directlyAssignableExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitDirectlyAssignableExpression(MGrammar.DirectlyAssignableExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#parenthesizedDirectlyAssignableExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedDirectlyAssignableExpression(MGrammar.ParenthesizedDirectlyAssignableExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#assignableExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignableExpression(MGrammar.AssignableExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#parenthesizedAssignableExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedAssignableExpression(MGrammar.ParenthesizedAssignableExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#assignableSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignableSuffix(MGrammar.AssignableSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#indexingSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitIndexingSuffix(MGrammar.IndexingSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#navigationSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitNavigationSuffix(MGrammar.NavigationSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#callSuffix}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitCallSuffix(MGrammar.CallSuffixContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#annotatedLambda}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotatedLambda(MGrammar.AnnotatedLambdaContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#valueArguments}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueArguments(MGrammar.ValueArgumentsContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#valueArgument}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitValueArgument(MGrammar.ValueArgumentContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#primaryExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrimaryExpression(MGrammar.PrimaryExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#parenthesizedExpression}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitParenthesizedExpression(MGrammar.ParenthesizedExpressionContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#literalConstant}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLiteralConstant(MGrammar.LiteralConstantContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#lambdaLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitLambdaLiteral(MGrammar.LambdaLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#functionLiteral}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitFunctionLiteral(MGrammar.FunctionLiteralContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#assignmentAndOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAssignmentAndOperator(MGrammar.AssignmentAndOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#equalityOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitEqualityOperator(MGrammar.EqualityOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#comparisonOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitComparisonOperator(MGrammar.ComparisonOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#inOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitInOperator(MGrammar.InOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#additiveOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAdditiveOperator(MGrammar.AdditiveOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#multiplicativeOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiplicativeOperator(MGrammar.MultiplicativeOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#prefixUnaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPrefixUnaryOperator(MGrammar.PrefixUnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#postfixUnaryOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitPostfixUnaryOperator(MGrammar.PostfixUnaryOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#excl}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitExcl(MGrammar.ExclContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#memberAccessOperator}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMemberAccessOperator(MGrammar.MemberAccessOperatorContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#safeNav}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSafeNav(MGrammar.SafeNavContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#annotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotation(MGrammar.AnnotationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#singleAnnotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSingleAnnotation(MGrammar.SingleAnnotationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#multiAnnotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitMultiAnnotation(MGrammar.MultiAnnotationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#annotationUseSiteTarget}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitAnnotationUseSiteTarget(MGrammar.AnnotationUseSiteTargetContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#unescapedAnnotation}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitUnescapedAnnotation(MGrammar.UnescapedAnnotationContext ctx);
	/**
	 * Visit a parse tree produced by {@link MGrammar#simpleIdentifier}.
	 * @param ctx the parse tree
	 * @return the visitor result
	 */
	T visitSimpleIdentifier(MGrammar.SimpleIdentifierContext ctx);
}
