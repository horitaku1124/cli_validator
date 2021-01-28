package com.github.horitaku1124.cli_validator.logic

import com.github.horitaku1124.cli_validator.model.HtmlNode

object HtmlNodePrint {
  fun prettyPrint(root: HtmlNode): String {
    val sb = StringBuffer()
    sb.append(root.name).append("->")
    for (child in root.children) {
      pp(sb, child, 0)
    }
    return sb.toString()
  }
  fun pp(sb: StringBuffer, child: HtmlNode, depth: Int) {
    if (child.type == HtmlNode.NodeType.Text) {
      if (child.innerText != null) {
        var text = child.innerText!!
        text = text.trim()
        if (text.isNotBlank()) {
          sb.append("  ".repeat(depth))
          sb.append("\"").append(text).append("\"")
          sb.append("\n")
        }
      }
    } else {
      sb.append("  ".repeat(depth))
      sb.append(child.name)
      sb.append("\n")
    }
    for (grandChild in child.children) {
      pp(sb, grandChild, depth + 1)
    }
  }
  fun toHaml(root: HtmlNode): String {
    val sb = StringBuffer()
    for (child in root.children) {
      insideToHaml(sb, child, 0)
    }
    return sb.toString()
  }
  private fun insideToHaml(sb: StringBuffer, child: HtmlNode, depth: Int) {
    if (child.type == HtmlNode.NodeType.Text) {
      if (child.innerText != null) {
        var text = child.innerText!!
        text = text.trim()
        if (text.isNotBlank()) {
          sb.append("  ".repeat(depth))
          .append(text)
          sb.append("\n")
        }
      }
    } else {
      sb.append("  ".repeat(depth))
      sb.append("%")
      sb.append(child.name)

      val attr = child.attr
      if (attr.containsKey("id")) {
        sb.append("#")
        sb.append(attr["id"])
      }
      if (attr.containsKey("class")) {
        sb.append(".")
        sb.append(attr["class"])
      }

      val attrs = arrayListOf<Pair<String, String>>()
      listOf("src", "href").forEach { key ->
        if (attr.containsKey(key)) {
          attrs.add(Pair(key, attr[key] ?: error("no key")))
        }
      }
      if (attrs.isNotEmpty()) {
        sb.append("{")
        sb.append(attrs
            .map { it.first + ": " + it.second }
            .joinToString(","))

        sb.append("}")
      }

      sb.append("\n")
    }
    for (grandChild in child.children) {
      insideToHaml(sb, grandChild, depth + 1)
    }
  }
}