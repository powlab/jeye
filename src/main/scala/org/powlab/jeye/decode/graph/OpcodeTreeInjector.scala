package org.powlab.jeye.decode.graph

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import org.powlab.jeye.decode.ExceptionHandlers
import org.powlab.jeye.decode.ExceptionStatement
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator
import org.powlab.jeye.decode.processor.control.SwitchInstructionInformator
import org.powlab.jeye.decode.graph.OpcodeNodes.scanOpcodeNodes
import org.powlab.jeye.decode.graph.OpcodeDetails.OpcodeDetail
import org.powlab.jeye.decode.processor.reference.ReferenceInformator
import org.powlab.jeye.decode.sids.SidProcessor
import org.powlab.jeye.decode.processor.store.StoreInformator.isReferenceStoreNode
import org.powlab.jeye.decode.processor.load.LoadInformator.isReferenceLoadNode
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.isMonitorExitNode
import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception.INJECTOR_AREA

/**
 * Иньекция узлов в дерево
 */
object OpcodeTreeInjector {

  /**
   * Дополнить дерево недостающими элементами
   */
  def inject(tree: OpcodeTree, exceptions: ExceptionHandlers) {
    // 1. Добавить блоки try/catch/finally
    injectTryCatchFinally(tree, exceptions)
    // 2. Добавить case/default блоки
    injectSwitch(tree)
  }

  private def injectTryCatchFinally(tree: OpcodeTree, exceptions: ExceptionHandlers) {
    if (exceptions.hasExceptions) {
      val plainTree = tree.plainTree
      val resources = tree.resources
      lazy val customNodes = new CustomNodes(tree)
      exceptions.getExceptions.filter(acceptException(_, plainTree)).foreach(exception => {
        val tryNode = customNodes.makeTryNode(exception.tryHandler.number)
        tree.add(tryNode)
        exception.catches.foreach(catchHandler => {
          val fromNode = plainTree.current(catchHandler.number)
          OpcodeTreeBuilder.fillTree(tree, fromNode)
          val catchNode = customNodes.makeCatchNode(catchHandler)
          tree.add(catchNode)
          tree.link(catchNode, tryNode)
          if (isReferenceStoreNode(fromNode)) {
            val catchGroup = new GroupOpcodeNode(ArrayBuffer[OpcodeNode](catchNode, fromNode), false, catchNode.position + 1)
            tree.bind(catchGroup, new OpcodeDetail)
          }
        })
        if (acceptFinal(exception, plainTree)) {
          val number = exception.finalNumber.number
          val fromNode = plainTree.current(number)
          OpcodeTreeBuilder.fillTree(tree, fromNode)
          val finalBranch = tree.current(number)
          val lastNode = findLastNode(finalBranch, tree)
          // TODO here: это нужно обсуждать, пока просто throw связываю со следующим
          if (ReferenceInformator.isAthrowNode(lastNode)) {
            val afterFinallyNode = plainTree.current(lastNode.runtimeOpcode.number + 1)
            if (afterFinallyNode != null) {
              // внимание, связывние нужно делать только,
              // если afterFinallyNode указывает на повторяющийся node
              val fbNext = tree.next(finalBranch)
              if (fbNext != null && checkLogicOpcodeEquals(fbNext, afterFinallyNode)) {
                tree.link(afterFinallyNode, lastNode)
              }
            }
            resources += lastNode
          }
          val finalNode = customNodes.makeFinallyNode(number)
          tree.add(finalNode)
          tree.link(finalNode, tryNode)
          if (isReferenceStoreNode(fromNode) || isReferenceLoadNode(fromNode)) {
            val finallyGroup = new GroupOpcodeNode(ArrayBuffer[OpcodeNode](finalNode, fromNode), false, finalNode.position + 1)
            tree.bind(finallyGroup, new OpcodeDetail)
          }
        }
      })
    }
  }

  private def injectSwitch(tree: OpcodeTree) {
    val switchOpcodes = tree.resources.getSwitchNodes
    if (!switchOpcodes.isEmpty) {
      val plainTree = tree.plainTree
      val customNodes = new CustomNodes(tree)
      switchOpcodes.foreach(node => {
        val runtimeOpcode = node.runtimeOpcode
        val switchData = SwitchInstructionInformator.branches(runtimeOpcode)
        val switchNumber = runtimeOpcode.number
        val switchNode = plainTree.current(switchNumber)
        // cases
        println("--------------------")
        val number2Nodes = Map[Int, ArrayBuffer[OpcodeNode]]()
        val numbers = new ArrayBuffer[Int]
        switchData._2.foreach({
          case (caseValue, number) =>
            var nodes = number2Nodes.getOrElse(number, null)
            if (nodes == null) {
              nodes = new ArrayBuffer[OpcodeNode]
              numbers += number
              number2Nodes.put(number, nodes)
            }
            val caseNode = customNodes.makeCaseNode(number, switchNumber, caseValue, nodes.size)
            nodes += caseNode
        })
        // default
        val defaultNumber = switchData._1
        var nodes = number2Nodes.getOrElse(defaultNumber, null)
        if (nodes == null) {
          nodes = new ArrayBuffer[OpcodeNode]
          numbers += defaultNumber
          number2Nodes.put(defaultNumber, nodes)
        }
        val defaultNode = customNodes.makeDefaultNode(defaultNumber, nodes.size)
        nodes += defaultNode
        // сохраняем приоритет
        numbers.foreach(number => {
          val nodes = number2Nodes(number)
          val firstNode = nodes.head
          if (nodes.size == 1) {
            tree.add(firstNode)
          } else {
            val group = new GroupOpcodeNode(nodes.to, false, nodes.last.position + 1)
            tree.add(group)
            scanOpcodeNodes(group, node => {
              if (! tree.has(node)) {
                tree.registryNode(node)
              }
            })
          }
        })

      })
    }
  }

  /**
   * TODO here: аналогичный код используется в TryCorrector - убрать дубли
   */
  private def acceptException(exception: ExceptionStatement, plainTree: OpcodeTree): Boolean = {
    exception.catches.nonEmpty || acceptFinal(exception, plainTree)
  }

  private def acceptFinal(exception: ExceptionStatement, plainTree: OpcodeTree): Boolean = {
    if (exception.finalNumber == null) {
      return false
    }
    val number = exception.finalNumber.number
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
   * Найти последний узел, начиная с указанного
   * TODO here: этот трюк не прокатит, если в блоке finally будут множественные if инструкции
   * Вариант решения на данном этапе сгодится для lower бетта версии
   * С другой стороны, множественные инструкции if в секции finally тоже редкость
   * Вывод: нужно глубоко продумать вариант построения sid для 'сырого' дерева или реализовать
   * другой алгоритм поиска последнего элемента
   * Этот кусок очень важен, так как если не определить конец блока finally далее движок
   * не сможет удалить повторяющиеся инструкции.
   */
  private def findLastNode(fromNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    val branchSelector = SidProcessor.draftSelector(fromNode, tree);
    var searchNode = branchSelector.last(tree.details(fromNode).sid)
    while (ComparisonInformator.isIfNode(searchNode)) {
      searchNode = branchSelector.last(tree.details(tree.nexts(searchNode)(1)).sid)
    }
    searchNode
  }

  private def checkLogicOpcodeEquals(first: OpcodeNode, second: OpcodeNode): Boolean = {
    first.runtimeOpcode.opcode == second.runtimeOpcode.opcode && first.runtimeOpcode.values.deep == second.runtimeOpcode.values.deep
  }

}