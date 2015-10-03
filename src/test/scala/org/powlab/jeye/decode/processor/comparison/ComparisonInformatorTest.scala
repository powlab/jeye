package org.powlab.jeye.decode.processor.comparison

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.TestUtils.makeNopOpcode
import org.powlab.jeye.decode.TestUtils.makeOpcode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode

class ComparisonInformatorTest {

  @Test
  def testIsIfNode() {
    assertFalse(isIfNode(null))
    assertFalse(isIfNode(makeNopOpcode))

    assertTrue(isIfNode(makeOpcode(OPCODE_IF_ACMPEQ)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IF_ACMPNE)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IF_ICMPEQ)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IF_ICMPGE)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IF_ICMPGT)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IF_ICMPLE)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IF_ICMPLT)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IF_ICMPNE)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IFEQ)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IFGE)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IFGT)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IFLE)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IFLT)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IFNE)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IFNONNULL)))
    assertTrue(isIfNode(makeOpcode(OPCODE_IFNULL)))
  }

}