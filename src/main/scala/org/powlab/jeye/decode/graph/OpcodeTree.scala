package org.powlab.jeye.decode.graph

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Buffer
import scala.collection.mutable.Map
import org.powlab.jeye.core.Opcodes
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.OpcodeTreeListeners._
import org.powlab.jeye.decode.sids.SidSelector
import org.powlab.jeye.decode.sids.Sid
import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception._

object OpcodeTree {
  // голова
  private val HEAD_RUNTIME_OPCODE = new RuntimeOpcode(-1, Opcodes.OPCODE_NOP, Array())
  private val HEAD_EXPRESSION = Sex("head");
  private val HEAD_NODE = new SimpleOpcodeNode(HEAD_RUNTIME_OPCODE)

  private def indexOf(nodes: Buffer[OpcodeNode], node: OpcodeNode): Int = nodes.indexWhere(_.id == node.id)

  private def removeFrom(nodes: Buffer[OpcodeNode], node: OpcodeNode): Int = {
    val index = indexOf(nodes, node)
    if (index != -1) {
      nodes.remove(index)
    }
    index
  }

  private def replaceIn(nodes: Buffer[OpcodeNode], replacedNode: OpcodeNode, newNode: OpcodeNode): Int = {
    val index = indexOf(nodes, replacedNode)
    if (index != -1) {
      nodes(index) = newNode
    }
    index
  }

  private def insertBefore(nodes: Buffer[OpcodeNode], node: OpcodeNode, newNode: OpcodeNode): Int = {
    val index = indexOf(nodes, node)
    if (index != -1) {
      nodes.insert(index, newNode)
    }
    index
  }

}

/** Связь */
class Relation {
  val previews: Buffer[OpcodeNode] = new ArrayBuffer[OpcodeNode]()
  val nexts: Buffer[OpcodeNode] = new ArrayBuffer[OpcodeNode]()
}

/**
 * Дерево(граф) инструкций
 * plainTree - физическая структура
 */
class OpcodeTree(val plainTree: OpcodeTree) {
  /** Всегда держим ссылку на хэд как маркер для начала обхода дерева, чтобы избежать манимуляций с ним*/
  val head = OpcodeTree.HEAD_NODE
  private val id2Node: Map[String, OpcodeNode] = Map[String, OpcodeNode]()
  private val id2Relation: Map[String, Relation] = Map[String, Relation]()
  private val id2Detail: Map[String, OpcodeDetail] = Map[String, OpcodeDetail]()
  /** Мэпинг id-узла на группу, которой он принадлежит.*/
  private val id2Owner: Map[String, OpcodeNode] = Map[String, OpcodeNode]()
  private val listeners = new ArrayBuffer[IdChanger]
  var selector: SidSelector = null

  // Инициализация головы
  registryNode(head)
  details(head).expression = OpcodeTree.HEAD_EXPRESSION

  val resources = new OpcodeTreeResource

  def top: OpcodeNode = next(head)

  /** Регистрация узла в дереве */
  def registryNode(node: OpcodeNode, details: OpcodeDetail = new OpcodeDetail) {
    id2Node.put(node.id, node)
    id2Relation.put(node.id, new Relation)
    if (node.isInstanceOf[GroupOpcodeNode]) {
      scanOpcodeNodes(node, someNode => id2Owner.put(someNode.id, node))
    } else {
      id2Owner.put(node.id, node)
    }
    changeDetails(node, details)
  }

  /** Подготовить счетчик перед новым обходом дерева */
  def prepared(): OpcodeTreeMarker = new OpcodeTreeMarker(this)

  /** Получить текущий узел, первый в горизонтальном списке */
  def current(nodeId: String): OpcodeNode = id2Node.getOrElse(nodeId, null)

  /** Получить текущий узел, первый в горизонтальном списке */
  def current(node: OpcodeNode): OpcodeNode = current(node.id)

  /** Получить текущий узел по номеру опкода, первый в горизонтальном списке */
  def current(number: Int): OpcodeNode = current(number.toString);

  /** Получить текущий узел по номеру опкода, последний в горизонтальном списке */
  def currentLast(number: Int): OpcodeNode = current(makeId(number, lastPosition(number)));

  /** Проверить существует ли указанный узел по номеру опкода */
  def has(number: Int): Boolean = current(number.toString) != null

  /** Проверить существует ли указанный узел по номеру опкода */
  def has(id: String): Boolean = current(id) != null

  /** Проверить существует ли указанный узел по номеру опкода */
  def has(node: OpcodeNode): Boolean = has(node.id)

  /** Получить предыдущий узел */
  def preview(node: OpcodeNode): OpcodeNode = relation(node.id).previews.headOption.getOrElse(null)

  /** Получить предыдущий узел, смещенный от указанного на count */
  def preview(node: OpcodeNode, count: Int): OpcodeNode = {
    val previewNode = preview(node)
    if (count > 1 && previewNode != null) {
      preview(previewNode, count - 1)
    } else {
      previewNode
    }
  }

  /** Проверить, что существует более 1 связи */
  def hasPreviews(node: OpcodeNode): Boolean = relation(node.id).previews.size > 1

  /**Получить предыдущие узлы */
  def previews(node: OpcodeNode): Buffer[OpcodeNode] = relation(node.id).previews

  /**Получить количесвто следующие узлы */
  def previewCount(node: OpcodeNode): Int = relation(node.id).previews.size

  /** Проверить, есть ли следующий */
  def hasNext(node: OpcodeNode): Boolean = relation(node.id).nexts.nonEmpty

  /**Получить следующий узел */
  def next(node: OpcodeNode): OpcodeNode = relation(node.id).nexts.headOption.getOrElse(null)

  /**Получить следующие узлы */
  def nexts(node: OpcodeNode): Buffer[OpcodeNode] = relation(node.id).nexts

  /**Получить количесвто следующие узлы */
  def nextCount(node: OpcodeNode): Int = relation(node.id).nexts.size

  /**Связать 2 узла между собой */
  def link(current: OpcodeNode, preview: OpcodeNode) {
    if (preview != null && current != null) {
      addNext(preview, current)
      addPreview(current, preview)
    }
  }

  /** Удалить узел */
  def removeFromNexts(target: OpcodeNode, removedNode: OpcodeNode): Int = {
    OpcodeTree.removeFrom(nexts(target), removedNode)
  }

  /** Удалить узел */
  def removeFromPreviews(target: OpcodeNode, removedNode: OpcodeNode): Int = {
    OpcodeTree.removeFrom(previews(target), removedNode)
  }

  /** Вставить перед */
  def insertBeforeInPreviews(target: OpcodeNode, beforeNode: OpcodeNode, newNodeNode: OpcodeNode): Int = {
    OpcodeTree.insertBefore(previews(target), beforeNode, newNodeNode)
  }

  /** Вставить перед */
  def insertBeforeInNexts(target: OpcodeNode, beforeNode: OpcodeNode, newNodeNode: OpcodeNode): Int = {
    OpcodeTree.insertBefore(nexts(target), beforeNode, newNodeNode)
  }

  /** Стереть информацию об узле */
  def erase(erasedNode: OpcodeNode) {
    val erasedId = erasedNode.id
    id2Node.remove(erasedId)
    id2Relation.remove(erasedId)
    id2Detail.remove(erasedId)
    id2Owner.remove(erasedId)
    resources -= erasedNode
  }

  /** Изолировать узел */
  def isolate(isolatedNode: OpcodeNode) {
    val previewNodes = previews(isolatedNode)
    val nextNodes = nexts(isolatedNode)
    previewNodes.foreach(preview => {
      nextNodes.foreach(next => {
        if (preview != next) {
          fireIdChangeEvent(isolatedNode.id, next.id)
          insertBeforeInNexts(preview, isolatedNode, next)
        }
      })
    })
    nextNodes.foreach(next => {
      previewNodes.foreach(preview => {
        if (preview != next) {
          fireIdChangeEvent(isolatedNode.id, preview.id)
          insertBeforeInPreviews(next, isolatedNode, preview)
        }
      })
    })
    previewNodes.foreach(removeFromNexts(_, isolatedNode))
    nextNodes.foreach(removeFromPreviews(_, isolatedNode))
    previewNodes.clear
    nextNodes.clear
    removeFromSelector(isolatedNode)
  }

  /** Удалить информацию об узле */
  def remove(removedNode: OpcodeNode) {
    isolate(removedNode)
    erase(removedNode)
  }

  def redefine(oldNode: OpcodeNode, newNode: OpcodeNode) {
    if (!has(oldNode.id)) {
      registryNode(oldNode)
    }
    if (!has(newNode.id)) {
      registryNode(newNode, details(oldNode))
    }
    replace(oldNode, newNode)
  }

  def replace(oldNode: OpcodeNode, newNode: OpcodeNode) {
    previews(oldNode).foreach(node => {
      OpcodeTree.replaceIn(nexts(node), oldNode, newNode)
      addPreview(newNode, node)
    })
    nexts(oldNode).foreach(node => {
      OpcodeTree.replaceIn(previews(node), oldNode, newNode)
      addNext(newNode, node)
    })
    fireIdChangeEvent(oldNode.id, newNode.id)
  }

  def changeNext(node: OpcodeNode, replacedNode: OpcodeNode, newNode: OpcodeNode) {
    OpcodeTree.replaceIn(nexts(node), replacedNode, newNode)
  }

  def add(newNode: OpcodeNode) {
    val id = makeId(newNode.runtimeOpcode.number, nextPosition(newNode.runtimeOpcode.number) - 1)
    registryNode(newNode)
    val current = this.current(id)
    val previewNodes = previews(current)
    previewNodes.foreach(preview => {
      changeNext(preview, current, newNode)
      addPreview(newNode, preview)
    })
    previewNodes.clear
    link(current, newNode)
    fireIdChangeEvent(id, newNode.id)
  }

  def owner(node: OpcodeNode): OpcodeNode = owner(node.id)

  def owner(nodeId: String): OpcodeNode = id2Owner.getOrElse(nodeId, null)

  /**
   *  Цель: все узлы группы должны вести себя как один узел, для этого
   *  все входящие связи (связи, которые указывают на любой из узлов группы) нужно нацелить на group узел
   *  и все исходящие аналогично. Приэтом у каждого узла группы должны быть свои детали
   */
  def bind(group: GroupOpcodeNode, details: OpcodeDetail) {
    registryNode(group, details)
    val opcodes = group.opcodes
    val incomes = new ArrayBuffer[OpcodeNode]
    val outcomes = new ArrayBuffer[OpcodeNode]
    opcodes.foreach(opcode => {
      fireIdChangeEvent(opcode.id, group.id)
      // Ищем узлы, которые указывают хотя бы на 1 из узлов opcodes, исключая самих себя
      previews(opcode).filter(node => (!opcodes.contains(node) && !incomes.contains(node))).foreach(preview => {
        incomes += preview
      })
      // Ищем узлы, на которые указывает узел из opcodes, исключая самих себя
      nexts(opcode).filter(node => (!opcodes.contains(node) && !outcomes.contains(node))).foreach(next => {
        outcomes += next
      })
    })
    // Меняем связь с внутреннего узла на групповой (incomeNode <-> group)
    incomes.foreach(incomeNode => {
      nexts(incomeNode).filter(node => (node != group && opcodes.contains(node))).foreach(nodeFromOpcodes => {
        val nextNodes = nexts(incomeNode)
        if (OpcodeTree.indexOf(nextNodes, group) == -1) {
          OpcodeTree.replaceIn(nextNodes, nodeFromOpcodes, group)
        } else {
          OpcodeTree.removeFrom(nextNodes, nodeFromOpcodes)
        }
        addPreview(group, incomeNode)
      })
    })
    // Меняем связь с внутреннего узла на групповой (group <-> outcomeNode)
    outcomes.foreach(outcomeNode => {
      previews(outcomeNode).filter(node => (node != group && opcodes.contains(node))).foreach(nodeFromOpcodes => {
        val previewNodes = previews(outcomeNode)
        if (OpcodeTree.indexOf(previewNodes, group) == -1) {
          OpcodeTree.replaceIn(previewNodes, nodeFromOpcodes, group)
        } else {
          OpcodeTree.removeFrom(previewNodes, nodeFromOpcodes)
        }
        addNext(group, outcomeNode) // выставили обратную связь outcomeNode -> group
      })
    })
  }

  def details(nodeId: String): OpcodeDetail = id2Detail(nodeId)

  def details(node: OpcodeNode): OpcodeDetail = details(node.id)

  def incDetails(node: OpcodeNode): IncDetail = {
    val detail = details(node.id)
    if (!detail.isInstanceOf[IncDetail]) {
      val reason = "Запрашиваемая детализация по узлу '" + node + "' не является inc-детализацией."
      val effect = "Обработка дерева инструкций будет прервана."
      val action = "Необходимо устранить дефект в логике получения inc-детализации."
      throw Exception(TREE_AREA, reason, effect, action)
    }
    detail.asInstanceOf[IncDetail]
  }

  def ifDetails(node: OpcodeNode): IfOpcodeDetail = {
    val detail = details(node.id)
    if (!detail.isInstanceOf[IfOpcodeDetail]) {
      val reason = "Запрашиваемая детализация по узлу '" + node + "' не является if-детализацией."
      val effect = "Обработка дерева инструкций будет прервана."
      val action = "Необходимо устранить дефект в логике получения if-детализации."
      throw Exception(TREE_AREA, reason, effect, action)
    }
    detail.asInstanceOf[IfOpcodeDetail]
  }

  def cycleDetails(node: OpcodeNode): CycleOpcodeDetail = {
    val detail = details(node.id)
    if (!isCycleDetails(detail)) {
      val reason = "Запрашиваемая детализация по узлу '" + node + "' не является cycle-детализацией."
      val effect = "Обработка дерева инструкций будет прервана."
      val action = "Необходимо устранить дефект в логике получения cycle-детализации."
      throw Exception(TREE_AREA, reason, effect, action)
    }
    detail.asInstanceOf[CycleOpcodeDetail]
  }

  def changeDetails(node: OpcodeNode, newDetails: OpcodeDetail) {
    changeDetails(node.id, newDetails)
  }

  def changeDetails(nodeId: String, newDetails: OpcodeDetail) {
    id2Detail.put(nodeId, newDetails)
    if (newDetails.isInstanceOf[IdChanger]) {
      listeners += newDetails.asInstanceOf[IdChanger]
    }
  }

  def nextPosition(node: OpcodeNode): Int = nextPosition(node.runtimeOpcode.number)

  def nextPosition(number: Int): Int = {
    var position: Int = 0
    while (has(makeId(number, position))) {
      position += 1
    }
    position
  }

  def lastPosition(number: Int): Int = nextPosition(number) - 1

  /**
   * Получить sid
   */
  def sid(node: OpcodeNode): String = details(node).sid
  /**
   * Получить sid как объект
   */
  def sido(node: OpcodeNode): Sid = new Sid(sid(node))

  def expression(node: OpcodeNode): IExpression = details(node).expression

  private def removeFromSelector(node: OpcodeNode) {
    if (selector != null) {
      OpcodeNodes.scanOpcodeNodes(node, someNode => {
        selector.clear(sid(someNode))
      })
    }
  }

  private def fireIdChangeEvent(oldId: String, newId: String) {
    listeners.foreach(_.change(oldId, newId))
  }

  /**Получить контейнер с данными по узлу */
  private def relation(nodeId: String): Relation = {
    var relation = id2Relation.getOrElse(nodeId, null)
    if (relation == null) {
      relation = new Relation
      id2Relation.put(nodeId, relation)
    }
    relation
  }

  private def containsNext(preview: OpcodeNode, current: OpcodeNode): Boolean = {
    OpcodeTree.indexOf(nexts(preview), current) != -1
  }

  private def containsPreview(current: OpcodeNode, preview: OpcodeNode): Boolean = {
    OpcodeTree.indexOf(previews(current), preview) != -1
  }

  private def addNext(preview: OpcodeNode, current: OpcodeNode) {
    if (!containsNext(preview, current)) {
      nexts(preview) += current
    }
  }

  private def addPreview(current: OpcodeNode, preview: OpcodeNode) {
    if (!containsPreview(current, preview)) {
      previews(current) += preview
    }
  }

  private def makeNothingOpcode(number: Int): OpcodeNode = {
    val nothingOpcode = new RuntimeOpcode(number, Opcodes.OPCODE_NOP, Array())
    val nothingNode = new SimpleOpcodeNode(nothingOpcode, false, nextPosition(number))
    nothingNode
  }

}