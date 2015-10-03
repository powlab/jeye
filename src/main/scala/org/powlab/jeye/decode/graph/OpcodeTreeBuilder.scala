package org.powlab.jeye.decode.graph

import org.powlab.jeye.core.Opcodes.OPCODE_RET
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator
import org.powlab.jeye.decode.processor.control.ReturnInstructionInformator
import org.powlab.jeye.decode.processor.control.SwitchInstructionInformator
import org.powlab.jeye.decode.processor.custom.CustomInformator
import org.powlab.jeye.decode.graph.OpcodeDetails.IfOpcodeDetail
import org.powlab.jeye.decode.processor.reference.ReferenceInformator
import org.powlab.jeye.decode.processor.wide.WideOpcodeInformator
import org.powlab.jeye.decode.processor.control.TableSwitchInstructionInformator.isTableSwitchNode
import scala.collection.mutable.ArrayBuffer

object OpcodeTreeBuilder {

  /**
   * Построить плоское дерево - по сути цепочка опкодов,
   * каждый опкод имеет один пердыдущий опкод и один следующий, кроме конечных узлов
   * Пример:    head <-> A <-> B <-> C
   */
  def buildPlainTree(runtimeOpcodes: Array[RuntimeOpcode]): OpcodeTree = {
    val tree = new OpcodeTree(null)
    var preview: OpcodeNode = tree.head
    runtimeOpcodes.foreach(nativeRuntimeOpcode => {
      val runtimeOpcode = WideOpcodeInformator.unwrap(nativeRuntimeOpcode);
      val current: OpcodeNode = makeOpcodeNode(runtimeOpcode)
      tree.registryNode(current)
      tree.link(current, preview)
      preview = current
    })
    tree
  }

  private def makeOpcodeNode(runtimeOpcode: RuntimeOpcode): OpcodeNode = {
    makeOpcodeNode(runtimeOpcode, 0)
  }

  def makeOpcodeNode(runtimeOpcode: RuntimeOpcode, position: Int): OpcodeNode = {
    val branchy = ReferenceInformator.isMonitorEnterCode(runtimeOpcode) ||
      ComparisonInformator.isIfCode(runtimeOpcode) ||
      SwitchInstructionInformator.isSwitchCode(runtimeOpcode) ||
      CustomInformator.isTryCode(runtimeOpcode)
    new SimpleOpcodeNode(runtimeOpcode, branchy, position)
  }

  def buildTree(plainTree: OpcodeTree): OpcodeTree = {
    val newTree = new OpcodeTree(plainTree)
    fillTree(newTree, plainTree.top)
    newTree.link(newTree.current(0), newTree.head)
    newTree
  }

  def fillTree(tree: OpcodeTree, fromNode: OpcodeNode) {
    val plainTree = tree.plainTree
    val resources = tree.resources
    val patterns = StreamPatterns(tree)
    def buildBranch(parentNode: OpcodeNode, number: Int) {
      val plainNode = plainTree.current(number)
      build(plainNode)
      val newNode = tree.current(number)
      tree.link(newNode, parentNode)
    }

    def processSwitchNode(switchNode: OpcodeNode, preview: OpcodeNode) {
      resources += switchNode
      val switchData = SwitchInstructionInformator.branches(switchNode.runtimeOpcode)
      // TODO here: нужна оптимизация для больших swith заюзать map вместо ArrayBuffer[Int]
      val targetNumbers = new ArrayBuffer[Int]
      switchData._2.foreach(npairs => {
        val caseNumber = npairs._2
        if (! targetNumbers.contains(caseNumber)) {
          targetNumbers += caseNumber
        }
      })
      val defaultNumber = switchData._1
      if (!targetNumbers.contains(defaultNumber)) {
        targetNumbers += defaultNumber
      }
      // для Lookup оставляем как есть,
      // а для Table - сортируем в порядке физического расположения узлов
      val orderedNumbers = if (isTableSwitchNode(switchNode)) {
        targetNumbers.sortWith(_ < _)
      } else {
        targetNumbers
      }
      orderedNumbers.foreach(targetNumber => buildBranch(switchNode, targetNumber))
      tree.link(switchNode, preview)
    }

    def processIfNode(ifNode: OpcodeNode, preview: OpcodeNode) {
      resources += ifNode
      val bodyNumber = plainTree.next(ifNode).runtimeOpcode.number
      buildBranch(ifNode, bodyNumber)
      val elseNumber = ComparisonInformator.branch(ifNode.runtimeOpcode)
      buildBranch(ifNode, elseNumber)
      tree.link(ifNode, preview)
      // Регистрируем детали
      val ifDetails = new IfOpcodeDetail()
      ifDetails.bodyId = tree.current(bodyNumber).id
      ifDetails.elseId = tree.current(elseNumber).id
      tree.changeDetails(ifNode, ifDetails)
    }

    def processGotoNode(gotoNode: OpcodeNode, preview: OpcodeNode) {
      resources += gotoNode
      val jumpedToNumber = GotoInstructionInformator.branch(gotoNode.runtimeOpcode);
      buildBranch(gotoNode, jumpedToNumber)
      tree.link(gotoNode, preview)
    }

    def build(fromNode: OpcodeNode) {
      var preview: OpcodeNode = null
      var node = fromNode

      while (node != null) {
        val runtimeOpcode = node.runtimeOpcode
        val number = runtimeOpcode.number
        if (tree.has(number)) {
          val markedNode = tree.current(number)
          tree.link(markedNode, preview)
          return
        }
        val current = node
        tree.registryNode(current)
        if (ComparisonInformator.isIfNode(node)) {
          processIfNode(current, preview)
          return
        } else if (GotoInstructionInformator.isGotoNode(node)) {
          processGotoNode(current, preview)
          return
        } else if (SwitchInstructionInformator.isSwitchNode(node)) {
          processSwitchNode(current, preview)
          return
        } else {
          tree.link(current, preview)
          // Проверяем очередной узел на шаблонность. Патерны рассматриваются для простых узлов
          patterns.check(current)
        }
        if (ReferenceInformator.isMonitorExitNode(node)) {
          resources += node
        }
        if (ReturnInstructionInformator.matches(runtimeOpcode.opcode) ||
          ReferenceInformator.isAthrowNode(node) || runtimeOpcode.opcode == OPCODE_RET) {
          return
        }
        preview = current
        node = plainTree.next(node)
      }
    }
    build(fromNode)
  }

}