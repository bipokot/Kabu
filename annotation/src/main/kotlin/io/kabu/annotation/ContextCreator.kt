package io.kabu.annotation

import kotlin.annotation.AnnotationRetention.SOURCE
import kotlin.annotation.AnnotationTarget.CONSTRUCTOR
import kotlin.annotation.AnnotationTarget.FUNCTION

@Retention(SOURCE)
@Target(FUNCTION, CONSTRUCTOR)
annotation class ContextCreator(
    val contextName: String
)
