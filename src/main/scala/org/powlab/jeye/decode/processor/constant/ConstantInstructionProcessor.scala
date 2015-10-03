package org.powlab.jeye.decode.processor.constant

import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.utils.DecodeUtils.getShort
import org.powlab.jeye.utils.DecodeUtils.intToByte

/**
 * Обработка констант
 */
class ConstantInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val values = runtimeOpcode.values
    runtimeOpcode.opcode match {
      case OPCODE_ACONST_NULL => push(NULL_EXPRESSION)
      case OPCODE_ICONST_M1 => push(INT_M1_EXPRESSION)
      case OPCODE_ICONST_0 => push(INT_0_EXPRESSION)
      case OPCODE_ICONST_1 => push(INT_1_EXPRESSION)
      case OPCODE_ICONST_2 => push(INT_2_EXPRESSION)
      case OPCODE_ICONST_3 => push(INT_3_EXPRESSION)
      case OPCODE_ICONST_4 => push(INT_4_EXPRESSION)
      case OPCODE_ICONST_5 => push(INT_5_EXPRESSION)
      case OPCODE_LCONST_0 => push(LONG_0_EXPRESSION)
      case OPCODE_LCONST_1 => push(LONG_1_EXPRESSION)
      case OPCODE_FCONST_0 => push(FLOAT_0_EXPRESSION)
      case OPCODE_FCONST_1 => push(FLOAT_1_EXPRESSION)
      case OPCODE_FCONST_2 => push(FLOAT_2_EXPRESSION)
      case OPCODE_DCONST_0 => push(DOUBLE_0_EXPRESSION)
      case OPCODE_DCONST_1 => push(DOUBLE_1_EXPRESSION)
      case OPCODE_BIPUSH => push(IntLiteralExpression(intToByte(values(0))))
      case OPCODE_SIPUSH => push(IntLiteralExpression(getShort(values(0), values(1))))
      case OPCODE_LDC => push(cpUtils.getConstantVariable(values(0)))
      case OPCODE_LDC_W | OPCODE_LDC2_W => push(cpUtils.getConstantVariable(getShort(values(0), values(1))))
      case OPCODE_NOP => doNothing(node)
      case _ => processException(node)
    }
  }

}