package org.powlab.jeye.decode.expression

import scala.collection.mutable.ListBuffer
import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.ExpressionViewer._
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._

import org.powlab.jeye.decode.expression.TernaryExpressionRef.TERNARY_STUB_EXPRESSION

/**
 * Выражения тернарного оператора
 */
object TernaryExpressionRef {
  val TERNARY_STUB_EXPRESSION = TernaryExpression(Sex("ternary not ready yet"), TRUE_EXPRESSION, FALSE_EXPRESSION)
}
/**
 * Ссылка на тернарное выражение
 * val TERNARY_STUB_EXPRESSION = Sex("ternary not ready yet")
 */
class TernaryExpressionRef(nativeDescriptor: ParameterDescriptor) extends ITypedExpression {
  var adaptedDescriptor = nativeDescriptor
  var expression: IExpression = TERNARY_STUB_EXPRESSION
  val values = new ListBuffer[IExpression]()
  def +=(that: IExpression): TernaryExpressionRef = {
    values += that
    this
  }

  def changeDescriptor(newDescriptor: ParameterDescriptor) {
    adaptedDescriptor = newDescriptor
  }

  def descriptor(): ParameterDescriptor = adaptedDescriptor;

  def view(parent: IExpression): String = {
    correctBracketsOnly(expression.view(this), false, parent.classifier, EC_MATH,
        EC_PRIMITIVE_CAST, EC_CHECK_CAST, EC_LINE)
  }

  def classifier(): ExpressionClassifier = EC_TERNARY_REF

}

object TernaryExpression {

  def apply(ifExpr: IExpression, positiveExpr: IExpression, negativeExpr: IExpression): ITypedExpression = {
    if (positiveExpr == TRUE_EXPRESSION && negativeExpr == FALSE_EXPRESSION) {
      new TernaryBooleanExpression(ifExpr)
    } else if (ifExpr.isInstanceOf[IfExpression] && positiveExpr == FALSE_EXPRESSION && negativeExpr == TRUE_EXPRESSION) {
      new TernaryBooleanExpression(ifExpr.asInstanceOf[IfExpression].reverse)
    } else {
      new TernaryExpression(ifExpr, positiveExpr, negativeExpr)
    }
  }
}

/**
 * Короткий вариант тернарного выражения
 */
class TernaryBooleanExpression(val ifExpr: IExpression) extends ITypedExpression {
  def view(parent: IExpression): String = ifExpr.view(this)

  def descriptor(): ParameterDescriptor = TYPE_BOOLEAN_DESCRIPTOR;
  def classifier(): ExpressionClassifier = EC_TERNARY_BOOLEAN

}

/**
 * Тернарное выражение
 */
class TernaryExpression(val ifExpr: IExpression, val positiveExpr: IExpression, val negativeExpr: IExpression) extends ITypedExpression {
  def view(parent: IExpression): String = ifExpr.view(this) + " ? " + positiveExpr.view(this) + " : " + negativeExpr.view(this)

  def descriptor(): ParameterDescriptor = positiveExpr.asInstanceOf[ITypedExpression].descriptor;
  def classifier(): ExpressionClassifier = EC_TERNARY

}


