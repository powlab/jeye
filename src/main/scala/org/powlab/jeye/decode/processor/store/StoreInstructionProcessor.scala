package org.powlab.jeye.decode.processor.store

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types
import org.powlab.jeye.core.Descriptors.{descOrder, MAX_ORDER}
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.LocalVariable
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.expra.ExpressionHelpers.convertLiteral
import org.powlab.jeye.decode.transformer.BoxingUtils
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.expra.ExpressionHelpers

class StoreInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    runtimeOpcode.opcode match {
      case OPCODE_ISTORE
        | OPCODE_ISTORE_0
        | OPCODE_ISTORE_1
        | OPCODE_ISTORE_2
        | OPCODE_ISTORE_3
        | OPCODE_LSTORE
        | OPCODE_LSTORE_0
        | OPCODE_LSTORE_1
        | OPCODE_LSTORE_2
        | OPCODE_LSTORE_3
        | OPCODE_FSTORE
        | OPCODE_FSTORE_0
        | OPCODE_FSTORE_1
        | OPCODE_FSTORE_2
        | OPCODE_FSTORE_3
        | OPCODE_DSTORE
        | OPCODE_DSTORE_0
        | OPCODE_DSTORE_1
        | OPCODE_DSTORE_2
        | OPCODE_DSTORE_3
        | OPCODE_ASTORE
        | OPCODE_ASTORE_0
        | OPCODE_ASTORE_1
        | OPCODE_ASTORE_2
        | OPCODE_ASTORE_3 => processBaseStoreInstruction(node)
      case OPCODE_BASTORE
        | OPCODE_CASTORE
        | OPCODE_SASTORE
        | OPCODE_AASTORE
        | OPCODE_IASTORE
        | OPCODE_LASTORE
        | OPCODE_FASTORE
        | OPCODE_DASTORE => processArrayStoreInstruction(node)
      case _ => processException(node)
    }
  }

  private def processBaseStoreInstruction(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val index = StoreInformator.getBaseStoreOpcodeIndex(runtimeOpcode)
    val variable = operandStacks.pop
    // TODO here: это не совсем верно, javac может использовать переменную повторно, но с другим типом
    val details = tree.details(node)
    if (! isNewLocalVar(node, localVariables(index), variable)) {
      val locVar = localVariables(index)
      var locValue = variable
      if (!mc.draft) {
        locValue = convertLiteral(locValue, locVar.descriptor.baseType)
      }
      // TODO here: transform - это попытка корректировки типа
      transform(locVar, locValue.descriptor)
      if (details.detailType == DETAIL_STORE_DUPS) {
        val dupVariable = operandStacks.pop
        val newVar = LocalVariableExpression(locVar, locValue, dupVariable.descriptor)
        push(newVar)
        return
      }
      if (details.detailType  == DETAIL_INC_LOAD_POST || details.detailType == DETAIL_REF_INC_POST) {
        val incDetails = tree.incDetails(node)
        if (details.detailType  == DETAIL_INC_LOAD_POST || BoxingUtils.isBoxingMethod(variable)) {
          val incExpr = new PostIncrementExpression(locVar, incDetails.value)
          if (incDetails.dup) {
            operandStacks.pop
            push(incExpr)
          } else {
            storeExpression(node, incExpr)
          }
          return
        } else if (incDetails.dup) {
          operandStacks.pop
        }
      } else if (details.detailType  == DETAIL_INC_LOAD_PRE || details.detailType == DETAIL_REF_INC_PRE) {
        val incDetails = tree.incDetails(node)
        if (details.detailType  == DETAIL_INC_LOAD_PRE || BoxingUtils.isBoxingMethod(variable)) {
          val incExpr = new PreIncrementExpression(locVar, incDetails.value)
          if (incDetails.dup) {
            operandStacks.pop
            push(incExpr)
          } else {
            storeExpression(node, incExpr)
          }
          return
        } else if (incDetails.dup) {
          operandStacks.pop
        }
      }
      val newVar = LocalVariableExpression(locVar, locValue, variable.descriptor)
      storeExpression(node, newVar)
    } else {
      val prefvar = if (mc.draft) variable else trace.analyze(index, variable)
      val newNameExpr = Sex(namer.getNextName(prefvar.descriptor.baseType, node, tree))
      val newLocalVarExpr = new NewLocalVariableExpression(newNameExpr, prefvar, prefvar.descriptor);
      val localVariable = new LocalVariable(index, newNameExpr, false, prefvar.descriptor)
      // Такое бывает: int I = 1+(j < 3 ? I=j+3 : 32);
      // TODO here: это усложнение, возможно правильно логику новая/неновая переменная вынести в трансформер.
      if (details.detailType == DETAIL_STORE_DUPS) {
        val dupVariable = operandStacks.pop
        val newVar = LocalVariableExpression(localVariable, prefvar, dupVariable.descriptor)
        push(newVar)
      } else {
        storeExpression(node, newLocalVarExpr)
      }
      localVariables(index) = localVariable
      if (mc.draft) {
        trace.add(localVariable)
      }
    }
  }

  private def processArrayStoreInstruction(node: OpcodeNode) {
    val valueNative = operandStacks.pop
    val index = operandStacks.pop
    val array = operandStacks.pop
    val arrDesc = array.descriptor
    val descriptor = if (arrDesc.isInstanceOf[ArrayDescriptor]) arrDesc.asInstanceOf[ArrayDescriptor].componentType else arrDesc
    val details = tree.details(node)
    val value = transform(valueNative, descriptor)
    if (details.detailType == DETAIL_STORE_DUPS_ARRAY) {
      val dupVar = operandStacks.pop
      val newArrayVar = SetArrayItemExpression(array, index, value, dupVar.descriptor)
      push(newArrayVar)
    } else {
      storeExpression(node, SetArrayItemExpression(array, index, value, value.descriptor))
    }
  }

  private def isNewLocalVar(node: OpcodeNode, localVar: ITypedExpression, variable: ITypedExpression): Boolean = {
    if (localVar == null) {
      return true
    }
    // Главная задача в том, чтобы понять, что типы разные, если это так, то нужно дать новое имя переменной
    val isPrimitive1 = descOrder(localVar.descriptor) != MAX_ORDER
    val isPrimitive2 = descOrder(variable.descriptor) != MAX_ORDER
    if (isPrimitive1 != isPrimitive2) {
      return true
    }
    // Это маленький хак
    if (namer.getVariantsCount(node, tree) > 1) {
      val name = namer.getNextName(localVar.descriptor.baseType, node, tree)
      if (ExpressionHelpers.getVarName(localVar) != name) {
        return true
      }
    }
    false
  }

}