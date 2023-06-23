package io.kabu.backend.declaration.util

import com.squareup.kotlinpoet.CodeBlock

sealed class PropertyAccessor {

    /**
     * getter/setter is forbidden:
     *  - setter will be absent
     *  - getter will cause a compile-time error
     */
    object Missing : PropertyAccessor()

    sealed class Existing : PropertyAccessor() {

        /**
         * Using implicit getter/setter implementation provided by Kotlin
         */
        object Default : PropertyAccessor()

        /**
         * Using explicit getter/setter implementation
         */
        data class Provided(val codeBlock: CodeBlock) : PropertyAccessor()
    }
}
