package io.kabu.backend.analyzer.handler.lambda.extension

import io.kabu.backend.diagnostic.builder.contextCreatorMissingError
import io.kabu.backend.diagnostic.builder.contextCreatorParameterNameError
import io.kabu.backend.diagnostic.builder.contextCreatorSyntaxError
import io.kabu.backend.diagnostic.builder.contextParameterMissingError
import io.kabu.backend.parser.Annotation
import io.kabu.backend.parser.Call
import io.kabu.backend.parser.IdentifierLeaf
import io.kabu.backend.parser.NaryExpression
import io.kabu.backend.util.Constants.EXTENSION_ANNOTATION_CONTEXT_CREATOR
import io.kabu.backend.util.Constants.EXTENSION_ANNOTATION_CONTEXT_PARAMETER

data class ExtensionAnnotation(
    val destinationParameterName: String,
    val contextCreatorDefinition: ContextCreatorDefinition
) {

    companion object {

        fun from(annotation: Annotation): ExtensionAnnotation {
            val contextParameter = annotation.arguments.find { it.name == EXTENSION_ANNOTATION_CONTEXT_PARAMETER }
            val name = (contextParameter?.expression as? IdentifierLeaf)
                ?.name
                ?: contextParameterMissingError(annotation)

            val contextCreator = annotation.arguments.find { it.name == EXTENSION_ANNOTATION_CONTEXT_CREATOR }
            val contextCreatorExpression = (contextCreator?.expression)
                ?: contextCreatorMissingError(annotation)

            val contextCreatorNaryExpression = (contextCreatorExpression as? NaryExpression)
                ?: contextCreatorSyntaxError(annotation)

            if (contextCreatorNaryExpression.operator !is Call) {
                contextCreatorSyntaxError(contextCreatorNaryExpression)
            }

            val contextCreatorName = (contextCreatorNaryExpression.operand as? IdentifierLeaf)?.name
                ?: contextCreatorSyntaxError(contextCreatorNaryExpression)

            val contextCreatorArgumentNames = contextCreatorNaryExpression.arguments.map {
                (it as? IdentifierLeaf)?.name
                    ?: contextCreatorParameterNameError(it)
            }

            val contextCreatorDefinition = ContextCreatorDefinition(contextCreatorName, contextCreatorArgumentNames)
            return ExtensionAnnotation(name, contextCreatorDefinition)
        }
    }
}

