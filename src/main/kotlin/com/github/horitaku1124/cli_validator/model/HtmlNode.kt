package com.github.horitaku1124.cli_validator.model

import java.util.*

open class HtmlNode() {
  lateinit var name: String
  lateinit var type: NodeType
  var attr = mapOf<String, String>()
  var innerText: String? = null
  var children = arrayListOf<HtmlNode>()
  var innerId: Int? = null

  enum class NodeType {
    TagNode, Text, Root, Empty
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
    type = if (tag.type == HtmlTag.TagType.Empty) NodeType.Empty else NodeType.TagNode
    innerId = tag.innerId
    attr = tag.attr!!
  }

  fun appendChild(child: HtmlNode) {
    children.add(child)
  }
  companion object {
    fun createTextNode(text: String): HtmlNode {
      val textNode = HtmlNode(NodeType.Text)
      textNode.innerText = text
      return textNode
    }
  }

  fun findElementByTagName(tagName: String): Optional<HtmlNode> {
    val found = digForTagName(tagName, this)
    return if (found == null) Optional.empty() else Optional.of(found)
  }

  private fun digForTagName(tagName: String, node:HtmlNode): HtmlNode? {
    if (node.type == NodeType.TagNode && node.name == tagName) {
      return node
    }
    for (child in node.children) {
      val found = digForTagName(tagName, child)
      if (found != null) {
        return found
      }
    }
    return null
  }

  override fun toString(): String {
    return when (type) {
      NodeType.TagNode -> {
        "<$name>"
      }
      NodeType.Text -> {
        "\"" + innerText + "\""
      }
      NodeType.Empty -> {
        "<$name/>"
      }
      else -> {
        "//"
      }
    }
  }

  fun isNameOf(checkName: String): Boolean {
    if (type == NodeType.TagNode || type == NodeType.Empty) {
      return name == checkName
    }
    return false
  }
}
