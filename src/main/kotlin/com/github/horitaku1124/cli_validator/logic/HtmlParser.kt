package com.github.horitaku1124.cli_validator.logic

import com.github.horitaku1124.cli_validator.model.HtmlNode
import com.github.horitaku1124.cli_validator.model.HtmlTag
import java.nio.file.Files
import java.nio.file.Path
import java.util.*

class HtmlParser {
  companion object {
    var EmptyTags = listOf("meta", "link", "img", "br", "hr")
  }
  fun removeScriptTag(html: String): String {
    val leftMath = "<script"
    val rightMath = "</script>"
    var retHtml = html
    while (true) {
      val index = retHtml.indexOf(leftMath)
      if (index < 0) {
        break
      }
      val indexEnd = retHtml.indexOf(rightMath)
      if (indexEnd < 0) {
        break
      }
      val left = retHtml.substring(0, index)
      val right = retHtml.substring(indexEnd + rightMath.length, retHtml.length)
      retHtml = left + right
    }
    return retHtml
  }

  fun removeStyleTag(html: String): String {
    val leftMath = "<style"
    val rightMath = "</style>"
    var retHtml = html
    while (true) {
      val index = retHtml.indexOf(leftMath)
      if (index < 0) {
        break
      }
      val indexEnd = retHtml.indexOf(rightMath)
      if (indexEnd < 0) {
        break
      }
      val left = retHtml.substring(0, index)
      val right = retHtml.substring(indexEnd + rightMath.length, retHtml.length)
      retHtml = left + right
    }
    return retHtml
  }

  fun parseHtml(html: String): ArrayList<HtmlTag> {
    var i = 0;
    var inTag = false
    var inComment = false
    var insideTag = StringBuffer()
    val htmlList = arrayListOf<HtmlTag>()
    while (i < html.length) {
      val c = html[i]
      if (inComment) {
        if (c == '-') {
          if (html[i + 1] == '-' && html[i + 2] == '>') {
            inComment = false
            i += 2
          }
        }
      } else if (inTag) {
        if (c == '>') {
          inTag = false
          var tagStr = insideTag.toString()
          var newTag: HtmlTag
          if (tagStr.indexOf("/") == 0) {
            tagStr = tagStr.substring(1)
            newTag = HtmlTag(HtmlTag.TagType.Close, tagStr)
          } else if (tagStr.lastIndexOf("/") == tagStr.length - 1) {
            tagStr = tagStr.substring(0, tagStr.length - 1)
            newTag = parseAttr(tagStr, HtmlTag.TagType.Empty)
          } else {
            newTag = parseAttr(tagStr)
          }
          newTag.innerId = htmlList.size + 1
          htmlList.add(newTag)

          insideTag = StringBuffer()
        } else {
          insideTag.append(c)
        }
      } else {
        if (c == '<') {
          if (insideTag.isNotEmpty()) {
            val newTag = HtmlTag(HtmlTag.TagType.Text, insideTag.toString())
            newTag.innerId = htmlList.size + 1
            htmlList.add(newTag)
          }
          if (html[i + 1] == '!' && html[i + 2] == '-' && html[i + 2] == '-') {
            i += 2
            inComment = true
          } else {
            inTag = true
          }
          insideTag = StringBuffer()

        } else {
          insideTag.append(c)
        }
      }
      i++
    }
    return htmlList
  }

  fun parseHtmlToTree(html: String): HtmlNode {
    val list = parseHtml(html)
    return if (list.size > 0 && list[0].name == "!DOCTYPE") {
      val list2 = list.subList(1, list.size)
      extractTree(list2)
    } else {
      extractTree(list)
    }
  }

  fun parseAttr(tagStr: String, tagType: HtmlTag.TagType = HtmlTag.TagType.Open): HtmlTag {
    var strBuf = StringBuffer()
    val tokens = arrayListOf<String>()
    var inSingleQuote = false
    var inDoubleQuote = false
    var i = 0
    while (i < tagStr.length) {
      val c = tagStr[i]
      if (inSingleQuote) {
        if (c == '\'') {
          inSingleQuote = false
          strBuf.append(c)
          tokens.add(strBuf.toString())
          strBuf = StringBuffer()
        } else {
          strBuf.append(c)
        }
      } else if (inDoubleQuote) {
        if (c == '"') {
          inDoubleQuote = false
          strBuf.append(c)
          tokens.add(strBuf.toString())
          strBuf = StringBuffer()
        } else {
          strBuf.append(c)
        }
      } else if (c == '=') {
        strBuf.append(c)
        if (tagStr[i + 1] == '\'') {
          inSingleQuote = true
          i++
          strBuf.append(tagStr[i])
        }
        if (tagStr[i + 1] == '"') {
          inDoubleQuote = true
          i++
          strBuf.append(tagStr[i])
        }
      } else if (c == ' ' || c == '\t' || c == '\n') {
        if (strBuf.isNotEmpty()) {
          tokens.add(strBuf.toString())
          strBuf = StringBuffer()
        }
      } else {
        strBuf.append(c)
      }
      i++
    }
    if (strBuf.isNotEmpty()) {
      tokens.add(strBuf.toString())
    }

    val attributes = HashMap<String, String>()
    for (j in 1 until tokens.size) {
      val pair = tokens[j].split("=")
      if (pair.size > 1) {
        attributes.put(pair[0], pair[1])
      }
    }
    return HtmlTag(tagType, tokens[0], attributes)
  }

  fun extractTree(htmlList: List<HtmlTag>): HtmlNode {
    val rootNode = HtmlNode("root", HtmlNode.NodeType.Root)

    val depth = Stack<HtmlNode>()
    depth.push(rootNode)

    var current = rootNode
    for (tag in htmlList) {
      if (tag.type == HtmlTag.TagType.Open) {
        if (EmptyTags.contains(tag.name)) {
          tag.type = HtmlTag.TagType.Empty
          val node = HtmlNode(tag)
          current.appendChild(node)
        } else {
          val node = HtmlNode(tag)
          current.appendChild(node)
          depth.push(node)
          current = node
        }
      } else if (tag.type == HtmlTag.TagType.Close) {
        depth.pop()
        current = if (depth.isNotEmpty()) {
          depth[depth.size - 1]
        } else {
          rootNode
        }
      } else if (tag.type == HtmlTag.TagType.Empty) {
        val emptyTag = HtmlNode()
        emptyTag.type = HtmlNode.NodeType.TagNode
        emptyTag.name = tag.name!!
        emptyTag.attr = tag.attr!!
        current.appendChild(emptyTag)
      } else if (tag.type == HtmlTag.TagType.Text) {
        val textNode = HtmlNode.createTextNode(tag.name!!)
        textNode.innerId = tag.innerId
        current.appendChild(textNode)
      }
    }
    return rootNode
  }

  private fun dig(node:HtmlNode, b:StringBuffer) {
    if (node.type == HtmlNode.NodeType.Text) {
      b.append(node.innerText)
    }
    for (child in node.children) {
      dig(child, b)
    }
  }

  fun allTextFromTree(nodeTree: HtmlNode): String {
    val buffer = StringBuffer()
    dig(nodeTree, buffer)
    return buffer.toString()
  }

  fun recoverGap(tagList: List<HtmlTag>): List<HtmlTag> {
    val returnArray = arrayListOf<HtmlTag>()
    val checkToEnd = listOf("body", "div", "p", "dd", "dt")
    val needToFill = listOf("p", "dd", "dt")
    var check: String? = null
    for (tag in tagList) {
      if (check != null) {
        if (tag.type == HtmlTag.TagType.Open && checkToEnd.contains(tag.name)) {
          returnArray.add(HtmlTag(
            type = HtmlTag.TagType.Close,
            name = check
          ))
          check = null
        }
      }
      if (check == null) {
        if (tag.name in needToFill) {
          check = tag.name
        }
      } else if (tag.type == HtmlTag.TagType.Close && tag.name == check) {
        check = null
      }
      returnArray.add(tag)
    }
    return returnArray
  }


  fun parseFileToNode(path: Path): HtmlNode {
    val htmlOrg = Files.readString(path)

    val parser = HtmlParser()
    val html = parser.removeScriptTag(parser.removeStyleTag(htmlOrg))

    val htmlList = parser.parseHtml(html)
    val nodeTree = parser.extractTree(htmlList)
    return nodeTree
  }
}
