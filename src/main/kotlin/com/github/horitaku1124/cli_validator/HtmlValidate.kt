package com.github.horitaku1124.cli_validator

import com.github.horitaku1124.cli_validator.logic.HtmlParser
import com.github.horitaku1124.cli_validator.model.HtmlTag
import com.github.horitaku1124.cli_validator.model.Message
import java.io.File
import java.util.*

var obsoleteTags = listOf(
        "acronym", "applet", "basefont", "big", "center", "dir", "font", "frame", "frameset",
        "isindex", "noframes", "s", "strike", "tt", "u"
)
var obsoleteAttributes: Map<String, List<String>> = hashMapOf(
        "a" to listOf("rev", "charset", "shape", "coords"),
        "link" to listOf("rev", "charset", "target"),
        "img" to listOf("longdesc", "name", "align", "hspace", "vspace"),
        "iframe" to listOf("longdesc", "name", "align", "frameborder", "marginheight", "marginwidth", "scrolling"),
        "head" to listOf("profile"),
        "html" to listOf("version"),
        "area" to listOf("nohref"),
        "meta" to listOf("scheme"),
        "object" to listOf("archive", "classid", "codebase", "codetype", "declare", "standby"),
        "td" to listOf("axis", "abbr", "scope", "align", "bgcolor", "char", "charoff", "nowrap", "width"),
        "th" to listOf("bgcolor", "char", "charoff", "nowrap", "width"),
        "br" to listOf("clear"),
        "body" to listOf("alink", "link", "vlink", "text", "background", "bgcolor"),
        "caption" to listOf("align"),
        "input" to listOf("align"),
        "legend" to listOf("align"),
        "hr" to listOf("align"),
        "div" to listOf("align"),
        "h1" to listOf("align"),
        "h2" to listOf("align"),
        "h3" to listOf("align"),
        "h4" to listOf("align"),
        "h5" to listOf("align"),
        "h6" to listOf("align"),
        "p" to listOf("align"),
        "col" to listOf("align"),
        "colgroup" to listOf("align"),
        "tfoot" to listOf("align"),
        "thead" to listOf("align"),
        "tr" to listOf("align"),
        "table" to listOf("align", "bgcolor", "border", "cellpadding", "cellspacing", "frame", "width")
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
    var result = true;
    var message: Message? = null
    val htmlPath = parentDir.toString()
    if (htmlPath.endsWith(".html")) {

      try {
        var html = File(htmlPath).readText()
        message = htmlValidator.checkHtmlFile(html)
      } catch (e: Exception) {
        e.printStackTrace()
        result = false
      }

      println(path + " " + (if (result) "OK" else "NG"))
      if (message != null) {
        for (warn in message.warnList) {
          println(" W - $warn")
        }
      }
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
        var result = true
        var message: Message? = null
        try {
          var html = File(htmlPath).readText()
          message = htmlValidator.checkHtmlFile(html)
        } catch (e: Exception) {
          e.printStackTrace()
          result = false
        }

        val filePath = if (htmlPath.indexOf(targetCanonicalPath) == 0 )
          htmlPath.replace(targetCanonicalPath, "") else htmlPath
        println(filePath + " " + (if (result) "OK" else "NG"))
        if (message != null) {
          for (warn in message.warnList) {
            println(" W - $warn")
          }
        }
      }
    }
    System.exit(if (succeed) 0 else 1)
  } else {
    System.err.println("It is not directory")
    System.exit(3)
  }
}

class HtmlValidator {
  fun checkHtmlFile(html: String): Message {
    var resultMessage = Message()
    var parser = HtmlParser()
    var htmlList = parser.parseHtml(html)
    for (tag in htmlList) {
      if (tag.type == HtmlTag.TagType.Open) {
        var name = tag.name;
        if (obsoleteTags.contains(name)) {
          resultMessage.warnList.add("Obsolete tag: $name")
        } else if (obsoleteAttributes.containsKey(name)) {
          var attr = tag.attr
          var candidates = obsoleteAttributes[name]
          if (attr != null && candidates != null) {
            for (set in attr.entries) {
              if (candidates.contains(set.key)) {
                resultMessage.warnList.add("Obsolete attr: " + set.key + " in " + name)
              }
            }
          }
        }
      }
    }
    var nodeList = parser.extractTree(htmlList)
    return resultMessage
  }
}
