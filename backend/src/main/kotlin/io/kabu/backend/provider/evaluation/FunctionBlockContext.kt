package io.kabu.backend.provider.evaluation

import io.kabu.backend.provider.group.FunDeclarationProviders
import io.kabu.backend.provider.group.OrderedNamedProviders
import io.kabu.backend.provider.group.OrderedNamedProvidersProvider
import io.kabu.backend.provider.provider.EmptyProvider
import io.kabu.backend.provider.provider.Provider

class FunctionBlockContext(
    private val funDeclarationProviders: FunDeclarationProviders
) {
    private var localVarCounter = 0
    private var notEvaluatedYet = true

    private val _allStatements: MutableList<String> = mutableListOf()
    val allStatements: List<String>
        get() = _allStatements

    // all accessible providers
    private val allProviders = OrderedNamedProviders()
    val allProvidersProvider = OrderedNamedProvidersProvider(allProviders)

    // actual providers (current candidates to propagation)
    val actualProviders = OrderedNamedProviders()
    val actualProvidersProvider = OrderedNamedProvidersProvider(actualProviders)

    init {
        val orderedNamedProviders = funDeclarationProviders.providers
        orderedNamedProviders.providers.forEach {
            registerActualProvider(it, orderedNamedProviders[it], emptyList())
        }
    }

    fun registerActualProvider(provider: Provider, name: String, statements: List<String>) {
        _allStatements += statements
        actualProviders.register(provider, name)
        //todo mb losing non-actual providers names in allProviders...
        allProviders.register(provider, name)
    }

    //todo rn getCodeForProvider
    fun getCodeForActualProvider(provider: Provider): String {
        return actualProvidersProvider
            .getChildRetrievalWay(selfName = null, provider, actualProvidersProvider)!!.codeBlock.toString()
    }

    fun unregisterActualProvider(provider: Provider) {
        actualProviders.unregister(provider)
    }

    // order is preserved
    fun replaceActualProvider(replacedProvider: Provider, replaceBy: List<Provider>) {
        if (replaceBy.singleOrNull() == replacedProvider) return // skipping replacement for itself

        val actualProvidersBackup = OrderedNamedProviders().apply {
            actualProviders.providers
                .forEach { provider -> register(provider, actualProviders[provider]) }
        }

        val providerReplacements = replaceBy.map { getReplacement(it) }

        actualProviders.providers.forEach { actualProviders.unregister(it) }

        actualProvidersBackup.providers.forEach { provider ->
            if (provider !== replacedProvider) {
                // copying other providers
                registerActualProvider(provider, actualProvidersBackup[provider], emptyList())
            } else {
                // replacing target provider
                providerReplacements.forEach {
                    registerActualProvider(it.provider, it.name, listOf(it.statements))
                }
            }
        }
    }

    private fun getReplacement(provider: Provider): Replacement {
        val name = nextVarName()
        val code = getCodeForActualProvider(provider)
        val statements = "val $name = $code"
        return Replacement(provider, name, statements)
    }

    fun registerLocalVar(name: String) {
        allProviders.register(EmptyProvider(), name)
    }

    fun addStatements(statements: List<String>) {
        _allStatements += statements
    }

    fun nextVarName(): String {
        var res: String
        do {
            res = "v${localVarCounter++}"
        } while (nameIsAlreadyTaken(res))
        return res
    }

    fun getUnoccupiedLocalVarName(desiredName: String): String {
        if (!nameIsAlreadyTaken(desiredName)) return desiredName

        var res: String
        var counter = 2
        do {
            res = "$desiredName${counter++}"
        } while (nameIsAlreadyTaken(res))
        return res
    }

    fun doEvaluation(): List<Provider> {
        check(notEvaluatedYet) { "Can not perform evaluation twice" }; notEvaluatedYet = false
        EvaluationHelper.doEvaluation(funDeclarationProviders, codeBlockContext = this)
        return actualProviders.providers
    }

    fun joinAllStatements(): String {
        return _allStatements.let {
            if (it.isNotEmpty()) it.joinToString("\n") else ""
        }
    }

    private fun nameIsAlreadyTaken(name: String) = allProviders.getOrNull(name) != null

    private data class Replacement(
        val provider: Provider,
        val name: String,
        val statements: String,
    )
}

