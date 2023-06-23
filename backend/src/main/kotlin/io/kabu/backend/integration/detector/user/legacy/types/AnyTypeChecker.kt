package io.kabu.backend.integration.detector.user.legacy.types

import io.kabu.backend.integration.detector.user.legacy.OperatorDeclarationRequest
import io.kabu.backend.integration.detector.user.legacy.TypeConflictChecker


class AnyTypeChecker : TypeConflictChecker(Any::class) {

    override fun check(request: OperatorDeclarationRequest) {
        super.check(request)

        //todo 'to' infix extension function
//        if (request.operator is InfixFunction && request.operator.symbol == "to") {
//            ownTypeConflict(request)
//        }
    }
}
