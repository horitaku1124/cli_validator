package com.github.horitaku1124.cli_validator.model


class HtmlTag(var type: TagType, var name: String) {
  enum class TagType {
    Open, Close, Empty;
  };

  override fun toString():String {
    return this.type.toString() + "\t" + name
  }
}