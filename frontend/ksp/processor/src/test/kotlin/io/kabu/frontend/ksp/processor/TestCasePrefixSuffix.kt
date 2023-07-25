package io.kabu.frontend.ksp.processor

internal const val KOTLIN_TEST_FILE_PREFIX =
    """
            package tests
            
            import io.kabu.annotation.Pattern 
            import io.kabu.annotation.LocalPattern 
            import io.kabu.annotation.ContextCreator
            import io.kabu.annotation.Context
            // lines below are for future imports or other declarations to fix line numbers used in tests            
            
            
            
            
            // end of common part
            """

internal const val KOTLIN_TEST_FILE_SUFFIX = ""

internal const val KOTLIN_TEST_SCRIPT_FILE_PREFIX =
    """
            package tests

            import io.kabu.annotation.Pattern
            import io.kabu.annotation.LocalPattern
            import io.kabu.annotation.ContextCreator
            // lines below are for future imports or other declarations to fix line numbers used in tests




            // end of common part
            run {
            """
internal const val KOTLIN_TEST_SCRIPT_FILE_SUFFIX = "" +
        "}"
