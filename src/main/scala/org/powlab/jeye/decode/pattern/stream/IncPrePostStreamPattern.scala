package org.powlab.jeye.decode.pattern.stream

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.math.MathInformator.{isAddNode, isSubNode}
import org.powlab.jeye.decode.processor.load.LoadInformator._
import org.powlab.jeye.decode.processor.store.StoreInformator._
import org.powlab.jeye.decode.processor.stack.StackInformator._
import org.powlab.jeye.utils.DecodeUtils

/**
 * Шаблон определения ++ и -- для локальных переменных
 * dload_3, dconst_1, dadd, dstore_3        -> double0++
 * dload_3, dconst_1, dadd, dup2, dstore_3  -> ++double0
 * Приоритет отдается операции reference0++
 *
 * TODO here: более правильным является перенос этой логики в трансформер,
 * а патерн нужно удалить - так будет работать быстрее.
 */
class IncPrePostStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = {
    if (checkDup(tree.preview(resolvedNode))) {
      val operNode = tree.preview(resolvedNode, 2)
      val incValue = if (isSubNode(operNode)) -1 else 1
      new IncDetail(DETAIL_INC_LOAD_PRE, incValue, true)
    } else {
      val operNode = tree.preview(resolvedNode)
      val incValue = if (isSubNode(operNode)) -1 else 1
      val dupNode = tree.preview(resolvedNode, 3)
      new IncDetail(DETAIL_INC_LOAD_POST, incValue, checkDup(dupNode))
    }
  }

  def resolve(storeNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    // 1. storeNode
    if (!isBaseStoreNode(storeNode) || tree.previewCount(storeNode) != 1) {
      return null
    }
    // 2. dup/dup2
    var dupNodePre = tree.preview(storeNode)
    if (!checkDup(dupNodePre) || tree.previewCount(dupNodePre) != 1) {
      // 2 инструкции может и не быть, это нормально
      dupNodePre = storeNode
    }
    // 3. add/sub
    val iOperNode = tree.preview(dupNodePre)
    if (!(isAddNode(iOperNode) || isSubNode(iOperNode)) || tree.previewCount(iOperNode) != 1) {
      return null
    }
    // 4. iconst_1/lconst_1/dconst_1/fconst_1
    val const1Node = tree.preview(iOperNode)
    if (const1Node == null ||
        !(const1Node.runtimeOpcode.opcode == OPCODE_ICONST_1 ||
            const1Node.runtimeOpcode.opcode == OPCODE_LCONST_1 ||
            const1Node.runtimeOpcode.opcode == OPCODE_DCONST_1 ||
            const1Node.runtimeOpcode.opcode == OPCODE_FCONST_1) ||
        tree.previewCount(const1Node) != 1) {
      return null
    }
    // 5. dup/dup2
    var dupNodePost = tree.preview(const1Node)
    if (!checkDup(dupNodePost) || tree.previewCount(dupNodePost) != 1) {
      // 2 инструкции может и не быть, это нормально
      dupNodePost = const1Node
    }
    // 6. load
    val loadNode = tree.preview(dupNodePost)
    if (!isBaseLoadNode(loadNode)) {
      return null
    }
    // сайд-эфект
    if (getBaseLoadOpcodeIndex(loadNode.runtimeOpcode) != getBaseStoreOpcodeIndex(storeNode.runtimeOpcode) ||
        (checkDup(dupNodePost) && checkDup(dupNodePre))) {
      return null
    }
    storeNode
  }

  private def checkDup(node: OpcodeNode): Boolean = {
    isDupNode(node) || isDup2Node(node)
  }

}



