package com.github.horitaku1124.cli_validator.model


class HtmlTag(var type: TagType, var name: String? = null) {
  enum class TagType {
    DocType, Text, Open, Close, Empty;
  };

  override fun toString():String {
    var str = name ?: ""
    return this.type.toString() + "\t" + (str.replace("\n", "\\n"))
  }
}