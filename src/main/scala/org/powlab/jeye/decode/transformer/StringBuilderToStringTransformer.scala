package org.powlab.jeye.decode.transformer

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expra.ExpressionGuide.Guide
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.decode.expra.GuideContext
import org.powlab.jeye.decode.processor.store._
import org.powlab.jeye.decode.processor.reference._
import scala.collection.mutable.Stack
import org.powlab.jeye.decode.expression.ForeachExpression
import org.powlab.jeye.decode.expression.ForExpression

/**
 * Трансформер склеиваемых строк с помощью StringBuilder в конкатинацию их с помощью +
 * Пример:
 * // блок 1. До
 * new StringBuilder("A").append("B")
 * // блок 2. После
 * "A" + "B"
 */
class StringBuilderToStringTransformer() extends Guide {

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
    //if (true) return
    val clfE = expr.classifier
    if (clfE == EC_INVOKE_VIRTUAL && isToString(expr)) {
      val appendExprs = ArrayBuffer[IExpression]()
      var appendExpr = getOwnerValue(expr)
      do {
        appendExprs += getFirstArgument(appendExpr)
        appendExpr = getOwnerValue(appendExpr)
      } while (appendExpr != null && appendExpr.classifier == EC_INVOKE_VIRTUAL)
      if (appendExpr != null && appendExpr.classifier == EC_NEW_OBJECT) {
        val firstExpr = getFirstArgument(appendExpr)
        // Не всегда есть в кострукторе new StringBuilder начальное значение
        if (firstExpr != null) {
          if (isStringValueOfString(firstExpr)) {
            appendExprs += getFirstArgument(firstExpr)
          } else {
            appendExprs += firstExpr
          }
        }
        // так как выражения поступают в обратном порядке, то last - это первое выражение
        val headExpr = appendExprs.last
        if (getDescriptor(headExpr) != Descriptors.TYPE_STRING_DESCRIPTOR) {
          appendExprs += StringLiteralExpression("")
        }

        val stringConcatExpr = new TypedExpression(new LineExpression(appendExprs.reverse, " + "), Descriptors.TYPE_STRING_DESCRIPTOR, EC_TYPE)
        context.markAsReplaced(expr, stringConcatExpr, parent)
      }
    }
  }

  private def isToString(expr: IExpression): Boolean = {
    val invokable = getInvokable(expr)
    "toString" == invokable.method.name && invokable.arguments.argumentValues.isEmpty &&
      invokable.method.clazz.meta == Descriptors.TYPE_STRING_BUILDER_DESCRIPTOR.meta
  }

  /**
   * Если это обарачивание самой строки: 'String.valueOf(string0)'
   * где string0 - это строка.
   */
  private def isStringValueOfString(expr: IExpression): Boolean = {
    val invokable = getInvokable(expr)
    invokable != null && "valueOf" == invokable.method.name && invokable.arguments.argumentValues.size == 1 &&
      invokable.method.clazz.meta == Descriptors.TYPE_STRING_DESCRIPTOR.meta &&
      getFirstArgument(expr).descriptor == Descriptors.TYPE_STRING_DESCRIPTOR
  }
}
