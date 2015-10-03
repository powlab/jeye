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
 * Поиск блоков foreach для массивов и сварачивание этой структуры
 * Пример:
 * // блок 1
 * int[] array2 = array0;
 * int int3 = array0.length;
 * for (int int4 = 0;int4 < int3;++int4) {
 *   int int5 = array2[int4];
 *   System.out.println(int5);
 * }
 * // блок 2
 * for (int int0 : array0) {
 *   System.out.println(int0);
 * }
 *
 *
 */
class ForeachArrayTransformer() extends Guide {

  /** Ссылка на массив */
  private var arrayRef: IExpression = null
  /** Ссылка на длину массива */
  private var lengthRef: IExpression = null

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
    //if (true) return
    val clfE = expr.classifier
    if (clfE == EC_STATEMENT) {
      val cycleRef = getFirst(expr)
      val oldArrayRef = arrayRef
      arrayRef = prepareArrayRef
      // проверяем, что это наш случай
      if (checkArrayRef(arrayRef) && checkLengthRef && checkCycleRef(cycleRef) && checkFirstExpr(expr.asInstanceOf[StatementExpression])) {
        context.markAsRemoved(arrayRef, parent)
        context.markAsRemoved(lengthRef, parent)
        // TODO here: переделать на пост обработку или отдельный трансформер для eclipse
        // ejc (примечание: таким образом удаляем декларацию переменной)
        if (oldArrayRef != arrayRef) {
          context.removeAddExprForParent(lengthRef)
        }

        val setVarExpr = expr.asInstanceOf[StatementExpression].expressions.head
        context.markAsRemoved(setVarExpr, expr)
        val varName = getVarName(setVarExpr)
        val descriptor = getDescriptor(setVarExpr)
        val nativeAssignValue = getAssignValue(arrayRef)
        val dublicateArrayRef = findDublicate(context, parent)
        val assignValue = if (dublicateArrayRef != null) {
          TypedExpression(getVarName(dublicateArrayRef), descriptor)
        } else {
          context.getReplaceExprFor(nativeAssignValue)
        }
        val foreachExpr = new ForeachExpression(TypedExpression(varName, descriptor), assignValue)
        context.markAsReplaced(cycleRef, foreachExpr, expr)
      }
      arrayRef = null
      lengthRef = null
    } else if (parent.classifier.has(EA_DEPTH) && clfE != EC_LABEL) {
      arrayRef = lengthRef
      lengthRef = expr
    }
  }

  /**
   * TODO here: это не обязательное улучшение, возможно даже лучше отключить
   */
  private def findDublicate(context: GuideContext, parent: IExpression): IExpression = {
    val assignValue = getAssignValue(arrayRef)
    val dublicate = getChildren(parent).find(expression => {
      expression == arrayRef || (checkArrayRef(expression) && getAssignValue(expression) == assignValue)
    })
    if (dublicate.isDefined) {
      val value = dublicate.get
      if (value != arrayRef && !context.isRemoved(value)) {
        return value
      }
    }
    null
  }

  // 'int int6 = (array5 = array4).length;' - вытаскиваем выражение 'array5 = array4'
  private def prepareArrayRef(): IExpression = {
    if (checkLengthRef()) {
      val assignValue = getAssignValue(lengthRef)
      val arrayRef = getFirst(assignValue)
      if (arrayRef.classifier == EC_STORE_VAR) {
        return arrayRef
      }

    }
    arrayRef
  }

  private def checkArrayRef(arrayRef: IExpression): Boolean = {
    if (arrayRef != null && (arrayRef.classifier == EC_STORE_NEW_VAR || arrayRef.classifier ==  EC_STORE_VAR)) {
      val classifier = getAssignValue(arrayRef).classifier
      classifier == EC_LOCAL_VARIABLE || classifier == EC_TYPE
    } else {
      false
    }
  }

  private def checkLengthRef(): Boolean = {
    lengthRef != null && lengthRef.classifier == EC_STORE_NEW_VAR &&
      getAssignValue(lengthRef).classifier == EC_ARRAY_LENGTH
  }

  private def checkCycleRef(cycleRef: IExpression): Boolean = {
    if (cycleRef.classifier != EC_FOR_CYCLE) {
      return false
    }
    val cycle = cycleRef.asInstanceOf[ForExpression]
    if (cycle.initExpr.classifier != EC_STORE_NEW_VAR || getInt(getAssignValue(cycle.initExpr)) != 0) {
      return false
    }
    if (cycle.condExpr.classifier != EC_IF_SIMPLE) {
      return false
    }
    if (!(cycle.postExpr.classifier == EC_PRE_INC || cycle.postExpr.classifier == EC_POST_INC)) {
      return false
    }
    val const = getIncConstant(cycle.postExpr)

    const.isDefined && const.get == 1
  }

  private def checkFirstExpr(statement: StatementExpression): Boolean = {
    if (statement.expressions.size < 2) {
      return false
    }
    val firstExpr = statement.expressions.head
    if (firstExpr.classifier != EC_STORE_NEW_VAR) {
      return false
    }
    val assignValue = getAssignValue(firstExpr)
    if (assignValue.classifier != EC_GET_ARRAY_ITEM) {
      return false
    }
    true
  }

}
