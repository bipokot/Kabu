package io.kabu.backend.diagnostic

sealed class SourceLocation {
    abstract val line: Int?
    abstract val startIndex: Int?
    abstract val endIndex: Int?

    protected fun StringBuilder.appendColumnsString() {
        if (startIndex != null) {
            if (startIndex == endIndex) {
                append(" [col. $startIndex]")
            } else {
                append(" [col. $startIndex..")
                if (endIndex != null) append("$endIndex")
                append("]")
            }
        }
    }
}

data class FileSourceLocation(
    val sourceFile: String,
    override val line: Int? = null,
    override val startIndex: Int? = null,
    override val endIndex: Int? = null,
) : SourceLocation() {

    override fun toString() = buildString {
        append(sourceFile)
        if (line != null) {
            append(":$line")
        }
        appendColumnsString()
    }
}

data class InlineSourceLocation(
    override val line: Int? = null,
    override val startIndex: Int? = null,
    override val endIndex: Int? = null,
) : SourceLocation()  {

    override fun toString() = buildString {
        if (line != null) append(":$line")
        appendColumnsString()
    }
}
