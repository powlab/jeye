package org.powlab.jeye.decode.transformer

import org.powlab.jeye.decode.expra.ExpressionGuide.Guide
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.decode.expra.GuideContext
import org.powlab.jeye.decode.expression.CheckCastExpression
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.MathExpression._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.utils.DecodeUtils
import java.lang.Double

/**
 * Трансормер для корректировки synchronized блоков
 * Пример:
 * было 'synchronized(reference1 = this)'
 * стало 'synchronized(this)'
 */
class SynchronizeCorrectTransformer extends Guide {

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
//    if (true) return
    val clfE = expr.classifier
    val clfP = parent.classifier
    if (clfE == EC_SYNCHRONIZE) {
      val syncValueExpr = getFirst(expr)
      if (syncValueExpr.classifier == EC_STORE_VAR) {
        val assignValue = getAssignValue(syncValueExpr)
        context.markAsReplaced(syncValueExpr, assignValue, expr)
        // TODO here: переделать на пост обработку или отдельный трансформер для eclipse
        // ejc (примечание: таким образом удаляем декларацию переменной)
        context.markAsRemoved(syncValueExpr, EMPTY_BLOCK_EXPRESSION)
      }
    }
  }

}
