package io.kabu.backend.legacy

import io.kabu.backend.util.Constants
import io.kabu.backend.util.IdentifierGenerator


object AccessorObjectNameGenerator {

    fun generateName(): String =
        IdentifierGenerator.generateRandomIdentifierString(Constants.ACCESSOR_OBJECT_NAME_LENGTH)
}
