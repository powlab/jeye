package org.powlab.jeye.decode.corrector

import org.powlab.jeye.core.Opcodes.OPCODE_ATHROW
import org.powlab.jeye.core.Opcodes.USCODE_CATCH
import org.powlab.jeye.core.Opcodes.USCODE_FINALLY
import org.powlab.jeye.core.Opcodes.USCODE_TRY
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.sids.Sid
import org.powlab.jeye.decode.sids.SidSelector
import org.powlab.jeye.decode.processor.control.SwitchInstructionInformator.isSwitchNode
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.ExceptionHandlers
import org.powlab.jeye.decode.ExceptionStatement
import org.powlab.jeye.decode.ExceptionHandler
import org.powlab.jeye.decode.graph.OpcodeTreeInjector
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.isMonitorExitNode

/**
 * Цель: почистить повторяющиеся опкоды в теле try/catch
 */
object TryCatchFinallyCorrector {

  /**
   * Обработать все finally, для каждого вызвать api поиска и удаление дублей
   */
  def processFinallyNodes(exceptions: ExceptionHandlers, tree: OpcodeTree) {
    val dublicatedNodes = ArrayBuffer[OpcodeNode]()
    exceptions.getExceptionHandlers.filter(acceptException(_, exceptions, tree.plainTree)).foreach(exception => {
      if (exception.isFinally) {
        dublicatedNodes ++= getDublicatedNodes(exception.to, exception.target, tree)
      } else {
        val exstOpt = exceptions.findExceptionStatement(exception)
        val finallyHandler = exstOpt.get.finalNumber
        val target = finallyHandler.handler.target
        dublicatedNodes ++= getDublicatedNodes(exception.to, target, tree)
      }
    })
    dublicatedNodes ++= tree.resources.getAthrowFinallyNodes
    dublicatedNodes.foreach(tree.remove)
  }

  private def getDublicatedNodes(dublicateTarget: Int, finallyTarget: Int, tree: OpcodeTree): ArrayBuffer[OpcodeNode] = {
    val finallyNode = getFinallyNode(finallyTarget, tree)
    val dublicateNode = getDublicateNode(dublicateTarget, dublicateTarget == finallyTarget, tree)
    val result = getDublicatedNodes(dublicateNode, finallyNode, tree)
    //println("[getDub] " + (dublicateTarget, finallyTarget, dublicateNode, finallyNode,  tree.next(finallyNode), result))
    result
  }

  private def getNode(number: Int, tree: OpcodeTree): OpcodeNode = {
    tree.plainTree.current(number)
  }

  private def getFinallyNode(number: Int, tree: OpcodeTree): OpcodeNode = {
    tree.owner(getNode(number, tree))
  }

  /**
   * Получить узел, с которого начинается повторяющаяся последовательность
   */
  private def getDublicateNode(number: Int, previewOnly: Boolean, tree: OpcodeTree): OpcodeNode = {
    val node = getNode(number, tree)
    if (!previewOnly && isGotoNode(node)) {
      return tree.next(node)
    }
    val plainTree = tree.plainTree
    val previewNode = plainTree.preview(node)
    if (isGotoNode(previewNode)) {
      return tree.next(previewNode)
    }
    if (previewOnly) {
      return null
    }
    tree.owner(node)
  }

  private def isNextMonitorExit(node: OpcodeNode, plainTree: OpcodeTree): Boolean = {
    isMonitorExitNode(plainTree.next(node))
  }

  /**
   * Обрабатываем только те блоки с ошибками у которых есть finally секция
   */
  private def acceptException(exception: ExceptionHandler, exceptions: ExceptionHandlers, plainTree: OpcodeTree): Boolean = {
    if (exception.isFinally && exception.to <= exception.target) {
      return acceptFinal(exception.target, plainTree)
    }
    val exstOpt = exceptions.findExceptionStatement(exception)
    if (exstOpt.isDefined) {
      val finalNumber = exstOpt.get.finalNumber
      if (finalNumber != null) {
        return acceptFinal(finalNumber.number, plainTree)
      }
    }
    false
  }

  private def acceptFinal(number: Int, plainTree: OpcodeTree): Boolean = {
    val checkedNode = plainTree.next(plainTree.current(number))
    if (isMonitorExitNode(checkedNode)) {
      return false
    }
    if (checkedNode != null && isMonitorExitNode(plainTree.next(checkedNode))) {
      return false
    }
    true
  }

  /**
   * Получить дубли, порожденные блоком finally
   */
  private def getDublicatedNodes(dublicateNode: OpcodeNode, finallyNode: OpcodeNode, tree: OpcodeTree): ArrayBuffer[OpcodeNode] = {
    val finallyFirstNode = tree.next(finallyNode)
    if (finallyFirstNode == null) {
      return ArrayBuffer.empty
    }
    // за зря не вызываем метод lookupSameNodes, так как он ресурсоемкий
    if (!isSameNode(dublicateNode, finallyFirstNode)) {
      return ArrayBuffer.empty
    }
    // осуществляем поиск дублей
    val dublicatedNodes = lookupSameNodes(dublicateNode, finallyFirstNode, tree)
    if (dublicatedNodes.isEmpty) {
      return ArrayBuffer.empty
    }

    // Временный хак, для try-with-resources. Равенство дубликатов должно осуществляться по другому принципу
    if (dublicatedNodes.size == 1) {
      return ArrayBuffer.empty
    }

    dublicatedNodes
  }

  /**
   * Получить список узлов, если при обходе дерева,
   * начиная с fromNode, очередной узел будет идентичен узлу при таком же обходе дерева, начиная с fromCopyNode
   * Другими словами, выделяем идентичные подмножества в дереве, начиная с fromNode и fromCopyNode
   */
  private def lookupSameNodes(fromNode: OpcodeNode, fromCopyNode: OpcodeNode, tree: OpcodeTree): ArrayBuffer[OpcodeNode] = {
    val marker = tree.prepared
    val sameNodes = new ArrayBuffer[OpcodeNode]
    def doScan(node: OpcodeNode, copyNode: OpcodeNode) {
      var preview: OpcodeNode = null
      var previewCopy: OpcodeNode = null
      var current: OpcodeNode = node
      var currentCopy: OpcodeNode = copyNode
      while (current != null && currentCopy != null && isSameNode(current, currentCopy)) {
        if (marker.isMarked(current) || marker.isMarked(currentCopy)) {
          return
        }
        marker.mark(current);
        marker.mark(currentCopy);
        sameNodes += current
        if (current.branchy) {
          tree.nexts(current).zip(tree.nexts(currentCopy)).foreach(pair => doScan(pair._1, pair._2))
          return
        }
        preview = current
        previewCopy = currentCopy
        current = tree.next(current)
        currentCopy = tree.next(currentCopy)
      }
    }
    doScan(fromNode, fromCopyNode)
    sameNodes
  }

  /**
   * Проверяем идентичность 2х инструкций
   */
  def isSameNode(node: OpcodeNode, checkedNode: OpcodeNode): Boolean = {
    if (node == null && checkedNode == null) {
      return true
    }
    if (node == null || checkedNode == null || node.branchy != checkedNode.branchy) {
      return false
    }
    val runtimeOpcode = node.runtimeOpcode
    val checkedRuntimeOpcode = checkedNode.runtimeOpcode
    if (runtimeOpcode.opcode != checkedRuntimeOpcode.opcode) {
      return false
    }
    val values = runtimeOpcode.values
    val checkedValues = checkedRuntimeOpcode.values
    if (values == null && checkedValues == null) {
      return true
    }
    if (values == null || checkedValues == null) {
      return false
    }
    if (values.length != checkedValues.length) {
      return false
    }
    values.corresponds(checkedValues) { _ == _ }
  }

}

