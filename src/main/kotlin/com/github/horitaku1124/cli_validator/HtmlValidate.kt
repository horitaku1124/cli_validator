package com.github.horitaku1124.cli_validator

import java.io.File
import java.io.FileInputStream
import java.util.*


fun main(args: Array<String>) {
  if (args.isEmpty()) {
    System.err.println("No html path")
    System.exit(1);
  }
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
        val result = htmlFileCanOpen(htmlPath)
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
  while (i < html.length) {
    var c = html[i]

    if (inTag) {
      if (c == '>') {
        inTag = false
        println(insideTag.toString())
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
}