package org.powlab.jeye.decode.pattern.cycle

import org.powlab.jeye.decode.expression.IfExpression
import org.powlab.jeye.decode.graph.OpcodeDetails.CycleOpcodeDetail
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNodes.before
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isSwitchChild
import org.powlab.jeye.utils.DecodeUtils.select

/**
 * Определение циклов типа while
 *
 * Примечание: любой тип цикла разварачивается через while - это своего рода общий тип для всех циклов
 */
class CycleWhilePattern extends CyclePattern {

  protected def makeCycleNode(ifNode: OpcodeNode, bodyNode: OpcodeNode, outNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val ifDetails = tree.ifDetails(ifNode)
    val ifBodyNode = tree.current(ifDetails.bodyId)

    var cycleExp = ifDetails.expression.asInstanceOf[IfExpression]
    if (bodyNode != ifBodyNode) {
      cycleExp = cycleExp.reverse
    }
    val cycleNode = ifNode
    // while
    val cycleDetails = new CycleOpcodeDetail
    cycleDetails.expression = cycleExp
    cycleDetails.bodyId = bodyNode.id
    cycleDetails.elseId = outNode.id
    cycleDetails.sid = ifDetails.sid
    tree.changeDetails(cycleNode, cycleDetails)
    cycleNode
  }

  protected def getBodyNode(ifNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val ifDetails = tree.ifDetails(ifNode)
    val ifBodyNode = tree.current(ifDetails.bodyId)
    val ifElseNode = tree.current(ifDetails.elseId)
    select(before(ifBodyNode, ifElseNode), ifBodyNode, ifElseNode)
  }

  protected def getOutNode(ifNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val ifDetails = tree.ifDetails(ifNode)
    val ifBodyNode = tree.current(ifDetails.bodyId)
    val ifElseNode = tree.current(ifDetails.elseId)
    select(before(ifBodyNode, ifElseNode), ifElseNode, ifBodyNode)
  }

  protected def acceptNode(branchedNode: OpcodeNode, tree: OpcodeTree): Boolean = isIfNode(branchedNode)

  /**
   * Логика определения цикла.
   * Если узел ifNode образует петлю - то это цикл
   * Для определения петли, нужно проитерироваться по узлам, начиная с ifNode и если очередной узел указывает на
   * ifNode, то это цикл. Алгоритм не оптимальный, нужно посмотреть в сторону алгоритмов по работе с деревом
   */
  protected def closureItself(ifNode: OpcodeNode, directionNode: OpcodeNode, tree: OpcodeTree): Boolean = {
    val marker = tree.prepared
    val searchId = ifNode.id
    val ifSid = tree.sid(ifNode)
    def scan(oNode: OpcodeNode): Boolean = {
      var current = oNode;
      while (current != null) {
        val curSid = tree.sid(current)
        if (!curSid.startsWith(ifSid)) {
          return false
        }
        if (current.id == searchId) {
          return true
        }
        if (marker.isMarked(current)) {
          return false
        }
        marker.mark(current)
        if (current.branchy) {
          var index: Int = 0
          val nexts = tree.nexts(current)
          while (index < nexts.size) {
            if (scan(nexts(index))) {
              return true
            }
            index += 1
          }
        }
        current = tree.next(current)
        // проверяем если это case/default тогда реальный узел - следующий
        if (isSwitchChild(current)) {
          current = tree.next(current)
        }
      }
      false
    }
    return scan(directionNode)
  }
}


