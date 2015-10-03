package org.powlab.jeye.utils

import org.powlab.jeye.core
import org.powlab.jeye.core.{Utils, Types}
import org.powlab.jeye.core.Types._
import org.junit.Test
import org.junit.Assert._
import org.powlab.jeye.utils.DecodeUtils._

class DecodeUtilsTest {

  @Test
  def getSimpleClassNameTest() {
    assertEquals("Object", getSimpleClassName("Object"))
    assertEquals("Object", getSimpleClassName("Object;"))
    assertEquals("Object", getSimpleClassName("[Object"))
    assertEquals("Object", getSimpleClassName("[Object;"))
    assertEquals("Object", getSimpleClassName("java/lang/Object"))
    assertEquals("Object", getSimpleClassName("java/lang/Object;"))
    assertEquals("Object", getSimpleClassName("java.lang.Object"))
    assertEquals("Object", getSimpleClassName("java.lang.Object;"))
    assertEquals("Object", getSimpleClassName("Ljava/lang/Object"))
    assertEquals("Object", getSimpleClassName("Ljava/lang/Object;"))
    assertEquals("Object", getSimpleClassName("Ljava.lang.Object;"))
    assertEquals("Object.Inner", getSimpleClassName("Ljava.lang.Object$Inner;"))

    assertNull(null, getSimpleClassName(null))
    assertNull(null, getSimpleClassName(""))
  }
}
