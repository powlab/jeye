package org.powlab.jeye.decode.graph

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception._

/**
 * Описание узлов.
 * Требования: ReadOnly
 */
object OpcodeNodes {

  def makeId(number: Int, position: Int): String = {
    var id = number.toString
    if (position != 0) {
      id += "#" + position
    }
    id
  }

  /**
   * Скопировать узел и увеличить id на единицу
   */
  def copyWithIncrementedId(node: OpcodeNode): OpcodeNode = {
    if (node == null) {
      return null
    }
    if (node.isInstanceOf[SimpleOpcodeNode]) {
      new SimpleOpcodeNode(node.runtimeOpcode, node.branchy, node.position + 1)
    } else if (node.isInstanceOf[GroupOpcodeNode]) {
      val group = node.asInstanceOf[GroupOpcodeNode]
      new GroupOpcodeNode(group.opcodes, group.branchy, group.position + 1, group.base)
    } else {
      val newType = node.getClass().getSimpleName()
      val reason = "Неизвестный тип узла '" + newType + "'"
      val effect = "Невозможно скопировать узел '" + node + "'"
      val action = "Необходимо добавить обработку нового типа '" + newType + "' для копирования узла. "
      throw Exception(NODE_AREA, reason, effect, action)
    }
  }

  def asGroup(node: OpcodeNode): GroupOpcodeNode = {
    if (node.isInstanceOf[GroupOpcodeNode]) {
      return node.asInstanceOf[GroupOpcodeNode]
    }
    val reason = "Узлел '" + node + "' не является групповым"
    val effect = "Невозможно осуществить приведение типа данного узла к групповому"
    val action = "Необходимо поправить бизнес логику использовани метода asGroup"
    throw Exception(NODE_AREA, reason, effect, action)
  }

  def before(someNode: OpcodeNode, anyNode: OpcodeNode): Boolean = {
    if (someNode.runtimeOpcode.number != anyNode.runtimeOpcode.number) {
      return someNode.runtimeOpcode.number < anyNode.runtimeOpcode.number
    }
    someNode.position < anyNode.position
  }

  /**
   * Получить список опкодов, развернув все группы
   */
  def getOpcodeNodes(node: OpcodeNode): ArrayBuffer[OpcodeNode] = {
    val nodes = ArrayBuffer[OpcodeNode]()
    scanOpcodeNodes(node, currentNode => nodes += currentNode)
    nodes
  }

  /** Обработчик */
  type NodeHandler = (OpcodeNode) => Unit

  /**
   * Пройтись по всем узлам и для каждого вызвать обработчик
   */
  def scanOpcodeNodes(node: OpcodeNode, handler: NodeHandler) {
    if (node.isInstanceOf[GroupOpcodeNode]) {
      node.asInstanceOf[GroupOpcodeNode].opcodes.foreach(innerNode => {
        scanOpcodeNodes(innerNode, handler)
      })
      handler(node)
    } else if (node.isInstanceOf[SimpleOpcodeNode]) {
      handler(node)
    } else {
      val newType = if (node == null) "null" else node.getClass().getSimpleName()
      val reason = "Неизвестный тип узла '" + newType + "'"
      val effect = "Невозможно определить обработчик для узла '" + node + "'"
      val action = "Необходимо добавить обработчик нового типа '" + newType + "'"
      throw Exception(NODE_AREA, reason, effect, action)
    }
  }

  /**
   * Пройтись по всем узлам первого уровня!
   */
  def plainScan(node: OpcodeNode, handler: NodeHandler) {
    if (node.group) {
      node.asInstanceOf[GroupOpcodeNode].opcodes.foreach(handler)
    } else {
      handler(node)
    }
  }

}

trait OpcodeNode {
  /** Идентификатор узла */
  def id: String
  /** Позиция при горизонтальном смещении */
  def position: Int
  /** Центральный опкод */
  def runtimeOpcode: RuntimeOpcode
  /** Признак, что этот узел ветвится, т.е. имеет детей (может иметь 1 и более) */
  def branchy: Boolean
  /** Признак, что этот узел - группа */
  def group: Boolean
}

/** Простой узел */
class SimpleOpcodeNode(val runtimeOpcode: RuntimeOpcode, val branchy: Boolean = false, val position: Int = 0) extends OpcodeNode {
  val nodeId = OpcodeNodes.makeId(runtimeOpcode.number, position)
  def id: String = nodeId
  def group: Boolean = false
  override def toString(): String = id + " " + runtimeOpcode.opcode.name
}

/** Составной узел */
class GroupOpcodeNode(val opcodes: ArrayBuffer[OpcodeNode],
  branchy: Boolean, position: Int = 0, val base: Int = 0) extends SimpleOpcodeNode(opcodes(base).runtimeOpcode, branchy, position) {
  override def group: Boolean = true
  override def toString(): String = id + " " + opcodes.mkString("[", " | ", "]")
}