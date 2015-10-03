package org.powlab.jeye.decode.graph

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map

/**
 * Цель: перепаковать узлы в дереве таким образом, чтобы остались только те
 * которые имеют выражение, но при этом сохранив информацию обо всех узлах
 */
object OpcodeTreeRepacker {

  private type Nodes = ArrayBuffer[OpcodeNode]

  def repack(tree: OpcodeTree) {
    val node2group = Map[OpcodeNode, Nodes]()
    val groups = new ArrayBuffer[Nodes]
    var concatNodes = new Nodes
    val marker = tree.prepared

    def checkOld(node: OpcodeNode) {
      if (tree.expression(node) == null) {
        val nodesOpt = node2group.get(node)
        if (nodesOpt.isDefined) {
          val nodes = nodesOpt.get
          nodes.insertAll(nodes.size - 1, concatNodes)
          concatNodes = new Nodes
        }
      } else if (concatNodes.nonEmpty) {
        val nodesOpt = node2group.get(node)
        if (nodesOpt.isDefined) {
          val nodes = nodesOpt.get
          nodes.insertAll(nodes.size - 1, concatNodes)
        } else {
          concatNodes += node
          concatNodes.foreach(node => {
            node2group += (node -> concatNodes)
          })
          groups += concatNodes
          concatNodes = new Nodes
        }
      }
    }
    def compact(node: OpcodeNode) {
      if (tree.expression(node) == null) {
        concatNodes += node
      } else if (concatNodes.nonEmpty) {
        concatNodes += node
        concatNodes.foreach(node => {
          node2group += (node -> concatNodes)
        })
        groups += concatNodes
        concatNodes = new Nodes
      }
    }

    def doSkan(node: OpcodeNode) {
      var preview: OpcodeNode = null
      var current: OpcodeNode = node
      while (current != null) {
        if (marker.isMarked(current)) {
          checkOld(current)
          return
        }
        compact(current)
        marker.mark(current);
        if (current.branchy) {
          tree.nexts(current).foreach(doSkan(_))
          return
        }
        preview = current
        current = tree.next(current)
      }
    }
    doSkan(tree.head)
    groups.foreach(nodes => {
      val baseNode = nodes.last
      val group = new GroupOpcodeNode(nodes, baseNode.branchy, tree.nextPosition(baseNode), nodes.size - 1)
      //println("[new group] " + group)
      tree.bind(group, tree.details(baseNode))
    })
  }

  /**
   * TODO here: старый вариант
   * Оставить только те узлы, которые содержат expression
   */
  //  private def repack(tree: OpcodeTree) {
  //    val removeNodes = new ArrayBuffer[OpcodeNode]
  //    OpcodeTreeHelper.scanTree(tree, node => {
  //      if (tree.details(node).expression == null) {
  //        removeNodes += node
  //      }
  //    })
  //    removeNodes.foreach(tree.remove)
  //    println("[removeNodes ] " + removeNodes)
  //  }

}

