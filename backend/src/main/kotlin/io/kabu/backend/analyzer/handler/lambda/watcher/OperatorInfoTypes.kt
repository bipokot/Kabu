package io.kabu.backend.analyzer.handler.lambda.watcher

import com.squareup.kotlinpoet.ClassName
import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.util.Constants.ANNOTATIONS_PACKAGE

object OperatorInfoTypes { //todo can we get them from KClass directly?

    /** see [io.kabu.annotations.EqualityInfo] */
    val EQUALITY_INFO_TYPE = ClassName(ANNOTATIONS_PACKAGE, "EqualityInfo")
    
    /** see [io.kabu.annotations.InclusionInfo] */
    val INCLUSION_INFO_TYPE = ClassName(ANNOTATIONS_PACKAGE, "InclusionInfo")

    /** see [io.kabu.annotations.RankingComparisonInfo] */
    val RANKING_COMPARISON_INFO_TYPE = ClassName(ANNOTATIONS_PACKAGE, "RankingComparisonInfo")

    /** see [io.kabu.annotations.StrictnessComparisonInfo] */
    val STRICTNESS_COMPARISON_INFO_TYPE = ClassName(ANNOTATIONS_PACKAGE, "StrictnessComparisonInfo")

    val TypeName.isOperatorInfoType get() = this in operatorInfoTypes

    private val operatorInfoTypes = listOf(
        EQUALITY_INFO_TYPE,
        INCLUSION_INFO_TYPE,
        RANKING_COMPARISON_INFO_TYPE,
        STRICTNESS_COMPARISON_INFO_TYPE,
    )
}
