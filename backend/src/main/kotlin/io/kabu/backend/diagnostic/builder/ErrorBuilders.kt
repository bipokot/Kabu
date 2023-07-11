@file:Suppress("TooManyFunctions")
package io.kabu.backend.diagnostic.builder

import io.kabu.backend.diagnostic.Origin
import io.kabu.backend.diagnostic.diagnosticError
import io.kabu.backend.exception.PatternParsingException
import io.kabu.backend.inout.input.method.PatternMethod
import io.kabu.backend.parameter.Parameter
import io.kabu.backend.parser.Annotation
import io.kabu.backend.parser.KotlinExpression
import io.kabu.backend.parser.LambdaExpression
import io.kabu.backend.parser.NaryExpression
import io.kabu.backend.parser.Operator
import io.kabu.backend.util.Constants.EXTENSION_ANNOTATION
import io.kabu.backend.util.Constants.EXTENSION_ANNOTATION_CONTEXT_CREATOR
import io.kabu.backend.util.Constants.EXTENSION_ANNOTATION_CONTEXT_PARAMETER
import io.kabu.backend.util.Constants.RECEIVER_PARAMETER_NAME


internal fun strictParametersOrderingError(expressionName: String, expectedParameter: Parameter): Nothing {
    diagnosticError(
        "Strict method parameters ordering failed on '$expressionName'. Expected '${expectedParameter.name}'",
        expectedParameter.origin
    )
}

internal fun contextCreatorParameterNameError(expression: KotlinExpression): Nothing {
    diagnosticError(
        "Context creator parameter must have an identifier compatible syntax: $expression",
        expression.origin
    )
}

internal fun contextCreatorSyntaxError(contextCreatorExpression: NaryExpression): Nothing {
    diagnosticError(
        "Context creator must have '<CONTEXT_NAME>(<PARAMETERS>)' syntax",
        contextCreatorExpression.operand
    )
}

internal fun contextCreatorSyntaxError(annotation: Annotation): Nothing {
    diagnosticError("Context creator must have '<CONTEXT_NAME>(<PARAMETERS>)' syntax: '$annotation'")
}

internal fun contextCreatorMissingError(annotation: Annotation): Nothing {
    diagnosticError(
        "Context creator is not set: '$annotation'. " +
            "Use '@$EXTENSION_ANNOTATION(…$EXTENSION_ANNOTATION_CONTEXT_CREATOR=<CONTEXT_NAME>(<PARAMETERS>)…)'"
    )
}

internal fun contextParameterMissingError(annotation: Annotation): Nothing {
    diagnosticError(
        "Context parameter is not set: '$annotation'. " +
            "Use '@$EXTENSION_ANNOTATION(…$EXTENSION_ANNOTATION_CONTEXT_PARAMETER=<PARAMETER>…)'"
    )
}

internal fun extensionAnnotationMissingError(expression: LambdaExpression): Nothing {
    diagnosticError(
        "Extension annotation not found. " +
            "Use '@$EXTENSION_ANNOTATION($EXTENSION_ANNOTATION_CONTEXT_PARAMETER=<NAME>, " +
            "$EXTENSION_ANNOTATION_CONTEXT_CREATOR=<CREATOR_NAME>(<PARAMETERS>))' syntax",
        expression.origin
    )
}

internal fun sameNamedParametersError(name: String, parameters: List<Parameter>): Nothing {
    diagnosticError("Function contains parameters with same name: '$name'", parameters.mapNotNull { it.origin })
}

internal fun signatureParameterMissingInPatternError(signatureParameter: Parameter): Nothing {
    diagnosticError(
        "Signature parameter $signatureParameter is missing in expression pattern",
        signatureParameter.origin
    )
}

internal fun signatureParameterMissingError(signatureParameter: Parameter): Nothing {
    diagnosticError(
        "Signature parameter $signatureParameter not found",
        signatureParameter.origin
    )
}

internal fun couldNotRetrieveReceiverValueError(patternMethod: PatternMethod): Nothing {
    diagnosticError(
        "Value for receiver can't be retrieved: " +
            "use '$RECEIVER_PARAMETER_NAME' parameter in expression or add any property to expression.",
        patternMethod
    )
}

internal fun patternParsingError(e: PatternParsingException): Nothing {
    diagnosticError("Pattern parsing failure: " + e.message.orEmpty(), e.origin)
}

internal fun unexpectedInvertedOrderingError(origins: List<Origin>): Nothing {
    diagnosticError("Inverted argument ordering is applicable with binary operator only", origins)
}

internal fun unknownFunctionParameterNameError(parameterName: String, patternMethod: PatternMethod): Nothing {
    diagnosticError("No function parameter with name '$parameterName' is defined", patternMethod)
}

internal fun parameterIsNotEvaluatedYetError(
    argumentName: String,
    availableParameters: List<String>,
    expression: KotlinExpression,
): Nothing {
    diagnosticError(
        "Property '$argumentName' is not accessible prior to evaluation of extension lambda. " +
            "Available parameter names: $availableParameters.",
        expression
    )
}

internal fun binaryOperatorRequiredForSetterError(origins: List<Origin>): Nothing {
    diagnosticError("Setter declaration require binary operator", origins)
}

internal fun watcherLambdaMissingError(operator: Operator): Nothing {
    diagnosticError(
        "Operator requires a watcher lambda but no watcher lambda context is accessible in its place",
        operator
    )
}

