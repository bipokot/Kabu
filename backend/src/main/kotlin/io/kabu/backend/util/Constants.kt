package io.kabu.backend.util


object Constants {

    const val PROJECT_BASE_PACKAGE = "io.kabu"
    const val RUNTIME_PACKAGE = "$PROJECT_BASE_PACKAGE.runtime"
    const val ANNOTATIONS_PACKAGE = "$PROJECT_BASE_PACKAGE.annotations"
    const val BACKEND_PACKAGE = "$PROJECT_BASE_PACKAGE.backend"

    const val EXTENSION_ANNOTATION = "Extend"
    const val EXTENSION_ANNOTATION_CONTEXT_CREATOR = "context"
    const val EXTENSION_ANNOTATION_CONTEXT_PARAMETER = "parameter"

    // name of context property in a context mediator class
    const val EXTENSION_CONTEXT_PROPERTY_NAME = "ctx" //todo hide this property from user

    const val RECEIVER_PARAMETER_NAME = "this"
    const val ACCESSOR_OBJECT_NAME_LENGTH = 4
}
