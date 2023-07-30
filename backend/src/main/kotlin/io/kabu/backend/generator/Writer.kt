package io.kabu.backend.generator

import com.squareup.kotlinpoet.AnnotationSpec
import com.squareup.kotlinpoet.CodeBlock
import com.squareup.kotlinpoet.FileSpec
import com.squareup.kotlinpoet.TypeSpec
import io.kabu.backend.declaration.AbstractTypeDeclaration
import io.kabu.backend.declaration.Declaration
import io.kabu.backend.declaration.functions.AbstractFunctionDeclaration
import io.kabu.backend.declaration.objects.AccessorObjectDeclaration
import io.kabu.backend.declaration.properties.AbstractPropertyDeclaration
import io.kabu.backend.util.Constants.RUNTIME_PACKAGE

class Writer(private val testMode: Boolean = false) {

    fun composeFileForDeclarations(packageName: String, declarations: List<Declaration>): String {
        val fileSpec = buildFileSpec(packageName, declarations)
        return removePublicModifier(fileSpec.toString())
    }

    private fun removePublicModifier(source: String): String {
        if (!testMode) return source

        var result = source.replace(operatorFunRegex, "operator")
        result = result.replace(infixFunRegex, "infix")
        result = result.replace(classRegex, "class")
        result = result.replace(objectRegex, "object")
        result = result.replace(valRegex, "val")
        return result
    }

    private fun buildFileSpec(packageName: String, declarations: List<Declaration>): FileSpec {
        return FileSpec.builder(packageName, "dummy").apply {
            addImport(RUNTIME_PACKAGE, listOf())
            addAnnotation(
                AnnotationSpec.builder(Suppress::class)
                    .useSiteTarget(AnnotationSpec.UseSiteTarget.FILE)
                    .apply { suppressList.forEach { addMember(CodeBlock.of("%S", it)) } }
                    .build()

            )
            addDeclarations(declarations)
        }.build()
    }

    private companion object {
        val operatorFunRegex = Regex("public operator")
        val infixFunRegex = Regex("public infix")
        val classRegex = Regex("public class")
        val objectRegex = Regex("public object")
        val valRegex = Regex("public val")

        val suppressList = listOf(
            "ConstructorParameterNaming",
            "CyclomaticComplexMethod",
            "DEPRECATION",
            "EmptyDefaultConstructor",
            "FunctionName",
            "FunctionNaming",
            "FunctionOnlyReturningConstant",
            "MagicNumber",
            "MatchingDeclarationName",
            "MaxLineLength",
            "MemberVisibilityCanBePrivate",
            "NAME_SHADOWING",
            "NonAsciiCharacters",
            "ObjectPropertyName",
            "PropertyName",
            "RedundantVisibilityModifier",
            "RemoveRedundantQualifierName",
            "SpellCheckingInspection",
            "TestFunctionName",
            "TooManyFunctions",
            "TopLevelPropertyNaming",
            "UNCHECKED_CAST",
            "UnnecessaryVariable",
            "unused",
            "UNUSED_ANONYMOUS_PARAMETER",
            "UNUSED_PARAMETER",
            "UNUSED_VARIABLE",
            "UnusedImport",
            "UnusedReceiverParameter",
            "VariableNaming",
            "FunctionParameterNaming",
        )
    }
}

internal fun TypeSpec.Builder.addDeclarations(declarations: List<Declaration>) {
    declarations.forEach { declaration ->
        when (declaration) {
            is AbstractTypeDeclaration -> {
                if (declaration is AccessorObjectDeclaration) error("Access object can not be inside a type")
                addType(declaration.generateTypeSpec())
            }
            is AbstractPropertyDeclaration -> addProperty(declaration.generatePropertySpec())
            is AbstractFunctionDeclaration -> addFunction(declaration.generateFunSpec())
            else -> error("must not be here")
        }
    }
}

internal fun FileSpec.Builder.addDeclarations(declarations: List<Declaration>) {
    declarations.forEach { declaration ->
        when (declaration) {
            is AbstractTypeDeclaration -> {
                if (declaration is AccessorObjectDeclaration && declaration.className.packageName != packageName) {
                    addImport(declaration.className.packageName, declaration.className.simpleName)
                }
                addType(declaration.generateTypeSpec())
            }
            is AbstractPropertyDeclaration -> addProperty(declaration.generatePropertySpec())
            is AbstractFunctionDeclaration -> addFunction(declaration.generateFunSpec())
            else -> error("must not be here")
        }
    }
}
