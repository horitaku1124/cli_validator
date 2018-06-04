package com.github.horitaku1124.cli_validator

import com.github.horitaku1124.cli_validator.model.HtmlTag
import java.io.File


fun main(args: Array<String>) {
  if (args.isEmpty()) {
    System.err.println("No html path")
    System.exit(1);
  }
  var a = HtmlValidator()
  val path = args[0]
  val parentDir = File(path)
  if (!parentDir.exists()) {
    System.err.println("Parent directory doesn't exists")
    System.exit(2)
  } else if (parentDir.isDirectory) {
    val files = parentDir.listFiles()
    var succeed = true
    for (child in files) {
      val htmlPath = child.absoluteFile.toPath().toString()
      if (htmlPath.endsWith(".html")) {
        val result = a.htmlFileCanOpen(htmlPath)
        if (!result) {
          succeed = false
        }
        println(htmlPath + " " + (if (result) "OK" else "NG"))
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

  fun parseHtml(html: String) {
    var i = 0;
    var inTag = false
    var insideTag = StringBuffer()
    var htmlList = arrayListOf<HtmlTag>()
    while (i < html.length) {
      var c = html[i]
      if (inTag) {
        if (c == '>') {
          inTag = false
          var isEmpty = false
          var attr = insideTag.toString()
          if (attr.indexOf("/") == 0) {
            isEmpty = true
            htmlList.add(HtmlTag(HtmlTag.TagType.Close, attr))
          } else {
            parseAttr(attr)
            htmlList.add(HtmlTag(HtmlTag.TagType.Open, attr))
//          println(insideTag.toString())
          }
        } else {
          insideTag.append(c)
        }
      } else {
        if (c == '<') {
          inTag = true
          insideTag = StringBuffer()
        }
      }
      i++
    }
    for (tag in htmlList) {
      println(tag)
    }
  }

  fun parseAttr(attr: String) {

  }
}
