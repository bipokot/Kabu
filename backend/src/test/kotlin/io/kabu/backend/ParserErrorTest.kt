package io.kabu.backend

import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.parser.PatternParser
import io.kabu.backend.parser.PatternString
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized

// todo make specific error matching by line:column:symbol
@RunWith(Parameterized::class)
class ParserErrorTest(val raw: String, val error: String) : Assert() {

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = listOf(
            // mismatched indexing
            "a[" to "",
            "a]" to "",
            "a[]" to "",

            // mismatched call
            "a(" to "",
            "a)" to "",

            // infix
            "a infix1 infix2 b" to ""
//            "a infix1 infix2 b noinline" to "" // todo not filing because noinline is some sort of special ಠ_ಠ

        ).map { arrayOf(it.first, it.second) }
    }

    @Test
    fun testParsingAndTreeTransform_BlackBox() {
        logger.debug("Raw: $raw")
        val result = PatternParser(PatternString(raw)).parse()
        assertTrue(result.isFailure)
//        assertEquals(error, result.exceptionOrNull())
    }
}

private val logger = InterceptingLogging.logger {}
