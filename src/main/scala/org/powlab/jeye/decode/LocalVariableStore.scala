package org.powlab.jeye.decode

import org.powlab.jeye.core._
import org.powlab.jeye.core.parsing.DescriptorParser._
import org.powlab.jeye.decode.processor.stack.StackInformator._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.utils.ConstantPoolUtils
import org.powlab.jeye.utils.AttributeUtils
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer

/**
 * Работа с локальными переменными
 */
object LocalVariableStore {

  /**
   * localCount - количество локальных переменных внутри метода (без учета параметров метода)
   */
  class LocalVariableStore(val localCount: Int, variables: Array[LocalVariable]) {
    private var typesUsage: Boolean = false
    def apply(index: Int): ITypedExpression = variables(index)
    def update(index: Int, variable: LocalVariable) { variables(index) = variable }
    def copy(variables: Array[LocalVariable]): LocalVariableStore = {
      val store = new LocalVariableStore(localCount, variables)
      store
    }
    // TODO here удалить метод позже (используется для печати)
    def store(): Array[LocalVariable] = variables
  }

  def apply(cpUtils: ConstantPoolUtils, method: MemberInfo, namer: Namer): LocalVariableStore = {
    val codeAttribute = AttributeUtils.get[CodeAttribute](method.attributes)
    val localVariables = new Array[LocalVariable](codeAttribute.max_locals)
    var localCount = codeAttribute.max_locals
    var index: Int = 0
    if (!AccessFlags.isStatic(method.access_flags)) {
      localVariables(index) = new LocalVariable(index, THIS_EXPRESSION, true, Pisc(cpUtils.thisClass.meta))
      index += 1
      localCount -= 1
    }
    val descriptor = cpUtils.getUtf8(method.descriptor_index)
    val methodDescriptor = parseMethodDescriptor(descriptor)

    val parameters = methodDescriptor.parameters
    parameters.foreach(parameter => {
      val argument = new LocalVariable(index, Sex(namer.getNextName(parameter.baseType, index)), true, parameter)
      localVariables(index) = argument
      if (isCategory2(argument)) {
        index += 1
        localCount -=1
      }
      index += 1
      localCount -= 1
    })
    new LocalVariableStore(localCount, localVariables)
  }

}