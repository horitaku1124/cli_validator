package com.github.horitaku1124.cli_validator.logic

import com.github.horitaku1124.cli_validator.model.HtmlNode

object HtmlSearch {
  fun findByName(nodes: HtmlNode, name: String): HtmlNode?{
    for (node in nodes.children) {
      val found = insideFindByName(node, name)
      if (found != null) {
        return found
      }
    }
    return null
  }
  private fun insideFindByName(node: HtmlNode, name: String): HtmlNode?{
    if (node.isNameOf(name)) {
      return node
    }

    for (child in node.children) {
      val found = insideFindByName(child, name)
      if (found != null) {
        return found
      }
    }
    return null
  }
}
