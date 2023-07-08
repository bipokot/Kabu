package io.kabu.backend.planner.namespace.conflict.detector.tests

import io.kabu.backend.planner.namespace.conflict.detector.BaseConflictDetectorTest
import io.kabu.backend.planner.namespace.conflict.detector.ConflictDetectorTestManagement
import io.kabu.backend.planner.namespace.conflict.detector.getDataPath
import io.kabu.backend.planner.namespace.conflict.detector.getManualPath
import org.junit.Ignore
import org.junit.Test
import org.junit.runners.Parameterized

private val TESTED_MANUALLY_PATH = getManualPath("string")
private val FILEPATH = getDataPath("string")

@Ignore
class StringConflictDetectorTest(
    raw: String,
    outcome: String,
    origin: String,
) : BaseConflictDetectorTest(raw, outcome, origin) {
    override val testedManuallyPath = TESTED_MANUALLY_PATH
    override val filepath = FILEPATH

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = load(FILEPATH)
    }
}

class StringConflictDetectorTestManagement : ConflictDetectorTestManagement(TESTED_MANUALLY_PATH, FILEPATH) {

    @[Ignore Test]
    fun addManuallyTested() = _addManuallyTested()

    @[Ignore Test]
    fun rewriteFile() = _rewriteFile()
}
