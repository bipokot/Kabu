package io.kabu.annotations

/**
 * Values to designate information obtained about actual operators used in runtime expression
 */
interface OperatorInfo

enum class EqualityInfo : OperatorInfo { EQUALS, NOT_EQUALS }

enum class InclusionInfo : OperatorInfo { IN, NOT_IN }

interface ComparisonOperatorInfo : OperatorInfo

enum class StrictnessComparisonInfo : ComparisonOperatorInfo {
    /**
     * Defines "<" and ">" operators
     */
    STRICT,

    /**
     * Defines "<=" and ">=" operators
     */
    RELAXED
}

enum class RankingComparisonInfo : ComparisonOperatorInfo {
    /**
     * Defines ">" and ">=" operators
     */
    GREATER,

    /**
     * Defines "<" and "<=" operators
     */
    LESS
}
