package com.github.horitaku1124.cli_validator.logic

import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Test

class HtmlNodePrintTests {
  var html1 = """<!DOCTYPE html>
<html lang="en">
<head>
<title>Test HTML1</title>
</head>
<body>
<p><img src="1.jpg" /></p>
<p><img src="2.jpg" ></p>
<p>This is text</p>
</body>
</html>"""
  @Test
  fun ppTest() {
    val parser = HtmlParser()
    val nodes = parser.parseHtml(html1)
    val trees = parser.extractTree(nodes)
    val text = HtmlNodePrint.prettyPrint(trees)
    println(text)
    assertThat(text, containsString("Test HTML1"))
  }
  @Test
  fun ppTest2() {
    val parser = HtmlParser()
    val trees = parser.parseHtmlToTree(html1)
    val text = HtmlNodePrint.prettyPrint(trees)
    println(text)
    assertThat(text, containsString("Test HTML1"))
  }
  @Test
  fun toHamlTest() {
    val parser = HtmlParser()
    val trees = parser.parseHtmlToTree(html1)
    val haml = HtmlNodePrint.toHaml(trees)
    println(haml)
    assertThat(haml, containsString("Test HTML1"))
  }
  @Test
  fun searchTest() {
    val parser = HtmlParser()
    val trees = parser.parseHtmlToTree(html1)
    val body = HtmlSearch.findByName(trees, "body")!!
    val text = HtmlNodePrint.prettyPrint(body)
    println(text)
    assertThat(text, containsString("This is text"))
  }
}
