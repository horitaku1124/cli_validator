package com.github.horitaku1124.cli_validator

import org.xml.sax.InputSource
import java.io.File
import java.lang.System.err
import javax.xml.parsers.DocumentBuilderFactory


fun main(args: Array<String>) {
  if (args.isEmpty()) {
    err.println("No xml path")
    System.exit(1);
  }
  val path = args[0]
  val parentDir = File(path)
  if (parentDir.isDirectory) {
    val files = parentDir.listFiles()
    var succeed = true
    for (child in files) {
      val xmlPath = child.absoluteFile.toPath().toString()
      if (xmlPath.endsWith(".xml")) {
        val result = xmlFileCanOpen(xmlPath)
        if (!result) {
          succeed = false
        }
        println(xmlPath + " " + (if (result) "OK" else "NG"))
      }
    }
    System.exit(if (succeed) 0 else 1)
  }
}

private fun xmlFileCanOpen(xmlPath: String): Boolean {
  return try {
    val factory = DocumentBuilderFactory.newInstance()

    factory.isIgnoringComments = true
    factory.isIgnoringElementContentWhitespace = true
    factory.isValidating = false

    val builder = factory.newDocumentBuilder()
    builder.parse(InputSource(xmlPath)) != null
  } catch (e: Exception) {
    e.printStackTrace()
    false
  }
}