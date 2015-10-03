package org.powlab.jeye.decode.expression

import org.powlab.jeye.core._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.ExpressionViewer._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.MathExpression.SIGN_ASSIGN
import org.powlab.jeye.utils.DecodeUtils

object LocalVariableExpression {
  def apply(newExpr: NewLocalVariableExpression): LocalVariableExpression = {
    new LocalVariableExpression(newExpr.variableName, SIGN_ASSIGN, newExpr.assignValue, newExpr.descriptor)
  }

  def apply(variableName: IExpression, assignValue: IExpression,
            descriptor: ParameterDescriptor): LocalVariableExpression = {
    new LocalVariableExpression(variableName, SIGN_ASSIGN, assignValue, descriptor)
  }

}

/**
 * Пример:
 * 1) c = true;
 * 2) (c = true)
 */
class LocalVariableExpression(val variableName: IExpression, val sign: SignExpression,
                              val assignValue: IExpression, val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val baseView = variableName.view(this) + sign.view(this) + assignValue.view(this)
    correctView(baseView, parent.classifier, EC_IF_WORD, EC_PUT_FIELD, EC_STORE_VAR,
        EC_STORE_ARRAY_VAR, EC_SYNCHRONIZE, EC_PUT_STATIC_FIELD)
  }
  def classifier(): ExpressionClassifier = EC_STORE_VAR
}

object DeclareLocalVariableExpression {
  def apply(expr: LocalVariableExpression): DeclareLocalVariableExpression = {
    new DeclareLocalVariableExpression(expr.variableName, expr.descriptor)
  }
  def apply(expr: NewLocalVariableExpression): DeclareLocalVariableExpression = {
    new DeclareLocalVariableExpression(expr.variableName, expr.descriptor)
  }
}

/**
 * Пример: boolean c;
 */
class DeclareLocalVariableExpression(val variableName: IExpression, val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val varType = DecodeUtils.getViewType(descriptor.meta)
    varType + " " + variableName.view(this) + ";"
  }
  def classifier(): ExpressionClassifier = EC_STORE_DECLARE_VAR
}

object NewLocalVariableExpression {
  def apply(expr: LocalVariableExpression): NewLocalVariableExpression = {
    new NewLocalVariableExpression(expr.variableName, expr.assignValue, expr.descriptor)
  }
}

class NewLocalVariableExpression(val variableName: IExpression, val assignValue: IExpression,
  val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val varType = DecodeUtils.getViewType(descriptor.meta)
    varType + " " + variableName.view(this) + " = " + assignValue.view(this) + ";"
  }
  def classifier(): ExpressionClassifier = EC_STORE_NEW_VAR
}

object SetArrayItemExpression {
  def apply(arrayVariable: IExpression, indexVariable: IExpression,
            assignValue: IExpression, descriptor: ParameterDescriptor): SetArrayItemExpression = {
    new SetArrayItemExpression(arrayVariable, indexVariable, SIGN_ASSIGN, assignValue, descriptor)
  }
}


class SetArrayItemExpression(val arrayVariable: IExpression, val indexVariable: IExpression, val sign: SignExpression,
                             val assignValue: IExpression, val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val baseView = arrayVariable.view(this) + "[" + indexVariable.view(this) + "]" + sign.view(this) + assignValue.view(this)
    correctView(baseView, parent.classifier, EC_IF_WORD, EC_PUT_FIELD, EC_STORE_VAR, EC_STORE_ARRAY_VAR,
        EC_SYNCHRONIZE, EC_PUT_STATIC_FIELD)
  }

  def classifier(): ExpressionClassifier = EC_STORE_ARRAY_VAR
}