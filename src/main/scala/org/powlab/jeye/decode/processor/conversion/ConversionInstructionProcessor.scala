package org.powlab.jeye.decode.processor.conversion

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.expression.ConversionExpression

class ConversionInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    runtimeOpcode.opcode match {
      case OPCODE_I2L
         | OPCODE_F2L
         | OPCODE_D2L => processConversion(TYPE_LONG)
      case OPCODE_I2F
         | OPCODE_L2F
         | OPCODE_D2F => processConversion(TYPE_FLOAT)
      case OPCODE_I2D
         | OPCODE_L2D
         | OPCODE_F2D => processConversion(TYPE_DOUBLE)
      case OPCODE_L2I
         | OPCODE_F2I
         | OPCODE_D2I => processConversion(TYPE_INT)
      case OPCODE_I2B => processConversion(TYPE_BYTE)
      case OPCODE_I2C => processConversion(TYPE_CHAR)
      case OPCODE_I2S => processConversion(TYPE_SHORT)
      case _ => processException(node)
    }
  }

  private def processConversion(baseType: BaseType) {
    val variable = operandStacks.pop
    val convertExpr = ConversionExpression(baseType, variable)
    push(convertExpr)
  }

}