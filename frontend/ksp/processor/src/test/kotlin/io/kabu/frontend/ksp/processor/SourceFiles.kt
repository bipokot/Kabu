package io.kabu.frontend.ksp.processor

import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language

fun createKotlinFile(
    @Language("kotlin", prefix = KOTLIN_TEST_FILE_PREFIX, suffix = KOTLIN_TEST_FILE_SUFFIX) code: String,
): SourceFile {
    return SourceFile.kotlin("testfile.kt", KOTLIN_TEST_FILE_PREFIX + code + KOTLIN_TEST_FILE_SUFFIX)
}

fun emptyJavaSourceFile() = SourceFile.java("Empty.java", "public class Empty {}")
