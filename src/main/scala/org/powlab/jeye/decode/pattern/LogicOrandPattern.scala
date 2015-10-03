package org.powlab.jeye.decode.pattern

import scala.collection.mutable.ArrayBuffer

import org.powlab.jeye.decode.expression.ComparisonExpressions.SIGN_GROUP_AND
import org.powlab.jeye.decode.expression.ComparisonExpressions.SIGN_GROUP_OR
import org.powlab.jeye.decode.expression.IfExpression
import org.powlab.jeye.decode.expression.IfGroupExpression
import org.powlab.jeye.decode.graph.OpcodeDetails.DETAIL_IF_OR_AND_GROUP
import org.powlab.jeye.decode.graph.OpcodeDetails.makeIfDetails
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.pattern.LogicPatterns.LogicPattern
import org.powlab.jeye.decode.pattern.LogicPatterns.isNodeIndependent
import org.powlab.jeye.decode.pattern.LogicXorPattern.isXor
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode

/**
 * Определение шаблонов логических операций OR/AND
 */
object LogicOrandPattern {

  def apply(): LogicOrandPattern = new LogicOrandPattern()

  class LogicOrandPattern() extends LogicPattern {
    def resolve(ifNode: OpcodeNode, tree: OpcodeTree): GroupOpcodeNode = {
      val ifDetail = tree.ifDetails(ifNode)
      val commonId = ifDetail.elseId;
      if (isOrand(ifNode, commonId, tree)) {
        val nodes = new ArrayBuffer[OpcodeNode]()
        nodes += ifNode
        var checkedIfNode = ifNode
        while (isOrand(checkedIfNode, commonId, tree)) {
          val checkedIfDetail = tree.ifDetails(checkedIfNode)
          checkedIfNode = tree.owner(checkedIfDetail.bodyId)
          nodes += checkedIfNode
        }
        val details = tree.ifDetails(checkedIfNode)
        val orandNode = new GroupOpcodeNode(nodes, true, tree.nextPosition(nodes.head))
        val expression = makeGroupExpresion(orandNode, tree)
        val orandDetails = makeIfDetails(DETAIL_IF_OR_AND_GROUP, details, expression)
        tree.bind(orandNode, orandDetails)

        return orandNode
      }

      null
    }

    private def isOrand(node: OpcodeNode, commonId: String, tree: OpcodeTree): Boolean = {
      val details = tree.ifDetails(node)
      val bodyNode = tree.owner(details.bodyId)
      if (!isXor(details) && isIfNode(bodyNode)) {
        val bodyDetails = tree.ifDetails(bodyNode)
        return isNodeIndependent(bodyNode, tree) && (commonId == bodyDetails.bodyId || commonId == bodyDetails.elseId)
      }
      false
    }

    private def makeGroupExpresion(ifGroup: GroupOpcodeNode, tree: OpcodeTree): IfGroupExpression = {
      val groups = ifGroup.opcodes
      val firstNode = groups.head
      val firstDetails = tree.ifDetails(firstNode)
      val lastDetails = tree.ifDetails(groups.last)
      if (lastDetails.bodyId == firstDetails.elseId) {
        val lastNode = groups.last
        val expressions = groups.map(node => {
          val nodeDetails = tree.ifDetails(node)
          val expression = nodeDetails.expression.asInstanceOf[IfExpression]
          if (node != lastNode) {
            expression.reverse
          } else {
            expression
          }
        })
        new IfGroupExpression(expressions, SIGN_GROUP_OR)
      } else {
        new IfGroupExpression(groups.map(node => tree.ifDetails(node).expression.asInstanceOf[IfExpression]), SIGN_GROUP_AND)
      }
    }
  }

}


