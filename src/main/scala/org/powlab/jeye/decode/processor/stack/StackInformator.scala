package org.powlab.jeye.decode.processor.stack

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types
import org.powlab.jeye.decode.expression.ITypedExpression
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.core.Descriptors

/**
 * Информатор для стэк инструкций
 */
object StackInformator {

  def isCategory1(variable: ITypedExpression) = {
    variable != null && !isCategory2(variable)
  }

  def isCategory2(variable: ITypedExpression) = {
    variable != null && Descriptors.isCategory2(variable.descriptor)
  }

  def isDupNode(node: OpcodeNode) = node != null && node.runtimeOpcode.opcode == OPCODE_DUP
  def isDupX1Node(node: OpcodeNode) = node != null && node.runtimeOpcode.opcode == OPCODE_DUP_X1
  def isDup2X1Node(node: OpcodeNode) = node != null && node.runtimeOpcode.opcode == OPCODE_DUP2_X1
  def isDup2Node(node: OpcodeNode) = node != null && node.runtimeOpcode.opcode == OPCODE_DUP2


}