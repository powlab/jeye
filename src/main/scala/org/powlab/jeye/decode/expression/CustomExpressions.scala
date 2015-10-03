package org.powlab.jeye.decode.expression

import org.powlab.jeye.decode.expression.ExpressionClassifiers._

class CaseExpression(val value: IExpression) extends IExpression {
  def view(parent: IExpression): String = "case " + value + ":"
  def classifier(): ExpressionClassifier = EC_CASE
}

/**
 * Пример_1: catch(Exception ex)
 * Пример_2: catch(IllegalStateException | IndexOutOfBoundsException iofbe)
 */
class CatchExpression(name: String, catchTypes: Array[String]) extends IExpression {
  def view(parent: IExpression): String = catchTypes.mkString("catch (", " | ", " " + name + ")")
  def classifier(): ExpressionClassifier = EC_CATCH
}




