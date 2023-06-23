package io.kabu.backend.provider.evaluation

enum class EvaluationRequirement {
    NONE, // evaluation is not performed (holders, argument values)
    MANDATORY, // Aux, watcher lambdas (for now)
//        OPTIONAL, // lambdas containing holder providers - depends on nested providers, particular lambdas settings and Analyzer settings

}
