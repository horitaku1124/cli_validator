package com.github.horitaku1124.cli_validator

import java.io.File
import java.io.FileInputStream
import java.lang.System.err
import kotlin.collections.HashMap
import kotlin.system.exitProcess

fun main(args: Array<String>) {
  if (args.isEmpty()) {
    err.println("No property path")
    exitProcess(1);
  }
  val path = args[0]
  val parentDir = File(path)
  if (!parentDir.exists()) {
    err.println("Parent directory doesn't exists")
    exitProcess(2)
  } else if (parentDir.isDirectory) {
    val files = parentDir.listFiles()
    var succeed = true
    for (child in files!!) {
      val filePath = child.absoluteFile.toPath().toString()
      if (filePath.endsWith(".properties")) {
        val result = propertyFileCanOpen2(filePath)
        if (!result.first) {
          succeed = false
        }
        println(filePath + " " + (if (result.first) "OK" else "NG"))
      }
    }
    System.exit(if (succeed) 0 else 1)
  } else if (parentDir.isFile) {
    val result = propertyFileCanOpen2(parentDir.absolutePath)
    println(parentDir.absolutePath + " " + (if (result.first) "OK" else "NG"))
    for (error in result.second) {
      err.println(error)
    }
    exitProcess(if (result.first) 0 else 1)
  } else {
    err.println("It is not directory")
    exitProcess(3)
  }
}

fun propertyFileCanOpen2(filePath: String): Pair<Boolean, List<String>> {
  val propMatch = Regex("""^([a-zA-Z0-9\\.]*) *= *(.*)""")
  var result = true
  val kvs = HashMap<String, String>()
  val errorList = ArrayList<String>()
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
          errorList.add("line ${index} key is empty")
          result = false
        } else if (kvs.containsKey(key)) {
          errorList.add("line ${index} key is duplicated => ${key}")
          result = false
        } else {
          kvs.put(key, value)
        }
      }
    }
    return Pair(result, errorList)
  } catch (e: Exception) {
    e.printStackTrace()
    return Pair(false, errorList)
  }
}
