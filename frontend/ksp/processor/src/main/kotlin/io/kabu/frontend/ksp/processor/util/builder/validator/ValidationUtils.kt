package io.kabu.frontend.ksp.processor.util.builder.validator

import com.google.devtools.ksp.getVisibility
import com.google.devtools.ksp.symbol.ClassKind
import com.google.devtools.ksp.symbol.FunctionKind
import com.google.devtools.ksp.symbol.KSClassDeclaration
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSFunctionDeclaration
import com.google.devtools.ksp.symbol.KSModifierListOwner
import com.google.devtools.ksp.symbol.Modifier
import com.google.devtools.ksp.symbol.Visibility
import io.kabu.frontend.ksp.processor.util.areNotSupported
import io.kabu.frontend.ksp.processor.util.isNotSupported
import java.util.*


internal fun notSupportedFunctionModifiers(function: KSModifierListOwner, role: String, vararg modifiers: Modifier) {
    modifiers.forEach { modifier ->
        areNotSupported(modifier in function.modifiers, function) {
            "${modifier.name.makeReadable()} functions as $role"
        }
    }
}

internal fun notSupportedClassModifiers(function: KSModifierListOwner, role: String, vararg modifiers: Modifier) {
    modifiers.forEach { modifier ->
        areNotSupported(modifier in function.modifiers, function) {
            "${modifier.name.makeReadable()} classes as $role"
        }
    }
}

internal fun notSupportedFunctionVisibilities(function: KSDeclaration, role: String, vararg visibilities: Visibility) {
    val functionVisibility = function.getVisibility()
    visibilities.forEach { visibility ->
        areNotSupported(visibility == functionVisibility, function) {
            "${visibility.name.makeReadable()} functions as $role"
        }
    }
}

internal fun notSupportedClassVisibilities(function: KSDeclaration, role: String, vararg visibilities: Visibility) {
    val functionVisibility = function.getVisibility()
    visibilities.forEach { visibility ->
        areNotSupported(visibility == functionVisibility, function) {
            "${visibility.name.makeReadable()} classes as $role"
        }
    }
}

internal fun notSupportedFunctionKinds(function: KSFunctionDeclaration, role: String, vararg kinds: FunctionKind) {
    val functionKind = function.functionKind
    kinds.forEach { kind ->
        areNotSupported(kind == functionKind, function) {
            "${kind.name.makeReadable()} functions as $role"
        }
    }
}

internal fun notSupportedClassKinds(classDeclaration: KSClassDeclaration, role: String, vararg classKinds: ClassKind) {
    val kindOfClass = classDeclaration.classKind
    classKinds.forEach { classKind ->
        isNotSupported(classKind == kindOfClass, classDeclaration) {
            "${classKind.name.makeReadable()} as $role"
        }
    }
}

private fun String.makeReadable() = lowercase().maybeCapitalize().replace('_', ' ')

private fun String.maybeCapitalize() =
    replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }


