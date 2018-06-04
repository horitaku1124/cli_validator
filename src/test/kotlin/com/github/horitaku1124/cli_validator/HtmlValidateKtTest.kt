package com.github.horitaku1124.cli_validator

import org.junit.Test
import com.github.horitaku1124.cli_validator.HtmlValidator

class HtmlValidateKtTest {

  @Test
  fun parseHtml() {
    var hv = HtmlValidator()
    hv.parseHtml("a")
  }

  @Test
  fun parseAttr() {
  }

}
