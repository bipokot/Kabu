package io.kabu.backend.analyzer.handler.lambda.watcher

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.util.Constants.RUNTIME_PACKAGE

object OperatorInfoTypes { //todo can we get them from KClass directly?

    /** see [io.kabu.runtime.EqualityInfo] */
    val EQUALITY_INFO_TYPE = ClassName(RUNTIME_PACKAGE, "EqualityInfo")
    
    /** see [io.kabu.runtime.InclusionInfo] */
    val INCLUSION_INFO_TYPE = ClassName(RUNTIME_PACKAGE, "InclusionInfo")

    /** see [io.kabu.runtime.RankingComparisonInfo] */
    val RANKING_COMPARISON_INFO_TYPE = ClassName(RUNTIME_PACKAGE, "RankingComparisonInfo")

    /** see [io.kabu.runtime.StrictnessComparisonInfo] */
    val STRICTNESS_COMPARISON_INFO_TYPE = ClassName(RUNTIME_PACKAGE, "StrictnessComparisonInfo")

    val TypeName.isOperatorInfoType get() = this in operatorInfoTypes

    private val operatorInfoTypes = listOf(
        EQUALITY_INFO_TYPE,
        INCLUSION_INFO_TYPE,
        RANKING_COMPARISON_INFO_TYPE,
        STRICTNESS_COMPARISON_INFO_TYPE,
    )
}
