package org.powlab.jeye.decode.pattern.stream

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.load.LoadInformator._
import org.powlab.jeye.decode.processor.store.StoreInformator._
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.{isInvokeStaticNode, isInvokeVirtualNode}
import org.powlab.jeye.decode.processor.math.MathInformator.{isAddNode, isSubNode}
import org.powlab.jeye.decode.processor.stack.StackInformator._

/**
 * Шаблон определения инкрементации типов
 * aload, [dup], invokevirtual, iconst_1, iadd/isub, invokestatic, astore => reference0++
 * aload, invokevirtual, const_1, add/sub, invokestatic, [dup], astore => ++reference0
 *
 * Приоритет отдается операции reference0++
 */
class RefIncPrePostStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = {
    if (isDupNode(tree.preview(resolvedNode))) {
      val operNode = tree.preview(resolvedNode, 3)
      val incValue = if (isSubNode(operNode)) -1 else 1
      new IncDetail(DETAIL_REF_INC_PRE, incValue, true)
    } else {
      val operNode = tree.preview(resolvedNode, 2)
      val incValue = if (isSubNode(operNode)) -1 else 1
      val dupNode = tree.preview(resolvedNode, 5)
      new IncDetail(DETAIL_REF_INC_POST, incValue, isDupNode(dupNode))
    }
  }

  def resolve(astoreNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    // 1. astore
    if (!isReferenceStoreNode(astoreNode) || tree.previewCount(astoreNode) != 1) {
      return null
    }
    // 2. dup
    var dupNodePre = tree.preview(astoreNode)
    if (!isDupNode(dupNodePre) || tree.previewCount(dupNodePre) != 1) {
      // 2 инструкции может и не быть, это нормально
      dupNodePre = astoreNode
    }
    // 3. invokestatic
    val invokeStaticNode = tree.preview(dupNodePre)
    if (!isInvokeStaticNode(invokeStaticNode) || tree.previewCount(invokeStaticNode) != 1) {
      return null
    }
    // 4. add/sub
    val iOperNode = tree.preview(invokeStaticNode)
    if (!(isAddNode(iOperNode) || isSubNode(iOperNode)) || tree.previewCount(iOperNode) != 1) {
      return null
    }
    // 5. iconst_1/lconst_1/dconst_1/fconst_1
    val const1Node = tree.preview(iOperNode)
    if (const1Node == null ||
        !(const1Node.runtimeOpcode.opcode == OPCODE_ICONST_1 ||
            const1Node.runtimeOpcode.opcode == OPCODE_LCONST_1 ||
            const1Node.runtimeOpcode.opcode == OPCODE_DCONST_1 ||
            const1Node.runtimeOpcode.opcode == OPCODE_FCONST_1) ||
        tree.previewCount(const1Node) != 1) {
      return null
    }
    // 6. invokevirtual
    val invokeVirtualNode = tree.preview(const1Node)
    if (!isInvokeVirtualNode(invokeVirtualNode) || tree.previewCount(invokeVirtualNode) != 1) {
      return null
    }
    // 7. dup
    var dupNodePost = tree.preview(invokeVirtualNode)
    if (!isDupNode(dupNodePost) || tree.previewCount(dupNodePost) != 1) {
      // 6 инструкции может и не быть, это нормально
      dupNodePost = invokeVirtualNode
    }
    // 8. aload
    val aloadNode = tree.preview(dupNodePost)
    if (!isReferenceLoadNode(aloadNode)) {
      return null
    }
    // сайд-эфект
    if (isDupNode(dupNodePost) && isDupNode(dupNodePre)) {
      return null
    }

    if (getBaseLoadOpcodeIndex(aloadNode.runtimeOpcode) != getBaseStoreOpcodeIndex(astoreNode.runtimeOpcode)) {
      return null
    }

    astoreNode
  }

}



