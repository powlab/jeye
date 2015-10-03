package org.powlab.jeye.decode.pattern

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.universe

import org.powlab.jeye.core.Opcodes.OPCODE_IFNE
import org.powlab.jeye.decode.expra.ExpressionAnalyzator.cast
import org.powlab.jeye.decode.expression.ComparisonExpressions.SIGN_GROUP_XOR
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.pattern.LogicPatterns.LogicPattern
import org.powlab.jeye.decode.pattern.LogicPatterns.isNodeIndependent
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode

/**
 * Определение шаблонов логических операций OR/AND
 *
 */
object LogicXorPattern {

  def apply(): LogicXorPattern = new LogicXorPattern()

  class LogicXorPattern() extends LogicPattern {
    def resolve(node: OpcodeNode, tree: OpcodeTree): GroupOpcodeNode = {
      if (!isXorPattern(node, tree)) {
        return null
      }
      val elseId = tree.ifDetails(node).elseId
      if (!isXorPattern(tree.current(elseId), tree)) {
        return null
      }
      val nodes = ArrayBuffer[OpcodeNode]()
      nodes += node
      var currentNode = tree.owner(elseId)
      do {
        nodes += currentNode
        currentNode = tree.owner(tree.ifDetails(currentNode).elseId)
      } while (isXorPattern(currentNode, tree))

      if (isXor(tree.details(currentNode))) {
        val details = tree.ifDetails(currentNode)
        val fullGroup = (new ArrayBuffer[OpcodeNode] += currentNode) ++ nodes
        val xorNode = new GroupOpcodeNode(fullGroup, true, tree.nextPosition(currentNode))
        val expression = makeXorExpression(xorNode, tree)
        val xorDetails = makeIfDetails(DETAIL_IF_XOR_GROUP, details, expression)
        tree.bind(xorNode, xorDetails)
        return xorNode
      }
      null
    }

    private def isXorPattern(checkedNode: OpcodeNode, tree: OpcodeTree): Boolean = {
      val checkedDetails = tree.details(checkedNode)
      if (isIfDetails(checkedDetails) && !isXor(checkedDetails) && isNodeIndependent(checkedNode, tree)) {
        val details = checkedDetails.asInstanceOf[IfOpcodeDetail]
        val bodyNode = tree.current(details.bodyId)
        return isGotoNode(bodyNode) && tree.hasNext(bodyNode) && tree.next(bodyNode) == tree.current(details.elseId)
      }
      false
    }

    private def makeXorExpression(xorGroup: GroupOpcodeNode, tree: OpcodeTree): IfGroupXorExpression = {
      val xorNode = xorGroup.opcodes.head
      val groups = xorGroup.opcodes.tail

      val reversed = getReverseStates(tree.details(xorNode).expression)
      val expressions = new ArrayBuffer[IfExpression]
      var index = 0;
      groups.foreach(ifNode => {
        val nodeDetails = tree.ifDetails(ifNode)
        val expression = nodeDetails.expression.asInstanceOf[IfExpression]
        if (reversed(index)) {
          expressions += expression.reverse
        } else {
          expressions += expression
        }
        index += 1
      })
      new IfGroupXorExpression(expressions, xorNode.runtimeOpcode.opcode == OPCODE_IFNE)
    }
    private def convertToBoolean(expression: IExpression, parent: IExpression): Boolean = {
      val value = expression.view(parent).toInt
      value == 0
    }

    /**
     * TODO here: когда будут переработы математические выражения, эта часть будет в 10 раз проще
     * потому что в одном математическом выражении будет все цифры, объедененные оператором ^
     * // 1 ^ 0 ^ 1 ... != 0
     */
    private def getReverseStates(expression: IExpression): ArrayBuffer[Boolean] = {
      val ifExpr = cast[IfSimpleExpression](expression)
      val mathExpr = cast[MathExpression](ifExpr.left)
      val results = new ArrayBuffer[Boolean]()
      results += convertToBoolean(mathExpr.variable, mathExpr)
      mathExpr.pairs.foreach(pair => {results += convertToBoolean(pair.variable, pair)})
      results
    }
  }

  def isXor(details: OpcodeDetail): Boolean = {
    val expression = details.expression
    if (details.detailType == DETAIL_IF_SINGLE && expression != null && expression.isInstanceOf[IfSimpleExpression]) {
      val leftExpression = expression.asInstanceOf[IfSimpleExpression].left
      return leftExpression.isInstanceOf[MathExpression] &&
        leftExpression.asInstanceOf[MathExpression].pairs.head.sign == SIGN_GROUP_XOR
    }
    false
  }

}


