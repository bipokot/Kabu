package io.kabu.backend

import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.parser.PatternParser
import io.kabu.backend.parser.PatternString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

@RunWith(Parameterized::class)
class OriginParserTestParameterized(val raw: String, val excerptsString: String) : Assert() {

    //todo add more tests with mixed precedence operators
    @Suppress("MaxLineLength")
    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            // base
            "a" to "'a'",

            // postfix
            "a++" to "'a++'('a' '++')",
            "a++--++" to "'a++--++'('a++--'('a++'('a' '++') '--') '++')",
            "a-- ()++(d ) " to "'a-- ()++(d )'('a-- ()++'('a-- ()'('a--'('a' '--') '()' []) '++') '(d )' ['d'])",

            // prefix
            "- d" to "'- d'('-' 'd')",
            "-+-+-+d" to "'-+-+-+d'('-' '+-+-+d'('+' '-+-+d'('-' '+-+d'('+' '-+d'('-' '+d'('+' 'd'))))))",
            "++--++d" to "'++--++d'('++' '--++d'('--' '++d'('++' 'd')))",
            "++--d" to "'++--d'('++' '--d'('--' 'd'))",

            // multiplicative
            "a * b /c" to "'a * b /c'('a * b'('a' '*' 'b') '/' 'c')",

            // additive
            "a - d" to "'a - d'('a' '-' 'd')",
            "a + b + c+d" to "'a + b + c+d'('a + b + c'('a + b'('a' '+' 'b') '+' 'c') '+' 'd')",

            // range
            "a..b" to "'a..b'('a' '..' 'b')",
            "a.. b ..c" to "'a.. b ..c'('a.. b'('a' '..' 'b') '..' 'c')",
            "-a.. b *c" to "'-a.. b *c'('-a'('-' 'a') '..' 'b *c'('b' '*' 'c'))",
            "a..<b" to "'a..<b'('a' '..<' 'b')",
            "a..<b ..< c" to "'a..<b ..< c'('a..<b'('a' '..<' 'b') '..<' 'c')",
            "a..<b .. c" to "'a..<b .. c'('a..<b'('a' '..<' 'b') '..' 'c')",
            "a.. b ..< c..<d" to "'a.. b ..< c..<d'('a.. b ..< c'('a.. b'('a' '..' 'b') '..<' 'c') '..<' 'd')",

            // infix
            "a b c" to "'a b c'('a' 'b' 'c')",
            "a b c d e" to "'a b c d e'('a b c'('a' 'b' 'c') 'd' 'e')",
            "a * c d e" to "'a * c d e'('a * c'('a' '*' 'c') 'd' 'e')",

            // named check
            "a in b" to "'a in b'('a' 'in' 'b')",
            "a in b in c" to "'a in b in c'('a in b'('a' 'in' 'b') 'in' 'c')",
            "a !in    b !in c" to "'a !in    b !in c'('a !in    b'('a' '!in' 'b') '!in' 'c')",

            // comparison
            "a > b * c < d" to "'a > b * c < d'('a > b * c'('a' '>' 'b * c'('b' '*' 'c')) '<' 'd')",
            "a >= b++ <= -c" to "'a >= b++ <= -c'('a >= b++'('a' '>=' 'b++'('b' '++')) '<=' '-c'('-' 'c'))",

            // equality
            "a == b++ * d == c" to "'a == b++ * d == c'('a == b++ * d'('a' '==' 'b++ * d'('b++'('b' '++') '*' 'd')) '==' 'c')",
            "a != b != c" to "'a != b != c'('a != b'('a' '!=' 'b') '!=' 'c')",

            // operator and assign
            "a /= b++ * { d += c }" to "'a /= b++ * { d += c }'('a' '/=' 'b++ * { d += c }'('b++'('b' '++') '*' '{ d += c }'(lambda ['d += c'('d' '+=' 'c')])))",
            "a = b + { d = c }" to "'a = b + { d = c }'('a' '=' 'b + { d = c }'('b' '+' '{ d = c }'(lambda ['d = c'('d' '=' 'c')])))",

            // lambda
            "!{a}" to "'!{a}'('!' '{a}'(lambda ['a']))",
            "a + !{ b * x}" to "'a + !{ b * x}'('a' '+' '!{ b * x}'('!' '{ b * x}'(lambda ['b * x'('b' '*' 'x')])))",

            // call
            "a ( b,c) " to "'a ( b,c)'('a' '( b,c)' ['b', 'c'])",
            "a (c)()" to "'a (c)()'('a (c)'('a' '(c)' ['c']) '()' [])",
            "a ( b,c)(d ) " to "'a ( b,c)(d )'('a ( b,c)'('a' '( b,c)' ['b', 'c']) '(d )' ['d'])",

            // indexing
            "a[b]" to "'a[b]'('a' '[b]' ['b'])",
            "a[ b] [c,d ]" to "'a[ b] [c,d ]'('a[ b]'('a' '[ b]' ['b']) '[c,d ]' ['c', 'd'])",

            // access
            "a.b" to "'a.b'('a' '.' 'b')",
            "a. b .c" to "'a. b .c'('a. b'('a' '.' 'b') '.' 'c')",

            // nested binary

            // mixed
            "++a - d" to "'++a - d'('++a'('++' 'a') '-' 'd')",
            "++--a - d" to "'++--a - d'('++--a'('++' '--a'('--' 'a')) '-' 'd')",
            "++--a - ++d +c" to "'++--a - ++d +c'('++--a - ++d'('++--a'('++' '--a'('--' 'a')) '-' '++d'('++' 'd')) '+' 'c')",
            "++--a - ++d" to "'++--a - ++d'('++--a'('++' '--a'('--' 'a')) '-' '++d'('++' 'd'))",
            "a++-- - +b" to "'a++-- - +b'('a++--'('a++'('a' '++') '--') '-' '+b'('+' 'b'))",
            "a++()" to "'a++()'('a++'('a' '++') '()' [])",
            "a++( b) [c,d ]--" to "'a++( b) [c,d ]--'('a++( b) [c,d ]'('a++( b)'('a++'('a' '++') '( b)' ['b']) '[c,d ]' ['c', 'd']) '--')",
            "a( ++b()--)[+-d ]" to "'a( ++b()--)[+-d ]'('a( ++b()--)'('a' '( ++b()--)' ['++b()--'('++' 'b()--'('b()'('b' '()' []) '--'))]) '[+-d ]' ['+-d'('+' '-d'('-' 'd'))])",
            "a-- * (b- --c[d] + e())" to "'a-- * (b- --c[d] + e()'('a--'('a' '--') '*' 'b- --c[d] + e()'('b- --c[d]'('b' '-' '--c[d]'('--' 'c[d]'('c' '[d]' ['d']))) '+' 'e()'('e' '()' [])))",
            "-a. b() .c ++" to "'-a. b() .c ++'('-' 'a. b() .c ++'('a. b() .c'('a. b()'('a. b'('a' '.' 'b') '()' []) '.' 'c') '++'))",

            // lambda annotations

        ).map { arrayOf(it.first, it.second) }
    }

    @Test
    fun testParsingAndTreeTransform_BlackBox() {
        logger.debug("Raw: $raw")
        val result = PatternParser(PatternString(raw)).parse()
        assertTrue(result.isSuccess)
        assertEquals(excerptsString, result.getOrThrow().toExcerptsString())
    }
}

private val logger = InterceptingLogging.logger {}
