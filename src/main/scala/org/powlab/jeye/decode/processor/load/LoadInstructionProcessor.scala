package org.powlab.jeye.decode.processor.load

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.expression.GetArrayItemExpression

class LoadInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    runtimeOpcode.opcode match {
      case OPCODE_ILOAD
         | OPCODE_ILOAD_0
         | OPCODE_ILOAD_1
         | OPCODE_ILOAD_2
         | OPCODE_ILOAD_3
         | OPCODE_LLOAD
         | OPCODE_LLOAD_0
         | OPCODE_LLOAD_1
         | OPCODE_LLOAD_2
         | OPCODE_LLOAD_3
         | OPCODE_FLOAD
         | OPCODE_FLOAD_0
         | OPCODE_FLOAD_1
         | OPCODE_FLOAD_2
         | OPCODE_FLOAD_3
         | OPCODE_DLOAD
         | OPCODE_DLOAD_0
         | OPCODE_DLOAD_1
         | OPCODE_DLOAD_2
         | OPCODE_DLOAD_3
         | OPCODE_ALOAD
         | OPCODE_ALOAD_0
         | OPCODE_ALOAD_1
         | OPCODE_ALOAD_2
         | OPCODE_ALOAD_3  => processBaseLoadInstruction(runtimeOpcode)
      case OPCODE_BALOAD
         | OPCODE_CALOAD
         | OPCODE_SALOAD
         | OPCODE_AALOAD
         | OPCODE_IALOAD
         | OPCODE_LALOAD
         | OPCODE_FALOAD
         | OPCODE_DALOAD => processArrayLoadInstruction(runtimeOpcode)
      case _ => processException(node)
    }
  }

  private def processBaseLoadInstruction(runtimeOpcode: RuntimeOpcode) {
    val index = LoadInformator.getBaseLoadOpcodeIndex(runtimeOpcode)
    var localVar = localVariables(index)
    if (localVar == null) {
      localVar = NULL_EXPRESSION
    }
    push(localVar)
  }

  private def processArrayLoadInstruction(runtimeOpcode: RuntimeOpcode) {
    val index = operandStacks.pop
    val array = operandStacks.pop
    val desc = array.descriptor
    val selectedType = if (desc.isArray) desc.asInstanceOf[ArrayDescriptor].componentType else desc
    val arrayItemExpr = new GetArrayItemExpression(array, index, selectedType);
    push(arrayItemExpr);
  }

}