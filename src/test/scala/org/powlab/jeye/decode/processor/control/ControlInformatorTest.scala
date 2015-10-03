package org.powlab.jeye.decode.processor.control

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.TestUtils.makeNopOpcode
import org.powlab.jeye.decode.TestUtils.makeOpcode
import org.powlab.jeye.decode.processor.control.SwitchInstructionInformator.isSwitchNode

class ControlInformatorTest {

  @Test
  def testIsSwitchNode {
    assertFalse(isSwitchNode(null))
    OPCODES.filter(opcode => opcode != OPCODE_TABLESWITCH && opcode != OPCODE_LOOKUPSWITCH).foreach(opcode =>
      assertFalse(isSwitchNode(makeOpcode(opcode))))

    assertTrue(isSwitchNode(makeOpcode(OPCODE_TABLESWITCH)))
    assertTrue(isSwitchNode(makeOpcode(OPCODE_LOOKUPSWITCH)))
  }

}