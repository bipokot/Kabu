package io.kabu.backend.integration.detector.user.legacy.detector

import io.kabu.backend.integration.detector.user.legacy.ConflictChecker
import io.kabu.backend.integration.detector.user.legacy.OperatorDeclarationRequest
import io.kabu.backend.integration.detector.user.legacy.TypeConflictChecker
import io.kabu.backend.integration.detector.user.legacy.types.AnyTypeChecker
import io.kabu.backend.integration.detector.user.legacy.types.LambdaTypeChecker
import io.kabu.backend.integration.detector.user.legacy.types.StringTypeChecker


object KotlinLangConflictDetector {

    private val checkers = listOf<ConflictChecker>(
        AnyTypeChecker(),
        StringTypeChecker(),
        LambdaTypeChecker(),
        TypeConflictChecker(Int::class),
        TypeConflictChecker(Boolean::class),
    )

    fun checkOperator(operatorDeclarationRequest: OperatorDeclarationRequest) {
        checkers
            //todo filter first by origin of operator's left parameter type:
            // if it is an internally created Holder class then check only for Any type
            .filter { it.isApplicableTo(operatorDeclarationRequest) }
            .forEach {
                it.check(operatorDeclarationRequest)
            }
    }

    fun cache() {
        checkers.forEach { it.cache() }
    }
}
