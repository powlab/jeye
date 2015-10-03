package org.powlab.jeye.decode.pattern.cycle

import org.powlab.jeye.core.Opcodes
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.expression.IfExpression
import org.powlab.jeye.decode.expression.WhileCycleExpression
import org.powlab.jeye.decode.graph.OpcodeDetails
import org.powlab.jeye.decode.graph.OpcodeDetails.CycleOpcodeDetail
import org.powlab.jeye.decode.graph.OpcodeNodes
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.graph.OpcodeTreeBuilder
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isSwitchChild

/**
 * Определение циклов типа do-while на основе инструкций семейства if
 * Пример:
 * do {
 *   ...
 * } while (a > b)
 *
 */
class CycleDoWhilePattern extends CycleInfinityTypePattern {

  protected override def makeCycleNode(ifNode: OpcodeNode, bodyNodeNative: OpcodeNode, outNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val bodyNode = tree.owner(bodyNodeNative)
    val cycleOpode = new RuntimeOpcode(bodyNode.runtimeOpcode.number, Opcodes.OPCODE_IFEQ, Array())
    val cycleNode = OpcodeTreeBuilder.makeOpcodeNode(cycleOpode, tree.nextPosition(bodyNode))
    tree.add(cycleNode)
    val cycleDetails = new CycleOpcodeDetail(OpcodeDetails.DETAIL_CYCLE_DO_WHILE)
    cycleDetails.expression = new WhileCycleExpression(tree.expression(ifNode).asInstanceOf[IfExpression].reverse, ";")
    cycleDetails.bodyId = bodyNode.id
    cycleDetails.elseId = outNode.id
    cycleDetails.sid = tree.sid(bodyNode)
    tree.changeDetails(cycleNode, cycleDetails)
    tree.replace(ifNode, cycleNode)
    tree.isolate(ifNode)
    //tree.link(outNode, cycleNode)
    cycleNode
  }

  protected override def getOutNode(ifNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    tree.next(ifNode)
  }

  protected override def getBodyNode(ifNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val jumpNumber = ComparisonInformator.branch(ifNode.runtimeOpcode)
    val position = tree.lastPosition(jumpNumber)
    val id = OpcodeNodes.makeId(jumpNumber, position)
    var bodyNode = tree.current(id)
    if (isSwitchChild(bodyNode)) {
      bodyNode = tree.next(bodyNode)
    }
    bodyNode
  }

  protected override def acceptNode(ifNode: OpcodeNode, tree: OpcodeTree): Boolean = {
    isIfNode(ifNode) && ComparisonInformator.branch(ifNode.runtimeOpcode) < ifNode.runtimeOpcode.number
  }

  protected override def isJumpRule(branchedNode: OpcodeNode, bodyNode: OpcodeNode, tree: OpcodeTree): Boolean = {
    val bodyDetails = tree.details(bodyNode)
    val outNode = getOutNode(branchedNode, tree)
    if (tree.previewCount(outNode) > 1) {
      return true
    }
    if (OpcodeDetails.isCycleDetails(bodyDetails)) {
      return false
    }
    true
  }

}