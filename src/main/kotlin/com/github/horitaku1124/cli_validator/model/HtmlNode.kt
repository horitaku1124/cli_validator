package com.github.horitaku1124.cli_validator.model

class HtmlNode() {
    lateinit var name: String
    lateinit var type: NodeType
    var attr = mapOf<String, String>()
    var innerText: String? = null
    var children = arrayListOf<HtmlNode>()
    var innerId: Int? = null

    enum class NodeType {
        TagNode, Text, Root
    }
    constructor(type: NodeType) : this() {
        this.type = type
    }
    constructor(name: String, type: NodeType) : this() {
        this.name = name
        this.type = type
    }

    constructor(tag: HtmlTag) : this() {
        name = tag.name!!
        type = NodeType.TagNode
        innerId = tag.innerId
        attr = tag.attr!!
    }

    fun appendChild(child: HtmlNode) {
        children.add(child)
    }
    companion object {
        fun createTextNode(text: String): HtmlNode {
            var textNode = HtmlNode(NodeType.Text)
            textNode.innerText = text
            return textNode
        }
    }
}