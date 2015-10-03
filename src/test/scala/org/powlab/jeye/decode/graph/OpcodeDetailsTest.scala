package org.powlab.jeye.decode.graph

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.graph.OpcodeTreeListeners._
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.junit.Assert._
import org.junit.Test

class OpcodeDetailsTest {

  @Test
  def testIsIfDetails {
    assertFalse(isIfDetails(null))
    assertFalse(isIfDetails(new IfOpcodeDetail(DETAIL_COMMON)))

    assertTrue(isIfDetails(new IfOpcodeDetail))
    assertTrue(isIfDetails(new IfOpcodeDetail(DETAIL_IF_OR_AND_GROUP)))
    assertTrue(isIfDetails(new IfOpcodeDetail(DETAIL_IF_SINGLE)))
    assertTrue(isIfDetails(new IfOpcodeDetail(DETAIL_IF_XOR_GROUP)))
  }
}