package org.powlab.jeye.decode.processor.custom

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.RuntimeOpcode

object CustomInformator {
  def isCaseNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == USCODE_CASE
  def isDefaultNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == USCODE_DEFAULT

  def isSwitchChild(node: OpcodeNode): Boolean = isCaseNode(node) || isDefaultNode(node)

  def isTryCode(runtimeOpcode: RuntimeOpcode): Boolean = runtimeOpcode.opcode == USCODE_TRY
  def isTryNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == USCODE_TRY
  def isCatchNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == USCODE_CATCH
  def isFinallyNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == USCODE_FINALLY
}




