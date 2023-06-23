package io.kabu.backend.r2c.tests

import io.kabu.backend.r2c.R2C_Management
import io.kabu.backend.r2c.R2C_Base
import io.kabu.backend.r2c.getDataPath
import io.kabu.backend.r2c.getManualPath
import org.junit.Ignore
import org.junit.Test
import org.junit.runners.Parameterized

private val TESTED_MANUALLY_PATH = getManualPath("assign")
private val FILEPATH = getDataPath("assign")

class AssignR2C_Test(raw: String, sample: String, termination: String) : R2C_Base(raw, sample, termination) {
    override val testedManuallyPath = TESTED_MANUALLY_PATH
    override val filepath = FILEPATH

    companion object {
        @JvmStatic
        @Parameterized.Parameters
        fun data() = load(FILEPATH)
    }
}

class AssignR2C_Management : R2C_Management(TESTED_MANUALLY_PATH, FILEPATH) {

    @[Ignore Test]
    fun addManuallyTested() = _addManuallyTested()

    @[Ignore Test]
    fun rewriteFile() = _rewriteFile()
}
