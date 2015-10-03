package org.powlab.jeye.decode.transformer

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core.Descriptors.TYPE_ITERATOR_DESCRIPTOR
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
 * Поиск блоков foreach для массивов и сварачивание этой структуры
 * Пример:
 * // блок 1
 * Iterator reference3 = reference0.iterator();
 * while (reference3.hasNext()) {
 *   String reference4 = (String) reference3.next();
 *   System.out.println(reference4);
 * }
 * // блок 2
 * for (String text : texts) {
 *   System.out.println(text);
 * }
 *
 *
 */
class ForeachIterableTransformer() extends Guide {

  /** Ссылка на итератор */
  private var iteratorRef: IExpression = null

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
//    if (true) return
    val clfE = expr.classifier
    if (clfE == EC_STATEMENT) {
      val cycleRef = getFirst(expr)
      // проверяем, что это наш случай
      if (checkIteratorRef && checkCycleRef(cycleRef) && checkFirstExpr(expr.asInstanceOf[StatementExpression])) {
        context.markAsRemoved(iteratorRef, parent)
        val getIterExpr = getAssignValue(iteratorRef)
        val referenceName = getOwnerValue(getIterExpr)

        val setVarExpr = expr.asInstanceOf[StatementExpression].expressions.head
        context.markAsRemoved(setVarExpr, expr)
        val varName = getVarName(setVarExpr)
        val descriptor = getDescriptor(setVarExpr)
        val foreachExpr = new ForeachExpression(TypedExpression(varName, descriptor), referenceName)
        context.markAsReplaced(cycleRef, foreachExpr, expr)
      }
      iteratorRef = null
    } else if (parent.classifier.has(EA_DEPTH) && clfE != EC_LABEL) {
      iteratorRef = expr
    }
  }

  /**
   * iteratorRef должна соответствовать reference.iterator()
   */
  private def checkIteratorRef(): Boolean = {
    if (iteratorRef != null && iteratorRef.classifier == EC_STORE_NEW_VAR) {
      val assignValue = getAssignValue(iteratorRef)
      return assignValue.classifier == EC_INVOKE_INTERFACE && "iterator" == getMethodName(assignValue) &&
      TYPE_ITERATOR_DESCRIPTOR == getDescriptor(assignValue)
    }
    false
  }

   /**
    * cycleRef должна соответствовать while(iterator.hasNext())
    */
  private def checkCycleRef(cycleRef: IExpression): Boolean = {
    if (cycleRef.classifier != EC_WHILE_CYCLE) {
      return false
    }
    val condExpr = getFirst(cycleRef) // iterator.hasNext() == true
    if (condExpr.classifier != EC_IF_BOOLEAN) {
      return false
    }

    val methodExp = getFirst(condExpr) // iterator.hasNext()
    if (methodExp.classifier != EC_INVOKE_INTERFACE) {
      return false
    }

    "hasNext" == getMethodName(methodExp)
  }

  /**
   * statement.head должна соответствовать iterator.next() или (Object) iterator.next()
   */
  private def checkFirstExpr(statement: StatementExpression): Boolean = {
    if (statement.expressions.size < 2) {
      return false
    }
    val firstExpr = statement.expressions.head
    if (! (firstExpr.classifier == EC_STORE_NEW_VAR || firstExpr.classifier == EC_STORE_VAR)) {
      return false
    }
    var assignValue = getAssignValue(firstExpr)
    if (assignValue == null) {
      return false
    }
    if (assignValue.classifier.has(EA_TYPED)) {
      assignValue = BoxingUtils.unbox(assignValue.asInstanceOf[ITypedExpression])
    }
    val iterValue = unwrap(assignValue)
    if (iterValue.classifier != EC_INVOKE_INTERFACE) {
      return false
    }
    return "next" == getMethodName(iterValue)
  }

}
