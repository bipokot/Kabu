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
                    .addMember(CodeBlock.of("%S", "DEPRECATION"))
                    .addMember(CodeBlock.of("%S", "UnusedImport"))
                    .addMember(CodeBlock.of("%S", "unused"))
                    .addMember(CodeBlock.of("%S", "UNUSED_PARAMETER"))
                    .addMember(CodeBlock.of("%S", "UNUSED_VARIABLE"))
                    .addMember(CodeBlock.of("%S", "NonAsciiCharacters"))
                    .addMember(CodeBlock.of("%S", "MatchingDeclarationName"))
                    .addMember(CodeBlock.of("%S", "UnnecessaryVariable"))
                    .addMember(CodeBlock.of("%S", "RemoveRedundantQualifierName"))
                    .addMember(CodeBlock.of("%S", "VariableNaming"))
                    .addMember(CodeBlock.of("%S", "ConstructorParameterNaming"))
                    .addMember(CodeBlock.of("%S", "TestFunctionName"))
                    .addMember(CodeBlock.of("%S", "TopLevelPropertyNaming"))
                    .addMember(CodeBlock.of("%S", "NAME_SHADOWING"))
                    .addMember(CodeBlock.of("%S", "UNUSED_ANONYMOUS_PARAMETER"))
                    .addMember(CodeBlock.of("%S", "TooManyFunctions"))
                    .addMember(CodeBlock.of("%S", "UnusedReceiverParameter"))
                    .addMember(CodeBlock.of("%S", "CyclomaticComplexMethod"))
                    .addMember(CodeBlock.of("%S", "MaxLineLength"))
                    .addMember(CodeBlock.of("%S", "PropertyName"))
                    .addMember(CodeBlock.of("%S", "FunctionOnlyReturningConstant"))
                    .addMember(CodeBlock.of("%S", "MagicNumber"))
                    .addMember(CodeBlock.of("%S", "SpellCheckingInspection"))
                    .addMember(CodeBlock.of("%S", "FunctionNaming"))
                    .addMember(CodeBlock.of("%S", "FunctionName"))
                    .addMember(CodeBlock.of("%S", "ObjectPropertyName"))
                    .addMember(CodeBlock.of("%S", "MemberVisibilityCanBePrivate"))
                    .addMember(CodeBlock.of("%S", "UNCHECKED_CAST"))
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
