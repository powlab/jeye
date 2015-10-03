package org.powlab.jeye.decode.pattern.ternary

import scala.collection.mutable.ArrayBuffer

import org.powlab.jeye.decode.expression.ReturnVarExpression
import org.powlab.jeye.decode.expression.TernaryExpression
import org.powlab.jeye.decode.graph.OpcodeDetails.OpcodeDetail
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.pattern.LogicPatterns.LogicPattern
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.processor.control.ReturnInstructionInformator.getReturnType
import org.powlab.jeye.decode.processor.control.ReturnInstructionInformator.isReturnNode
import org.powlab.jeye.decode.processor.control.ReturnInstructionInformator.isVoidReturnNode

/**
 * Определение контсрукций связанных с тернарным оператором ? представленным в коде с помощью return
 * Пример:
   if (int0 > 1) {
     return true;
   }
   return false;
 * - данное выражение сварачивается в тернарку, хотя по виду им не является
 * Результат: return int0 > 1;
 * Логика определения тернарки на уровне выражений
 */
class SimilarTernaryPattern() extends LogicPattern {
    def resolve(ifNode: OpcodeNode, tree: OpcodeTree): GroupOpcodeNode = {
      if (isIfNode(ifNode)) {
        val details = tree.ifDetails(ifNode)
        val bodyNode = tree.current(details.bodyId)
        val elseNode = tree.current(details.elseId)
        if (isReturnNode(bodyNode) && isReturnNode(elseNode) &&
            !isVoidReturnNode(bodyNode) && getReturnType(bodyNode) == getReturnType(elseNode) &&
            tree.previewCount(bodyNode) == 1 && tree.previewCount(elseNode) == 1) {
          val nodes = ArrayBuffer(ifNode, bodyNode, elseNode)
          val posExpr = tree.details(bodyNode).expression.asInstanceOf[ReturnVarExpression].variable
          val negExpr = tree.details(elseNode).expression.asInstanceOf[ReturnVarExpression].variable

          val ternaryExpr = TernaryExpression(details.expression, posExpr, negExpr)
          val ternaryDetails = new OpcodeDetail
          ternaryDetails.expression = new ReturnVarExpression(ternaryExpr)

          val group = new GroupOpcodeNode(nodes, false, tree.nextPosition(ifNode))
          tree.bind(group, ternaryDetails)
          return group
        }
      }
      null
    }

}

