package org.powlab.jeye.decode

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.RuntimeOpcodes._
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.SimpleOpcodeNode

object TestUtils {

  val NODE_NOP = makeOpcode(OPCODE_NOP)

  def makeOpcode(opcode: OpCode): OpcodeNode = {
    makeOpcode(opcode, Array())
  }

  def makeOpcode(opcode: OpCode, values: Array[Int]): OpcodeNode = {
    val nothingOpcode = new RuntimeOpcode(0, opcode, values)
    val nothingNode = new SimpleOpcodeNode(nothingOpcode)
    nothingNode
  }

  def makeNopOpcode = NODE_NOP

}
