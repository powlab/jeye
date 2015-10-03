package org.powlab.jeye.decode.pattern.stream

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.store.StoreInformator.isArrayStoreNode

/**
 * Dups + Store - говорит нам о том, что выражение нужно вернуть в операнд стэк
 * Группа состоит из Store
 *
 * Для работы с массивами данных
 */
class StoreDupsArrayStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = new OpcodeDetail(DETAIL_STORE_DUPS_ARRAY)

  def resolve(storeNode: OpcodeNode, tree: OpcodeTree): OpcodeNode = {
    if (!isArrayStoreNode(storeNode) || tree.previewCount(storeNode) != 1) {
      return null
    }
    val dupNode = tree.preview(storeNode)
    val pOpcode = dupNode.runtimeOpcode.opcode
    if (!(pOpcode == OPCODE_DUP_X2 || pOpcode == OPCODE_DUP2_X2)) {
      return null
    }
    storeNode
  }

}



