package io.kabu.backend.planner.parameter.group

import io.kabu.backend.provider.group.renameClashingParametersNames
import org.junit.Assert
import org.junit.Test

class ParametersTest : Assert() {

    @Test
    fun testNoConflict() {
        val parameters = listOf("a", "b")
        val result = renameClashingParametersNames(parameters)

        assertEquals(listOf("a", "b"), result)
        assertAllUnique(result)
    }

    @Test
    fun testConflict() {
        val parameters = listOf("a", "a")
        val result = renameClashingParametersNames(parameters)

        assertEquals(listOf("a1", "a2"), result)
        assertAllUnique(result)
    }

    @Test
    fun testConflict2() {
        val parameters = listOf("a", "b", "a")
        val result = renameClashingParametersNames(parameters)

        assertEquals(listOf("a1", "b", "a2"), result)
        assertAllUnique(result)
    }

    @Test
    fun testInducedConflict() {
        val parameters = listOf("a", "a", "a1")
        val result = renameClashingParametersNames(parameters)

        assertEquals(listOf("a2", "a3", "a1"), result)
        assertAllUnique(result)
    }

    @Test
    fun testMultipleInducedConflicts() {
        val parameters = listOf("a", "a", "a1", "a1")
        val result = renameClashingParametersNames(parameters)

        assertEquals(listOf("a2", "a3", "a11", "a12"), result)
        assertAllUnique(result)
    }

    @Test
    fun testMultipleInducedConflicts2() {
        val parameters = listOf("a", "b", "a", "a1", "b", "b", "a1", "b2", "b3")
        val result = renameClashingParametersNames(parameters)

        assertEquals(listOf("a2", "b1", "a3", "a11", "b4", "b5", "a12", "b2", "b3"), result)
        assertAllUnique(result)
    }

    private fun assertAllUnique(result: List<String>) {
        assertTrue(result.groupBy { it }.all { it.value.size == 1 })
    }
}
