package org.powlab.jeye.decode.corrector

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.expression.Expressions.BREAK_EXPRESSION
import org.powlab.jeye.decode.expression.Expressions.CONTINUE_EXPRESSION
import org.powlab.jeye.decode.graph.GotoTypes._
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.graph.BreakWithLabelExpression
import org.powlab.jeye.decode.graph.ContinueWithLabelExpression

/**
 * Цель: упростить и скорректировать дерево, удалить 'лишние' узлы и др
 * TODO here: нужно отрефакторить и сделать код более прозрачным
 */
object OpcodeTreeSimplifier {

  def simplify(tree: OpcodeTree) {

    // Упрощаем дерево, заменяем слова goto и monitor_exit на правильный эквивалент
    val removalNodes = new ArrayBuffer[OpcodeNode]()
    removalNodes ++= tree.resources.getMonitorExitNodes

    tree.resources.getGotoNodes.foreach(node => {
      val gotoType = detect(node, tree)
      if (gotoType == GT_JUMP) {
        removalNodes += node
      } else if (gotoType == GT_CONTINUE) {
        tree.details(node).expression = CONTINUE_EXPRESSION
      } else if (gotoType == GT_BREAK) {
        tree.details(node).expression = BREAK_EXPRESSION
      } else if (gotoType == GT_BREAK_TO_LABEL) {
        val cycle = getCycleNode(node, GT_BREAK_TO_LABEL, tree)
        if (cycle != null) {
          val cycleDetail = tree.cycleDetails(cycle)
          val labelName = if (cycleDetail.label == null) "label" + node.runtimeOpcode.number else cycleDetail.label
          // TODO here: Это выражение пренадлежит только циклу и не правильно его пихать в gotoNode
          tree.details(node).expression = new BreakWithLabelExpression(labelName)
          cycleDetail.label = labelName
        } else {
          // TODO here: удалить этот хак
          tree.details(node).expression = BREAK_EXPRESSION
        }
      } else if (gotoType == GT_CONTINUE_TO_LABEL) {
        val cycle = getCycleNode(node, GT_CONTINUE_TO_LABEL, tree)
        if (cycle != null) {
          val cycleDetail = tree.cycleDetails(cycle)
          val labelName = if (cycleDetail.label == null) "label" + node.runtimeOpcode.number else cycleDetail.label
          // TODO here: Это выражение пренадлежит только циклу и не правильно его пихать в gotoNode
          tree.details(node).expression = new ContinueWithLabelExpression(labelName)
          cycleDetail.label = labelName
        } else {
          // TODO here: удалить этот хак
          tree.details(node).expression = CONTINUE_EXPRESSION
        }
      }
    })
    removalNodes.foreach(node => {
      tree.details(node).expression = null
    })

  }

  private def getCycleNode(gotoNode: OpcodeNode, gotoType: GotoType, tree: OpcodeTree): OpcodeNode = {
    val selector = tree.selector
    val targetNode = tree.next(gotoNode)
    if (targetNode != null) {
      if (gotoType == GT_CONTINUE_TO_LABEL && isCycleDetails(tree.details(targetNode))) {
        return targetNode
      }
      if (gotoType == GT_BREAK_TO_LABEL) {
        val targetSid = tree.sido(targetNode)
        val cycleSid = targetSid.previewId
        if (selector.contains(cycleSid)) {
          val cycleNode = selector.current(cycleSid)
          if (cycleNode != null && isCycleDetails(tree.details(cycleNode))) {
            return cycleNode
          }
        }
      }
    }
    null
  }

}

