package org.powlab.jeye.decode.transformer

import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.decode.expression.ConversionExpression
import org.powlab.jeye.decode.processor.reference._

/**
 * Преобразование кода, boxing-unboxing для типов:
 *  int/Integer, byte/Byte, short/Short, double/Double, long/Long, float/Float
 */
object BoxingUtils {

  private val SUPPORTED_TYPES: Map[ParameterDescriptor, Boolean] = Map(
    TYPE_REF_BOOLEAN_DESCRIPTOR -> true,
    TYPE_REF_BYTE_DESCRIPTOR -> true,
    TYPE_REF_SHORT_DESCRIPTOR -> true,
    TYPE_REF_INTEGER_DESCRIPTOR -> true,
    TYPE_REF_FLOAT_DESCRIPTOR -> true,
    TYPE_REF_LONG_DESCRIPTOR -> true,
    TYPE_REF_DOUBLE_DESCRIPTOR -> true,
    TYPE_BOOLEAN_DESCRIPTOR -> true,
    TYPE_BYTE_DESCRIPTOR -> true,
    TYPE_SHORT_DESCRIPTOR -> true,
    TYPE_INT_DESCRIPTOR -> true,
    TYPE_FLOAT_DESCRIPTOR -> true,
    TYPE_LONG_DESCRIPTOR -> true,
    TYPE_DOUBLE_DESCRIPTOR -> true
  )

  private val PRIMITIVE_ANALOG: Map[ParameterDescriptor, ParameterDescriptor] = Map(
    TYPE_REF_BOOLEAN_DESCRIPTOR -> TYPE_BOOLEAN_DESCRIPTOR,
    TYPE_REF_BYTE_DESCRIPTOR -> TYPE_BYTE_DESCRIPTOR,
    TYPE_REF_SHORT_DESCRIPTOR -> TYPE_SHORT_DESCRIPTOR,
    TYPE_REF_INTEGER_DESCRIPTOR -> TYPE_INT_DESCRIPTOR,
    TYPE_REF_FLOAT_DESCRIPTOR -> TYPE_FLOAT_DESCRIPTOR,
    TYPE_REF_LONG_DESCRIPTOR -> TYPE_LONG_DESCRIPTOR,
    TYPE_REF_DOUBLE_DESCRIPTOR -> TYPE_DOUBLE_DESCRIPTOR
  )

  def isPrimitive(param: ParameterDescriptor): Boolean = {
    descOrder(param) < MAX_ORDER
  }

  def notPrimitive(param: ParameterDescriptor): Boolean = {
    !isPrimitive(param)
  }

  def getPrimitiveAnalog(paramRef: ParameterDescriptor) = PRIMITIVE_ANALOG(paramRef)

  def isBoxingMethod(expression: IExpression): Boolean = {
    if (expression.classifier == EC_INVOKE_STATIC) {
      isBooleanValueOfMethod(expression) || isByteValueOfMethod(expression) || isShortValueOfMethod(expression) ||
      isIntegerValueOfMethod(expression) || isFloatValueOfMethod(expression) || isLongValueOfMethod(expression) ||
      isDoubleValueOfMethod(expression)
    } else {
      false
    }
  }

  def isUnboxingMethod(expression: IExpression): Boolean = {
    if (EC_INVOKE_VIRTUAL == expression.classifier) {
      isBooleanValueMethod(expression) || isByteValueMethod(expression) || isShortValueMethod(expression) ||
      isIntValueMethod(expression) || isFloatValueMethod(expression) || isLongValueMethod(expression) ||
      isDoubleValueMethod(expression)
    } else {
      false
    }
  }

  def unbox(expression: ITypedExpression): ITypedExpression = {
    val param = expression.descriptor
    val unboxExpr = unboxPrimitive(param, expression)
    if (unboxExpr == expression) {
      unboxRef(param, expression)
    } else {
      unboxExpr
    }
  }

  private def unboxRef(param: ParameterDescriptor, expression: ITypedExpression): ITypedExpression = {
    // 1. Обработка типа Byte
    if (param == TYPE_REF_BOOLEAN_DESCRIPTOR && isBooleanValueOfMethod(expression)) {
      return processBoxing(param, expression, TYPE_BOOLEAN)
    }
    // 2. Обработка типа Byte
    if (param == TYPE_REF_BYTE_DESCRIPTOR && isByteValueOfMethod(expression)) {
      return processBoxing(param, expression, TYPE_BYTE)
    }
    // 3. Обработка типа Short
    if (param == TYPE_REF_SHORT_DESCRIPTOR && isShortValueOfMethod(expression)) {
      return processBoxing(param, expression, TYPE_SHORT)
    }
    // 4. Обработка типа Integer
    if (param == TYPE_REF_INTEGER_DESCRIPTOR && isIntegerValueOfMethod(expression)) {
      return processBoxing(param, expression, TYPE_INT)
    }
    // 5. Обработка типа Float
    if (param == TYPE_REF_FLOAT_DESCRIPTOR && isFloatValueOfMethod(expression)) {
      return processBoxing(param, expression, TYPE_FLOAT)
    }
    // 6. Обработка типа Long
    if (param == TYPE_REF_LONG_DESCRIPTOR && isLongValueOfMethod(expression)) {
      return processBoxing(param, expression, TYPE_LONG)
    }
    // 7. Обработка типа Double
    if (param == TYPE_REF_DOUBLE_DESCRIPTOR && isDoubleValueOfMethod(expression)) {
      return processBoxing(param, expression, TYPE_DOUBLE)
    }
    expression
  }

  private def unboxPrimitive(param: ParameterDescriptor, expression: ITypedExpression): ITypedExpression = {
    // 1. Обработка типа byte
    if (param == TYPE_BOOLEAN_DESCRIPTOR && isBooleanValueMethod(expression)) {
      return getOwnerValue(expression).asInstanceOf[ITypedExpression]
    }
    // 2. Обработка типа byte
    if (param == TYPE_BYTE_DESCRIPTOR && isByteValueMethod(expression)) {
      return getOwnerValue(expression).asInstanceOf[ITypedExpression]
    }
    // 3. Обработка типа short
    if (param == TYPE_SHORT_DESCRIPTOR && isShortValueMethod(expression)) {
      return getOwnerValue(expression).asInstanceOf[ITypedExpression]
    }
    // 4. Обработка типа int
    if (param == TYPE_INT_DESCRIPTOR && isIntValueMethod(expression)) {
      return getOwnerValue(expression).asInstanceOf[ITypedExpression]
    }
    // 5. Обработка типа float
    if (param == TYPE_FLOAT_DESCRIPTOR && isFloatValueMethod(expression)) {
      return getOwnerValue(expression).asInstanceOf[ITypedExpression]
    }
    // 6. Обработка типа long
    if (param == TYPE_LONG_DESCRIPTOR && isLongValueMethod(expression)) {
      return getOwnerValue(expression).asInstanceOf[ITypedExpression]
    }
    // 7. Обработка типа double
    if (param == TYPE_DOUBLE_DESCRIPTOR && isDoubleValueMethod(expression)) {
      return getOwnerValue(expression).asInstanceOf[ITypedExpression]
    }

    expression
  }

  private def processBoxing(param: ParameterDescriptor, expression: ITypedExpression, baseType: BaseType): ITypedExpression = {
    val argExpr = getFirstArgument(expression)
    val argDesc = argExpr.descriptor
    if (argDesc.baseType != baseType) {
      return ConversionExpression(baseType, argExpr)
    }
    return argExpr
  }

  private def ignoreParam(param: ParameterDescriptor): Boolean = {
    param.isArray || param.baseType == TYPE_VOID || !SUPPORTED_TYPES.getOrElse(param, false)
  }

  private val CLASS_BOOLEAN = "java/lang/Boolean"
  private val CLASS_BYTE = "java/lang/Byte"
  private val CLASS_SHORT = "java/lang/Short"
  private val CLASS_INTEGER = "java/lang/Integer"
  private val CLASS_FLOAT = "java/lang/Float"
  private val CLASS_LONG = "java/lang/Long"
  private val CLASS_DOUBLE = "java/lang/Double"
  private val METHOD_VALUE_OF = "valueOf"
  private val MD_BOOLEAN = "(Z)Ljava/lang/Boolean;"
  private val MD_BYTE = "(B)Ljava/lang/Byte;"
  private val MD_SHORT = "(S)Ljava/lang/Short;"
  private val MD_INTEGER = "(I)Ljava/lang/Integer;"
  private val MD_FLOAT = "(F)Ljava/lang/Float;"
  private val MD_LONG = "(J)Ljava/lang/Long;"
  private val MD_DOUBLE = "(D)Ljava/lang/Double;"
  private val METHOD_BOOLEAN_VALUE = "booleanValue"
  private val METHOD_BYTE_VALUE = "byteValue"
  private val METHOD_SHORT_VALUE = "shortValue"
  private val METHOD_INT_VALUE = "intValue"
  private val METHOD_FLOAT_VALUE = "floatValue"
  private val METHOD_LONG_VALUE = "longValue"
  private val METHOD_DOUBLE_VALUE = "doubleValue"

  private def isBooleanValueOfMethod(expression: IExpression): Boolean = {
    checkStaticMethod(expression, CLASS_BOOLEAN, MD_BOOLEAN)
  }

  private def isBooleanValueMethod(expression: IExpression): Boolean = {
    checkMethod(expression, METHOD_BOOLEAN_VALUE)
  }

  private def isByteValueOfMethod(expression: IExpression): Boolean = {
    checkStaticMethod(expression, CLASS_BYTE, MD_BYTE)
  }

  private def isByteValueMethod(expression: IExpression): Boolean = {
    checkMethod(expression, METHOD_BYTE_VALUE)
  }

  private def isShortValueOfMethod(expression: IExpression): Boolean = {
    checkStaticMethod(expression, CLASS_SHORT, MD_SHORT)
  }

  private def isShortValueMethod(expression: IExpression): Boolean = {
    checkMethod(expression, METHOD_SHORT_VALUE)
  }

  private def isIntegerValueOfMethod(expression: IExpression): Boolean = {
    checkStaticMethod(expression, CLASS_INTEGER, MD_INTEGER)
  }

  private def isIntValueMethod(expression: IExpression): Boolean = {
    checkMethod(expression, METHOD_INT_VALUE)
  }

  private def isFloatValueOfMethod(expression: IExpression): Boolean = {
    checkStaticMethod(expression, CLASS_FLOAT, MD_FLOAT)
  }

  private def isFloatValueMethod(expression: IExpression): Boolean = {
    checkMethod(expression, METHOD_FLOAT_VALUE)
  }

  private def isLongValueOfMethod(expression: IExpression): Boolean = {
    checkStaticMethod(expression, CLASS_LONG, MD_LONG)
  }

  private def isLongValueMethod(expression: IExpression): Boolean = {
    checkMethod(expression, METHOD_LONG_VALUE)
  }

  private def isDoubleValueOfMethod(expression: IExpression): Boolean = {
    checkStaticMethod(expression, CLASS_DOUBLE, MD_DOUBLE)
  }

  private def isDoubleValueMethod(expression: IExpression): Boolean = {
    checkMethod(expression, METHOD_DOUBLE_VALUE)
  }

  private def checkStaticMethod(expression: IExpression, className: String, methodDescriptor: String): Boolean = {
    val classifier = expression.classifier
    if (classifier == EC_INVOKE_STATIC && getMethodName(expression) == METHOD_VALUE_OF) {
      val clazz = getClassOwner(expression)
      val mDesc = getMethodDescriptor(expression)
      clazz.name == className && mDesc != null && mDesc.toString == methodDescriptor
    } else {
        false
    }
  }

  private def checkMethod(expression: IExpression, methodName: String): Boolean = {
    val classifier = expression.classifier
    classifier == EC_INVOKE_VIRTUAL && getMethodName(expression) == methodName
  }

}