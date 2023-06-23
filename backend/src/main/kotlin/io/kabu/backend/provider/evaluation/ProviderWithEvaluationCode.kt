package io.kabu.backend.provider.evaluation

import io.kabu.backend.provider.provider.Provider

data class ProviderWithEvaluationCode(
    val provider: Provider,
    val code: EvaluationCode,
)
