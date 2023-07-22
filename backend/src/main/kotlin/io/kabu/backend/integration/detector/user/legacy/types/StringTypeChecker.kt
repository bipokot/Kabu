package io.kabu.backend.integration.detector.user.legacy.types

import io.kabu.backend.integration.detector.user.legacy.OperatorDeclarationRequest
import io.kabu.backend.integration.detector.user.legacy.TypeConflictChecker
import io.kabu.backend.parser.Additive


class StringTypeChecker : TypeConflictChecker(String::class) {

    override fun check(request: OperatorDeclarationRequest) {
        super.check(request)

        if (request.operator is Additive.BinaryPlus) {
            val conflictingStandardMethod = functions.single { it.name == request.operator.overriding.function!! }
            ownTypeConflict(request, conflictingStandardMethod)
        }
    }
}
