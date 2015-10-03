package org.powlab.jeye.decode.pattern.ternary

import scala.collection.mutable.ArrayBuffer

import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.pattern.LogicPatterns.LogicPattern
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode

/**
 * Определение шаблонов логических операций связанных с тернарным оператором ?
 * Пример: condition ? valueWhenTrue: valueWhenFalse
 */
object LogicTernaryPattern {

  def apply(): LogicTernaryPattern = new LogicTernaryPattern()

  class LogicTernaryPattern() extends LogicPattern {
    def resolve(ifNode: OpcodeNode, tree: OpcodeTree): GroupOpcodeNode = {
      val nodes = new ArrayBuffer[OpcodeNode]
      val targetNumber = processTernary(ifNode, tree, nodes)
      if (targetNumber != null) {
        new GroupOpcodeNode(nodes, false, tree.nextPosition(ifNode))
      } else {
        null
      }
    }

    private def processTernary(ifNode: OpcodeNode, tree: OpcodeTree, nodes: ArrayBuffer[OpcodeNode]): String = {
      nodes += ifNode
      val ifDetails = tree.ifDetails(ifNode)
      val bodyNode = tree.current(ifDetails.bodyId)
      val elseNode = tree.current(ifDetails.elseId)
      val bodyTarget = getNodeTarget(bodyNode, tree, nodes)
      val elseTarget = bodyTarget != elseNode.id match {
        case true => getNodeTarget(elseNode, tree, nodes)
        case false => elseNode.id
      }
      if (bodyTarget == elseTarget) {
        val bodyTargetNode = tree.current(bodyTarget)
        // Особый случай, требует переосмысления см. AssignTest12
        if (isGotoNode(bodyTargetNode) && tree.previewCount(bodyTargetNode) > 1 &&
            tree.nexts(nodes(0)).indexOf(bodyTargetNode) == -1) {
          tree.previews(bodyTargetNode).foreach(preview => {
            nodes -= preview
          })
          getNodeTarget(bodyTargetNode, tree, nodes)
        } else {
          bodyTarget
        }
      } else {
        null;
      }
    }

    private def getNodeTarget(node: OpcodeNode, tree: OpcodeTree, nodes: ArrayBuffer[OpcodeNode]): String = {
      if (isGotoNode(node) && tree.hasNext(node) && node.group) {
        nodes += node
        return tree.next(node).id
      } else if (isIfNode(node) && !nodes.contains(node)) {
        return processTernary(node, tree, nodes)
      }
      null
    }

  }

}


