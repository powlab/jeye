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
 * Трансормер для преобразования некоторых конструкций и выражений
 * в одекватный вид. Например, число, 9223372036854775807L лучше в
 * коде отображать как Long.MAX_VALUE
 */
class PrettyViewTransformer extends Guide {

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
//    if (true) return

    // Long MAX/MIN constant pretty
    processMinMaxLongConstant(context, expr, parent)

    // Int MAX/MIN constant pretty
    processMinMaxIntegerConstant(context, expr, parent)

    // Double MAX/MIN constant pretty
    processMinMaxDoubleConstant(context, expr, parent)

    // Varargs pretty: 'test(new Integer[]{1, 2, 3})' -> 'test(1, 2, 3)'
    processVarArgs(context, expr, parent)

    // Выражение 'a ^ -1' эквивалентно '~a'
    processXorTilde(context, expr, parent)

    // Выражения 'x = x + a' эквивалентно 'x += a'
    processAssignExpression(context, expr, parent)

    // Удалить избыточные выражения 'String a = "25"; return a;' -> 'return "25";'
    processReduntantExpression(context, expr, parent)
  }

  private def processMinMaxLongConstant(context: GuideContext, expr: IExpression, parent: IExpression) {
    val clfE = expr.classifier
    if (EC_LONG == clfE) {
      val longValue = getLong(expr)
      if (longValue == 9223372036854775807L) {
        context.markAsReplaced(expr, LongLiteralExpression(longValue, "Long.MAX_VALUE"), parent)
      } else if (longValue == -9223372036854775808L) {
        context.markAsReplaced(expr, LongLiteralExpression(longValue, "Long.MIN_VALUE"), parent)
      }
    }
  }

  private def processMinMaxIntegerConstant(context: GuideContext, expr: IExpression, parent: IExpression) {
    val clfE = expr.classifier
    if (EC_INT == clfE) {
      val intValue = getInt(expr)
      if (intValue == 2147483647) {
        context.markAsReplaced(expr, IntLiteralExpression(intValue, "Integer.MAX_VALUE"), parent)
      } else if (intValue == -2147483648) {
        context.markAsReplaced(expr, IntLiteralExpression(intValue, "Integer.MIN_VALUE"), parent)
      }
    }
  }

  private def processMinMaxDoubleConstant(context: GuideContext, expr: IExpression, parent: IExpression) {
    val clfE = expr.classifier
    if (EC_DOUBLE == clfE) {
      val doubleValue = getDouble(expr)
      if (doubleValue == Double.MAX_VALUE) {
        context.markAsReplaced(expr, DoubleLiteralExpression(doubleValue, "Double.MAX_VALUE"), parent)
      } else if (doubleValue == Double.MIN_VALUE) {
        context.markAsReplaced(expr, DoubleLiteralExpression(doubleValue, "Double.MIN_VALUE"), parent)
      }
    }
  }

  private def processVarArgs(context: GuideContext, expr: IExpression, parent: IExpression) {
    val clfE = expr.classifier
    val clfP = parent.classifier
    if (EC_ARGUMENTS == clfP) {
      val grandExpr = context.getParentFor(parent)
      if (grandExpr.classifier.has(EA_INVOKEABLE)) {
        val method = getInvokable(grandExpr).method
        val step = context.getStep(parent)
        if (method.isVarargs && step.isLast) {
          val arrayExpr = context.getReplaceExprFor(expr)
          if (arrayExpr.classifier == EC_INIT_ARRAY) {
            context.removeReplaceExprFor(expr)
            val contentExpr = arrayExpr.asInstanceOf[InitArrayExpression].values
            val newExpr = new TypedExpression(contentExpr, getDescriptor(expr), EC_TYPE)
            context.markAsReplaced(expr, newExpr, parent)
          } else if (expr == NULL_EXPRESSION) {
            val param = method.parameters.last
            val newExpr = new CheckCastExpression(DecodeUtils.getViewType(param.meta), expr, param)
            context.markAsReplaced(expr, newExpr, parent)
          } else if (EC_NEW_ARRAY == clfE && getArrayDimension(expr) == 1 &&
            getInt(getArrayDimensionValue(expr, 0)) == 0) {
            context.markAsReplaced(expr, EMPTY_EXPRESSION, parent)
          }
        }
      }
    }
  }

  private def processXorTilde(context: GuideContext, expr: IExpression, parent: IExpression) {
    val clfE = expr.classifier
    if (EC_MATH == clfE && getSign(expr) == MathExpression.SIGN_XOR) {
      val mathExpr = expr.asInstanceOf[MathExpression]
      if (mathExpr.pairs.size == 1) {
        val mathPair = mathExpr.pairs.head
        if (isTilde(mathExpr.variable, mathPair.variable)) {
          val tildeExpr = new TildeExpression(mathExpr.variable)
          context.markAsReplaced(mathExpr, tildeExpr, parent)
        } else if (isTilde(mathPair.variable, mathExpr.variable)) {
          val tildeExpr = new TildeExpression(mathPair.variable)
          context.markAsReplaced(mathExpr, tildeExpr, parent)
        }
      }
    }
  }

  private def isTilde(leftExpr: ITypedExpression, rightExpr: ITypedExpression): Boolean = {
    (leftExpr.classifier == EC_LOCAL_VARIABLE || leftExpr.classifier == EC_GET_FIELD || leftExpr.classifier == EC_GET_STATIC_FIELD) &&
      (rightExpr.classifier == EC_INT && getInt(rightExpr) == -1)
  }

  private def processAssignExpression(context: GuideContext, expr: IExpression, parent: IExpression) {
    val clfE = expr.classifier
    if (clfE.has(EA_ASSIGNABLE)) {
      val assignValue = getAssignValue(expr)
      if (assignValue.classifier == EC_MATH) {
        val name = getVarName(expr)
        val firstExpr = getFirst(assignValue)
        val signExpr = getSign(assignValue)
        if (name == firstExpr.view(assignValue) && SIGN_ASSIGNABLE_MAP.containsKey(signExpr)) {
          val newSign = SIGN_ASSIGNABLE_MAP.get(signExpr)
          val newAssignValue = extractAssignExpression(assignValue.asInstanceOf[MathExpression])
          val newExpr = if (expr.classifier == EC_STORE_VAR) {
            val locVarExpr = expr.asInstanceOf[LocalVariableExpression]
            new LocalVariableExpression(locVarExpr.variableName, newSign, newAssignValue, locVarExpr.descriptor)
          } else if (expr.classifier == EC_STORE_ARRAY_VAR) {
            val setArrExpr = expr.asInstanceOf[SetArrayItemExpression]
            new SetArrayItemExpression(setArrExpr.arrayVariable, setArrExpr.indexVariable, newSign, newAssignValue, setArrExpr.descriptor)
          } else if (expr.classifier == EC_PUT_FIELD) {
            val putExpr = expr.asInstanceOf[PutFieldExpression]
            new PutFieldExpression(putExpr.fieldOwner, newSign, newAssignValue, putExpr.field)
          } else if (expr.classifier == EC_PUT_STATIC_FIELD) {
            val putStaticExpr = expr.asInstanceOf[PutStaticFieldExpression]
            new PutStaticFieldExpression(putStaticExpr.fieldOwner, newSign, newAssignValue, putStaticExpr.field)
          } else {
            EMPTY_EXPRESSION
          }
          if (newExpr != EMPTY_EXPRESSION) {
            context.markAsReplaced(expr, newExpr, parent)
          }
        }
      }
    }
  }

  private def extractAssignExpression(mathExpr: MathExpression): ITypedExpression = {
    val mathPair = mathExpr.pairs(0)
    if (mathExpr.pairs.length == 1) {
      mathPair.variable
    } else {
      new MathExpression(mathPair.variable, mathExpr.pairs.tail)
    }
  }

  /**
   * Удаляем лишние выражение, например:
   * String a = "25";
   * return a;
   *
   * На пустом месте создали переменную a. В результате должно быть return "25";
   */
  private def processReduntantExpression(context: GuideContext, expr: IExpression, parent: IExpression) {
    val clfE = expr.classifier
    if (clfE != EC_STORE_NEW_VAR && clfE != EC_STORE_VAR) {
      return
    }
    val returnExpr = getChildAfter(expr, parent)
    if (returnExpr == null || returnExpr.classifier != EC_RETURN) {
      return
    }
    val varNameExpr = getVarNameExpr(expr)
    val returnVarName = getFirst(returnExpr)
    if (varNameExpr.view(expr) == returnVarName.view(returnExpr)) {
      val assignValue = getAssignValue(expr).asInstanceOf[ITypedExpression]
      context.markAsRemoved(expr, parent)
      context.markAsReplaced(returnVarName, assignValue, returnExpr)
    }
  }

}
