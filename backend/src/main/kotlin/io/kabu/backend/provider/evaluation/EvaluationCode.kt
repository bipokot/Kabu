package io.kabu.backend.provider.evaluation


sealed class EvaluationCode {
    object MatchingIdentifier : EvaluationCode()
    class Code(val code: String) : EvaluationCode()
}
