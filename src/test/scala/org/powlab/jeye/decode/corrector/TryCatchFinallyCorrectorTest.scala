package org.powlab.jeye.decode.corrector

import org.junit.Assert._
import org.junit.Test
import org.powlab.jeye.decode.TestUtils.makeNopOpcode
import org.powlab.jeye.decode.TestUtils.makeOpcode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.corrector.TryCatchFinallyCorrector.isSameNode
import org.powlab.jeye.core.Opcodes

class TryCatchFinallyCorrectorTest {

  /**
   * Тест на идентичность 2х инструкций
   */
  @Test
  def testIsSame {
    assertFalse(isSameNode(makeNopOpcode, null))
    assertFalse(isSameNode(null, makeNopOpcode))
    assertFalse(isSameNode(makeNopOpcode, makeOpcode(Opcodes.OPCODE_AALOAD)))

    assertTrue(isSameNode(null, null))
    assertTrue(isSameNode(makeNopOpcode, makeNopOpcode))
    assertTrue(isSameNode(makeOpcode(Opcodes.OPCODE_AALOAD, Array(0, 1, 2)), makeOpcode(Opcodes.OPCODE_AALOAD, Array(0, 1, 2))))
  }

}

