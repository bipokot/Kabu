package io.kabu.backend.integration.detector.user.legacy

import io.kabu.backend.common.log.InterceptingLogging

abstract class ConflictChecker {

    internal abstract fun cache()

    abstract fun isApplicableTo(request: OperatorDeclarationRequest): Boolean

    abstract fun check(request: OperatorDeclarationRequest)

    protected fun standardExtensionConflict(request: OperatorDeclarationRequest) {
        logger.warn(
            "Operator ${request.operator} can be resolved ambiguously (Kotlin stdlib extension exists)",
            request.operator
        )
    }

    private companion object {
        val logger = InterceptingLogging.logger {}
    }
}
