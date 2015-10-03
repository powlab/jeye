package org.powlab.jeye.decode.processor.custom

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.powlab.jeye.core.Opcodes.USCODE_CASE
import org.powlab.jeye.core.Opcodes.USCODE_CATCH
import org.powlab.jeye.core.Opcodes.USCODE_DEFAULT
import org.powlab.jeye.core.Opcodes.USCODE_FINALLY
import org.powlab.jeye.core.Opcodes.USCODE_TRY
import org.powlab.jeye.decode.TestUtils.makeNopOpcode
import org.powlab.jeye.decode.TestUtils.makeOpcode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isCaseNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isCatchNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isDefaultNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isFinallyNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isSwitchChild
import org.powlab.jeye.decode.processor.custom.CustomInformator.isTryNode

class CustomInformatorTest {

  @Test
  def testIsCaseNode {
    assertFalse(isCaseNode(null))
    assertFalse(isCaseNode(makeNopOpcode))

    assertTrue(isCaseNode(makeOpcode(USCODE_CASE)))
  }

  @Test
  def testIsDefaultNode {
    assertFalse(isDefaultNode(null))
    assertFalse(isDefaultNode(makeNopOpcode))

    assertTrue(isDefaultNode(makeOpcode(USCODE_DEFAULT)))
  }

  @Test
  def testIsSwitchChild {
    assertFalse(isSwitchChild(null))
    assertFalse(isSwitchChild(makeNopOpcode))

    assertTrue(isSwitchChild(makeOpcode(USCODE_CASE)))
    assertTrue(isSwitchChild(makeOpcode(USCODE_DEFAULT)))

  }

  @Test
  def testIsTryNode {
    assertFalse(isTryNode(null))
    assertFalse(isTryNode(makeNopOpcode))

    assertTrue(isTryNode(makeOpcode(USCODE_TRY)))
  }

  @Test
  def testIsCatchNode {
    assertFalse(isCatchNode(null))
    assertFalse(isCatchNode(makeNopOpcode))

    assertTrue(isCatchNode(makeOpcode(USCODE_CATCH)))
  }

  @Test
  def testIsFinallyNode {
    assertFalse(isFinallyNode(null))
    assertFalse(isFinallyNode(makeNopOpcode))

    assertTrue(isFinallyNode(makeOpcode(USCODE_FINALLY)))
  }

}




