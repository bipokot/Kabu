package io.kabu.backend.pattern

import com.squareup.kotlinpoet.ANY
import com.squareup.kotlinpoet.BOOLEAN
import com.squareup.kotlinpoet.INT
import com.squareup.kotlinpoet.LambdaTypeName
import com.squareup.kotlinpoet.STRING
import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.UNIT
import com.squareup.kotlinpoet.asTypeName
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.parameter.Parameter
import io.kabu.backend.util.Constants.RECEIVER_PARAMETER_NAME
import io.kabu.runtime.EqualityInfo
import io.kabu.runtime.InclusionInfo
import io.kabu.runtime.RankingComparisonInfo
import io.kabu.runtime.StrictnessComparisonInfo

class PatternWithSignature(input: String) {

    val pattern: String
    val signature: Signature

    init {
        val split = input.split(PATTERN_AND_SIGNATURE_SEPARATOR)
        if (split.size in 1..2) {
            pattern = split[0].trim()
            signature = parseShortParametersString(split.getOrNull(1) ?: "")
        } else error("Unable to parse ${PatternWithSignature::class.simpleName}: $input")
    }

    /**
     * `<typeCode>[numeric index][?]`
     */
    private fun parseShortParametersString(shortStringParameters: String): Signature {

        fun decode(shortCode: String): Parameter {
            var code = shortCode

            // nullability
            val nullable = code.endsWith('?')
            if (nullable) code = code.dropLast(1)
            val identifier = code

            // removing index at the end
            code = code.dropLastWhile { it in '0'..'9' }

            // type
            val typeName = typeCodeToTypeName(code).copy(nullable = nullable)
            return Parameter(identifier, typeName, Origin(excerpt = shortCode))
        }

        var str = shortStringParameters


        val receiverPart = str
            .substringBefore(RECEIVER_PART_SEPARATOR, missingDelimiterValue = "")
            .takeIf { it.isNotEmpty() }
        if (receiverPart != null) str = str.removePrefix(receiverPart).drop(1)
        val returnsPart = str
            .substringAfter(RETURN_PART_SEPARATOR, missingDelimiterValue = "")
            .takeIf { it.isNotEmpty() }
        if (returnsPart != null) str = str.removeSuffix(returnsPart).dropLast(1)
        val parametersPart = str

        val receiver = receiverPart?.trim()?.let(::decode)?.type
            ?.let { Parameter(RECEIVER_PARAMETER_NAME, it, Origin(excerpt = receiverPart)) }
        val params = parametersPart.trim().takeIf { it.isNotEmpty() }?.split(WHITESPACES)?.map(::decode) ?: emptyList()
        val returns = returnsPart?.trim()?.let(::decode)?.type ?: UNIT

        return Signature(
            receiver = receiver,
            parameters = params,
            returnedType = returns
        )
    }

    private fun typeCodeToTypeName(typeCode: String): TypeName =
        typeCodeToTypeName[typeCode] ?: error("Unknown type code: $typeCode")

    private companion object {
        private val WHITESPACES = Regex("\\s")
        private const val PATTERN_AND_SIGNATURE_SEPARATOR = "//"
        private const val RECEIVER_PART_SEPARATOR = '.'
        private const val RETURN_PART_SEPARATOR = ':'

        private val typeCodeToTypeName = mapOf(
            "a" to ANY,
            "u" to UNIT,
            "i" to INT,
            "b" to BOOLEAN,
            "s" to STRING,
            "fu" to LambdaTypeName.get(returnType = UNIT),
            "fa" to LambdaTypeName.get(returnType = ANY),
            "fi" to LambdaTypeName.get(returnType = INT),
            "fb" to LambdaTypeName.get(returnType = BOOLEAN),
            "fs" to LambdaTypeName.get(returnType = STRING),
            "rank" to RankingComparisonInfo::class.asTypeName(),
            "strict" to StrictnessComparisonInfo::class.asTypeName(),
            "incl" to InclusionInfo::class.asTypeName(),
            "equal" to EqualityInfo::class.asTypeName(),
        )
    }
}

