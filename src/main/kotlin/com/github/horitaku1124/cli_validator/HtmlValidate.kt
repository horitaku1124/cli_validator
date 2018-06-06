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
        val filePath = if ( htmlPath.indexOf(path) == 0 ) htmlPath.replace(path, "") else htmlPath
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
    var insideTag = StringBuffer()
    var htmlList = arrayListOf<HtmlTag>()
    while (i < html.length) {
      var c = html[i]
      if (inTag) {
        if (c == '>') {
          inTag = false
          var tagStr = insideTag.toString()
          if (tagStr.indexOf("/") == 0) {
            tagStr = tagStr.substring(1)
            htmlList.add(HtmlTag(HtmlTag.TagType.Close, tagStr))
          } else {
            parseAttr(tagStr)
            var attributes = tagStr.split(" ")
            var tagName = attributes[0]
            if (tagName == "!DOCTYPE") {
              htmlList.add(HtmlTag(HtmlTag.TagType.DocType, attributes[1]))
            } else {
              var attr:HashMap<String, String> = HashMap()
              for (j in 1 until attributes.size) {
                var attrPair = attributes[j].split("=")
                attr.put(attrPair[0], attrPair[1])
              }
              htmlList.add(HtmlTag(HtmlTag.TagType.Open, tagName, attr))
            }
//          println(insideTag.toString())
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
          inTag = true
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

  fun parseAttr(attr: String) {

  }
}
