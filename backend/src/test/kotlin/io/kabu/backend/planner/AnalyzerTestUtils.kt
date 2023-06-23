package io.kabu.backend.planner

import com.squareup.kotlinpoet.TypeName
import io.kabu.backend.common.log.InterceptingLogging
import io.kabu.backend.parameter.EntryParameter
import io.kabu.backend.planner.AnalyzerTestUtils.logger
import io.kabu.backend.util.Constants


object AnalyzerTestUtils {
    const val filename = "Generated"
    const val TEST_GENERATED_DIR = "src/test/kotlin" // near this test

    const val clientMethod = "completion"
    const val targetPackage = "${Constants.BACKEND_PACKAGE}.planner"
    const val FILEPATH = "src/test/resources/io/kabu/backend/planner/plannerSuccessTest.json"
    val logger = InterceptingLogging.logger {}
}

fun parametersOf(vararg params: Pair<String, TypeName>): List<EntryParameter> {
    return params.map { EntryParameter(it.first, it.second) }
}

fun completion(vararg parameters: Any?) {
    logger.info { "completion: ${parameters.joinToString()}" }
}

fun Any?.completion(vararg parameters: Any?) {
    logger.info { "completion: $this; ${parameters.joinToString()}" }
}

