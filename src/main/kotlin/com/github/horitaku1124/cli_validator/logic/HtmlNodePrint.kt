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
      sb.append("\n")
    }
    for (grandChild in child.children) {
      insideToHaml(sb, grandChild, depth + 1)
    }
  }
}