@file:Suppress("MagicNumber")

package io.kabu.backend.parser

import io.kabu.backend.diagnostic.HasOrigin
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.parser.Arity.BINARY
import io.kabu.backend.parser.Arity.NARY
import io.kabu.backend.parser.Arity.UNARY

/**
 * @property function overridable function name (by convention)
 * @property translation actual compiler translation
 * @property mustReturn what the translation function must return, if FREE - no restrictions
 */
data class Overriding(
    val function: String,
    val translation: String? = null,
    val mustReturn: FunctionMustReturn = FunctionMustReturn.FREE
)

/**
 * Descendants can possibly be used in a resulting code.
 * Only those operators implemented that support overriding in some way
 *
 * @property expressionType type of this operator expression in usage site
 * @property invertedArgumentOrdering if there is difference between argument ordering in use site of operator
 * and its translation
 */
sealed class Operator( //todo rename to OperatorType, or use ParsedOperator(OperatorType, origin) in KotlinExpression ?
    val symbol: String,
    val priority: Int,
    val arity: Arity,
    val expressionType: EvaluatedExpressionType = EvaluatedExpressionType.FREE,
    val overriding: Overriding? = null,
    //todo remove as we should not mix **type** of an operator and actual parsed operator (with origin) ?
    override val origin: Origin
) : HasOrigin {

    /** If there is difference between argument ordering in use site of operator and its translation */
    open val invertedArgumentOrdering: Boolean = false

    fun getFunctionNameOrThrow(): String = overriding?.function
        ?: diagnosticError("Operator $this is not supported", this)

    override fun toString() = this::class.simpleName ?: "Operator"
}

sealed class UnaryOperator(
    symbol: String,
    priority: Int,
    expressionType: EvaluatedExpressionType = EvaluatedExpressionType.FREE,
    overriding: Overriding? = null,
    origin: Origin,
) : Operator(symbol, priority, UNARY, expressionType, overriding, origin)

sealed class BinaryOperator(
    symbol: String,
    priority: Int,
    expressionType: EvaluatedExpressionType = EvaluatedExpressionType.FREE,
    overriding: Overriding? = null,
    origin: Origin
) : Operator(symbol, priority, BINARY, expressionType, overriding, origin)

sealed class NaryOperator(
    symbol: String,
    priority: Int,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding? = null,
    origin: Origin
) : Operator(symbol, priority, NARY, expressionType, overriding, origin)

// ######### 15 priority #########

class Call(origin: Origin) : NaryOperator(
    symbol = "call",
    priority = 15,
    expressionType = EvaluatedExpressionType.FREE,
    overriding = Overriding("invoke", "a.invoke(parametersList)"),
    origin = origin
)

class Indexing(origin: Origin) : NaryOperator(
    symbol = "index",
    priority = 15,
    expressionType = EvaluatedExpressionType.FREE,
    overriding = Overriding("get", "a.get(parametersList)"),
    origin = origin
)

sealed class UnaryPostfix(
    symbol: String,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding? = null,
    origin: Origin
) : UnaryOperator(symbol, 15, expressionType, overriding, origin) {

    /*
    The inc() and dec() functions must return a value, which will be assigned to the variable
    on which the ++ or -- operation was used.
    They shouldn't mutate the object on which the inc or dec was invoked.
    The compiler:
    - Checks that the return type of the function is a subtype of T
     */
    class PlusPlusPostfix(origin: Origin) : UnaryPostfix(
        symbol = "++",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("inc", "a.inc()", FunctionMustReturn.ASSIGNABLE),
        origin = origin
    )

    class MinusMinusPostfix(origin: Origin) : UnaryPostfix(
        symbol = "--",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("dec", "a.dec()", FunctionMustReturn.ASSIGNABLE),
        origin = origin
    )
}

class Access(origin: Origin) : BinaryOperator(".", 15, origin = origin)

//object SafeAccess : BinaryOperator("?.", 15), NotSupported
//object Question : BinaryOperator("?", 15), NotSupported

// ######### 14 priority #########

sealed class UnaryPrefix(
    symbol: String,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding? = null,
    origin: Origin
) : UnaryOperator(symbol, 14, expressionType, overriding, origin) {

    class UnaryMinus(origin: Origin) : UnaryPrefix(
        symbol = "-",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("unaryMinus", "a.unaryMinus()"),
        origin = origin
    )

    class UnaryPlus(origin: Origin) : UnaryPrefix(
        symbol = "+",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("unaryPlus", "a.unaryPlus()"),
        origin = origin
    )

    class PlusPlusPrefix(origin: Origin) : UnaryPrefix(
        symbol = "++",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("inc", "a.inc()", FunctionMustReturn.ASSIGNABLE),
        origin = origin
    )

    class MinusMinusPrefix(origin: Origin) : UnaryPrefix(
        symbol = "--",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("dec", "a.dec()", FunctionMustReturn.ASSIGNABLE),
        origin = origin
    )

    class Not(origin: Origin) : UnaryPrefix(
        symbol = "!",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("not", "a.not()"),
        origin = origin
    )
}

//class Label(symbol: String) : UnaryOperator(symbol, 14), NotSupported

// ######### 13 priority #########

//sealed class TypeRHS(symbol: String) : BinaryOperator(symbol, 13), NotSupported {
//    object Colon : TypeRHS(":")
//    object As : TypeRHS("as")
//    object AsQuestion : TypeRHS("as?")
//}

// ######### 12 priority #########

sealed class Multiplicative(
    symbol: String,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding? = null,
    origin: Origin
) : BinaryOperator(symbol, 12, expressionType, overriding, origin) {

    class Multiply(origin: Origin) : Multiplicative(
        symbol = "*",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("times", "a.times(b)"),
        origin = origin
    )

    class Divide(origin: Origin) : Multiplicative(
        symbol = "/",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("div", "a.div(b)"),
        origin = origin
    )

    class Remainder(origin: Origin) : Multiplicative(
        symbol = "%",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("rem", "a.rem(b)"),
        origin = origin
    )
}

// ######### 11 priority #########

sealed class Additive(
    symbol: String,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding? = null,
    origin: Origin
) : BinaryOperator(symbol, 11, expressionType, overriding, origin) {

    class BinaryPlus(origin: Origin) : Additive(
        symbol = "+",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("plus", "a.plus(b)"),
        origin = origin
    )

    class BinaryMinus(origin: Origin) : Additive(
        symbol = "-",
        expressionType = EvaluatedExpressionType.FREE,
        overriding = Overriding("minus", "a.minus(b)"),
        origin = origin
    )
}

// ######### 10 priority #########

class RangeTo(origin: Origin) : BinaryOperator(
    symbol = "..",
    priority = 10,
    expressionType = EvaluatedExpressionType.FREE,
    overriding = Overriding("rangeTo", "a.rangeTo(b)"),
    origin = origin
)

class RangeUntil(origin: Origin) : BinaryOperator(
    symbol = "..<",
    priority = 10,
    expressionType = EvaluatedExpressionType.FREE,
    overriding = Overriding("rangeUntil", "a.rangeUntil(b)"),
    origin = origin
)

// ######### 9 priority #########

class InfixFunction(
    symbol: String,
    origin: Origin
) : BinaryOperator(
    symbol = symbol,
    priority = 9,
    expressionType = EvaluatedExpressionType.FREE,
    overriding = Overriding(symbol, "a.$symbol(b)"),
    origin = origin
)

// ######### 8 priority #########

//object Elvis : BinaryOperator("?:", 8), NotSupported

// ######### 7 priority #########

sealed class InclusionCheck(
    symbol: String,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding? = null,
    origin: Origin
) : BinaryOperator(symbol, 7, expressionType, overriding, origin) {

    class In(origin: Origin) : InclusionCheck(
        symbol = "in",
        expressionType = EvaluatedExpressionType.BOOLEAN,
        overriding = Overriding(
            function = "contains",
            translation = "b.contains(a)",
            mustReturn = FunctionMustReturn.BOOLEAN
        ),
        origin = origin
    ) {
        override val invertedArgumentOrdering = true
    }

    class NotIn(origin: Origin) : InclusionCheck(
        symbol = "!in",
        expressionType = EvaluatedExpressionType.BOOLEAN,
        overriding = Overriding(
            function = "contains",
            translation = "!b.contains(a)",
            mustReturn = FunctionMustReturn.BOOLEAN
        ),
        origin = origin
    ) {
        override val invertedArgumentOrdering = true
    }
}

//    object Is : NamedCheck("is"), NotSupported
//    object NotIs : NamedCheck("!is"), NotSupported

// ######### 6 priority #########

sealed class Comparison(
    symbol: String,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding? = null,
    origin: Origin
) : BinaryOperator(symbol, 6, expressionType, overriding, origin) {

    class Less(origin: Origin) : Comparison(
        symbol = "<",
        expressionType = EvaluatedExpressionType.BOOLEAN,
        overriding = Overriding(
            function = "compareTo",
            translation = "a.compareTo(b) < 0",
            mustReturn = FunctionMustReturn.INT,
        ),
        origin = origin
    )

    class Greater(origin: Origin) : Comparison(
        symbol = ">",
        expressionType = EvaluatedExpressionType.BOOLEAN,
        overriding = Overriding(
            function = "compareTo",
            translation = "a.compareTo(b) > 0",
            mustReturn = FunctionMustReturn.INT,
        ),
        origin = origin
    )

    class LessOrEqual(origin: Origin) : Comparison(
        symbol = "<=",
        expressionType = EvaluatedExpressionType.BOOLEAN,
        overriding = Overriding(
            function = "compareTo",
            translation = "a.compareTo(b) <= 0",
            mustReturn = FunctionMustReturn.INT,
        ),
        origin = origin
    )

    class GreaterOrEqual(origin: Origin) : Comparison(
        symbol = ">=",
        expressionType = EvaluatedExpressionType.BOOLEAN,
        overriding = Overriding(
            function = "compareTo",
            translation = "a.compareTo(b) >= 0",
            mustReturn = FunctionMustReturn.INT,
        ),
        origin = origin
    )
}

// ######### 5 priority #########

sealed class Equality(
    symbol: String,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding?,
    origin: Origin
) : BinaryOperator(symbol, 5, expressionType, overriding, origin) {
    /*
    These operators only work with the function equals(other: Any?): Boolean,
    which can be overridden to provide custom equality check implementation.
     */

    class EqualTo(origin: Origin) : Equality(
        symbol = "==",
        expressionType = EvaluatedExpressionType.BOOLEAN,
        overriding = Overriding(
            function = "equals",
            translation = "a?.equals(b) ?: (b === null)",
            mustReturn = FunctionMustReturn.BOOLEAN,
        ),
        origin = origin
    )

    class NotEqualTo(origin: Origin) : Equality(
        symbol = "!=",
        expressionType = EvaluatedExpressionType.BOOLEAN,
        overriding = Overriding(
            function = "equals",
            translation = "!(a?.equals(b) ?: (b === null))",
            mustReturn = FunctionMustReturn.BOOLEAN,
        ),
        origin = origin
    )
}

// ######### 4 priority #########

//object Conjunction : BinaryOperator("&&", 4), NotSupported

// ######### 3 priority #########

//object Disjunction : BinaryOperator("||", 3), NotSupported

// ######### 2 priority #########

//object Spread : UnaryOperator("*", 2), NotSupported

// ######### 1 priority #########

sealed class ModAssign(
    symbol: String,
    expressionType: EvaluatedExpressionType,
    overriding: Overriding? = null,
    origin: Origin
) : BinaryOperator(symbol, 1, expressionType, overriding, origin) {

    /*
    For the assignment operations, e.g. a += b, the compiler performs the following steps:
    - If the function from the right column is available
        - If the corresponding binary function (i.e. plus() for plusAssign()) is available too, report error (ambiguity)
        - Make sure its return type is Unit, and report an error otherwise,
        - Generate code for a.plusAssign(b);
    - Otherwise, try to generate code for a = a + b
        - this includes a type check: the type of 'a + b' must be a subtype of a
     */
    class PlusAssign(origin: Origin) : ModAssign(
        symbol = "+=",
        expressionType = EvaluatedExpressionType.NONE,
        overriding = Overriding("plusAssign", "a.plusAssign(b)", FunctionMustReturn.UNIT),
        origin = origin
    )

    class MinusAssign(origin: Origin) : ModAssign(
        symbol = "-=",
        expressionType = EvaluatedExpressionType.NONE,
        overriding = Overriding("minusAssign", "a.minusAssign(b)", FunctionMustReturn.UNIT),
        origin = origin
    )

    class MultiplyAssign(origin: Origin) : ModAssign(
        symbol = "*=",
        expressionType = EvaluatedExpressionType.NONE,
        overriding = Overriding("timesAssign", "a.timesAssign(b)", FunctionMustReturn.UNIT),
        origin = origin
    )

    class DivideAssign(origin: Origin) : ModAssign(
        symbol = "/=",
        expressionType = EvaluatedExpressionType.NONE,
        overriding = Overriding("divAssign", "a.divAssign(b)", FunctionMustReturn.UNIT),
        origin = origin
    )

    class RemainderAssign(origin: Origin) : ModAssign(
        symbol = "%=",
        expressionType = EvaluatedExpressionType.NONE,
        overriding = Overriding("remAssign", "a.remAssign(b)", FunctionMustReturn.UNIT),
        origin = origin
    )
}

class Assign(origin: Origin) : BinaryOperator(
    symbol = "=",
    priority = 1,
    expressionType = EvaluatedExpressionType.NONE,
    overriding = Overriding("set", "<???>", FunctionMustReturn.UNIT),
    origin = origin
)
