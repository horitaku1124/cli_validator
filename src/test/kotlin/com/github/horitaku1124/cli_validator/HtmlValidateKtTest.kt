package com.github.horitaku1124.cli_validator

import com.github.horitaku1124.cli_validator.HtmlValidator
import com.github.horitaku1124.cli_validator.model.HtmlTag
import org.hamcrest.CoreMatchers.`is` as Is
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test

class HtmlValidateKtTest {
  var html1 = """<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Test HTML</title>
</head>
<body>
This is main content
</body>
</html>"""
  var hv = HtmlValidator()
  @Before
  fun setup() {

  }

  @Test
  fun parseHtml1() {
    var htmlList = hv.parseHtml("<html></html>")
    assertThat(htmlList.size, Is(2))
    assertThat(htmlList[0].type, Is(HtmlTag.TagType.Open))
    assertThat(htmlList[0].name, Is("html"))
    assertThat(htmlList[1].type, Is(HtmlTag.TagType.Close))
    assertThat(htmlList[1].name, Is("html"))
  }
  @Test
  fun parseHtml2() {
    var htmlList = hv.parseHtml(html1)
    assertThat(htmlList.size, Is(19))
  }

  @Test
  fun parseAttr() {
  }

}
