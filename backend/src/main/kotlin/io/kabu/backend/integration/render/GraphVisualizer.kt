package io.kabu.backend.integration.render

import io.kabu.backend.node.FunctionNode
import io.kabu.backend.node.HolderTypeNode
import io.kabu.backend.node.Node
import io.kabu.backend.node.ObjectTypeNode
import io.kabu.backend.node.PackageNode
import io.kabu.backend.node.PropertyNode
import io.kabu.backend.node.TypeNode
import java.net.URLEncoder


class GraphVisualizer {

    fun generateMermaidDiagramAsFlowchart(
        nodes: Set<Node>,
        styling: Boolean = true,
        title: String? = null,
    ): String = buildString {

        fun bold(text: String): String {
            return if (styling) "<b>$text</b>" else text
        }

        fun leftAlign(text: String): String {
            return if (styling) "<div style='text-align: left'>$text</div>" else text
        }

        fun fixPaddings(text: String): String {
            return if (styling) "<div style='padding: 3px'>$text</div>" else text
        }

        // initial diagram settings
        appendLine("flowchart")
        if (styling) {
            appendLine("direction BT")
        }
//        appendLine("%%{init: {\"flowchart\": {\"defaultRenderer\": \"elk\"}} }%%")

        if (title != null) {
            appendLine("___TITLE___[[${fixPaddings(title)}]]")
        }

        val names = associateWithUniqueNames(
            nodes, wholeQuote = "", nameQuote = "_", indexSeparator = "_"
        )

        // defining nodes themselves
        if (styling) {
            appendLine("")
        }
        nodes.forEach { node ->
            val nodeContents = buildString {
                append("${bold(names[node]!!.name)}\\n")
                when (node) {
                    is HolderTypeNode -> {
                        buildString {
                            node.fieldTypes.forEach { fieldType ->
                                append("- ${fieldType.name}\\n")
                            }
                        }.let {
                            if (it.isNotBlank()) leftAlign(it) else it
                        }.let {
                            append(it)
                        }
                    }

                    is FunctionNode -> {
                        buildString {
                            node.parameters.forEach { parameter ->
                                append("- ${parameter.typeNode.name}\\n")
                            }
                        }.let {
                            if (it.isNotBlank()) leftAlign(it) else it
                        }.let {
                            append(it)
                        }
                        append(bold(": ${node.returnTypeNode.name}"))
                    }

                    is PropertyNode -> {
                        buildString {
                            node.receiverTypeNode?.let { receiver ->
                                append("RECEIVER: ${receiver.name}\\n")
                            }
                        }.let {
                            if (it.isNotBlank()) leftAlign(it) else it
                        }.let {
                            append(it)
                        }
                        append(bold(": ${node.returnTypeNode.name}"))
                    }
                }
            }
            val braces: Pair<String, String> = when (node) {
                is PackageNode -> "[" to "]"
                is HolderTypeNode -> "[" to "]" //fa:fa-camera-retro
                is FunctionNode -> "([" to "])"
                is PropertyNode -> "(" to ")"
                else -> "[" to "]"
            }
            val details = if (nodeContents.isNotBlank()) {
                "${braces.first}\"${fixPaddings(nodeContents)}\"${braces.second}"
            } else ""
            appendLine("${names[node]?.id}$details") //fa:fa-car
        }

        // defining nodes links
        if (styling) {
            appendLine("")
        }
        nodes.forEach { node ->
            node.dependencies.distinct().forEach { dependency -> //todo make dependencies a set
                val link = if (dependency == node.namespaceNode) "-.->" else "-->"
                appendLine("${names[node]?.id} $link ${names[dependency]?.id}")
            }
        }

        // styles
        if (styling) {
            appendLine("")
            // style id2 fill:#bbf,stroke:#f66,stroke-width:2px,color:#fff,stroke-dasharray: 5 5
            // classDef default fill:#f9f,stroke:#333,stroke-width:4px; // default style
            appendLine("classDef classHolder fill:#CCC,stroke:#333,stroke-width:2px;")
            appendLine("classDef classFun fill:#CCF,stroke:#333,stroke-width:2px;")
            appendLine("classDef classFunTerminal fill:#BBF,stroke:#333,stroke-width:5px;")
            appendLine("classDef classProperty fill:#CCF,stroke:#333,stroke-width:2px;")
            appendLine("classDef classPropertyTerminal fill:#BBF,stroke:#333,stroke-width:5px;")
            appendLine("classDef classPackageNode fill:#3B7,stroke:#333,stroke-width:2px,stroke-dasharray:5 3;")
            nodes.forEach { node ->
                when (node) {
                    is HolderTypeNode -> appendLine("class ${names[node]?.id} classHolder")
                    is FunctionNode -> {
                        val style = if (node.isTerminal) "classFunTerminal" else "classFun"
                        appendLine("class ${names[node]?.id} $style")
                    }
                    is PropertyNode -> {
                        val style = if (node.isTerminal) "classPropertyTerminal" else "classProperty"
                        appendLine("class ${names[node]?.id} $style")
                    }
                    is PackageNode -> {
                        appendLine("class ${names[node]?.id} classPackageNode")
                    }
                }
            }
        }
    }

    private fun associateWithUniqueNames(
        nodes: Set<Node>,
        wholeQuote: String = "",
        nameQuote: String = "'",
        indexSeparator: String = "-",
    ): Map<Node, Names> {

        fun composeName(
            node: Node,
            tryCount: Int,
            kindSeparator: String,
            nameQuote: String,
            indexSeparator: String,
            namespaceSeparator: String = ".",
            includeNamespace: Boolean = true
        ): String {
            val sanitizedNamespace = node.namespaceNode?.recursiveName?.replace(".", namespaceSeparator).orEmpty()
            val namespacePart = if (includeNamespace) "$sanitizedNamespace$namespaceSeparator" else ""
            val name = "${node.getKind()}$kindSeparator$namespacePart${node.name}".trim()
            return if (tryCount == 1) name
            else "$wholeQuote$nameQuote$name$nameQuote$indexSeparator$tryCount$wholeQuote"
        }

        // associating nodes with unique names
        val names = mutableMapOf<Node, Names>()
        nodes.forEach { node ->
            var tryCount = 1
            var composedId: String
            var composedName: String
            do {
                composedId = composeName(
                    node, tryCount,
                    kindSeparator = "_",
                    nameQuote = nameQuote,
                    indexSeparator = indexSeparator,
                    namespaceSeparator = "_",
                    includeNamespace = true,
                )
                composedName = composeName(
                    node, tryCount,
                    kindSeparator = " ",
                    nameQuote = "'",
                    indexSeparator = "-",
                    namespaceSeparator = "/",
                    includeNamespace = false,
                )
                tryCount++
            } while (names.any { it.value.id == composedId })

            names[node] = Names(id = composedId, name = composedName)
        }
        return names
    }

    private fun Node.getKind(): String? = when (this) {
        is FunctionNode -> "fun"
        is ObjectTypeNode -> "obj"
        is TypeNode -> "class"
        is PropertyNode -> "val"
        is PackageNode -> "pkg"
        else -> null
    }

    @Suppress("HttpUrlsUsage")
    fun getMermaidRenderLink(mermaidDiagram: String): String {
        val encodedDiagram = URLEncoder.encode(mermaidDiagram, "UTF-8")
        return "$ONLINE_MERMAID_RENDERER_BASE_URL/generate?data=$encodedDiagram"
    }

    private class Names(
        val id: String,
        val name: String,
    )

    companion object {
        private const val ONLINE_MERMAID_RENDERER_BASE_URL = "http://localhost"

        fun visualize(nodes: Set<Node>, title: String? = null) {
            val titleText = title?.let { "$title :" }.orEmpty()
            GraphVisualizer().run {
                val mermaidDiagram = generateMermaidDiagramAsFlowchart(nodes, title = title)
                println(titleText + getMermaidRenderLink(mermaidDiagram))
                println(mermaidDiagram)
            }
        }
    }
}
