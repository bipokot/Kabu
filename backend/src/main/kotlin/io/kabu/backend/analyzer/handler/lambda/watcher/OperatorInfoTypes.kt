package io.kabu.backend.analyzer.handler.lambda.watcher

import com.squareup.kotlinpoet.TypeName
import com.squareup.kotlinpoet.asTypeName
import io.kabu.runtime.EqualityInfo
import io.kabu.runtime.InclusionInfo
import io.kabu.runtime.RankingComparisonInfo
import io.kabu.runtime.StrictnessComparisonInfo

object OperatorInfoTypes {

    val TypeName.isOperatorInfoType get() = this in OPERATOR_INFO_TYPES

    private val OPERATOR_INFO_TYPES = listOf(
        EqualityInfo::class,
        InclusionInfo::class,
        RankingComparisonInfo::class,
        StrictnessComparisonInfo::class,
    ).map {
        it.asTypeName()
    }
}
