package io.kabu.backend.plannerx

open class AnalyzerXTest : XTest() {

    protected fun getDiagramOfAnalyzedPattern(patternWithSignature: String): String {
        val nodes = analyzePatternWithSignature(patternWithSignature)
        return getDiagramOfNodes(nodes)
    }
}
