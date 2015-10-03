package org.powlab.jeye.decode

import org.powlab.jeye.core._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import scala.collection.mutable.Stack

object LocalVariable {

  val SYNTHETIC = -1

  def apply(index: Int, expression: ITypedExpression): LocalVariable = {
    new LocalVariable(index, expression, false, expression.descriptor)
  }

  def apply(expression: IExpression, descriptor: ParameterDescriptor): LocalVariable = {
    new LocalVariable(SYNTHETIC, expression, false, descriptor)
  }
}

/** Пояснение: Для локальных переменных необходимо хранить индекс, чтобы можно было определть тип */
class LocalVariable(val index: Int, val name: IExpression, val argument: Boolean, val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = name.view(this)

  def classifier(): ExpressionClassifier = EC_LOCAL_VARIABLE

  def isSynthetic = index == LocalVariable.SYNTHETIC
}
