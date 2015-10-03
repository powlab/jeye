package org.powlab.jeye.decode.expra

import org.powlab.jeye.core._
import org.powlab.jeye.core.Types
import org.powlab.jeye.core.parsing.DescriptorParser
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expra.ExpressionAnalyzator._
import org.powlab.jeye.utils.{ConstantPoolUtils, DecodeUtils}

import scala.collection.mutable

/**
 * Ищем enum
 * TODO here: Переделать через трансформер
 */
class SwitchByEnumExpra(methodName: String, descriptor: MethodDescriptor) extends ExpressionHandler {

  var enumClassName: String = ""
  var map: mutable.Map[Int, String] = mutable.Map.empty

  def accept(): Boolean = {
    val returnType = descriptor.returnType
    if (descriptor.parameters.isEmpty && returnType.isArray) {
      val array = returnType.asInstanceOf[ArrayDescriptor]
      array.dimension == 1 && array.lowType == Types.TYPE_INT
    } else {
      DecodeUtils.isStaticBlock(methodName)
    }
  }

  override def apply(expression: IExpression, parent: IExpression): Unit = {
    if (!accept) {
      return
    }
    map = mutable.Map[Int, String]()
    val setExprOpt = get[SetArrayItemExpression](expression)
    if (setExprOpt.nonEmpty && isTryBlock(parent)) {
      val setExpr = setExprOpt.get
      val enumField = extractEnumField(setExpr.indexVariable)
      val ordinal = extractOrdinal(setExpr.assignValue)
      if (enumField != null && ordinal > -1) {
        map += (ordinal -> enumField.field.name)
        enumClassName = enumField.field.clazz.simpleName
      }
    }
  }

  private def extractEnumField(expression: IExpression): GetStaticFieldExpression = {
    expression match {
      case invokeExpr: InvokeVirtualExpression =>
        val getStaticFieldOpt = get[GetStaticFieldExpression](invokeExpr.ownerValue)
        if ("ordinal".equals(invokeExpr.methodName) && getStaticFieldOpt.nonEmpty) {
          return getStaticFieldOpt.get
        }
      case _ =>
    }
    null
  }

  private def extractOrdinal(expression: IExpression): Int = {
    expression match {
      case expression1: IntLiteralExpression =>
        expression1.value
      case _ =>
        -1
    }
  }

}