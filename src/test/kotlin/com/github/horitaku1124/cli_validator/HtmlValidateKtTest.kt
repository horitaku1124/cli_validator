package com.github.horitaku1124.cli_validator

import com.github.horitaku1124.cli_validator.logic.HtmlParser
import com.github.horitaku1124.cli_validator.model.HtmlTag
import org.hamcrest.CoreMatchers.containsString
import org.hamcrest.CoreMatchers.not
import org.junit.Assert.assertThat
import org.junit.Before
import org.junit.Test
import org.hamcrest.CoreMatchers.`is` as Is

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
  var html2 = """<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<title>Test HTML</title>
  <!--[if lt IE 9]>
    <script src="/js/html5shiv.js"></script>
  <![endif]-->
</head>
<body>
This is main content
</body>
</html>"""
  var html3 = """<!DOCTYPE html>
<html lang="en">
<head>
<title>Test HTML3</title>
</head>
<body>
<p><img src="1.jpg" /></p>
<p><img src="2.jpg" ></p>
</body>
</html>"""
  var html4 = """<!DOCTYPE html>
<html lang="en">
<head>
<title>Test HTML4</title>
<script>
alert('abc');
</script>
<style>
* {
 margin:0;
 padding:0;
}
</style>
</head>
<body>
<p>This is HTML 4 document</p>
</body>
</html>"""

  var hv = HtmlParser()
  @Before
  fun setup() {
  }

  @Test
  fun parseHtml1() {
    val htmlList = hv.parseHtml("<html></html>")
    assertThat(htmlList.size, Is(2))
    assertThat(htmlList[0].type, Is(HtmlTag.TagType.Open))
    assertThat(htmlList[0].name, Is("html"))
    assertThat(htmlList[1].type, Is(HtmlTag.TagType.Close))
    assertThat(htmlList[1].name, Is("html"))
  }

  @Test
  fun parseHtml2() {
    val htmlList = hv.parseHtml(html1)
    assertThat(htmlList.size, Is(19))
  }
  @Test
  fun parseHtml3() {
    val htmlList = hv.parseHtml(html1)
    val nodeTree = hv.extractTree(htmlList)
    val texts = hv.allTextFromTree(nodeTree)
    assertThat(texts, containsString("This is main content"))
  }
  @Test
  fun parseHtml4() {
    val htmlList = hv.parseHtml(html2)
    assertThat(htmlList.size, Is(20))
  }

  @Test
  fun parseHtml5() {
    val htmlList = hv.parseHtml("<a class='c1 c2 ' href='/' \n  width='100' \n  height='200'>")
    assertThat(htmlList.size, Is(1))
  }

  @Test
  fun parseHtml6() {
    val htmlList = hv.parseHtml(html3)
    assertThat(htmlList.get(15).name, Is("img"))
    assertThat(htmlList.get(15).type, Is(HtmlTag.TagType.Empty))
    assertThat(htmlList.get(19).name, Is("img"))
    assertThat(htmlList.get(19).type, Is(HtmlTag.TagType.Open))
    assertThat(htmlList.size, Is(25))
  }

  @Test
  fun parseHtml7() {
    val html = hv.removeScriptTag(hv.removeStyleTag(html4))
    val htmlList = hv.parseHtml(html)
    val nodeTree = hv.extractTree(htmlList)
    val text = hv.allTextFromTree(nodeTree)
    assertThat(text, containsString("This is HTML 4 document"))
    assertThat(text, not(containsString("alert('abc');")))
    assertThat(text, not(containsString("margin:0;")))
  }
}
