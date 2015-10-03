package org.powlab.jeye.decode.processor.math

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.processor.math._
import org.powlab.jeye.decode.processor.math.MathInformator.getTwoOperationSign
import org.powlab.jeye.decode.expra.ExpressionAnalyzator._
import scala.collection.mutable.ArrayBuffer

class MathInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    runtimeOpcode.opcode match {
      case OPCODE_IADD
         | OPCODE_LADD
         | OPCODE_FADD
         | OPCODE_DADD
         | OPCODE_ISUB
         | OPCODE_LSUB
         | OPCODE_FSUB
         | OPCODE_DSUB
         | OPCODE_IMUL
         | OPCODE_LMUL
         | OPCODE_FMUL
         | OPCODE_DMUL
         | OPCODE_IDIV
         | OPCODE_LDIV
         | OPCODE_FDIV
         | OPCODE_DDIV
         | OPCODE_IREM
         | OPCODE_LREM
         | OPCODE_FREM
         | OPCODE_DREM
         | OPCODE_ISHL
         | OPCODE_LSHL
         | OPCODE_ISHR
         | OPCODE_LSHR
         | OPCODE_IUSHR
         | OPCODE_LUSHR
         | OPCODE_IAND
         | OPCODE_LAND
         | OPCODE_IOR
         | OPCODE_LOR
         | OPCODE_IXOR
         | OPCODE_LXOR => processTwoArgOperation(getTwoOperationSign(runtimeOpcode.opcode))
      case OPCODE_INEG
         | OPCODE_LNEG
         | OPCODE_FNEG
         | OPCODE_DNEG => processNegateOperation
      case OPCODE_IINC => processIncrementOperation(node)
      case _ => processException(node)
    }
  }

  private def processTwoArgOperation(sign: SignExpression) {
    val arg2 = operandStacks.pop
    val arg1 = operandStacks.pop
    // TODO here: определение дескриптора - выбор максимального из arg1 - arg2
    val mathExpr = MathExpression(arg1, sign, arg2)
    push(mathExpr)
  }

  private def processNegateOperation() {
    val value = operandStacks.pop
    val negateExpr = new NegateExpression(value);
    push(negateExpr)
  }

  private def processIncrementOperation(node: OpcodeNode) {
    val values = node.runtimeOpcode.values
    val index = values(0)
    val constant = values(1);
    val variable = localVariables(index);
    val details = tree.details(node)
    if (details.detailType == DETAIL_INC_LOAD_POST) {
      val newVar = new PostIncrementExpression(variable, constant)
      push(newVar)
    } else if (details.detailType == DETAIL_INC_LOAD_PRE) {
      val newVar = new PreIncrementExpression(variable, constant)
      push(newVar)
    } else {
      storeExpression(node, new PreIncrementExpression(variable, constant))
    }
  }

}