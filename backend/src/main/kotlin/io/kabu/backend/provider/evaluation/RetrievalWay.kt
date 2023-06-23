package io.kabu.backend.provider.evaluation

import com.squareup.kotlinpoet.CodeBlock

data class RetrievalWay(
    val codeBlock: CodeBlock,
    val isReentrant: Boolean,
)
