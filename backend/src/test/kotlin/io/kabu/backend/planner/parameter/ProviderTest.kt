package io.kabu.backend.planner.parameter

import io.kabu.backend.provider.provider.Provider
import org.junit.Assert
import org.junit.Test

class ProviderTest : Assert() {

    val provider = TestHolderProvider(
        "h1",
        listOf(
            TestHolderProvider(
                "h2",
                listOf(
                    TestLambdaProvider("L1", TestArgumentProvider("a1")),
                    TestEmptyProvider("e1")
                )
            ),
            TestHolderProvider(
                "h3",
                listOf(
                    TestHolderProvider(
                        "h4",
                        listOf(
                            TestHolderProvider(
                                "h5",
                                listOf(
                                    TestHolderProvider(
                                        "h6",
                                        listOf(TestArgumentProvider("a2"))
                                    ),
                                    TestHolderProvider(
                                        "h7",
                                        listOf(TestArgumentProvider("a3"))
                                    )
                                )
                            )
                        )
                    )
                )
            )
        ),
    )

    @Test
    fun testFind() {
        val a3provider = findProvider(provider, "a3")
        assertEquals("Argument(a3)", a3provider.toString())
    }

    @Test
    fun testHasProviderRecursively() {
        val a3provider = findProvider(provider, "a3")
        assertTrue(provider.hasProviderRecursively(a3provider))
    }

    @Test
    fun testGetPathToProvider() {
        val a3provider = findProvider(provider, "a3")
        val path = provider.getPathToProvider(a3provider)
        assertEquals(findProviders(provider, "h1", "h3", "h4", "h5", "h7", "a3"), path)
    }
    @Test
    fun testGetPathToProvider2() {
        val e1 = findProvider(provider, "e1")
        val path = provider.getPathToProvider(e1)
        assertEquals(findProviders(provider, "h1", "h2", "e1"), path)
    }

    private fun findProvider(provider: Provider, providerName: String): Provider {
        return provider.findProvider { it.generateName() == providerName }!!
    }

    private fun findProviders(provider: Provider, vararg providerNames: String): List<Provider> {
        return providerNames.mapNotNull { name ->
            provider.findProvider { it.generateName() == name }
        }
    }
}
