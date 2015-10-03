package org.powlab.jeye.decode.graph

import org.powlab.jeye.decode.graph.OpcodeNodes._

/**
 * Набор утилитных методов
 */
object OpcodeTreeHelper {

  def scanTree(tree: OpcodeTree, handler: NodeHandler) {
    scanTree(tree, tree.head, handler)
  }

  /**
   *  Обход дерева dev.powlab.org 178.62.179.49
   *  TODO here: оптимизация, заменить рекурсию на стэковую обработку
   */
  def scanTree(tree: OpcodeTree, fromNode: OpcodeNode, handler: NodeHandler) {
    val marker = tree.prepared
    def doSkan(node: OpcodeNode) {
      var preview: OpcodeNode = null
      var current: OpcodeNode = node
      while (current != null) {
        if (marker.isMarked(current)) {
          return
        }
        marker.mark(current);
        handler(current)
        if (current.branchy) {
          tree.nexts(current).foreach(doSkan(_))
          return
        }
        preview = current
        current = tree.next(current)
      }
    }
    doSkan(fromNode)
  }

  def copy(tree: OpcodeTree): OpcodeTree = {
    val newTree = new OpcodeTree(tree.plainTree)
    def registryNode(node: OpcodeNode) {
      if (! newTree.has(node)) {
        val newDetails = OpcodeDetails.copyDetails(tree.details(node))
        newTree.registryNode(node, newDetails)
      }
    }
    // Получить ссылку на ранее созданный узел или создать новый
    def get(node: OpcodeNode): OpcodeNode = {
      scanOpcodeNodes(node, registryNode)
      newTree.current(node.id)
    }
    // 2. copy of links
    scanTree(tree, opcodeNode => {
      val newOpcode = get(opcodeNode)
      tree.nexts(opcodeNode).foreach(nextOpcodeNode => {
        val nextNewNode = get(nextOpcodeNode)
        newTree.link(nextNewNode, newOpcode)
      })
    })
    newTree.link(newTree.current(0), newTree.head)
    val newResources = newTree.resources
    // TODO here: так как инструкции неизменяемые, то можно просто перекладывать их
    tree.resources.scanAll(opcodeNode => {
      val newResource = newTree.current(opcodeNode.id)
      newResources += newResource
    })
    newResources ++= tree.resources.getPatternResults
    newResources ++= tree.resources.getClassifiers

    newTree
  }

}