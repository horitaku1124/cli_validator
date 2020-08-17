package com.github.horitaku1124.cli_validator

import java.io.File
import java.io.FileInputStream
import java.lang.System.err
import java.util.*
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
    System.exit(2)
  } else if (parentDir.isDirectory) {
    val files = parentDir.listFiles()
    var succeed = true
    for (child in files) {
      val filePath = child.absoluteFile.toPath().toString()
      if (filePath.endsWith(".properties")) {
        val result = propertyFileCanOpen(filePath)
        if (!result) {
          succeed = false
        }
        println(filePath + " " + (if (result) "OK" else "NG"))
      }
    }
    exitProcess(if (succeed) 0 else 1)
  } else {
    err.println("It is not directory")
    exitProcess(3)
  }
}

fun propertyFileCanOpen(filePath: String): Boolean {
  try {
    val prop = Properties()
    prop.load(FileInputStream(filePath))
//    for (key in prop.keys()) {
//      println(key)
//      println(prop.get(key))
//    }
    return true
  } catch (e: Exception) {
    e.printStackTrace()
    return false
  }
}