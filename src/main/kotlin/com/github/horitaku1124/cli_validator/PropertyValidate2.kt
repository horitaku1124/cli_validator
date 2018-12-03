package com.github.horitaku1124.cli_validator

import java.io.File
import java.io.FileInputStream
import kotlin.collections.HashMap

fun main(args: Array<String>) {
  if (args.isEmpty()) {
    System.err.println("No property path")
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
      val filePath = child.absoluteFile.toPath().toString()
      if (filePath.endsWith(".properties")) {
        val result = propertyFileCanOpen2(filePath)
        if (!result) {
          succeed = false
        }
        println(filePath + " " + (if (result) "OK" else "NG"))
      }
    }
    System.exit(if (succeed) 0 else 1)
  } else if (parentDir.isFile) {
    val result = propertyFileCanOpen2(parentDir.absolutePath)
    println(parentDir.absolutePath + " " + (if (result) "OK" else "NG"))
    System.exit(if (result) 0 else 1)
  } else {
    System.err.println("It is not directory")
    System.exit(3)
  }
}

fun propertyFileCanOpen2(filePath: String): Boolean {
  val propMatch = Regex("""^([a-zA-Z0-9\\.]*)=(\S*)""")
  var result = true
  val kvs = HashMap<String, String>()
  try {
    val file = FileInputStream(filePath)
    val br = file.bufferedReader()
    var index = 0
    while (true) {
      index++
      val line = br.readLine() ?: break
      if (line.startsWith("#")) {
        continue
      }
      val match:MatchResult? = propMatch.matchEntire(line)
      if (match != null) {
        val key = match.groupValues.get(1)
        val value = match.groupValues.get(2)

        if (key.isEmpty()) {
          System.err.println("line ${index} key is empty")
          result = false
        } else if (kvs.containsKey(key)) {
          System.err.println("line ${index} key is duplicated => ${key}")
          result = false
        } else {
          kvs.put(key, value)
        }
      }
    }
    return result
  } catch (e: Exception) {
    e.printStackTrace()
    return false
  }
}