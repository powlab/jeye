package org.powlab.jeye.decode.processor.comparison

import org.powlab.jeye.core.Descriptors.isIntableDesc
import org.powlab.jeye.core.Descriptors.minDesc
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.ComparisonExpressions._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor

class ComparisonInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    runtimeOpcode.opcode match {
      case OPCODE_IFEQ => processCompWithZeroOpcode(node, SIGN_EQ)
      case OPCODE_IFGE => processCompWithZeroOpcode(node, SIGN_GE)
      case OPCODE_IFGT => processCompWithZeroOpcode(node, SIGN_GT)
      case OPCODE_IFLE => processCompWithZeroOpcode(node, SIGN_LE)
      case OPCODE_IFLT => processCompWithZeroOpcode(node, SIGN_LT)
      case OPCODE_IFNE => processCompWithZeroOpcode(node, SIGN_NE)
      case OPCODE_IF_ICMPEQ => processCompOpcode(node, SIGN_EQ)
      case OPCODE_IF_ICMPGE => processCompOpcode(node, SIGN_GE)
      case OPCODE_IF_ICMPGT => processCompOpcode(node, SIGN_GT)
      case OPCODE_IF_ICMPLE => processCompOpcode(node, SIGN_LE)
      case OPCODE_IF_ICMPLT => processCompOpcode(node, SIGN_LT)
      case OPCODE_IF_ICMPNE => processCompOpcode(node, SIGN_NE)
      case OPCODE_IF_ACMPEQ => processCompOpcode(node, SIGN_EQ)
      case OPCODE_IF_ACMPNE => processCompOpcode(node, SIGN_NE)
      case OPCODE_IFNULL => processCompWithNullOpcode(node, SIGN_EQ)
      case OPCODE_IFNONNULL => processCompWithNullOpcode(node, SIGN_NE)
      case OPCODE_LCMP  => processCmpOpcode(node)
      case OPCODE_FCMPL  => processCmpOpcode(node)
      case OPCODE_FCMPG  => processCmpOpcode(node)
      case OPCODE_DCMPL  => processCmpOpcode(node)
      case OPCODE_DCMPG  => processCmpOpcode(node)
      case _ => processException(node)
    }
  }

  private def processCmpOpcode(node: OpcodeNode) {
    val rightVariable = operandStacks.pop
    val leftVariable = operandStacks.pop
    val expression = new CmpExpression(leftVariable, rightVariable)
    push(expression)
  }

  private def processCompWithZeroOpcode(node: OpcodeNode, sign: SignExpression) {
    val leftVariable = operandStacks.pop
    val rightVariable = INT_0_EXPRESSION
    val correctedSign = reverseSign(sign)
    storeExpression(node, processIfStatement(leftVariable, correctedSign, rightVariable))
  }

  private def processCompOpcode(node: OpcodeNode, sign: SignExpression) {
    var rightVariable = operandStacks.pop
    var leftVariable = operandStacks.pop
    if (isIntableDesc(leftVariable.descriptor) && isIntableDesc(rightVariable.descriptor)) {
      val toDescriptor = minDesc(leftVariable.descriptor, rightVariable.descriptor)
      rightVariable = transform(rightVariable, toDescriptor)
      leftVariable = transform(leftVariable, toDescriptor)
    }
    val correctedSign = reverseSign(sign)
    storeExpression(node, processIfStatement(leftVariable, correctedSign, rightVariable))
  }

  private def processCompWithNullOpcode(node: OpcodeNode, sign: SignExpression) {
    val leftVariable = operandStacks.pop
    val rightVariable = NULL_EXPRESSION
    val correctedSign = reverseSign(sign)
    storeExpression(node, processIfStatement(leftVariable, correctedSign, rightVariable))
  }

  private def processIfStatement(leftVariable: ITypedExpression, sign: IExpression, rightVariable: ITypedExpression): IExpression = {
    if (isBooleanExpression(leftVariable, sign, rightVariable)) {
      new IfBooleanExpression(leftVariable, sign == SIGN_EQ)
    } else if (leftVariable.isInstanceOf[CmpExpression]) {
      /**
       * TODO here:
       * 1) не проверены опкод OPCODE_FCMPG
       * 2) Возможно переменная будет не слева а справа, нужна большая выборка, чтобы это понять
       */
      val cmpExpr = leftVariable.asInstanceOf[CmpExpression]
      new IfSimpleExpression(cmpExpr.leftExpression, sign, cmpExpr.rightExpression)
    }  else {
      new IfSimpleExpression(leftVariable, sign, rightVariable)
    }
  }

  /**
   * Проверить, трансформируется ли текущее состояние в простую boolean операцию
   * Пример, java-code:
   * if (!a1.equals(a2)) будет представлен как if (a1.equals(a2) == 0) что не совсем верно
   */
  private def isBooleanExpression(leftVariable: ITypedExpression, sign: IExpression, rightVariable: ITypedExpression): Boolean = {
    (sign == SIGN_EQ || sign == SIGN_NE) &&
        leftVariable.descriptor.baseType  == Types.TYPE_BOOLEAN &&
        rightVariable == INT_0_EXPRESSION
  }

}