package org.powlab.jeye.decode.expression

import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.utils.DecodeUtils

/**
 * Цикл for
 */
class ForExpression(val initExpr: IExpression, val condExpr: IExpression, val postExpr: IExpression) extends IExpression {
  def view(parent: IExpression): String = "for (" + resolve(initExpr) + resolve(condExpr) + resolvePost(postExpr) + ")"

  def classifier(): ExpressionClassifier = EC_FOR_CYCLE

  private def resolve(expression: IExpression): String = {
    val exprView = expression.view(this)
    if (exprView.lastOption.getOrElse('\0') != ';') {
      exprView + ";"
    } else {
      exprView
    }
  }

  private def resolvePost(expression: IExpression): String = {
    val exprView = expression.view(this)
    if (exprView.last == ';') {
      exprView.dropRight(1)
    } else {
      exprView
    }
  }

}

/**
 * Цикл foreach, пример
 * for (int a : b)
 *
 * где b массив int (int b[] = new int[] {0, 1, 2})
 *
 * varNameExpr   = a
 * iterableExpr  = b
 */
class ForeachExpression(val varNameExpr: ITypedExpression, val iterableExpr: IExpression) extends IExpression {
  def view(parent: IExpression): String = {
    val varType = DecodeUtils.getViewType(varNameExpr.descriptor.meta)
    "for (" + varType + " " + varNameExpr + " : " + iterableExpr + ")"
  }

  def classifier(): ExpressionClassifier = EC_FOREACH_CYCLE
}

/**
 * Цикл while, пример
 * while (a < 10)
 *
 * condition = a < 10
 */
class WhileCycleExpression(val condition: IExpression, semicolon: String = "") extends IExpression {
  val wrap = condition.classifier == EC_IF_BOOLEAN || condition.classifier == EC_IF_SIMPLE
  val whilePattern = DecodeUtils.select(wrap, "while (%s)" + semicolon, "while %s" + semicolon)
  def view(parent: IExpression): String = String.format(whilePattern, condition.view(this))
  def classifier(): ExpressionClassifier = EC_WHILE_CYCLE
}


