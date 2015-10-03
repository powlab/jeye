package org.powlab.jeye.decode.processor.control

import org.powlab.jeye.core.Descriptors.TYPE_INT_DESCRIPTOR
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.ClassFacade.methodKey
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.expra.ExpressionAnalyzator._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.expression.GetArrayItemExpression
import org.powlab.jeye.utils.DecodeUtils
import org.powlab.jeye.decode.expression.SwitchExpression
import org.powlab.jeye.decode.expression.ReturnVarExpression
import org.powlab.jeye.decode.graph.OpcodeNode

class ControlInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    runtimeOpcode.opcode match {
      case OPCODE_IRETURN => processReturnOpcode(node)
      case OPCODE_LRETURN => processReturnOpcode(node)
      case OPCODE_FRETURN => processReturnOpcode(node)
      case OPCODE_DRETURN => processReturnOpcode(node)
      case OPCODE_ARETURN => processReturnOpcode(node)
      case OPCODE_RETURN => processReturnOpcode(node)
      case OPCODE_TABLESWITCH => processTableSwitchOpcode(node)
      case OPCODE_LOOKUPSWITCH => processLookupSwitchOpcode(node)
      case OPCODE_GOTO => processGotoOpcode(node)
      case OPCODE_GOTO_W => processGotoOpcode(node)
      case OPCODE_JSR => doNothing(node)
      case OPCODE_JSR_W => doNothing(node)
      case OPCODE_RET => doNothing(node)
      case _ => processException(node)
    }
  }

  private def processReturnOpcode(node: OpcodeNode) {
    val baseType = ReturnInstructionInformator.getReturnType(node)
    if (baseType == TYPE_VOID) {
      // очищаем стэк значений, так как осуществляется выход из метода
      operandStacks.clear
      // return;
      storeExpression(node, EMPTY_RETURN_EXPRESSION)
    } else {
      val methodDescriptor = classFacade.getMethodDescriptor(methodKey(mc.method))
      val variable = popAndFix(methodDescriptor.returnType)
      val returnExpr = new ReturnVarExpression(variable);
      // например return 10;
      storeExpression(node, returnExpr)
    }
  }

  private def processTableSwitchOpcode(node: OpcodeNode) {
    processSwitchOpcode(node)
  }

  private def processLookupSwitchOpcode(node: OpcodeNode) {
    processSwitchOpcode(node)
  }

  private def processSwitchOpcode(node: OpcodeNode) {
    val variable = operandStacks.pop
    val switchExpr = new SwitchExpression(prepareSwitchVariable(variable))
    storeExpression(node, switchExpr)
  }

  /**
   * Преобразуем switch expr, если это switch по enum
   */
  def prepareSwitchVariable(variable: ITypedExpression): ITypedExpression = {
    scan(variable, (exception, parent) => {
      val getItemExprOpt = get[GetArrayItemExpression](variable)
      if (getItemExprOpt.isEmpty) {
        return variable
      }
      val getItemExpr = getItemExprOpt.get
      val ivokeOpt = get[InvokeVirtualExpression](getItemExpr.indexVariable)
      if (ivokeOpt.isEmpty) {
        return variable
      }
      val invokeExp = ivokeOpt.get
      if (!"ordinal".equals(invokeExp.methodName)) {
        return variable
      }
      val reference = invokeExp.ownerValue
      val className = DecodeUtils.getSimpleClassName(reference.descriptor.meta)
      val enumFieldName = mc.extra.getEnumFieldName(className, 1)
      if (enumFieldName != null) {
        return reference
      }
      return invokeExp
    })
    variable
  }

  private def processGotoOpcode(node: OpcodeNode) {
    storeExpression(node, JUMPED_EXPRESSION)
  }

}