package io.kabu.annotations

import org.intellij.lang.annotations.Language
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(SOURCE)
@Target(FUNCTION)
annotation class LocalPattern(
    @Language("kotlin", prefix = "fun expression() { ", suffix = " }")
    val pattern: String
)
