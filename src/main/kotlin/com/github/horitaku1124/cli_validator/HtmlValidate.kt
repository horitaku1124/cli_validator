package com.github.horitaku1124.cli_validator

import com.github.horitaku1124.cli_validator.model.HtmlTag
import java.io.File

var obsoleteTags = listOf(
        "acronym", "applet", "basefont", "big", "center", "dir", "font", "frame", "frameset",
        "isindex", "noframes", "s", "strike", "tt", "u"
)
var obsoleteAttributes: Map<String, List<String>> = hashMapOf(
        "a" to listOf("rev", "charset"),
        "table" to listOf("align", "bgcolor", "border", "cellpadding")
)

fun searchDirectory(parentDir:File, found:ArrayList<File>) {
  if (parentDir.isDirectory) {
    var children = parentDir.listFiles()
    for (child in children) {
      searchDirectory(child, found)
    }
  } else {
    found.add(parentDir)
  }
}

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
      result = htmlValidator.checkHtmlFile(htmlPath)

      println(path + " " + (if (result) "OK" else "NG"))
    }
    System.exit(if (result) 0 else 1)
  } else if (parentDir.isDirectory) {
    var targetCanonicalPath = parentDir.canonicalPath
    var htmlFiles = arrayListOf<File>()
    searchDirectory(parentDir, htmlFiles)
    var succeed = true
    for (child in htmlFiles) {
      val htmlPath = child.canonicalPath
      if (htmlPath.endsWith(".html")) {
        val result = htmlValidator.checkHtmlFile(htmlPath)
        if (!result) {
          succeed = false
        }
        val filePath = if (htmlPath.indexOf(targetCanonicalPath) == 0 )
          htmlPath.replace(targetCanonicalPath, "") else htmlPath
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
  fun checkHtmlFile(filePath: String): Boolean {
    try {
      var html = File(filePath).readText()
      var htmlList = parseHtml(html)
      for (tag in htmlList) {
        if (tag.type == HtmlTag.TagType.Open) {
          var name = tag.name;
          if (obsoleteTags.contains(name)) {
            println("Obsolete tag: " + name)
          } else if (obsoleteAttributes.containsKey(name)) {
            var attr = tag.attr
            var candidates = obsoleteAttributes[name]
            if (attr != null && candidates != null) {
              for (set in attr.entries) {
                if (candidates.contains(set.key)) {
                  println("Obsolete attr: " + set.key + " in " + name)
                }
              }
            }
          }
        }
      }
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
            htmlList.add(parseAttr(tagStr))
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
