package com.github.horitaku1124.cli_validator.logic

import com.github.horitaku1124.cli_validator.model.HtmlNode
import com.github.horitaku1124.cli_validator.model.HtmlTag
import java.util.*


class HtmlParser {
  fun removeScriptTag(html: String): String {
    val leftMath = "<script"
    val rightMath = "</script>"
    var retHtml = html
    while (true) {
      var index = retHtml.indexOf(leftMath)
      if (index < 0) {
        break
      }
      var indexEnd = retHtml.indexOf(rightMath)
      if (indexEnd < 0) {
        break
      }
      var left = retHtml.substring(0, index)
      var right = retHtml.substring(indexEnd + rightMath.length, retHtml.length)
      retHtml = left + right
    }
    return retHtml
  }

  fun parseHtml(html: String): ArrayList<HtmlTag> {
    var i = 0;
    var inTag = false
    var inComment = false
    var insideTag = StringBuffer()
    var htmlList = arrayListOf<HtmlTag>()
    while (i < html.length) {
      var c = html[i]
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
            var newTag = HtmlTag(HtmlTag.TagType.Text, insideTag.toString())
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

  fun parseAttr(tagStr: String, tagType: HtmlTag.TagType = HtmlTag.TagType.Open): HtmlTag {
    var strBuf = StringBuffer()
    var tokens = arrayListOf<String>()
    var inSingleQuote = false
    var inDoubleQuote = false
    var i = 0
    while (i < tagStr.length) {
      var c = tagStr[i]
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

    var attributes = HashMap<String, String>()
    for (j in 1 until tokens.size) {
      var pair = tokens[j].split("=")
      if (pair.size > 1) {
        attributes.put(pair[0], pair[1])
      }
    }
    return HtmlTag(tagType, tokens[0], attributes)
  }

  fun extractTree(htmlList: ArrayList<HtmlTag>): HtmlNode {
    var rootNode = HtmlNode("root", HtmlNode.NodeType.Root)

    var depth = Stack<HtmlNode>()
    depth.push(rootNode)

    var current = rootNode
    for (tag in htmlList) {
      if (tag.type == HtmlTag.TagType.Open) {
        if (tag.name == "meta" || tag.name == "link") {
          tag.type = HtmlTag.TagType.Empty
          var node = HtmlNode(tag)
          current.appendChild(node)
        } else {
          var node = HtmlNode(tag)
          current.appendChild(node)
          depth.push(node)
          current = node
        }
      } else if (tag.type == HtmlTag.TagType.Close) {
        depth.pop()
        current = depth.get(depth.size - 1)
//        rootNode.appendChild(node)
      } else if (tag.type == HtmlTag.TagType.Text) {
        var textNode = HtmlNode.createTextNode(tag.name!!)
        textNode.innerId = tag.innerId
        current.appendChild(textNode)
      }

    }
    return rootNode
  }
}