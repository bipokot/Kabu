package io.kabu.frontend.ksp.processor

internal fun getKotlinTestFilePrefix(packageName: String) =
    KOTLIN_TEST_FILE_PREFIX.replace("package $DEFAULT_TEST_PACKAGE", "package $packageName")

internal fun getKotlinTestScriptFilePrefix(packageName: String) =
    KOTLIN_TEST_SCRIPT_FILE_PREFIX.replace("package $DEFAULT_TEST_PACKAGE", "package $packageName")

internal const val DEFAULT_TEST_PACKAGE = "tests"

internal const val KOTLIN_TEST_FILE_PREFIX =
    "\npackage " +
            DEFAULT_TEST_PACKAGE +
            "\n\n" +
            "import io.kabu.annotation.Pattern\n" +
            "import io.kabu.annotation.LocalPattern\n" +
            "import io.kabu.annotation.ContextCreator\n" +
            "import io.kabu.annotation.Context\n" +
            "// lines below are for future imports or other declarations to fix line numbers used in tests\n" +
            "\n\n\n\n" +
            "// end of common part\n"

internal const val KOTLIN_TEST_FILE_SUFFIX = ""

internal const val KOTLIN_TEST_SCRIPT_FILE_PREFIX =
    "\npackage " +
            DEFAULT_TEST_PACKAGE +
            "\n\n" +
            "import io.kabu.annotation.Pattern\n" +
            "import io.kabu.annotation.LocalPattern\n" +
            "import io.kabu.annotation.ContextCreator\n" +
            "import io.kabu.annotation.Context\n" +
            "// lines below are for future imports or other declarations to fix line numbers used in tests\n" +
            "\n\n\n\n" +
            "// end of common part\n" +
            "run {\n"

internal const val KOTLIN_TEST_SCRIPT_FILE_SUFFIX = "" +
        "}"
