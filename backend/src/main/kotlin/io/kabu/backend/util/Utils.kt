package io.kabu.backend.util

inline fun <T> ifTake(condition: Boolean, block: () -> T): T? = if (condition) block() else null

fun String?.spacedValue(): String = if (this != null) "$this " else " "

fun String.decaps() = replaceFirstChar(Char::lowercase)
