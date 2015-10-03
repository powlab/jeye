package org.powlab.jeye.utils

import org.junit.Test
import org.junit.Assert._
import org.powlab.jeye.utils.StringWordParser._

class StringWordParserTest {

  @Test
  def testParser() {
    parse(null, Array(), (word => {fail("test fail point"); false}))
    parse("", Array(), (word => {fail("test fail point"); false}))
    parse(";", Array(';'), (word => {fail("test fail point"); false}))
    parse(";;;", Array(';'), (word => {fail("test fail point"); false}))
    var index = 0
    parse(";;;a", Array(';'), (word => {assertEquals("a", word); index += 1; false}))
    assertEquals(index, 1)
    index = 0

    parse("a;;;", Array(';'), (word => {assertEquals("a", word); index += 1; false}))
    assertEquals(index, 1)
    index = 0

    parse(";a;;", Array(';'), (word => {assertEquals("a", word); index += 1; false}))
    assertEquals(index, 1)
    index = 0

    parse("a;a;a;a", Array(';'), (word => {assertEquals("a", word); index += 1; false}))
    assertEquals(index, 4)
    index = 0

    parse("aaa", Array(';'), (word => {assertEquals("aaa", word); index += 1; false}))
    assertEquals(index, 1)
    index = 0
  }

  @Test
  def testParserBack() {
    parseBack(null, Array(), (word => {fail("test fail point"); false}))
    parseBack("", Array(), (word => {fail("test fail point"); false}))
    parseBack(";", Array(';'), (word => {fail("test fail point"); false}))
    parseBack(";;;", Array(';'), (word => {fail("test fail point"); false}))
    var index = 0
    parseBack(";;;a", Array(';'), (word => {assertEquals("a", word); index += 1; false}))
    assertEquals(index, 1)
    index = 0

    parseBack("a;;;", Array(';'), (word => {assertEquals("a", word); index += 1; false}))
    assertEquals(index, 1)
    index = 0

    parseBack(";a;;", Array(';'), (word => {assertEquals("a", word); index += 1; false}))
    assertEquals(index, 1)
    index = 0

    parseBack("a;a;a;a", Array(';'), (word => {assertEquals("a", word); index += 1; false}))
    assertEquals(index, 4)
    index = 0

    parseBack("aaa", Array(';'), (word => {assertEquals("aaa", word); index += 1; false}))
    assertEquals(index, 1)
    index = 0
  }
}
