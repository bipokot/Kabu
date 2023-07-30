package io.kabu.backend.integration.detector.user.legacy.types

import com.squareup.kotlinpoet.LambdaTypeName
import io.kabu.backend.integration.detector.user.legacy.OperatorDeclarationRequest
import io.kabu.backend.integration.detector.user.legacy.TypeConflictChecker
import io.kabu.backend.parser.Call


class LambdaTypeChecker : TypeConflictChecker(Function::class) {

    override fun isApplicableTo(request: OperatorDeclarationRequest): Boolean {
        return request.rawProviders.left.type is LambdaTypeName
    }

    override fun check(request: OperatorDeclarationRequest) {
        super.check(request)

        // forbid  '{ ... }()' patterns
        if (request.operator is Call && request.rawProviders.right.isEmpty()) {
            ownTypeConflict(request, request.operator.overriding.function!!)
        }
    }
}
