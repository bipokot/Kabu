package io.kabu.backend.provider.evaluation

import io.kabu.backend.provider.provider.Provider

data class ReplacementProviderWithCode(
    val provider: Provider,
    val code: String,
)
