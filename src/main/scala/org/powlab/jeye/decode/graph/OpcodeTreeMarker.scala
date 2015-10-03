package org.powlab.jeye.decode.graph

import scala.collection.mutable.Map

class OpcodeTreeMarker(tree: OpcodeTree) {
  private val id2Node: Map[String, OpcodeNode] = Map[String, OpcodeNode]()
  /** Проверить, является ли узел помеченным */
  def isMarked(node: OpcodeNode): Boolean = id2Node.contains(node.id)

  /** Пометить узел как пройденный */
  def mark(node: OpcodeNode) {
    id2Node += node.id -> node
  }
}