package io.kabu.backend.processor

data class PartialOptions(
    val allowUnsafe: Boolean? = null,
    val hideInternalProperties: Boolean? = null,
    val accessorObjectIsInSamePackage: Boolean? = null,
)
