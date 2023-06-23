package io.kabu.backend.parser

/**
 * Defines a type which has to return a translation callable (e.g. function, getter)
 */
enum class FunctionMustReturn {

    /**
     * No restrictions on what to return
     */
    FREE,

    /**
     * Must return a value of type, assignable to some usage-defined type (e.g. for increment/decrement operators)
     */
    ASSIGNABLE,

    /**
     * Must return a Boolean
     */
    BOOLEAN,

    /**
     * Must return an Int
     */
    INT,

    /**
     * Must return a Unit
     */
    UNIT,
}
