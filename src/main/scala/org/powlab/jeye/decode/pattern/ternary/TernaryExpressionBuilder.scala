package org.powlab.jeye.decode.pattern.ternary

import org.powlab.jeye.core._
import org.powlab.jeye.core.Types.isIntable
import org.powlab.jeye.decode.expra.ExpressionHelpers.convertLiteral
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode

/**
 * Выражения тернарного оператора
 * TODO here: падает на тесте ShortCircuitAssignTest4b
 */
object TernaryExpressionBuilder {

  /**
   * TODO here: переделать статику на обычный класс построитель TernaryExpressionBuilder, чтобы избежать 4х аргументов в методах и неоправданную рекурсию
   */
  def buildTernaryExpression(ternaryNode: GroupOpcodeNode, ternaryRef: TernaryExpressionRef, tree: OpcodeTree): IExpression = {
    var values = ternaryRef.values.iterator
    val nodes = ternaryNode.opcodes.iterator

    // TODO here: этот try необходим, пока не будут исправлены ошибки в pattern определения if групп
    try {
        buildTernaryExpression(nodes.next, nodes, values, ternaryRef, tree)
    } catch {
      case ex: Exception => CommentExpression("problem in ternary expression", true)
    }
  }

  private def buildTernaryExpression(node: OpcodeNode, nodes: Iterator[OpcodeNode], values: Iterator[IExpression], ternaryRef: TernaryExpressionRef, tree: OpcodeTree): IExpression = {
    val ifExpr = tree.expression(node)
    val baseType = ternaryRef.descriptor.baseType
    val positiveExpr = selectExpression(nodes, values, ternaryRef, tree)
    val negativeExpr = selectExpression(nodes, values, ternaryRef, tree)
    TernaryExpression(ifExpr, convertExpr(positiveExpr, baseType), convertExpr(negativeExpr, baseType))
  }

  private def convertExpr(expr: IExpression, baseType: BaseType): IExpression = {
    if (isIntable(baseType) && expr.isInstanceOf[ITypedExpression]) {
      convertLiteral(expr.asInstanceOf[ITypedExpression], baseType)
    } else {
      expr
    }
  }

  private def selectExpression(nodes: Iterator[OpcodeNode], values: Iterator[IExpression], ternaryRef: TernaryExpressionRef, tree: OpcodeTree): IExpression = {
    val node: OpcodeNode = if (nodes.hasNext) nodes.next else null
    if (isIfNode(node)) {
      buildTernaryExpression(node, nodes, values, ternaryRef, tree)
    } else {
      values.next
    }
  }

}