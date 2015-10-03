package org.powlab.jeye.decode

import org.powlab.jeye.core._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.parsing.DescriptorParser._
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.decode.processor.stack.StackInformator._
import org.powlab.jeye.decode.processor.conversion._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.utils.ConstantPoolUtils
import org.powlab.jeye.utils.AttributeUtils
import scala.collection.mutable.Map
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.expression.ConversionExpression

/**
 * Работа с локальными переменными
 */
object LocalVariableTrace {

  def apply(): LocalVariableTrace = {
    new LocalVariableTrace()
  }

}

private class Trace(val variable: LocalVariable, val descriptor: ParameterDescriptor)

class LocalVariableTrace() {
  private var completed: Boolean = false
  private val traces = new ArrayBuffer[Trace]
  private val descriptors = Map[Int, ParameterDescriptor]()

  def add(variable: LocalVariable) {
    add(variable, variable.descriptor)
  }

  def add(variable: LocalVariable, descriptor: ParameterDescriptor) {
    traces += new Trace(variable, descriptor)
  }

  def complete() {
    completed = true
    traces.filter(! _.variable.isSynthetic).foreach(trace => {
      val descriptor = trace.descriptor
      val variable = trace.variable
      if (!descriptors.contains(variable.index)) {
        descriptors.put(variable.index, descriptor)
      } else {
        val keepedDescriptor = descriptors(variable.index)
        // работа с примитивами
        if (isSubInt(descriptor.baseType)) {
          descriptors.put(variable.index, descriptor)
        }
      }
    })
  }

  def analyze(index: Int, variable: ITypedExpression): ITypedExpression = {
    if (! descriptors.contains(index)) {
      return variable
    }
    val descriptor = descriptors(index)
    if (! isIntable(descriptor.baseType)) {
      return variable
    }
    return convertLiteral(variable, descriptor.baseType)
  }

  def checkcast(variable: ITypedExpression, descriptor: ParameterDescriptor): ITypedExpression = {
    if (descriptor == TYPE_INT_DESCRIPTOR && isSubIntDesc(variable.descriptor)) {
        return ConversionExpression(descriptor.baseType, variable)
    }
    variable
  }

}