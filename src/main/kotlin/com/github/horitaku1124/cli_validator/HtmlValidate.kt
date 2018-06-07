package com.github.horitaku1124.cli_validator

import com.github.horitaku1124.cli_validator.model.HtmlTag
import java.io.File


fun main(args: Array<String>) {
  if (args.isEmpty()) {
    System.err.println("No html path")
    System.exit(1);
  }
  val htmlValidator = HtmlValidator()
  val path = args[0]
  val parentDir = File(path)
  if (!parentDir.exists()) {
    System.err.println("Parent directory doesn't exists")
    System.exit(2)
  } else if (parentDir.isFile) {
    var result = false;
    val htmlPath = parentDir.toString()
    if (htmlPath.endsWith(".html")) {
      result = htmlValidator.htmlFileCanOpen(htmlPath)

      println(path + " " + (if (result) "OK" else "NG"))
    }
    System.exit(if (result) 0 else 1)
  } else if (parentDir.isDirectory) {
    val files = parentDir.listFiles()
    var succeed = true
    for (child in files) {
      val htmlPath = child.absoluteFile.toPath().toString()
      if (htmlPath.endsWith(".html")) {
        val result = htmlValidator.htmlFileCanOpen(htmlPath)
        if (!result) {
          succeed = false
        }
        val filePath = if (htmlPath.indexOf(path) == 0 ) htmlPath.replace(path, "") else htmlPath
        println(filePath + " " + (if (result) "OK" else "NG"))
      }
    }
    System.exit(if (succeed) 0 else 1)
  } else {
    System.err.println("It is not directory")
    System.exit(3)
  }
}

class HtmlValidator {
  fun htmlFileCanOpen(filePath: String): Boolean {
    try {
      var html = File(filePath).readText()

      parseHtml(html)
      return true
    } catch (e: Exception) {
      e.printStackTrace()
      return false
    }
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
          if (tagStr.indexOf("/") == 0) {
            tagStr = tagStr.substring(1)
            htmlList.add(HtmlTag(HtmlTag.TagType.Close, tagStr))
          } else {
            var newTag = parseAttr(tagStr)

            htmlList.add(newTag)
          }
          insideTag = StringBuffer()
        } else {
          insideTag.append(c)
        }
      } else {
        if (c == '<') {
          if (insideTag.isNotEmpty()) {
            htmlList.add(HtmlTag(HtmlTag.TagType.Text, insideTag.toString()))
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
    for (tag in htmlList) {
      println(tag)
    }
    return htmlList
  }

  fun parseAttr(tagStr: String): HtmlTag{
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
        if (strBuf.length > 0) {
          tokens.add(strBuf.toString())
          strBuf = StringBuffer()
        }
      } else {
        strBuf.append(c)
      }
      i++
    }
    if (strBuf.length > 0) {
      tokens.add(strBuf.toString())
    }

    var attributes = HashMap<String, String>()
    for (j in 1 until tokens.size) {
      var pair = tokens[j].split("=")
      if (pair.size > 1) {
        attributes.put(pair[0], pair[1])
      }
    }
    return HtmlTag(HtmlTag.TagType.Open, tokens[0], attributes)
  }
}
