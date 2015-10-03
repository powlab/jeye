package org.powlab.jeye.decode.pattern.stream

import org.powlab.jeye.core.Opcodes.OPCODE_DUP
import org.powlab.jeye.core.Opcodes.OPCODE_DUP2
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.stack.StackInformator._
import org.powlab.jeye.decode.processor.store.StoreInformator.isBaseStoreNode
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.{isPutFieldNode, isPutStaticNode}

/**
 * Dups + Store - говорит нам о том, что выражение нужно вернуть в операнд стэк
 * Dups + putField  - говорит нам о том, что выражение нужно вернуть в операнд стэк
 * Группа состоит Store
 */
class StoreDupsStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = new OpcodeDetail(DETAIL_STORE_DUPS)

  def resolve(node: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    var pNode = processLocal(node, tree)
    if (pNode == null) {
      pNode = processField(node, tree)
      if (pNode == null) {
        pNode = processStaticField(node, tree)
      }
    }
    pNode
  }

  private def processLocal(storeNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    if (!isBaseStoreNode(storeNode) || tree.previewCount(storeNode) != 1) {
      return null
    }
    val dupNode = tree.preview(storeNode)
    val pOpcode = dupNode.runtimeOpcode.opcode
    if (! (isDupNode(dupNode) || isDup2Node(dupNode))) {
      return null
    }
    storeNode
  }

  private def processField(putFieldNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    if (!isPutFieldNode(putFieldNode) || tree.previewCount(putFieldNode) != 1) {
      return null
    }
    val dupNode = tree.preview(putFieldNode)
    val pOpcode = dupNode.runtimeOpcode.opcode
    if (! isDupX1Node(dupNode)) {
      return null
    }
    putFieldNode
  }

  private def processStaticField(putStaticFieldNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    if (!isPutStaticNode(putStaticFieldNode) || tree.previewCount(putStaticFieldNode) != 1) {
      return null
    }
    val dupNode = tree.preview(putStaticFieldNode)
    val pOpcode = dupNode.runtimeOpcode.opcode
    if (! isDupNode(dupNode)) {
      return null
    }
    putStaticFieldNode
  }

}



