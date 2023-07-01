package io.kabu.annotation

import org.intellij.lang.annotations.Language
import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(SOURCE)
@Target(FUNCTION)
annotation class Pattern(
    @Language("kotlin", prefix = "fun expression() { ", suffix = " }")
    val pattern: String
)
