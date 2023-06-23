package io.kabu.backend.integration.detector.user.legacy

import io.kabu.backend.parser.Operator
import io.kabu.backend.provider.group.RawProviders


interface OperatorDeclarationRequest {
    val operator: Operator
    val rawProviders: RawProviders
}
