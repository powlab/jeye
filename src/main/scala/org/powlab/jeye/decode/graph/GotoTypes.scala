package org.powlab.jeye.decode.graph

import org.powlab.jeye.decode.processor.comparison.ComparisonInformator
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.sids.Sid
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator._
import org.powlab.jeye.decode.processor.control.SwitchInstructionInformator.isSwitchNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isSwitchChild

/**
 * Перечисление возможных представлений инструкции goto.
 */
object GotoTypes {

  class GotoType(name: String) {
    override def toString = name
  }

  val GT_BREAK = new GotoType("break")
  val GT_BREAK_TO_LABEL = new GotoType("break to label")
  val GT_CONTINUE = new GotoType("continue")
  val GT_CONTINUE_TO_LABEL = new GotoType("continue to label")
  val GT_JUMP = new GotoType("goto")
  // TODO here: для инструкции switch по строке возможен переход из тела одного case в другой через оператор goto
  // TODO here: реализация отложена
  val GT_CROSS_JUMP = new GotoType("cross_jump")

  /**
   * Инструкция goto может быть представлена как break, continue или быть не значащей (jump)
   */
  def detect(gotoNode: OpcodeNode, tree: OpcodeTree, strict: Boolean = true): GotoType = {
    // Если это не goto инструкция то и тип для нее неопределен
    if (!isGotoNode(gotoNode)) {
      return null
    }
    // Если sid отсутсвует, то это не значащая goto инструкция в составе некой группы
    if (tree.sid(gotoNode) == null) {
      return GT_JUMP
    }
    val selector = tree.selector
    val gsid = tree.sido(gotoNode)
    // Если узел goto не вложен ни в одну структуру, то это просто переход
    if (!gsid.hasParentSid) {
      return GT_JUMP
    }
    val gotoNextNode = tree.next(gotoNode)
    // проверяем если это case/default тогда реальный узел - следующий
    val targetNode = if (isSwitchChild(gotoNextNode)) {
      tree.next(gotoNextNode)
    } else {
      gotoNextNode
    }

    // Если goto указывает на последний незначащий узел, то это или jump тип или break
    val psid = gsid.parentSid
    var tsid = psid
    var gotoType = GT_JUMP
    var lastFlag = strict
    while (tsid != null && selector.contains(tsid.sid)) {
      val parentNode = selector.current(tsid.sid)
      // Если владелец goto инструкции - это switch, то переход всегда является break, при определенных условиях конечно
      if (isSwitchNode(parentNode) && gotoType == GT_JUMP) {
        if (targetNode == null) {
          return GT_BREAK
        }
        val targetSid = tree.sid(targetNode)
        if (!tsid.isParentFor(targetSid)) {
          gotoType = GT_BREAK
        } else if (isSwitchChild(targetNode)) {
          return GT_BREAK
        }
      }
      val parentDetails = tree.details(parentNode)
      // Флаг проверки, что рассматриваемый элемент всегда последний, используется для определения естественного перехода
      if (lastFlag && selector.contains(tsid.nextId) && !isSameById(parentNode, targetNode)) {
        lastFlag = false
      }
      // Если владелец goto инструкции - это цикл
      if (isCycleDetails(parentDetails)) {
        if (targetNode == null) {
          return GT_BREAK
        }
        // Если переход внутри цикла, то это просто переход
        val targetSid = tree.sid(targetNode)
        if (tsid.isParentFor(targetSid) && gotoType == GT_JUMP) {
          return GT_JUMP
        }
        // Если переход осуществляется на цикл то это continue
        if (isSameById(parentNode, tree.owner(targetNode))) {
          if (gotoType == GT_JUMP) {
            // если это естественный переход, то возвращаем jump, иначе continue
            if (lastFlag) {
              return GT_JUMP
            }
            return GT_CONTINUE
          }
          return GT_CONTINUE_TO_LABEL
        }
        // Если переход осуществляется за цикл, то это break
        if (gotoType == GT_JUMP) {
          gotoType = GT_BREAK
          if (tsid.nextId == targetSid) {
            return gotoType
          }
        } else {
          return GT_BREAK_TO_LABEL
        }
      }
      tsid = tsid.parentSid
    }

    gotoType
  }

  /**
   * Основная задача метода определить идентичность по id
   * 25 идентичен 25
   * 25#2 идентичен 25#1
   */
  private def isSameById(node1: OpcodeNode, node2: OpcodeNode): Boolean = {
    if (node1 == null) {
      return false
    }
    if (node2 == null) {
      return false
    }
    if (node1.id == node2.id) {
      return true
    }
    val sharp1 = node1.id.indexOf('#')
    val sharp2 = node2.id.indexOf('#')
    return sharp1 != -1 && sharp1 == sharp2 && node1.id.substring(0, sharp1) == node2.id.substring(0, sharp2)
  }

}