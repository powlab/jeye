package org.powlab.jeye.decode.pattern.stream

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.{isInvokeStaticNode, isInvokeVirtualNode}
import org.powlab.jeye.decode.processor.math.MathInformator.{isAddNode, isSubNode}
import org.powlab.jeye.decode.processor.stack.StackInformator._
import org.powlab.jeye.decode.processor.reference.ReferenceInformator._
import org.powlab.jeye.utils.DecodeUtils

/**
 * Шаблон определения ++ и -- для полей класса
 * getfield, [dup_x1], iconst_1, isub, putfield    -> field--
 * getfield, iconst_1, iadd, dup_x1, putfield      -> ++field
 * Приоритет отдается операции reference0++
 */
class FieldIncPrePostStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = {
    if (checkDup(tree.preview(resolvedNode))) {
      val operNode = tree.preview(resolvedNode, 2)
      val incValue = if (isSubNode(operNode)) -1 else 1
      new IncDetail(DETAIL_FIELD_INC_PRE, incValue, true)
    } else {
      val operNode = tree.preview(resolvedNode)
      val incValue = if (isSubNode(operNode)) -1 else 1
      val dupNode = tree.preview(resolvedNode, 3)
      new IncDetail(DETAIL_FIELD_INC_POST, incValue, checkDup(dupNode))
    }
  }

  def resolve(putFieldNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    // 1. putfield/putstatic
    if (!(isPutFieldNode(putFieldNode) || isPutStaticNode(putFieldNode)) || tree.previewCount(putFieldNode) != 1) {
      return null
    }
    // 2. dup_x1
    var dupNodePre = tree.preview(putFieldNode)
    if (!checkDup(dupNodePre) || tree.previewCount(dupNodePre) != 1) {
      // 2 инструкции может и не быть, это нормально
      dupNodePre = putFieldNode
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
    // 5. dup
    var dupNodePost = tree.preview(const1Node)
    if (!checkDup(dupNodePost) || tree.previewCount(dupNodePost) != 1) {
      // 6 инструкции может и не быть, это нормально
      dupNodePost = const1Node
    }
    // 6. getfield/getstatic
    val getFieldNode = tree.preview(dupNodePost)
    if (!(isGetFieldNode(getFieldNode) || isGetStaticNode(getFieldNode))) {
      return null
    }
    // сайд-эфект
    if (checkDup(dupNodePost) && checkDup(dupNodePre)) {
      return null
    }
    // сайд-эфект
    if ((isGetStaticNode(getFieldNode) && isPutFieldNode(putFieldNode)) ||
        (isGetFieldNode(getFieldNode) && isPutStaticNode(putFieldNode)) ||
        (isDupNode(dupNodePost) && !isPutStaticNode(putFieldNode)) ||
        (isDupNode(dupNodePre) && !isPutStaticNode(putFieldNode)) ||
        (isDup2Node(dupNodePost) && !isPutStaticNode(putFieldNode)) ||
        (isDup2Node(dupNodePre) && !isPutStaticNode(putFieldNode))) {
      return null
    }
    // работаем с одним и тем же типом
    if(DecodeUtils.unsignShort(getFieldNode.runtimeOpcode.values) != DecodeUtils.unsignShort(putFieldNode.runtimeOpcode.values)) {
      return null
    }

    putFieldNode
  }

  private def checkDup(node: OpcodeNode): Boolean = {
    isDupX1Node(node) || isDup2X1Node(node) || isDupNode(node) || isDup2Node(node)
  }

}



