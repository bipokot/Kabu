package io.kabu.frontend.ksp.processor

import com.tschuchort.compiletesting.SourceFile
import org.intellij.lang.annotations.Language

fun createKotlinFiles(code: String): List<SourceFile> {
    return groupByPackageNames(code).map { createKotlinFile(it.value, it.key) }
}

fun groupByPackageNames(code: String): MutableMap<String, String> {
    val map = mutableMapOf<String, String>() // packageName -> contents

    fun addPackageContents(packageName: String, contents: MutableList<String>) {
        if (contents.all { it.isBlank() }) {
            contents.clear()
            return
        } else {
            map[packageName] = contents.joinToString("\n")
            contents.clear()
        }
    }

    var packageName = DEFAULT_TEST_PACKAGE
    val packageContents = mutableListOf<String>()
    for (line in code.lines()) {
        val matcher = PACKAGE_REGEX.matchEntire(line)
        if (matcher != null) {
            addPackageContents(packageName, packageContents)
            packageName = matcher.groupValues[1]
            continue
        }
        packageContents += line
    }
    addPackageContents(packageName, packageContents)

    return map
}

fun createKotlinFile(
    @Language("kotlin", prefix = KOTLIN_TEST_FILE_PREFIX, suffix = KOTLIN_TEST_FILE_SUFFIX) code: String,
    packageName: String,
): SourceFile {
    val packagePath = packageName.replace('.', '/')
    return SourceFile.kotlin(
        name = "$packagePath/testfile.kt",
        contents = getKotlinTestFilePrefix(packageName) + code + KOTLIN_TEST_FILE_SUFFIX
    )
}

fun emptyJavaSourceFile() = SourceFile.java("Empty.java", "public class Empty {}")

private val PACKAGE_REGEX = Regex("^\\s*//\\s*PACKAGE\\s*(\\S+)\\s*")
