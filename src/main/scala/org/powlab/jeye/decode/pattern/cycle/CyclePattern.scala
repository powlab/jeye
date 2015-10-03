package org.powlab.jeye.decode.pattern.cycle

import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree

/**
 * Базовый контракт: определение циклов
 *
 */
abstract class CyclePattern {

  /**
   * Определить цикл.
   * Если цикл успешно определен - возвращается узел, его описывающий, иначе null
   */
  def resolve(branchedNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    if (isCycle(branchedNode, tree)) {
      val bodyNode = getBodyNode(branchedNode, tree)
      val outNode = getOutNode(branchedNode, tree)
      makeCycleNode(branchedNode, bodyNode, outNode, tree)
    } else {
      null
    }
  }

  protected def makeCycleNode(branchedNode: OpcodeNode, bodyNode: OpcodeNode, outNode: OpcodeNode, tree: OpcodeTree): OpcodeNode

  /**
   * Логика определения цикла.
   * Если узел branchedNode образует петлю - то это цикл
   * Для определения петли, нужно проитерироваться по узлам, начиная с branchedNode и если очередной узел указывает на
   * branchedNode, то это цикл. Алгоритм не оптимальный, нужно посмотреть в сторону алгоритмов по работе с деревом
   */
  protected def isCycle(branchedNode: OpcodeNode, tree: OpcodeTree): Boolean = {
    if (acceptNode(branchedNode, tree)) {
      val bodyNode = getBodyNode(branchedNode, tree)
      if (bodyNode != null) {
        return closureItself(branchedNode, bodyNode, tree)
      }
    }
    false
  }

  protected def getBodyNode(branchedNode: OpcodeNode, tree: OpcodeTree): OpcodeNode;

  protected def getOutNode(branchedNode: OpcodeNode, tree: OpcodeTree): OpcodeNode;

  protected def acceptNode(branchedNode: OpcodeNode, tree: OpcodeTree): Boolean

  /**
   * Алгоритм определения замыкания на себя - петля, что означает наличия цикла
   */
  protected def closureItself(branchedNode: OpcodeNode, bodyNode: OpcodeNode, tree: OpcodeTree): Boolean

}


