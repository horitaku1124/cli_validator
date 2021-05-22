package com.github.horitaku1124.cli_validator.model

data class HtmlTag(var type: TagType, var name: String? = null, var attr: Map<String, String>? = null) {
  enum class TagType {
    DocType, Text, Open, Close, Empty;
  }

  var innerId: Int? = null

  override fun toString():String {
    val str = name ?: ""
    var output = this.type.toString()
    output += "\t" + (str.replace("\n", "\\n"))
    if (attr != null && attr!!.isNotEmpty()) {
      output += "\t" + attr
    }
    return output
  }
}