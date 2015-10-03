package org.powlab.jeye.decode.graph

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception._
import org.junit.Assert._
import org.junit.Test
import org.powlab.jeye.decode.TestUtils._
import org.powlab.jeye.core.Opcodes._

/**
 * Описание узлов.
 * Требования: ReadOnly
 */
class OpcodeNodesTest {

  @Test
  def testScanOpcodeNodes {
    // 1. стресс тест -> null
    try {
     scanOpcodeNodes(null, node => {fail("Обработчик не должен быть вызван")})
    } catch {
      case ex: Throwable => assertTrue(ex.getMessage().contains("Область: " + Exception.NODE_AREA))
    }

    // 2. single
    scanOpcodeNodes(NODE_NOP, node => {assertEquals(NODE_NOP, node)})

    // 3. group
    val opcodes = ArrayBuffer[OpcodeNode](NODE_NOP)
    val group = new GroupOpcodeNode(opcodes, false)
    val expected = ArrayBuffer[OpcodeNode](NODE_NOP, group)
    val actual = new ArrayBuffer[OpcodeNode]
    scanOpcodeNodes(group, node => {actual += node})
    assertEquals(expected, actual)
  }

}