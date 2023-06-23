package io.kabu.backend.plannerx

import org.junit.Test

class TerminalFunctionAnalyzerXTest : AnalyzerXTest() {

    @Test
    fun test1() {
        """
            flowchart
            class__String["class String\n"]
            class__Int["class Int\n"]
            pkg__io.kabu.backend.plannerx["pkg io.kabu.backend.plannerx\n"]
            fun_io_kabu_backend_plannerx_times(["fun times\n- String\n- Int\n: Unit"])
            fun_io_kabu_backend_plannerx_times -.-> pkg__io.kabu.backend.plannerx
        """.trimIndent() assertEq getDiagramOfAnalyzedPattern("s * i // s i")
    }
}
