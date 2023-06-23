package io.kabu.backend

import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.diagnostic.FileSourceLocation
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.builder.patternParsingError
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.PatternParser
import io.kabu.backend.exception.PatternParsingException
import io.kabu.backend.parser.PatternString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

class PatternParserTest : Assert() {

    @Test()
    fun testAdHoc() {
        val pattern = "i { b.x[a].d = s }"

        val patternString = PatternString(pattern, Origin(FileSourceLocation("file.kt", 23)))
        try {
            val currentExpression = PatternParser(patternString).parse()
            val expression = currentExpression.getOrThrow()
            logger.debug { "Expression: $expression" }

            fun KotlinExpression.pr(level: Int = 0) {
                repeat(level) { print("  ") }
                logger.debug("Origin: $origin")
                children.forEach {
                    it.pr(level + 1)
                }
            }
//            expression.pr()

            logger.debug("Excerpts string: ${expression.toExcerptsString()}")

        } catch (e: PatternParsingException) {
            logger.error(e) { e.origin }
            patternParsingError(e)
        }
    }
}

@RunWith(Parameterized::class)
class PatternParserTestParameterized(val raw: String, val parsed: String) : Assert() {

    //todo add more tests with mixed precedence operators
    @Suppress("MaxLineLength")
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
                // base
                "a" to "a",

                // postfix
                "a++" to "(a ++)",
                "b --" to "(b --)",
                "a + b++" to "(a + (b ++))",
                "a++ / b--" to "((a ++) / (b --))",
                "a++ / b--" to "((a ++) / (b --))",
                "b----" to "((b --) --)",
                "a--++--" to "(((a --) ++) --)",
                "b--++------++" to "((((((b --) ++) --) --) --) ++)",

                // prefix
                "--a" to "(-- a)",
                "++ a" to "(++ a)",
                "+a" to "(+ a)",
                "- a" to "(- a)",
                "!a" to "(! a)",
                "+ a + -b" to "((+ a) + (- b))",
                "a + +b" to "(a + (+ b))",
                "++(a--)" to "(++ (a --))",
                "--i++" to "(-- (i ++))",
                "++a---b" to "((++ (a --)) - b)",
                "a%!b" to "(a % (! b))",
                "+-i" to "(+ (- i))",
                "+-+--i" to "(+ (- (+ (-- i))))",
                "+-a+-b" to "((+ (- a)) + (- b))",
                "!a+!!b" to "((! a) + (! (! b)))",

                // multiplicative
                "a * b" to "(a * b)",
                "a/b" to "(a / b)",
                "a %b" to "(a % b)",
                "a %b /  c *d% e " to "((((a % b) / c) * d) % e)",
                "a *b % (c *d)% e " to "(((a * b) % (c * d)) % e)",

                // additive
                "a+b" to "(a + b)",
                "a-b" to "(a - b)",
                "a-b + c -d" to "(((a - b) + c) - d)",
                "a+b - (c+ d)" to "((a + b) - (c + d))",

                // range
                "a..b" to "(a .. b)",
                "a..b + c" to "(a .. (b + c))",
                "a..b ..c" to "((a .. b) .. c)",
                "a..b ..c .. d" to "(((a .. b) .. c) .. d)",
                "a..<b" to "(a ..< b)",
                "a + b ..< c" to "((a + b) ..< c)",
                "a..<b ..c ..< d" to "(((a ..< b) .. c) ..< d)",

                // infix
                "a func b" to "(a `func` b)",
                "a * b func c" to "((a * b) `func` c)",
                "b func c + d" to "(b `func` (c + d))",
                "b func c func2 d" to "((b `func` c) `func2` d)",
                "b func c func2 (d func3 e)" to "((b `func` c) `func2` (d `func3` e))",

                // named check
                "a in b" to "(a in b)",
                "a !in b" to "(a !in b)",
                "a+b in c" to "((a + b) in c)",
                "a + (b !in c)" to "(a + (b !in c))",
                "a in b in c" to "((a in b) in c)",
                "a in b in (c in d)" to "((a in b) in (c in d))",

                // comparison
                "a < b" to "(a < b)",
                "a > b" to "(a > b)",
                "a <= b" to "(a <= b)",
                "a >= b" to "(a >= b)",
                "a > b + c" to "(a > (b + c))",
                "a*b <= c" to "((a * b) <= c)",
                "a > b < c" to "((a > b) < c)",
                "a > b * d < c" to "((a > (b * d)) < c)",
                "a > b < c >= d" to "(((a > b) < c) >= d)",
                "a > (b < c) >= d" to "((a > (b < c)) >= d)",

                // equality
                "a==b" to "(a == b)",
                "a!=b" to "(a != b)",
                "a==b * c" to "(a == (b * c))",
                "a + b == c" to "((a + b) == c)",
                "a == b==c" to "((a == b) == c)",
                "a == b==(c ==d)" to "((a == b) == (c == d))",

                // operator and assign
                "a+=b" to "(a += b)",
                "a -= b" to "(a -= b)",
                "a*= b" to "(a *= b)",
                "a /=b" to "(a /= b)",
                "a%=b" to "(a %= b)",

                // lambda
                "{a}" to "(lambda [a])",
                "{!a}" to "(lambda [(! a)])",
                "a {}" to "(a call [(lambda [])])",
                "{}{}" to "((lambda []) call [(lambda [])])",
                "a { b / c }" to "(a call [(lambda [(b / c)])])",
                "a(b) { c + d }" to "(a call [b, (lambda [(c + d)])])",
                "a func { b * c }" to "(a `func` (lambda [(b * c)]))",
                "a({b},c) {d}" to "(a call [(lambda [b]), c, (lambda [d])])",

                // call
                "b()" to "(b call [])",
                "a(b,c)" to "(a call [b, c])",
                "a (b, c, d,e)" to "(a call [b, c, d, e])",
                "a(b, c) { d }" to "(a call [b, c, (lambda [d])])",
                "{a }()" to "((lambda [a]) call [])",
                "{a }{  b}" to "((lambda [a]) call [(lambda [b])])",
                "{}{ b}" to "((lambda []) call [(lambda [b])])",
                "{a }{}" to "((lambda [a]) call [(lambda [])])",
                "b++()" to "((b ++) call [])",

                // indexing
                "a[b]" to "(a index [b])",
                "a[b, c,d]" to "(a index [b, c, d])",
                "a[b, c,d][e]" to "((a index [b, c, d]) index [e])",
                "a[b, c[f[e]],d]" to "(a index [b, (c index [(f index [e])]), d])",

                // nested binary
                "a* b+ c" to "((a * b) + c)",
                "(a * b + c / d) % e + f" to "((((a * b) + (c / d)) % e) + f)",
                "e % (a .. b + c / d)" to "(e % (a .. (b + (c / d))))",
                "(a .. b * c ) % e + f .. g" to "((((a .. (b * c)) % e) + f) .. g)",

                // assign
                "i { b.x = s }" to "(i call [(lambda [((b . x) = s)])])",
                "i { b[a].x = s }" to "(i call [(lambda [(((b index [a]) . x) = s)])])",
                "i { b()[a].x = s }" to "(i call [(lambda [((((b call []) index [a]) . x) = s)])])",
                "i { b[a]().x = s }" to "(i call [(lambda [((((b index [a]) call []) . x) = s)])])",
                "i { b().x[a] = s }" to "(i call [(lambda [((((b call []) . x) index [a]) = s)])])",
                "i { b.x[a].d = s }" to "(i call [(lambda [((((b . x) index [a]) . d) = s)])])",

                // mixed
                "(a)(b)(c)" to "((a call [b]) call [c])",
                "{a}{b}{c}" to "(((lambda [a]) call [(lambda [b])]) call [(lambda [c])])",
                "a--(b)++" to "(((a --) call [b]) ++)",
                "i infixTest { s }" to "(i `infixTest` (lambda [s]))",
                "+{ abc }" to "(+ (lambda [abc]))",

                // lambda annotations
                "b @Abc { }" to "(b call [(@[Abc()] lambda [])])",
                "b @Abc() { }" to "(b call [(@[Abc()] lambda [])])",
                "b @Abc @Def { }" to "(b call [(@[Abc(), Def()] lambda [])])",
                "b @Abc @Def(par1 = val1) { }" to "(b call [(@[Abc(), Def(par1=val1)] lambda [])])",
                "b @[Abc] { }" to "(b call [(@[Abc()] lambda [])])",
                "b @[Abc()] { }" to "(b call [(@[Abc()] lambda [])])",
                "b @[Abc Def] { }" to "(b call [(@[Abc(), Def()] lambda [])])",
                "b @Abc(value1) { }" to "(b call [(@[Abc(value1)] lambda [])])",
                "b @Abc(value1, value2) { }" to "(b call [(@[Abc(value1, value2)] lambda [])])",
                "b @Abc(parameter = file) { }" to "(b call [(@[Abc(parameter=file)] lambda [])])",
                "b @Abc(parameter = value, parameter2=value2) { }" to "(b call [(@[Abc(parameter=value, parameter2=value2)] lambda [])])",
                "b @Abc(parameter1 = value, value2) { }" to "(b call [(@[Abc(parameter1=value, value2)] lambda [])])",
                "@Ann(value1) {}[bar]" to "((@[Ann(value1)] lambda []) index [bar])",
                "foo * @Ann() {}" to "(foo * (@[Ann()] lambda []))",
                "! @Ann() {}" to "(! (@[Ann()] lambda []))",
                "xxx yyy @A {}" to "(xxx `yyy` (@[A()] lambda []))",

                "c" to "c"
        ).map { arrayOf(it.first, it.second) }
    }

    @Test
    fun testParsingAndTreeTransform_BlackBox() {
        logger.debug("Raw: $raw")
        val result = PatternParser(PatternString(raw)).parse()
        assertTrue(result.isSuccess)
        assertEquals(parsed, result.getOrThrow().toString())
    }
}

private val logger = InterceptingLogging.logger {}
