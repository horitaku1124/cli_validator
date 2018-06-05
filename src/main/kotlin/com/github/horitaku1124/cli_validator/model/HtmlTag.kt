package com.github.horitaku1124.cli_validator.model


class HtmlTag(var type: TagType, var name: String? = null, var attr: HashMap<String, String>? = null) {
  enum class TagType {
    DocType, Text, Open, Close, Empty;
  };

  override fun toString():String {
    var str = name ?: ""
    var output = this.type.toString()
    output += "\t" + (str.replace("\n", "\\n"))
    if (attr != null && attr!!.isNotEmpty()) {
      output += "\t" + attr
    }
    return output
  }
}