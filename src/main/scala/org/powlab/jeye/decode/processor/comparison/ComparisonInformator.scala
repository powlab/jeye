package org.powlab.jeye.decode.processor.comparison

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.processor.control.BranchBase
import org.powlab.jeye.decode.graph.OpcodeNode

object ComparisonInformator extends BranchBase {

  val ifMap = Map(
    OPCODE_IFEQ -> true,
    OPCODE_IFGE -> true,
    OPCODE_IFGT -> true,
    OPCODE_IFLE -> true,
    OPCODE_IFLT -> true,
    OPCODE_IFNE -> true,
    OPCODE_IF_ICMPEQ -> true,
    OPCODE_IF_ICMPGE -> true,
    OPCODE_IF_ICMPGT -> true,
    OPCODE_IF_ICMPLE -> true,
    OPCODE_IF_ICMPLT -> true,
    OPCODE_IF_ICMPNE -> true,
    OPCODE_IF_ACMPEQ -> true,
    OPCODE_IF_ACMPNE -> true,
    OPCODE_IFNULL -> true,
    OPCODE_IFNONNULL -> true)

  def matches(opcode: OpCode): Boolean = ifMap.getOrElse(opcode, false)
  def matches(runtimeOpcode: RuntimeOpcode): Boolean = matches(runtimeOpcode.opcode)
  def isIfCode(runtimeOpcode: RuntimeOpcode): Boolean = matches(runtimeOpcode)
  def isIfNode(node: OpcodeNode): Boolean = node != null && matches(node.runtimeOpcode);

}