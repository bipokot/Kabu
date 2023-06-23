package io.kabu.backend.diagnostic

data class Origin(
    val sourceLocation: SourceLocation? = null,
    val excerpt: String? = null,
    val extra: String? = null,
    val parent: Origin? = null,
) {

    override fun toString(): String = buildString {
        if (parent != null) {
            append(parent.toString())
            append(" => ")
        }
        if (sourceLocation != null) append(sourceLocation.toString())
        appendIfNotNull(excerpt) { " \"$it\"" }
        appendIfNotNull(extra) { " ($it)" }
    }

    private fun StringBuilder.appendIfNotNull(string: String?, transform: ((String) -> String)? = null) {
        if (string != null) {
            append(if (transform != null) transform(string) else string)
        }
    }
}
