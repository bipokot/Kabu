package io.kabu.backend.processor

data class Options(
    val allowUnsafe: Boolean,
    val hideInternalProperties: Boolean,
    val accessorObjectIsInSamePackage: Boolean
) {

    companion object {

        fun fromPartial(partialOptions: PartialOptions): Options = Options(
            allowUnsafe = partialOptions.allowUnsafe ?: DEFAULT.allowUnsafe,
            hideInternalProperties = partialOptions.hideInternalProperties ?: DEFAULT.hideInternalProperties,
            accessorObjectIsInSamePackage = partialOptions.accessorObjectIsInSamePackage
                ?: DEFAULT.accessorObjectIsInSamePackage,

        )

        val DEFAULT = Options(
            allowUnsafe = false,
            hideInternalProperties = true,
            accessorObjectIsInSamePackage = true //todo false doesn't work
        )
    }
}

