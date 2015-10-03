package org.powlab.jeye.decode.transformer

import org.powlab.jeye.decode.expra.ExpressionGuide.Guide
import org.powlab.jeye.decode.expra.ExpressionHelpers.getInvokable
import org.powlab.jeye.decode.expra.GuideContext
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.IExpression
import org.powlab.jeye.core.ClassFile
import org.powlab.jeye.core.ConstantValueAttribute
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.utils.AttributeUtils
import java.util.IdentityHashMap
import scala.collection.mutable.Map
import org.powlab.jeye.decode.expression.LocalVariableExpression
import org.powlab.jeye.decode.expression.NewLocalVariableExpression
import org.powlab.jeye.decode.expression.DeclareLocalVariableExpression
import org.powlab.jeye.decode.expression.LocalVariableExpression
import org.powlab.jeye.decode.LocalVariable
import org.powlab.jeye.decode.expra.ExprSid

/**
 * Корректируем локальные переменные, где-то нужно проинициализировать, где-то добавить тип
 *
 */
class VaribaleCorrectTransformer() extends Guide {

  private val vars = Map[String, VarData]()

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
    //if (true) return
    val clfE = expr.classifier
    if (clfE == EC_STORE_NEW_VAR) {
      registryNewVar(context, expr)
    }
    if (clfE == EC_STORE_VAR && !isArgumentVar(expr)) {
      checkUsageVarRegion(context, expr, parent)
      checkDeclare(context, expr, parent)
    }
  }

  /**
   * Область видимости объявления переменной не достижима для области использования и наооборот. Исправим это
   * см. тест CondJumpTestSplit2
   *
   * TODO here: возможно нужно добавить мониторинг использования переменных до ее создания.
   */
  private def checkUsageVarRegion(context: GuideContext, expr: IExpression, parent: IExpression) {
    val varName = getVarName(expr)
    if (vars.contains(varName)) {
      val varData = vars(varName)
      val esid = context.esid(expr)
      // Область видимости объявления переменной не достижима для области использования
      // Значит, обьявить переменную в общей области видимости
      if (!varData.esid.reachable(esid)) {
        // уже 1 раз заменяли, возможно нужно еще ра, а возможно и нет
        if (varData.decalreExpr != null) {
          // уже достижима
          if (varData.previewCommmonCount <= esid.commonCount(varData.esid)) {
            return
          }
          context.removeAddExprFor(varData.decalreExpr)
        }

        // 1. Сначало заменим обявление новой переменной (пример: 'int a = 1;' => 'a = 1;')
        if (varData.isNew) {
          val localVar = LocalVariableExpression(varData.mainExpr.asInstanceOf[NewLocalVariableExpression])
          context.markAsReplaced(varData.mainExpr, localVar, varData.getParent)
        } else {
          context.markAsRemoved(varData.mainExpr, varData.getParent)
        }
        // 2. Теперь поднимем объявление новой переменной в общую область (пример, 'int a;')
        val topCount = esid.commonCount(varData.esid)
        var upCount = varData.esid.count - topCount
        var curExpr = varData.mainExpr
        var curParent = varData.getParent
        while (upCount > 1) {
          upCount = upCount - 1
          curExpr = curParent
          curParent = varData(curExpr)
        }
        if (varData.isNew) {
          val newLocalExpr = varData.mainExpr.asInstanceOf[NewLocalVariableExpression]
          val declareExpr = DeclareLocalVariableExpression(newLocalExpr)
          context.link(declareExpr, curParent)
          context.markAsAddBefore(curExpr, declareExpr, curParent)
          varData.decalreExpr = declareExpr
          varData.declareEsid = context.esid(curExpr)
        } else {
          context.markAsAddBefore(curExpr, varData.mainExpr, curParent)
        }
      }
    }
  }

  /**
   * Не хватает определения переменной
   */
  private def checkDeclare(context: GuideContext, expr: IExpression, parent: IExpression) {
    val varName = getVarName(expr)
    if (!vars.contains(varName)) {
      val localVar = expr.asInstanceOf[LocalVariableExpression]
      // нет определения переменной i = j + (i = j + 5); должно быть int i = j + (i = j + 5);
      if (parent.classifier.has(EA_DEPTH)) {
        val newVar = NewLocalVariableExpression(localVar)
        context.markAsReplaced(expr, newVar, parent)
        context.link(newVar, parent)
        registryNewVar(context, newVar)
      } else if (! context.isRemoved(localVar)) {
        val declareVar = DeclareLocalVariableExpression(localVar)
        val ownerVar = context.markAsAddBefore(expr, declareVar)
        context.link(declareVar, ownerVar)
        registryNewVar(context, declareVar)
      }
    }

  }

  private def registryNewVar(context: GuideContext, expr: IExpression) {
    val name = getVarName(expr)
    val varData = new VarData(name, context.esid(expr), expr)
    var curExpr = expr
    var parent = context.getParentFor(expr)
    do {
      varData(curExpr) = parent
      curExpr = parent
      parent = context.getParentFor(curExpr)
    } while (parent != null)
    vars(name) = varData
  }

}

/**
 * Контейнер с данными, которые используются только внутри трансформера
 */
class VarData(val name: String, val esid: ExprSid, val mainExpr: IExpression) {
  private val parents = new IdentityHashMap[IExpression, IExpression]()
  def apply(expr: IExpression): IExpression = parents.get(expr)
  def update(expr: IExpression, parent: IExpression) {
    parents.put(expr, parent)
  }
  def getParent(): IExpression = this(mainExpr)
  def isNew: Boolean = mainExpr.classifier == EC_STORE_NEW_VAR
  // Уже заменяли на это
  var decalreExpr: IExpression = null
  var declareEsid: ExprSid = null
  def previewCommmonCount: Int = declareEsid.commonCount(esid)

  override def toString(): String = "[" + esid + "] expr = " + mainExpr
}
