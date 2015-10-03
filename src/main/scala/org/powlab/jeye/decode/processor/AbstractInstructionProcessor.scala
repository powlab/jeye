package org.powlab.jeye.decode.processor

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes.{ OpCode, categoryName }
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.utils.PrintUtils
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.core.Types
import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception._
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.expra.ExpressionHelpers.convertLiteral
import org.powlab.jeye.decode.LocalVariable
import org.powlab.jeye.decode.graph.OpcodeNode

abstract class AbstractInstructionProcessor(val mc: MethodContext) {

  //Здесь расположить алиасы на методы из mc

  def process(node: OpcodeNode)

  protected val operandStacks = mc.frames

  protected val localVariables = mc.localVariables

  protected val classFile = mc.classFile

  protected val cpUtils = classFile.constantPoolUtils;

  protected val namer = mc.namer

  protected val tree = mc.tree

  protected val trace = mc.trace

  protected val classFacade = mc.extra.classFacade


  def storeExpression(node: OpcodeNode, expression: IExpression) {
    tree.details(node).expression = expression
    tree.resources += expression.classifier
  }

  def push(expression: ITypedExpression) {
    operandStacks.push(expression)
    tree.resources += expression.classifier
  }

  def processException(node: OpcodeNode) {
    val opcode = node.runtimeOpcode.opcode
    val reason = "Обнаружен opcode '" + opcode.name + "' (категория '" + categoryName(opcode.category) + "'), для которого не найден обработчик в процессоре " + getClass().getSimpleName()
    val methodName = cpUtils.getUtf8(mc.method.name_index)
    val effect = "Обработка инструкций в методе '" + methodName + "' будет прекращена."
    val action = "Необходимо добавить обработчик для '" + opcode.name + "' инструкции"
    throw Exception(PROCESSOR_AREA, reason, effect, action)
  }

  def doNothing(graph: OpcodeNode) {}

  /**
   * Цель: скорректировать тип локальной переменной типа int в boolean, byte, char, short
   */
  def popAndFix(descriptor: ParameterDescriptor): ITypedExpression = {
    transform(operandStacks.pop, descriptor)
  }

  def transform(variable: ITypedExpression, descriptor: ParameterDescriptor): ITypedExpression = {
    if (mc.draft && variable.isInstanceOf[LocalVariable]) {
      val localVar = variable.asInstanceOf[LocalVariable]
      trace.add(localVar, descriptor)
    } else if (!mc.draft) {
      if (Types.isSubInt(descriptor.baseType)) {
        return convertLiteral(variable, descriptor.baseType)
      }
      val newVar = trace.checkcast(variable, descriptor)
      if (newVar != variable) {
        tree.resources += newVar.classifier
        return newVar
      }
    }
    return variable
  }

}

trait IndexType {
  // extractors
  type getIndex = (RuntimeOpcode) => Int
  val getIndexR: getIndex = (runtimeOpcode => runtimeOpcode.values(0))
  val getIndex0: getIndex = (runtimeOpcode => 0)
  val getIndex1: getIndex = (runtimeOpcode => 1)
  val getIndex2: getIndex = (runtimeOpcode => 2)
  val getIndex3: getIndex = (runtimeOpcode => 3)
  val getIndexM1: getIndex = (runtimeOpcode => -1)

  def map(opcodes: Array[OpCode], baseType: BaseType): Map[OpCode, BaseType] = opcodes.map(opcode => opcode -> baseType).toMap
}