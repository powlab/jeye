package org.powlab.jeye.decode.transformer

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expra.ExpressionGuide.Guide
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.decode.expra.GuideContext
import org.powlab.jeye.decode.processor.store._
import org.powlab.jeye.decode.processor.reference._
import scala.collection.mutable.Stack

import org.powlab.jeye.decode.transformer.InitArrayDetector._
import scala.collection.mutable.Buffer

/**
 * Поиск блоков инициализации массива и сварачивание этой структуры
 * Пример:
 *  // блок 1
 * String[] array2 = new String[3];
 * array2[0] = "a";
 * array2[1] = "b";
 * array2[2] = "c";
 * String[] array3 = array2;
 * // блок 2
 * String[] array2 = new String[]{"a", "b", "c"};
 * На вход подается блок 1 на выходе должен быть блок 2
 *
 *
 */
class InitArrayTransformer() extends Guide {

  private var detector: InitArrayDetector = null
  private var detectors = Stack[InitArrayDetector]()
  private var monitor = ArrayBuffer[InitArrayDetector]()
  // oldName - newName
  private var pairs = new ArrayBuffer[Tuple2[String, String]]

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
    //if (true) return
    val clfE = expr.classifier
    if (pairs.nonEmpty && clfE == EC_LOCAL_VARIABLE) {
      val pairOpt = pairs.find(_._1 == expr.view(parent))
      if (pairOpt.isDefined) {
        val pair = pairOpt.get
        context.markAsReplaced(expr, TypedExpression(pair._2, getDescriptor(expr)), parent)
      }
    }
    if (monitor.nonEmpty) {
      var removeDetectorOpt = monitor.find(_.handle(context, expr, parent) == InitArrayDetector.COMPLETED)
      if (removeDetectorOpt.isDefined) {
        val removeDetector = removeDetectorOpt.get
        if (removeDetector.pair != null) {
          pairs += removeDetector.pair
        }
        removeDetector.remove(context)
        monitor -= removeDetector
      }
    }
    if (EC_STORE_NEW_VAR == clfE && parent.classifier.has(EA_DEPTH)) {
      if (acceptNewVar(expr, parent)) {
        if (detector != null) {
          detectors.push(detector)
        }
        detector = new InitArrayDetector(expr, parent)
        return
      }
    }
    if (detector != null) {
      val result = detector.handle(context, expr, parent)
      if (result == InitArrayDetector.SKIP) {
        detector = resolveDetector
      } else if (result == InitArrayDetector.COMPLETED) {
        monitor += detector
        detector = resolveDetector
      }
    }
  }

  private def resolveDetector(): InitArrayDetector = {
    if (detectors.nonEmpty) detectors.pop else null
  }
  /**
   * Проверить, что создаем новый массив
   * Пример: String[] array2 = new String[3];
   */
  private def acceptNewVar(expr: IExpression, parent: IExpression): Boolean = {
    val assignValue = getAssignValue(expr)
    if (assignValue.classifier == EC_NEW_ARRAY && getArrayDimension(assignValue) > 0) {
      val dimensionValue = getArrayDimensionValue(assignValue, 0)
      getInt(dimensionValue) > 0
    } else {
      false
    }
  }

}

private object InitArrayDetector {
  val SKIP = 0
  val IN_PROCESS = 1
  val COMPLETED = 2
}

private class InitArrayDetector(expression: IExpression, parentExpression: IExpression) {
  private val exprs = (new ArrayBuffer[IExpression] += expression)
  private val dimesions = getInt(getArrayDimensionValue(getAssignValue(expression), 0))
  private val assignValues = new Array[IExpression](dimesions)
  private var packedExpr: InitArrayExpression = null
  private var whenComplete: IExpression = null
  var pair: Tuple2[String, String] = null
  def handle(context: GuideContext, expr: IExpression, parent: IExpression): Int = {
    if (whenComplete != null) {
      if (context.isLastCompletedExpr(whenComplete) || parent == parentExpression ||
          !context.hasParentFor(whenComplete)) {
        whenComplete = null
      } else {
        return IN_PROCESS
      }
    }
    val clfE = expr.classifier
    if (packedExpr != null) {
      if (clfE == EC_LOCAL_VARIABLE && expr.view(parent) == getVarName(expression)) {
        // проверим на замену
        val values = replaceValues(context)
        context.markAsReplaced(expr, new InitArrayExpression(packedExpr.declare,
            new ArgumentsExpression(values.map(getDescriptor).toArray, values)), parent)
        if (parent.classifier == EC_STORE_NEW_VAR) {
          pair = (getVarName(expression), getVarName(parent))
        }
        return COMPLETED
      }
      return IN_PROCESS // еще в процессе
    }
    if (parentExpression != parent) {
      return IN_PROCESS // еще в процессе определения шаблона
    }
    if (EC_STORE_ARRAY_VAR == clfE) {
      val arrayIndex = getArrayIndex(expr)
      exprs += expr
      if (arrayIndex < dimesions) {
        assignValues(arrayIndex) = expr
      }
      whenComplete = expr
      if (dimesions == arrayIndex + 1) {
        packedExpr = makeInitExpression
        return COMPLETED // шаблоном готов
      } else {
        return IN_PROCESS // еще в процессе определения шаблона
      }
    } else if (exprs.size > 1 && packedExpr == null) {
      packedExpr = makeInitExpression
      return COMPLETED
    }
    return SKIP // не являвляется искомым шаблоном
  }
  def remove(context: GuideContext) {
    context.markAsRemoved(exprs, parentExpression)
  }

  /**
   * Выражение - инициализация массива
   */
  private def makeInitExpression(): InitArrayExpression = {
    val componentType = getComponentType(getAssignValue(expression))
    val descriptor = getDescriptor(expression).asInstanceOf[ArrayDescriptor]
    val newDimension = descriptor.dimension
    val dimensionValues = Array.fill[ITypedExpression](newDimension)(EMPTY_EXPRESSION)
    val declare = new NewArrayExpression(componentType, dimensionValues, descriptor)
    val values = assignValues.map(value => {
      if (value == null) {
        getDefaultValue(descriptor.componentType).asInstanceOf[ITypedExpression]
      } else {
        getAssignValue(value).asInstanceOf[ITypedExpression]
      }
    }).toBuffer[ITypedExpression]
    new InitArrayExpression(declare, new ArgumentsExpression(values.map(getDescriptor).toArray, values))
  }

  private def replaceValues(context: GuideContext): Buffer[ITypedExpression] = {
    packedExpr.values.argumentValues.map(exprItem => {
      val newExprItem = context.getReplaceExprFor(exprItem)
      if (newExprItem != exprItem) {
        context.removeReplaceExprFor(exprItem)
        if (newExprItem.classifier == EC_INIT_ARRAY) {
          new InitArrayExpression(EMPTY_EXPRESSION, newExprItem.asInstanceOf[InitArrayExpression].values)
        } else {
          newExprItem.asInstanceOf[ITypedExpression]
        }
      } else {
        exprItem.asInstanceOf[ITypedExpression]
      }
    })
  }

}
