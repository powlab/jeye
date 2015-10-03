package org.powlab.jeye.decode.expression

import org.powlab.jeye.core._
import org.powlab.jeye.decode.expression.ExpressionClassifiers.EC_GET_ARRAY_ITEM

class GetArrayItemExpression(val arrayVariable: IExpression, val indexVariable: IExpression, val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = arrayVariable.view(this) + "[" + indexVariable.view(this) + "]"
  def classifier(): ExpressionClassifier = EC_GET_ARRAY_ITEM
}