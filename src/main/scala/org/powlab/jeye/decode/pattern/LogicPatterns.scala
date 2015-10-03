package org.powlab.jeye.decode.pattern

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.pattern.ternary._
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.GroupOpcodeNode

/**
 * Определение шаблонов логических операций OR/AND/XOR, циклов
 */
object LogicPatterns {

  /**
   *  Проверить, что main узлел, независимый, т.е. количество улов, указывающих на node меньше 2
   *  Если кол-во больше или равно 2, то это значит, что node является частью какой-то другой группы
   *  и не может сам являться узлом, который определяет новую группу.
   */
  def isNodeIndependent(node: OpcodeNode, tree: OpcodeTree): Boolean = {
    node == null || tree.previewCount(node) < 2 || tree.previews(node).filter(isIfNode(_)).map(_.id).toSet.size < 2
  }

  trait LogicPattern {
    def resolve(node: OpcodeNode, tree: OpcodeTree): GroupOpcodeNode
  }

  private val ORAND_PATTERN = LogicOrandPattern()
  private val XOR_PATTERN = LogicXorPattern()
  /**
   *  Цель: распознать if патерны OR/AND/XOR
   */
  def groupIfNodes(tree: OpcodeTree): ArrayBuffer[OpcodeNode] = {
    var result = new ArrayBuffer() ++ tree.resources.getIfNodes.map(tree.owner)
    var previewCount = 0
    while (previewCount != result.size && result.size > 1) {
      val buffer = new ArrayBuffer[OpcodeNode]()
      result.foreach(ifNode => {
        if (ifNode == tree.owner(ifNode)) {
          // Определяем группу связанную операторами && и ||
          val orandGroup = ORAND_PATTERN.resolve(ifNode, tree)
          if (orandGroup != null) {
            buffer += orandGroup
          } else {
            // Определяем группу связанную оператором ^
            val xorGroup = XOR_PATTERN.resolve(ifNode, tree)
            if (xorGroup != null) {
              buffer += xorGroup
            } else {
              buffer += ifNode
            }
          }
        }
      })
      previewCount = result.size
      result = buffer
    }
    result
  }

  private val TERNARY_PATTERN = LogicTernaryPattern()

  /**
   * Цель: определить конструкции типа ?, пример:
   * int a = (b == 7 ? 1 : 2)
   *
   * На выходе имеем множество id опкодов , которые говорят о конце выражения ?, что поможет нам при повторном
   * обходе дерева понять, где нужно 'зависнуть', чтобы получить условие, значение1 и значение2 ( условие ? значение1 : значение2)
   *
   * TODO here: переработать - оптимизировать (заюзать Stream)
   */
  def getTernaryNodes(ifNodes: ArrayBuffer[OpcodeNode], tree: OpcodeTree): ArrayBuffer[GroupOpcodeNode] = {
    val ternaryNodes = new ArrayBuffer[GroupOpcodeNode]
    val nodeMarker = tree.prepared
    ifNodes.foreach(ifNode => {
      if (! nodeMarker.isMarked(ifNode)) {
        val ternaryNode = TERNARY_PATTERN.resolve(ifNode, tree)
        if (ternaryNode != null) {
          ternaryNodes += ternaryNode
          scanOpcodeNodes(ternaryNode, nodeMarker.mark)
        }
      }
    })

    ternaryNodes
  }

  private val SIMILAR_TERNARY_OPERATOR = new SimilarTernaryPattern

  def getSimilarTernaryNodes(ifNodes: ArrayBuffer[OpcodeNode], tree: OpcodeTree): ArrayBuffer[GroupOpcodeNode] = {
    val ternaryGroups = new ArrayBuffer[GroupOpcodeNode]
    ifNodes.foreach(ifNode => {
      val ternaryGroup = SIMILAR_TERNARY_OPERATOR.resolve(ifNode, tree)
      if (ternaryGroup != null) {
        ternaryGroups += ternaryGroup
      }
    })
    ternaryGroups
  }

}


