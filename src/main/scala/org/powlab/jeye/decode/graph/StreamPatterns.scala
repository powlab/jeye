package org.powlab.jeye.decode.graph

import org.powlab.jeye.decode.processor.comparison._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.sids._
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.pattern.stream._

/**
 * Определение шаблонов во время построения дерева.
 * Основная задача - избавиться отхардкодов, которые сейчас существует в процессорах инструкций
 *
 * Наименование патернов: Целевая инструкция + логическая цепочка.
 * Например: StoreDupsPattern (Store - Целевая инструкция, Dups - логическая)
 */
object StreamPatterns {

  private val STREAM_PATTERNS = Array(new RefIncPrePostStreamPattern, new FieldIncPrePostStreamPattern,
    new IncPrePostStreamPattern, new StoreDupsStreamPattern, new StoreDupsArrayStreamPattern, 
    new IncLoadPostStreamPattern, new IncLoadPreStreamPattern, new CheckCastStreamPattern, new ArrayDupStreamPattern)

  /**
   * Оповещаем о конце, заносим все группы в дерево
   */
  def process(groups: ArrayBuffer[PatternResult], tree: OpcodeTree) {
    groups.filter(result => result.pattern.resolve(result.fromNode, tree) != null).foreach(result => {
      val group = result.group
      val pattern = result.pattern
      if (group.isInstanceOf[GroupOpcodeNode]) {
        if (tree.has(group.id)) {
          val current = tree.current(group.id)
          val newCurrent = copyWithIncrementedId(current)
          tree.redefine(current, newCurrent)
          tree.remove(current)
        }
        tree.bind(group.asInstanceOf[GroupOpcodeNode], pattern.details(group, tree))
      } else {
        tree.changeDetails(group, pattern.details(group, tree))
      }
    })
  }

  def apply(tree: OpcodeTree): StreamPatterns = new StreamPatterns(STREAM_PATTERNS, tree)

}

/**
 * Результат поиска шаблона
 * group - найденный шаблон
 * pattern - с помощью кого был найден шаблон
 * fromNode - узел с которого был обнаружен шаблон
 */
case class PatternResult(group: OpcodeNode, pattern: StreamPattern, fromNode: OpcodeNode)

/**
 * Определение шаблонов
 */
class StreamPatterns(patterns: Array[StreamPattern], tree: OpcodeTree) {
  private val nodeMarker = tree.prepared
  private val resources = tree.resources
  // Помечаем уже существующие шаблоны как использованные
  resources.getPatternResults.foreach(patternResult => {
    scanOpcodeNodes(patternResult.group, nodeMarker.mark)
  })
  def check(node: OpcodeNode) {
    patterns.find(pattern => {
      val newGroup = pattern.resolve(node, tree)
      // Шаблон инструкций определен
      if (newGroup != null && checkNotMarked(newGroup)) {
        // помечаем узлы в группе
        getOpcodeNodes(newGroup).foreach(nodeMarker.mark)
        // добавляем новую группу
        resources += PatternResult(newGroup, pattern, node)
        true
      } else {
        false
      }
    })
  }

  /**
   * Проверяем, что ни одна из инструкций не учавствует в другом шаблоне, найденом ранее
   */
  private def checkNotMarked(node: OpcodeNode): Boolean = {
    return getOpcodeNodes(node).find(nodeMarker.isMarked).isEmpty
  }

}


