package io.kabu.frontend.ksp.processor.util

import com.google.devtools.ksp.symbol.FileLocation
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSDeclaration
import com.google.devtools.ksp.symbol.KSNode
import com.google.devtools.ksp.symbol.KSTypeArgument
import com.google.devtools.ksp.symbol.KSValueParameter
import io.kabu.backend.diagnostic.FileSourceLocation
import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.frontend.ksp.diagnostic.builder.notSupportedError

internal fun areNotSupported(clause: Boolean = true, ksNode: KSNode? = null, message: () -> String) {
    if (clause) {
        val messageString = message() + " aren't supported yet"

        if (ksNode != null) notSupportedError(messageString, originOf(ksNode))
        else diagnosticError(messageString)
    }
}

internal fun isNotSupported(clause: Boolean = true, ksNode: KSNode? = null, message: () -> String) {
    if (clause) {
        val messageString = message() + " isn't supported yet"

        if (ksNode != null) notSupportedError(messageString, originOf(ksNode))
        else diagnosticError(messageString)
    }
}

internal fun originOf(ksNode: KSNode, parent: Origin? = null): Origin {
    val excerpt = when (ksNode) {
        is KSDeclaration -> ksNode.simpleName.getShortName()
        is KSTypeArgument -> ksNode.toString()
        is KSValueParameter -> ksNode.name?.asString()
        else -> null
    }
    val fileLocation = ksNode.location as? FileLocation
    val sourceLocation = fileLocation?.let { FileSourceLocation(it.filePath, it.lineNumber) }
    return Origin(excerpt = excerpt, sourceLocation = sourceLocation, parent = parent)
}

internal inline fun <reified T> KSAnnotated.getAnnotation() =
    getAnnotationOrNull<T>()!!

internal inline fun <reified T> KSAnnotated.getAnnotationOrNull() =
    annotations.singleOrNull {
        val annotationQualifiedName = it.annotationType.resolve().declaration.qualifiedName?.asString()
        annotationQualifiedName == T::class.qualifiedName
    }
