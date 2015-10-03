package org.powlab.jeye.decode.transformer

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.Types
import org.powlab.jeye.decode.ClassFacade
import org.powlab.jeye.decode.expra.ExpressionGuide.Guide
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.decode.expra.GuideContext
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.Expressions.NULL_EXPRESSION
import org.powlab.jeye.decode.transformer.BoxingUtils._
import org.powlab.jeye.utils.DecodeUtils
import org.powlab.jeye.decode.ClassFacades

/**
 * Преобразование кода, boxing-unboxing для типов:
 *  int/Integer, byte/Byte, short/Short, double/Double, long/Long, float/Float
 */
class AutoBoxingTransformer(classFacade: ClassFacade) extends Guide {

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
//    if (true) return
    val clfE = expr.classifier
    val clfP = parent.classifier

    if (!clfE.has(EA_TYPED)) {
      return
    }
    // корректировка конфликтных вызовов (одинаковые имена методов и похожие типы аргументов)
    val typedExpr = expr.asInstanceOf[ITypedExpression]
    if (clfP == EC_ARGUMENTS) {
      val grandExpr = context.getParentFor(parent)
      if (grandExpr.classifier.has(EA_INVOKEABLE) &&
        correctInvokeExpr(context, typedExpr, typedExpr, parent.asInstanceOf[ArgumentsExpression])) {
        return
      }
    }
    // обработка тернарного выражения
    if (clfP == EC_TERNARY && context.getStep(parent).now == 1) {
      processTernaryExpression(context, typedExpr, parent)
    }
    val unboxExpr = BoxingUtils.unbox(typedExpr)
    if (unboxExpr == expr) {
      // корректировка по боксингу для cast выражений
      if (clfP == EC_PRIMITIVE_CAST) {
        correctPrimCastExpr(context, typedExpr, typedExpr, parent.asInstanceOf[ITypedExpression])
      }
      return
    }
    // ограничение по боксингу для if
    if (clfP == EC_IF_SIMPLE) {
      checkIfExpr(context, unboxExpr, typedExpr, parent.asInstanceOf[IfSimpleExpression])
      return
    }
    // корректировка по боксингу для cast выражений
    if (clfP == EC_PRIMITIVE_CAST) {
      correctPrimCastExpr(context, unboxExpr, typedExpr, parent.asInstanceOf[ITypedExpression])
      return
    }
    // корректировка по боксингу для вызовов метода выражений
    if (clfP == EC_ARGUMENTS) {
      val grandExpr = context.getParentFor(parent)
      if (grandExpr.classifier.has(EA_INVOKEABLE)) {
        correctInvokeExpr(context, unboxExpr, typedExpr, parent.asInstanceOf[ArgumentsExpression])
      }
      return
    }

    context.markAsReplaced(expr, unboxExpr, parent)
  }

  /**
   * Обработка тернарки, особый случай, необходимо добавить тип byte, char, short к позитив условию
   * На входе:  'short short0 = boolean1 ? 1 : 0;'
   * На выходе: 'short short0 = boolean1 ? (short) 1 : 0;'
   */
  private def processTernaryExpression(context: GuideContext, expr: ITypedExpression, parent: IExpression) {
    val expParam = expr.descriptor
    if (!isPrimitive(expParam)) {
      return
    }
    val terParent = findTernaryParentCorrect(context, expr)
    if (terParent == null) {
      return
    }
    val terParam = getDescriptor(terParent)
    if (terParent.classifier != EC_PRIMITIVE_CAST &&
        isPrimitive(terParam) && (descOrder(terParam) < descOrder(expParam) ||
        ((expr.classifier == EC_SHORT || expr.classifier == EC_BYTE || expr.classifier == EC_CHAR) &&
            expr.view(parent).matches("\\d+")))) {
      val baseType = terParent.descriptor.baseType
      val castExpr = ConversionExpression(baseType, expr)
      context.markAsReplaced(expr, castExpr, parent)
    }
  }

  private def checkIfExpr(context: GuideContext, unboxExpr: ITypedExpression, expr: ITypedExpression, parent: IfSimpleExpression): Boolean = {
    if (parent.left == NULL_EXPRESSION || parent.right == NULL_EXPRESSION) {
      return false
    }
    if (isBoxingMethod(parent.left) || isBoxingMethod(parent.right)) {
      return false
    }
    if (isUnboxingMethod(parent.left) && isUnboxingMethod(parent.right) && parent.right == expr) {
      val newUnboxExpr = ConversionExpression(expr.descriptor.baseType, unboxExpr)
      context.markAsReplaced(expr, newUnboxExpr, parent)
      return true
    }

    context.markAsReplaced(expr, unboxExpr, parent)
    true
  }

  private def correctPrimCastExpr(context: GuideContext, unboxExpr: ITypedExpression, expr: ITypedExpression, parentConvExpr: ITypedExpression): Boolean = {
    if (descOrder(parentConvExpr.descriptor) < descOrder(expr.descriptor)) {
      return false
    }
    val grandExpr = context.getParentFor(parentConvExpr)
    val grandClassifier = grandExpr.classifier
    if (grandClassifier == EC_ARGUMENTS) {
      val argsExpr = grandExpr.asInstanceOf[ArgumentsExpression]
      val methodExpr = context.getParentFor(grandExpr)
      if (canInvokeMethod(unboxExpr, parentConvExpr, argsExpr, methodExpr) && checkClassFacade(methodExpr)) {
        if (isBoxingMethod(methodExpr)) {
          val baseType = getBaseType(parentConvExpr)
          val castExpr = ConversionExpression(baseType, unboxExpr)
          context.markAsReplaced(parentConvExpr, castExpr, argsExpr)
        } else {
          context.markAsReplaced(parentConvExpr, unboxExpr, argsExpr)
        }
      }
      context.markAsReplaced(expr, unboxExpr, parentConvExpr)
      return true
    } else if (grandClassifier == EC_TERNARY) {
      val rightParent = findTernaryParentCorrect(context, grandExpr)
      if (rightParent != null) {
        val param = getDescriptor(rightParent)
        if (descOrder(param) < descOrder(parentConvExpr.descriptor)) {
          context.markAsReplaced(parentConvExpr, getFirst(parentConvExpr), grandExpr)
        }
      }
    } else if (grandClassifier.has(EA_TYPE)) {
      val grandParam = getDescriptor(grandExpr)
      if (descOrder(grandParam) < descOrder(parentConvExpr.descriptor)) {
        context.markAsReplaced(parentConvExpr, getFirst(parentConvExpr), grandExpr)
      }
    } else if (grandClassifier == EC_IF_SIMPLE) {
      val ifExpr = grandExpr.asInstanceOf[IfSimpleExpression]
      val leftParam = getDescriptor(ifExpr.left)
      val rightParam = getDescriptor(ifExpr.right)
      if (ifExpr.left == parentConvExpr && descOrder(leftParam) > descOrder(expr.descriptor)) {
        context.markAsReplaced(parentConvExpr, getFirst(parentConvExpr), ifExpr)
      }
      if (ifExpr.right == parentConvExpr && descOrder(rightParam) > descOrder(expr.descriptor)) {
        context.markAsReplaced(parentConvExpr, getFirst(parentConvExpr), ifExpr)
      }
    }
    // 'anyDouble + (double) int0' -> 'anyDouble + int0'
    if (grandClassifier == EC_MATH_PAIR) {
      val mathExpr = context.getParentFor(grandExpr)
      if (mathExpr.classifier == EC_MATH) {
        val mathOrig = mathExpr.asInstanceOf[MathExpression]
        val index = mathOrig.pairs.indexOf(grandExpr)
        val paramDesc = if (index > 0) {
          mathOrig.pairs(index - 1).variable.descriptor
        } else {
          mathOrig.variable.descriptor
        }
        if (descOrder(paramDesc) >= descOrder(parentConvExpr.descriptor)) {
          context.markAsReplaced(parentConvExpr, getFirst(parentConvExpr), grandExpr)
        }
      }
    }
    // 'return (short) int0;' -> 'return int0;'
    if (grandClassifier == EC_RETURN) {
      val paramDesc = getDescriptor(grandExpr)
      if (descOrder(paramDesc) != MAX_ORDER && descOrder(paramDesc) > descOrder(expr.descriptor)) {
        context.markAsReplaced(parentConvExpr, getFirst(parentConvExpr), grandExpr)
      }
    }
    if (unboxExpr != expr) {
      context.markAsReplaced(expr, unboxExpr, parentConvExpr)
    }
    false
  }

  private def findTernaryParentCorrect(context: GuideContext, expr: IExpression): ITypedExpression = {
    var parentExpr = context.getParentFor(expr)
    while (parentExpr.classifier == EC_TERNARY) {
      parentExpr = context.getParentFor(parentExpr)
    }
    if (parentExpr.classifier == EC_TERNARY_REF) {
      val ternOwnerExpr = context.getParentFor(parentExpr)
      if (ternOwnerExpr.classifier == EC_PRIMITIVE_CAST) {
        return ternOwnerExpr.asInstanceOf[ITypedExpression]
      }
    }
    if (parentExpr.classifier.has(EA_TYPED)) {
      parentExpr.asInstanceOf[ITypedExpression]
    } else {
      null
    }
  }

  private def correctInvokeExpr(context: GuideContext, unboxExpr: ITypedExpression, expr: ITypedExpression, argsExpr: ArgumentsExpression): Boolean = {
    val methodExpr = context.getParentFor(argsExpr)
    if (unboxExpr != expr) {
      if (canInvokeMethod(unboxExpr, expr, argsExpr, methodExpr)) {
        context.markAsReplaced(expr, unboxExpr, argsExpr)
        return true
      }
      // Например: Integer.valueOf(reference0.intValue())
      if (BoxingUtils.isBoxingMethod(methodExpr) && context.getReplaceExprFor(methodExpr) != methodExpr) {
        // reference0.intValue() -> (int) reference0
        val newUnboxExpr = ConversionExpression(expr.descriptor.baseType, unboxExpr)
        context.markAsReplaced(expr, newUnboxExpr, argsExpr)
        return true
      }
    } else {
      // Например: someMethod(array7) -> someMethod((String[]) array), где array любой тип отличный от String[]
      val index = argsExpr.argumentValues.indexOf(expr)
      val argParam = argsExpr.paramTypes(index)
      if (needCheckCast(expr, argsExpr, methodExpr)) {
        val newUnboxExpr = BoxingUtils.unbox(expr)
        val checkCastExpr = new CheckCastExpression(DecodeUtils.getViewType(argParam.meta), newUnboxExpr, argParam)
        context.markAsReplaced(expr, checkCastExpr, argsExpr)
        return true
      }
      if (redundantCheckCast(expr, argParam)) {
        context.markAsReplaced(expr, unwrap(expr), argsExpr)
      }
      val variants = classFacade.getParamVariants(getMethodName(methodExpr), index, getMethodDescriptor(methodExpr))
      if (callConfilicts(argParam, variants)) {
        return true
      }
    }
    false
  }

  private def canInvokeMethod(newArgExpr: ITypedExpression, oldArgExpr: ITypedExpression, argsExpr: ArgumentsExpression, methodExpr: IExpression): Boolean = {
    val index = argsExpr.argumentValues.indexOf(oldArgExpr)
    if (index != -1) {
      val newArgParam = newArgExpr.descriptor
      val argParam = argsExpr.paramTypes(index)
      val mDesc = getMethodDescriptor(methodExpr)
      val variants = classFacade.getParamVariants(getMethodName(methodExpr), index, mDesc)
      // Какой из 2х и более одноименных методов нужно вызывать?
      if (callConfilicts(argParam, variants)) {
        // Если это примитив из одноименных методов с примитивами нет, то и конфликта нет
        if (notPrimitive(argParam) || variants.count(_.baseType != Types.TYPE_REFERENCE) > 1) {
          return false
        }
      }
      if (BoxingUtils.isBoxingMethod(methodExpr)) {
        return false
      }
      // Рассмторим случай для примитива
      if (isPrimitive(argParam)) {
        var checkedParam = newArgParam
        if (notPrimitive(newArgParam)) {
          checkedParam = getPrimitiveAnalog(newArgParam)
        }
        return descOrder(checkedParam) <= descOrder(argParam)
      }
    }
    true
  }

  /**
   * Проверка, что для вызываемого метода зарегистрирован класс-фасад, иначе все рассуждения по поводу
   * возможных конфликтов по типу не имеет смысла
   */
  private def checkClassFacade(methodExpr: IExpression): Boolean = {
    val clazz = getInvokable(methodExpr).method.clazz
    val classFacadeOpt = ClassFacades.get(clazz.meta)
    classFacadeOpt.isDefined
  }

  private def redundantCheckCast(expr: ITypedExpression, argParam: ParameterDescriptor): Boolean = {
    expr.classifier == EC_CHECK_CAST && unwrap(expr).asInstanceOf[ITypedExpression].descriptor.meta == argParam.meta
  }

  private def needCheckCast(expr: ITypedExpression, argsExpr: ArgumentsExpression, methodExpr: IExpression): Boolean = {
    val index = argsExpr.argumentValues.indexOf(expr)
    if (index != -1) {
      val argParam = argsExpr.paramTypes(index)
      if (difArray(argParam, expr.descriptor)) {
        return true
      }
      val variants = classFacade.getParamVariants(getMethodName(methodExpr), index, getMethodDescriptor(methodExpr))
      // Какой из 2х и более одноименных методов нужно вызывать?
      if (callConfilicts(argParam, variants)) {
        if (notPrimitive(expr.descriptor)) {
          val exprBaseType = expr.descriptor.baseType
          val refCount = variants.count(_.baseType == Types.TYPE_REFERENCE)
          return refCount > 1 && (relativeWith(argParam, expr.descriptor) || expr == NULL_EXPRESSION)
        }
      }
    }
    return false
  }

  private def callConfilicts(paramOld: ParameterDescriptor, variants: ArrayBuffer[ParameterDescriptor]): Boolean = {
    if (variants.size < 2) {
      return false
    }
    val resultOpt = variants.find(param => {
      paramOld.baseType == param.baseType
    })
    resultOpt.isDefined
  }

  private def difArray(param: ParameterDescriptor, realParam: ParameterDescriptor): Boolean = {
    param.isArray && realParam.isArray && param.meta != realParam.meta
  }

  private def relativeWith(param: ParameterDescriptor, relativeParam: ParameterDescriptor): Boolean = {
    relativeNumberOrder(param) < relativeNumberOrder(relativeParam)
  }

  private def relativeNumberOrder(param: ParameterDescriptor): Int = {
    if (param == TYPE_OBJECT_DESCRIPTOR) {
      1
    } else if (param == TYPE_REF_NUMBER_DESCRIPTOR) {
      2
    } else if (param == TYPE_REF_INTEGER_DESCRIPTOR || param == TYPE_REF_BYTE_DESCRIPTOR ||
      param == TYPE_REF_SHORT_DESCRIPTOR || param == TYPE_REF_LONG_DESCRIPTOR ||
      param == TYPE_REF_FLOAT_DESCRIPTOR || param == TYPE_REF_DOUBLE_DESCRIPTOR) {
      3
    } else {
      4
    }
  }

}
