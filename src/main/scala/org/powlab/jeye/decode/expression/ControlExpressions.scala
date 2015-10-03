package org.powlab.jeye.decode.expression

import org.powlab.jeye.core._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.core.Descriptors._

class SwitchExpression(val variable: ITypedExpression) extends IExpression {
  def view(parent: IExpression): String = "switch(" + variable.view(this) + ")"
  def classifier(): ExpressionClassifier = EC_SWITCH
}

class ReturnVarExpression(val variable: ITypedExpression) extends ITypedExpression {
  def view(parent: IExpression): String = "return " + variable.view(this) + ";"
  def descriptor(): ParameterDescriptor = variable.descriptor
  def classifier(): ExpressionClassifier = EC_RETURN
}