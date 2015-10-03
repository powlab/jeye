package org.powlab.jeye.decode.processor.stack

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.processor.stack.StackInformator._
import org.powlab.jeye.decode.expression.ExpressionClassifiers.EC_LOCAL_VARIABLE
import org.powlab.jeye.decode.graph.OpcodeNode

class StackInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    runtimeOpcode.opcode match {
      case OPCODE_DUP => processDupOpcode
      case OPCODE_DUP_X1 => processDupX1Opcode
      case OPCODE_DUP_X2 => processDupX2Opcode
      case OPCODE_DUP2 => processDup2Opcode
      case OPCODE_DUP2_X1 => processDup2X1Opcode
      case OPCODE_DUP2_X2 => processDup2X2Opcode
      case OPCODE_POP => processPopOpcode(node)
      case OPCODE_POP2 => processPop2Opcode(node)
      case OPCODE_SWAP => processSwapOpcode
      case _ => processException(node)
    }
  }

  private def processDupOpcode() {
    val variable = operandStacks.pop
    push(variable)
    push(variable)
  }

  private def processDupX1Opcode() {
    val variable1 = operandStacks.pop
    val variable2 = operandStacks.pop
    push(variable1)
    push(variable2)
    push(variable1)
  }

  private def processDupX2Opcode() {
    val variable1 = operandStacks.pop
    val variable2 = operandStacks.pop
    if (isCategory2(variable2)) {
      push(variable1)
      push(variable2)
      push(variable1)
    } else {
      val variable3 = operandStacks.pop
      push(variable1)
      push(variable3)
      push(variable2)
      push(variable1)
    }
  }

  private def processDup2Opcode() {
    val variable1 = operandStacks.pop
    if (isCategory2(variable1)) {
      push(variable1)
      push(variable1)
    } else {
      val variable2 = operandStacks.pop
      push(variable2)
      push(variable1)
      push(variable2)
      push(variable1)
    }
  }

  private def processDup2X1Opcode() {
    val variable1 = operandStacks.pop
    val variable2 = operandStacks.pop
    // Form 2
    if (isCategory2(variable1) && isCategory1(variable2)) {
      push(variable1)
      push(variable2)
      push(variable1)
    } else {
      // Form 1
      val variable3 = operandStacks.pop
      push(variable2)
      push(variable1)
      push(variable3)
      push(variable2)
      push(variable1)
    }
  }

  private def processDup2X2Opcode() {
    val variable1 = operandStacks.pop
    val variable2 = operandStacks.pop
    // form4:
    if (isCategory2(variable1) && isCategory2(variable2)) {
      push(variable1)
      push(variable2)
      push(variable1)
    } else {
      val variable3 = operandStacks.pop
      // form3:
      if (!isCategory2(variable1)
        && !isCategory2(variable2)
        && isCategory2(variable3)) {
        push(variable2)
        push(variable1)
        push(variable3)
        push(variable2)
        push(variable1)

      } else // form2:
      if (isCategory2(variable1)
        && !isCategory2(variable2)
        && !isCategory2(variable3)) {
        push(variable1)
        push(variable3)
        push(variable2)
        push(variable1)
        // form1:
      } else {
        val variable4 = operandStacks.pop
        push(variable2)
        push(variable1)
        push(variable4)
        push(variable3)
        push(variable2)
        push(variable1)
      }
    }
  }

  private def processPopOpcode(node: OpcodeNode) {
    processVariable(node)
  }

  private def processPop2Opcode(node: OpcodeNode) {
    val variable = processVariable(node)
    if (isCategory1(variable)) {
      processVariable(node)
    }
  }

  /**
   * TODO here: при выталкивании variable из стека в нашей логики существует 2 сценария
   * 1) Выталкиваемое значение нужно сохранить как выражение
   * 2) Выталкиваемое значение нужно проигнорировать
   * Для принятия решения требуется найти некоторое количество примеров с pop.
   * На данном этапе реализована наипростейшая логика
   */
  private def processVariable(node: OpcodeNode): ITypedExpression = {
    val expression = operandStacks.pop
    if (!(expression == NULL_EXPRESSION || expression.classifier == EC_LOCAL_VARIABLE)) {
        storeExpression(node, expression)
    }
    expression
  }

  private def processSwapOpcode() {
    val variable1 = operandStacks.pop
    val variable2 = operandStacks.pop
    push(variable1)
    push(variable2)
  }

}