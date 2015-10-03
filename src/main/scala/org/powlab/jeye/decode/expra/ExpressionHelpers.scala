package org.powlab.jeye.decode.expra

import scala.collection.mutable.Buffer
import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.utils.ClassInformator
import org.powlab.jeye.decode.processor.reference.FieldInstructionData
import org.powlab.jeye.decode.LocalVariable
import java.lang.Double

/**
 * Вспомогательные функции связанные с выражением
 */
object ExpressionHelpers {

  def convertLiteral(expression: ITypedExpression, newBaseType: BaseType): ITypedExpression = {
    val cls = expression.classifier
    if (cls == EC_INT) {
      val intValue = getInt(expression)
      if (intValue == 0 && newBaseType == TYPE_BOOLEAN) {
        return FALSE_EXPRESSION
      } else if (intValue == 1 && newBaseType == TYPE_BOOLEAN) {
        return TRUE_EXPRESSION
      } else if (newBaseType == TYPE_CHAR) {
        return CharLiteralExpression(intValue)
      } else if (newBaseType == TYPE_BYTE) {
        return ByteLiteralExpression(intValue)
      } else if (newBaseType == TYPE_SHORT) {
        return ShortLiteralExpression(intValue)
      }
    } else if (cls == EC_TERNARY_REF && Types.isBaseType(newBaseType)) {
      // TODO here: Не лучшее решение, но следствие неудачной реализации тернарки
      val ternRefExpr = expression.asInstanceOf[TernaryExpressionRef]
      ternRefExpr.changeDescriptor(getBaseTypeDescriptor(newBaseType))
    }
    expression
  }

  def getDefaultValue(descriptor: ParameterDescriptor): IExpression = {
    if (descriptor.isArray || isReferenceDescriptor(descriptor)) {
      NULL_EXPRESSION
    }
    if (descriptor.baseType == TYPE_BOOLEAN) {
      return BooleanLiteralExpression(false)
    }
    if (descriptor.baseType == TYPE_BYTE) {
      return ByteLiteralExpression(0)
    }
    if (descriptor.baseType == TYPE_CHAR) {
      return CharLiteralExpression(0)
    }
    if (descriptor.baseType == TYPE_SHORT) {
      return ShortLiteralExpression(0)
    }
    if (descriptor.baseType == TYPE_INT) {
      return IntLiteralExpression(0)
    }
    if (descriptor.baseType == TYPE_FLOAT) {
      return IntLiteralExpression(0)
    }
    if (descriptor.baseType == TYPE_LONG) {
      return LongLiteralExpression(0)
    }
    if (descriptor.baseType == TYPE_DOUBLE) {
      return DoubleLiteralExpression(0)
    }

    NULL_EXPRESSION
  }

  def getInt(expression: IExpression): Int = {
    if (expression.classifier == EC_INT) {
      expression.asInstanceOf[IntLiteralExpression].value
    } else {
      -1 // TODO here: throw exception
    }
  }

  def getDouble(expression: IExpression): Double = {
    if (expression.classifier == EC_DOUBLE) {
      expression.asInstanceOf[DoubleLiteralExpression].value
    } else {
      throw new RuntimeException("No double value in " + expression.view(EMPTY_EXPRESSION))
    }
  }

  def getLong(expression: IExpression): Long = {
    if (expression.classifier == EC_LONG) {
      expression.asInstanceOf[LongLiteralExpression].value
    } else {
      throw new RuntimeException("No long value in " + expression.view(EMPTY_EXPRESSION))
    }
  }

  def getIncConstant(expression: IExpression): Option[Int] = {
    val classifier = expression.classifier
    if (classifier == EC_POST_INC) {
      return Option.apply(expression.asInstanceOf[PostIncrementExpression].constant)
    } else if (classifier == EC_PRE_INC) {
      return Option.apply(expression.asInstanceOf[PreIncrementExpression].constant)
    }
    None
  }

  def getDescriptor(expression: IExpression): ParameterDescriptor = {
    if (expression.classifier.has(EA_TYPED)) {
      expression.asInstanceOf[ITypedExpression].descriptor
    } else {
      null
    }
  }

  def getComponentType(expression: IExpression): String = {
    if (expression.classifier == EC_NEW_ARRAY) {
      expression.asInstanceOf[NewArrayExpression].componentType
    } else {
      null
    }
  }

  def getArrayDimension(expression: IExpression): Int = {
    if (expression.classifier == EC_NEW_ARRAY) {
      expression.asInstanceOf[NewArrayExpression].dimensionValues.size
    } else {
      -1 // эквивалентно null
    }
  }

  def getArrayDimensionValue(expression: IExpression, index: Int): IExpression = {
    if (expression.classifier == EC_NEW_ARRAY) {
      return expression.asInstanceOf[NewArrayExpression].dimensionValues(index)
    }
    null
  }

  def getArrayIndexExpr(expression: IExpression): IExpression = {
    if (expression.classifier == EC_STORE_ARRAY_VAR) {
      expression.asInstanceOf[SetArrayItemExpression].indexVariable
    } else if (expression.classifier == EC_GET_ARRAY_ITEM) {
      expression.asInstanceOf[GetArrayItemExpression].indexVariable
    } else {
      null
    }
  }

  def getArrayIndex(expression: IExpression): Int = {
    val indexExpr = getArrayIndexExpr(expression)
    if (indexExpr != null) {
      getInt(indexExpr)
    } else {
      -1
    }
  }

  def getVarNameExpr(expression: IExpression): IExpression = {
    if (expression.classifier == EC_STORE_NEW_VAR) {
      expression.asInstanceOf[NewLocalVariableExpression].variableName
    } else if (expression.classifier == EC_STORE_VAR) {
      expression.asInstanceOf[LocalVariableExpression].variableName
    } else if (expression.classifier == EC_STORE_DECLARE_VAR) {
      expression.asInstanceOf[DeclareLocalVariableExpression].variableName
    } else if (expression.classifier == EC_PUT_FIELD) {
      Sex(expression.asInstanceOf[PutFieldExpression].field.name)
    } else if (expression.classifier == EC_PUT_STATIC_FIELD) {
      Sex(expression.asInstanceOf[PutStaticFieldExpression].field.name)
    } else if (expression.classifier == EC_LOCAL_VARIABLE) {
      expression.asInstanceOf[LocalVariable].name
    } else {
      null
    }
  }

  def getVarName(expression: IExpression): String = {
    val expr = getVarNameExpr(expression)
    if (expr != null) {
      expr.view(expression)
    } else {
      null
    }
  }

  def isArgumentVar(expr: IExpression): Boolean = {
    val varNameExpr = getVarNameExpr(expr)
    varNameExpr != null && varNameExpr.classifier == EC_LOCAL_VARIABLE &&
     varNameExpr.asInstanceOf[LocalVariable].argument
  }

  def getAssignValue(expression: IExpression): IExpression = {
    if (expression.classifier == EC_STORE_ARRAY_VAR) {
      expression.asInstanceOf[SetArrayItemExpression].assignValue
    } else if (expression.classifier == EC_STORE_NEW_VAR) {
      expression.asInstanceOf[NewLocalVariableExpression].assignValue
    } else if (expression.classifier == EC_STORE_VAR) {
      expression.asInstanceOf[LocalVariableExpression].assignValue
    } else if (expression.classifier == EC_PUT_FIELD) {
      expression.asInstanceOf[PutFieldExpression].fieldValue
    } else if (expression.classifier == EC_PUT_STATIC_FIELD) {
      expression.asInstanceOf[PutStaticFieldExpression].fieldValue
    } else {
      null
    }
  }

  def unwrap(expression: IExpression): IExpression = {
    if (expression.classifier == EC_CHECK_CAST) {
      expression.asInstanceOf[CheckCastExpression].variable
    } else {
      expression
    }
  }

  def getInvokable(expression: IExpression): Invokeable = {
    val classifier = expression.classifier
    if (classifier.has(EA_INVOKEABLE)) {
      expression.asInstanceOf[Invokeable]
    } else {
      null
    }
  }

  def getMethodName(expression: IExpression): String = {
    val classifier = expression.classifier
    if (classifier.has(EA_INVOKEABLE)) {
      getInvokable(expression).method.name
    } else {
      null
    }
  }

  def getFirstArgument(expression: IExpression): ITypedExpression = {
    val classifier = expression.classifier
    var arguments: ArgumentsExpression = null
    if (classifier.has(EA_INVOKEABLE)) {
      arguments = getInvokable(expression).arguments
    } else if (classifier == EC_NEW_OBJECT) {
      arguments = expression.asInstanceOf[NewObjectExpression].arguments
    }
    if (arguments != null && arguments.argumentValues.nonEmpty) {
      arguments.argumentValues(0)
    } else {
      null
    }
  }

  def getMethodDescriptor(expression: IExpression): MethodDescriptor = {
    val classifier = expression.classifier
    if (classifier.has(EA_INVOKEABLE)) {
      getInvokable(expression).method.descriptor
    } else {
      null
    }
  }

  def getClassOwner(expression: IExpression): ClassInformator = {
    val classifier = expression.classifier
    if (classifier == EC_INVOKE_STATIC) {
      expression.asInstanceOf[InvokeStaticExpression].classOwner
    } else {
      null
    }
  }

  def getOwnerValue(expression: IExpression): IExpression = {
    val classifier = expression.classifier
    if (classifier == EC_INVOKE_INTERFACE) {
      expression.asInstanceOf[InvokeInterfaceExpression].ownerValue
    } else if (classifier == EC_INVOKE_VIRTUAL) {
      expression.asInstanceOf[InvokeVirtualExpression].ownerValue
    } else {
      null
    }
  }

  def getBaseType(expression: IExpression): BaseType = {
    val classifier = expression.classifier
    if (classifier == EC_PRIMITIVE_CAST) {
      return expression.asInstanceOf[ConversionExpression].targetType
    }
    null
  }

  def getSign(expression: IExpression): SignExpression = {
    val classifier = expression.classifier
    if (classifier == EC_MATH) {
      return expression.asInstanceOf[MathExpression].sign
    }
    null
  }

  /**
   * Получить первое выражение, являющееся аргуметном текущего
   */
  def getFirst(expression: IExpression): IExpression = {
    val classifier = expression.classifier
    if (classifier == EC_STATEMENT) {
      return expression.asInstanceOf[StatementExpression].getBaseExpr
    } else if (classifier == EC_WHILE_CYCLE) {
      return expression.asInstanceOf[WhileCycleExpression].condition
    } else if (classifier == EC_IF_BOOLEAN) {
      return expression.asInstanceOf[IfBooleanExpression].expression
    } else if (classifier == EC_PRIMITIVE_CAST) {
      return expression.asInstanceOf[ConversionExpression].variable
    } else if (classifier == EC_MATH) {
      return expression.asInstanceOf[MathExpression].variable
    } else if (classifier == EC_GET_ARRAY_ITEM) {
      return expression.asInstanceOf[GetArrayItemExpression].arrayVariable
    } else if (classifier == EC_ARRAY_LENGTH) {
      return expression.asInstanceOf[ArrayLengthExpression].arrayref
    } else if (classifier == EC_SYNCHRONIZE) {
      return expression.asInstanceOf[MonitorEnterExpression].variable
    } else if (classifier == EC_RETURN) {
      return expression.asInstanceOf[ReturnVarExpression].variable
    } else {
      return null
    }
  }

  def getChildren(expression: IExpression): Buffer[IExpression] = {
    val classifier = expression.classifier
    if (classifier == EC_STATEMENT) {
      expression.asInstanceOf[StatementExpression].expressions
    } else if (classifier == EC_BLOCK) {
      expression.asInstanceOf[BlockExpression].expressions
    } else {
      Buffer.empty[IExpression]
    }
  }

  def getChildAfter(child: IExpression, parent: IExpression): IExpression = {
    val children = getChildren(parent)
    val index = children.indexOf(child)
    if (index != -1 && index + 1 < children.size) {
      children(index + 1)
    } else {
      null
    }
  }

  def getFieldInstructionData(expression: IExpression): FieldInstructionData = {
    val classifier = expression.classifier
    if (classifier == EC_PUT_FIELD) {
      expression.asInstanceOf[PutFieldExpression].field
    } else if (classifier == EC_PUT_STATIC_FIELD) {
      expression.asInstanceOf[PutStaticFieldExpression].field
    } else if (classifier == EC_GET_FIELD) {
      expression.asInstanceOf[GetFieldExpression].field
    } else if (classifier == EC_GET_STATIC_FIELD) {
      expression.asInstanceOf[GetStaticFieldExpression].field
    } else {
      null
    }
  }

}