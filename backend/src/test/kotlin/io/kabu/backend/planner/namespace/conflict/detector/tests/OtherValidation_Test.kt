package io.kabu.backend.planner.namespace.conflict.detector.tests

import io.kabu.backend.planner.namespace.conflict.detector.ConflictDetector_Base
import io.kabu.backend.planner.namespace.conflict.detector.ConflictDetector_Management
import io.kabu.backend.planner.namespace.conflict.detector.getDataPath
import io.kabu.backend.planner.namespace.conflict.detector.getManualPath
import org.junit.Ignore
import org.junit.Test
import org.junit.runners.Parameterized

private val TESTED_MANUALLY_PATH = getManualPath("other")
private val FILEPATH = getDataPath("other")

//todo create its own test suite for tests not related to conflict detection?
@Ignore
class OtherValidation_Test(raw: String, outcome: String, origin: String) : ConflictDetector_Base(raw, outcome, origin) {
    override val testedManuallyPath = TESTED_MANUALLY_PATH
    override val filepath = FILEPATH

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = load(FILEPATH)
    }
}

class OtherValidation_Management : ConflictDetector_Management(TESTED_MANUALLY_PATH, FILEPATH) {

    @[Ignore Test]
    fun addManuallyTested() = _addManuallyTested()

    @[Ignore Test]
    fun rewriteFile() = _rewriteFile()
}
