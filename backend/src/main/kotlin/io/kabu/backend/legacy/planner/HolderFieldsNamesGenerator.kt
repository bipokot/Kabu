package io.kabu.backend.legacy.planner

import io.kabu.backend.integration.NameAndType
import io.kabu.backend.integration.NamedTypeNode

/**
 *      A   B   C
 *      F1  F2  F3
 */
object HolderFieldsNamesGenerator {

    fun getNameForIndex(index: Int): String = "f${index + 1}"

    fun rename(list: List<NameAndType>): List<NameAndType> {
        return list.mapIndexed { index, item ->
            NamedTypeNode(getNameForIndex(index), item.typeNode)
        }
    }
}

