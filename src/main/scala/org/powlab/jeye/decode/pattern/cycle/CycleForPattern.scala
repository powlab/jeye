package org.powlab.jeye.decode.pattern.cycle

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe

import org.powlab.jeye.decode.expra.ExpressionAnalyzator.get
import org.powlab.jeye.decode.expression.Expressions.EMPTY_EXPRESSION
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.graph.OpcodeDetails.CycleOpcodeDetail
import org.powlab.jeye.decode.graph.OpcodeDetails.DETAIL_CYCLE_FOR
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode
import org.powlab.jeye.decode.sids.Sid

/**
 * Определение циклов типа for
 * TODO here:
 *   Наверное более правильным подходом будет анализ кода всега метода и выявления в нем циклов отличных от while
 *   Это задача более сложная и требует обдумывания
 *
 */
class CycleForPattern {

  def resolve(whileCycleNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val cycleDetail = tree.cycleDetails(whileCycleNode)
    val selector = tree.selector
    var initActionNode = selector.preview(cycleDetail.sid)
    if (isGotoNode(initActionNode)) {
      initActionNode = selector.preview(tree.sid(initActionNode))
    }
    var postActionNode = selector.last(new Sid(cycleDetail.sid).childId(1))
    if (isGotoNode(postActionNode)) {
      postActionNode = selector.preview(tree.sid(postActionNode))
    }
    // TODO here: проверить, что есть что-то общее между initActionNode, условием и postActionNode
    // это задача требует определенной переработки Expression, внесения в них variable
    // на первом этапе сделаем через чур просто, но не совсем правильно
    if (initActionNode == null) {
      return null
    }
    if (postActionNode == null) {
      return null
    }
    val postExpression = tree.expression(postActionNode)
    val postVarName = analyzePostExpression(postExpression)
    if (postVarName == null) {
      return null
    }

    var initExpr = tree.expression(initActionNode);
    val initVarName = analyzeInitExpression(initExpr)
    if (postVarName != initVarName) {
      return null
    }
    val useInitExpr = postVarName == initVarName
    if (!useInitExpr) {
      initExpr = EMPTY_EXPRESSION
    }

    val forCycleDetails = new CycleOpcodeDetail(DETAIL_CYCLE_FOR)
    forCycleDetails.sid = cycleDetail.sid
    forCycleDetails.bodyId = cycleDetail.bodyId
    forCycleDetails.elseId = cycleDetail.elseId
    forCycleDetails.expression = new ForExpression(initExpr, cycleDetail.expression, postExpression)

    if (useInitExpr) {
      tree.isolate(initActionNode)
    }
    tree.isolate(postActionNode)
    val group = ArrayBuffer[OpcodeNode]()
    group += whileCycleNode
    if (useInitExpr) {
      group += initActionNode
    }
    group += postActionNode
    val forCycleNode = new GroupOpcodeNode(group, true, tree.nextPosition(group.head))
    tree.bind(forCycleNode, forCycleDetails)
    forCycleNode
  }

  private def analyzePostExpression(expression: IExpression): String = {
    val preExprOpt = get[PreIncrementExpression](expression)
    if (preExprOpt.isDefined) {
      val owner = preExprOpt.get
      return owner.variable.view(owner)
    }
    val postExprOpt = get[PostIncrementExpression](expression)
    if (postExprOpt.isDefined) {
      val owner = postExprOpt.get
      return owner.variable.view(owner)
    }
    val varExprOpt = get[LocalVariableExpression](expression)
    if (varExprOpt.isDefined) {
      val varExpr = varExprOpt.get
      return varExpr.variableName.view(varExpr)
    }
    null
  }

  private def analyzeInitExpression(expression: IExpression): String = {
    val newVarExprOpt = get[NewLocalVariableExpression](expression)
    if (newVarExprOpt.isDefined) {
      val owner = newVarExprOpt.get
      return owner.variableName.view(owner)
    }
    val varExprOpt = get[LocalVariableExpression](expression)
    if (varExprOpt.isDefined) {
      val owner = varExprOpt.get
      return owner.variableName.view(owner)
    }
    null
  }

}
