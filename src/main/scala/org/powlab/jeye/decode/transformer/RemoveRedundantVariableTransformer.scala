package org.powlab.jeye.decode.transformer

import org.powlab.jeye.decode.expra.ExpressionGuide.Guide
import org.powlab.jeye.decode.expra.ExpressionHelpers.getInvokable
import org.powlab.jeye.decode.expra.GuideContext
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.IExpression
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.core.ClassFile
import org.powlab.jeye.core.ConstantValueAttribute
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.utils.AttributeUtils
import org.powlab.jeye.decode.expression.MethodExpression
import org.powlab.jeye.core.AccessFlags
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import org.powlab.jeye.decode.expra.ExprSid
import scala.collection.mutable.Map

/**
 * [scope=class]
 * Трансформер для удаления лишних и не используемых задекларированных переменных.
 * Например:
 * String e; // объявлена e, которая нигде не используется.
 */
class RemoveRedundantVariableTransformer() extends Guide {

  private val varNames = Map[String, DeclareData]()

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
    //if (true) return
    val clfE = expr.classifier
    if (clfE == EC_METHOD_DECLARE) {
      varNames.clear
    }
    if (clfE == EC_STORE_DECLARE_VAR) {
      varNames(getVarName(expr)) = new DeclareData(context.esid(expr), expr, parent)
    }
    if (varNames.nonEmpty) {
      if (clfE == EC_STORE_VAR && varNames.contains(getVarName(expr))) {
        val varName = getVarName(expr)
        val declareData = varNames(varName)
        val esid = context.esid(expr)
        varNames -= varName
      }
      // Признак конца метода
      if (parent.classifier == EC_METHOD && clfE == EC_EMPTY) {
        varNames.values.foreach(declareData => {
          context.markAsRemoved(declareData.expr, declareData.parent)
        })
        varNames.clear
      }
    }
  }
}

class DeclareData(val esid: ExprSid, val expr: IExpression, val parent: IExpression)
