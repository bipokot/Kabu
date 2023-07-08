package io.kabu.backend.r2c.tests

import io.kabu.backend.r2c.PatternTestManagement
import io.kabu.backend.r2c.BasePatternTest
import io.kabu.backend.r2c.getDataPath
import io.kabu.backend.r2c.getManualPath
import org.junit.Ignore
import org.junit.Test
import org.junit.runners.Parameterized

private val TESTED_MANUALLY_PATH = getManualPath("complexUserTypes")
private val FILEPATH = getDataPath("complexUserTypes")

class ComplexUserTypesPatternTest(raw: String, sample: String, termination: String) :
    BasePatternTest(raw, sample, termination) {

    override val testedManuallyPath = TESTED_MANUALLY_PATH
    override val filepath = FILEPATH

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = load(FILEPATH)
    }
}

class ComplexUserTypesManagement : PatternTestManagement(TESTED_MANUALLY_PATH, FILEPATH) {

    @[Ignore Test]
    fun addManuallyTested() = _addManuallyTested()

    @[Ignore Test]
    fun rewriteFile() = _rewriteFile()
}
