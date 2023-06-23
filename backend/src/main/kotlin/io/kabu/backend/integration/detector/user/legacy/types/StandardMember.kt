package io.kabu.backend.integration.detector.user.legacy.types

import io.kabu.backend.util.poet.SerializedType


interface StandardMember {
    val containingType: SerializedType
    val name: String
}

data class StandardMethod(
    override val containingType: SerializedType,
    override val name: String,
    val receiver: SerializedType?,
    val parameterTypes: List<SerializedType>,
) : StandardMember

data class StandardProperty(
    override val containingType: SerializedType,
    override val name: String,
) : StandardMember
