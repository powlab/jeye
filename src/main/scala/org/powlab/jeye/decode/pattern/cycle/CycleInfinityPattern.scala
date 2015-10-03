package org.powlab.jeye.decode.pattern.cycle

import org.powlab.jeye.core.Opcodes
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.expression.Expressions.TRUE_EXPRESSION
import org.powlab.jeye.decode.expression.IfBooleanExpression
import org.powlab.jeye.decode.graph.OpcodeDetails.CycleOpcodeDetail
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.graph.OpcodeTreeBuilder
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.branch
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.{isSwitchChild, isTryNode}
import org.powlab.jeye.decode.graph.OpcodeDetails

/**
 * Определение циклов типа while на основе инструкции goto - бесконечный цикл
 * Пример:
 * while (true) {
 *   ...
 * }
 *
 */
class CycleInfinityTypePattern extends CyclePattern {

  protected def makeCycleNode(branchedNode: OpcodeNode, bodyNode: OpcodeNode, outNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val cycleOpode = new RuntimeOpcode(bodyNode.runtimeOpcode.number, Opcodes.OPCODE_IFEQ, Array())
    val cycleNode = OpcodeTreeBuilder.makeOpcodeNode(cycleOpode, tree.nextPosition(bodyNode))
    tree.add(cycleNode)
    val cycleDetails = new CycleOpcodeDetail
    cycleDetails.expression = new IfBooleanExpression(TRUE_EXPRESSION, false)
    cycleDetails.bodyId = bodyNode.id
    cycleDetails.elseId = if (outNode == null) tree.head.id else outNode.id
    cycleDetails.sid = tree.sid(bodyNode)
    tree.changeDetails(cycleNode, cycleDetails)
    tree.link(outNode, cycleNode)
    cycleNode
  }

  protected def getOutNode(branchedNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val outNode = tree.plainTree.next(branchedNode)
    if (outNode != null) tree.owner(outNode) else null
  }

  protected def getBodyNode(branchedNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    var bodyNode = tree.next(branchedNode)
    if (isSwitchChild(bodyNode)) {
      bodyNode = tree.next(bodyNode)
    }
    bodyNode
  }

  protected def acceptNode(branchedNode: OpcodeNode, tree: OpcodeTree): Boolean = {
    isGotoNode(branchedNode) && branch(branchedNode.runtimeOpcode) < branchedNode.runtimeOpcode.number
  }

  /**
   * Алгоритм определения замыкания на себя - петля, что означает наличия цикла
   */
  protected def closureItself(branchedNode: OpcodeNode, bodyNode: OpcodeNode, tree: OpcodeTree): Boolean = {
    val bodySid = tree.sido(bodyNode)
    val scopeSid = bodySid.parentId
    val branchSid = tree.sido(branchedNode)
    val branchedParentSid = branchSid.parentId
    // 1. Бесконечный цикл только в одной плоскости
    // 2. Или оптимизатор решил 2 goto сделать, когда последний вложенный блок-if
    //    1ый goto из ветки positive (if), 2ой из ветки negative (else)
    // 3. Или переходы по метке
    if (branchedParentSid != scopeSid &&
        !(isIfDoubleGotoOptimization(branchedNode, tree) ||
          isJumpRule(branchedNode, bodyNode, tree))) {
      return false
    }
    val marker = tree.prepared
    val searchId = branchedNode.id

    def scan(oNode: OpcodeNode): Boolean = {
      var current = oNode;
      while (current != null) {
        val cSid = tree.sid(current)
        if (scopeSid != null && !cSid.startsWith(scopeSid)) {
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
    return scan(bodyNode)
  }

  /**
   * Пример оптимизации
   * if (cond1) {
   *    ...........
   *    goto SOME_NODE_1;  // #NUMBER1
   * } else {
   *    ...........
   *    goto SOME_NODE_1;  // #NUMBER2
   * }
   * Задача этого метода в том, чтобы опередлить, что узлы с номерами #NUMBER1 и #NUMBER2
   * - это goto узлы, которые переходят на один и тотже узел SOME_NODE_1
   */
  private def isIfDoubleGotoOptimization(branchedNode: OpcodeNode, tree: OpcodeTree): Boolean = {
    if (!isGotoNode(branchedNode)) {
      return false
    }
    val selector = tree.selector
    val branchSid = tree.sido(branchedNode)
    val parentSid = branchSid.parentSid
    val parentNode = selector.current(parentSid.sid)
    // Это условие проработано на половину, нужно разбираться: ControlFlowTest1
    // нужно добавить проверку, что цикл уже определен для узла
//    if (isTryNode(parentNode)) {
//      return true
//    }
    if (! isIfNode(parentNode)) {
      return false
    }
    val ifLastNode = selector.last(parentSid.childId(1))
    val elseLastNode = selector.last(parentSid.childId(2))
    (isGotoNode(ifLastNode) && elseLastNode == branchedNode && branch(ifLastNode.runtimeOpcode) == branch(branchedNode.runtimeOpcode))
  }
  /**
   * Здесь определенно нужно добавить некоторую логику cм. тест ControlFlowTest2b и ControlFlowTest2
   */
  protected def isJumpRule(branchedNode: OpcodeNode, bodyNode: OpcodeNode, tree: OpcodeTree): Boolean = {
//    val bodyDetails = tree.details(bodyNode)
//    if (OpcodeDetails.isCycleDetails(bodyDetails)) {
//      return false
//    }
//    true
    return false
  }
}