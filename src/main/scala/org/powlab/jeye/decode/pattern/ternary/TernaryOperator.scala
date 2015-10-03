package org.powlab.jeye.decode.pattern.ternary

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.collection.mutable.Set

import org.powlab.jeye.core.Exception
import org.powlab.jeye.decode.expression.TernaryExpressionRef
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNodes.plainScan
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.pattern.LogicPatterns
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.branch
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode
/**
 * Тернарный оператор
 */
object TernaryOperator {

  private def getTarget(ternaryNode: GroupOpcodeNode, tree: OpcodeTree): String = {
    val gotoNodeOpt = ternaryNode.opcodes.find(isGotoNode(_))
    if (gotoNodeOpt.isEmpty) {
      val reason = "Не найдена инструкция 'goto' " + ternaryNode.opcodes + " при определении инструкции назначения тернарного оператора"
      val effect = "Невозможно определить шаблон тернарного оператора, обработка метода будет прекращена"
      val action = "Необходимо исправить логику определения шаблона тернарного оператора"
      throw Exception(Exception.TERNARY_AREA, reason, effect, action)
    }
    val gotoNode = gotoNodeOpt.get
    val targetId = branch(gotoNode.runtimeOpcode).toString
    val streamGroupOpt = tree.resources.getPatternResults.find(patternResult => {
      patternResult.group.group &&
        patternResult.group.asInstanceOf[GroupOpcodeNode].opcodes.find(_.id == targetId).isDefined
    })
    if (streamGroupOpt.isDefined) {
      streamGroupOpt.get.group.id
    } else {
      targetId
    }
  }

  /**
   * На выходе имеем множество id опкодов , которые говорят о конце выражения ?, что поможет нам при повторном
   * обходе дерева понять, где нужно 'зависнуть', чтобы получить условие, значение1 и значение2 ( условие ? значение1 : значение2)
   */
  def getTargets(ifNodes: ArrayBuffer[OpcodeNode], tree: OpcodeTree): Set[String] = {
    val ternaryNodes = LogicPatterns.getTernaryNodes(ifNodes, tree)
    Set() ++ ternaryNodes.map(ternaryNode => getTarget(ternaryNode, tree))
  }

  /**
   * Цель: Упроздняем if вида 'condition ? value1 : value2'
   */
  def processTernary(questions: Map[String, TernaryExpressionRef], ifNodes: ArrayBuffer[OpcodeNode], tree: OpcodeTree): ArrayBuffer[OpcodeNode] = {
    val ternaryNodes = LogicPatterns.getTernaryNodes(ifNodes, tree)
    val removal = ArrayBuffer[OpcodeNode]()
    if (ternaryNodes.size != questions.size) {
      val reason = "Количество шаблонов тернарного оператора '" + ternaryNodes.size +
        "' не совпадает с количеством найденных выражений '" + questions.size + "', полученных при анализе дерева"
      val effect = "Невозможно правильно построить тернарное выражение, обработка метода будет прекращена "
      val action = "Необходимо исправить логику определения шаблона тернарного оператора"
      throw Exception(Exception.TERNARY_AREA, reason, effect, action)
    }
    ternaryNodes.foreach(ternaryNode => {
      val ternaryRef = questions(getTarget(ternaryNode, tree))
      ternaryRef.expression = TernaryExpressionBuilder.buildTernaryExpression(ternaryNode, ternaryRef, tree)
      plainScan(ternaryNode, node => {
        tree.isolate(node)
        if (isIfNode(node)) {
          removal += node
        }
      })
    })
    removal
  }

  def compactSimilarTernary(ifNodes: ArrayBuffer[OpcodeNode], tree: OpcodeTree): ArrayBuffer[OpcodeNode] = {
    LogicPatterns.getSimilarTernaryNodes(ifNodes, tree).map(_.opcodes.head)
  }

}


