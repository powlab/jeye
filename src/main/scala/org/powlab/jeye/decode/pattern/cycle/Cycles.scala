package org.powlab.jeye.decode.pattern.cycle

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer

import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree

import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode

/**
 * Определение шаблонов типа цикл
 */
object Cycles {

  private val cycleWhilePattern = new CycleWhilePattern
  private val cycleInfinityPattern = new CycleInfinityTypePattern
  private val cycleDoWhilePattern = new CycleDoWhilePattern
  private val cycleForPattern = new CycleForPattern

  /**
   * Определение циклов (все циклы сначало - это while циклы)
   */
  def detectCycles(ifNodes: ArrayBuffer[OpcodeNode], tree: OpcodeTree): ArrayBuffer[OpcodeNode] = {
    ifNodes.filter(cycleWhilePattern.resolve(_, tree) != null)
  }

  def detectInfinityCycles(gotoNodes: Buffer[OpcodeNode], ifNodes: Buffer[OpcodeNode], tree: OpcodeTree): InfinityCycleResult = {
    val cycleNodes = ArrayBuffer[OpcodeNode]()
    val gotoDetectNodes = ArrayBuffer[OpcodeNode]()
    val ifDetectNodes = ArrayBuffer[OpcodeNode]()
    val branchNodes = (ArrayBuffer[OpcodeNode]() ++ gotoNodes ++ ifNodes).sortWith((node1, node2) => {
      node1.runtimeOpcode.number < node2.runtimeOpcode.number
    });
    branchNodes.foreach(branchNode => {
      if (isGotoNode(branchNode)) {
        val cycleNode = cycleInfinityPattern.resolve(branchNode, tree)
        if (cycleNode != null) {
          cycleNodes += cycleNode
          gotoDetectNodes += branchNode
        }
      }
      if (isIfNode(branchNode)) {
        val cycleNode = cycleDoWhilePattern.resolve(branchNode, tree)
        if (cycleNode != null) {
          cycleNodes += cycleNode
          ifDetectNodes += branchNode
        }
      }
    });
    new InfinityCycleResult(gotoDetectNodes, ifDetectNodes, cycleNodes)
  }

  /**
   * Определение циклов 'for'
   */
  def detectForCycles(cycleNodes: ArrayBuffer[OpcodeNode], tree: OpcodeTree): ArrayBuffer[OpcodeNode] = {
    cycleNodes.filter(cycleForPattern.resolve(_, tree) != null)
  }

}

class InfinityCycleResult(val gotoNodes: Buffer[OpcodeNode], val ifNodes: Buffer[OpcodeNode], val cycleNodes : Buffer[OpcodeNode]) {
  def notEmpty = cycleNodes.nonEmpty
}

