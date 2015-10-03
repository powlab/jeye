package org.powlab.jeye.decode

import org.powlab.jeye.core._
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.core.parsing.DescriptorParser._
import org.powlab.jeye.decode.processor.stack.StackInformator._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.utils.ConstantPoolUtils
import org.powlab.jeye.utils.AttributeUtils
import scala.collection.mutable.Map
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode
import org.powlab.jeye.decode.processor.store.StoreInformator._

/**
 * Определитель имени локальных переменных метода по информации из LocalVariableTableAttribute.
 * TODO here: требуется оптимизация, написано не оптимально
 */
class LVTNamer(varTable: LocalVariableTableAttribute, method: MemberInfo, cpUtils: ConstantPoolUtils) extends Namer {

  private val lvt = varTable.local_variable_table
//  println("***************")
//  println("method: " + cpUtils.getUtf8(method.name_index))
//  varTable.local_variable_table.foreach(item => {
//    println(item.index, item, cpUtils.getUtf8(item.name_index))
//  })

  /**
   * Получение корректного имени для аргументов метода
   */
  override def getNextName(aType: BaseType, argIndex: Int): String = {
    val lvtRecordOpt = lvt.find(lvtRecord => {
      lvtRecord.start_pc == 0 && lvtRecord.index == argIndex
    })
    if (lvtRecordOpt.isDefined) {
      val name = cpUtils.getUtf8(lvtRecordOpt.get.name_index)
      registryName(name, aType)
    } else {
      super.getNextName(aType, argIndex)
    }
  }

  /**
   * Получение корректного имени для локальных переменных метода
   */
  override def getNextName(aType: BaseType, node: OpcodeNode, tree: OpcodeTree): String = {
    val index = getBaseStoreOpcodeIndex(node.runtimeOpcode)
    if (index != -1) {
      val variants = lvt.filter(_.index == index)
      if (variants.nonEmpty) {
        // только 1 имя по указанному индексу
        if (variants.size == 1) {
          val name = cpUtils.getUtf8(variants.head.name_index)
          return registryName(name, aType)
        }
        val startPc = resolveStartPc(node, tree)
        val variantOpt = variants.find(_.start_pc == startPc)
        // точное совпадение по началу использования переменной
        if (variantOpt.isDefined) {
          val name = cpUtils.getUtf8(variantOpt.get.name_index)
          return registryName(name, aType)
        }
        // фильтруем по региону использования
        val variantsRegion = variants.filter(record => {
          startPc >= record.start_pc && startPc < record.start_pc + record.length
        })
        if (variantsRegion.size == 1) {
          val name = cpUtils.getUtf8(variantsRegion.head.name_index)
          return registryName(name, aType)
        }
        // TODO here: этот участок кода на проработку
      }

    }
    super.getNextName(aType, node, tree);
  }

  override def getVariantsCount(node: OpcodeNode, tree: OpcodeTree): Int = {
    val index = getBaseStoreOpcodeIndex(node.runtimeOpcode)
    if (index != -1) {
      lvt.count(_.index == index)
    } else {
      super.getVariantsCount(node, tree)
    }
  }

  private def resolveStartPc(node: OpcodeNode, tree: OpcodeTree): Int = {
    val plainTree = tree.plainTree
    if (!plainTree.hasNext(node)) {
      return node.runtimeOpcode.number
    }
    var nextNode = plainTree.next(node)
//    if (isGotoNode(nextNode) && tree.hasNext(nextNode)) {
//      nextNode = tree.next(nextNode)
//    }
    return nextNode.runtimeOpcode.number
  }

}