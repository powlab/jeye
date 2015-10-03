package org.powlab.jeye.decode.pattern.stream

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.load.LoadInformator._
import org.powlab.jeye.decode.processor.math.MathInformator.isIncrementNode

/**
 * Load + Inc -> int++/int--
 * Группа состоит из Load + Inc
 */
class IncLoadPostStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = {
    new IncDetail(DETAIL_INC_LOAD_POST, resolvedNode.runtimeOpcode.values(1), false)
  }

  def resolve(incNode: OpcodeNode, tree: OpcodeTree): GroupOpcodeNode = {
    if (!isIncrementNode(incNode) || tree.previewCount(incNode) != 1) {
      return null
    }

    val loadNode = tree.preview(incNode)
    if (!isBaseLoadNode(loadNode)) {
      return null
    }

    if (getBaseLoadOpcodeIndex(loadNode.runtimeOpcode) != incNode.runtimeOpcode.values(0)) {
      return null
    }

    val buffer = ArrayBuffer(loadNode, incNode)
    val position = tree.nextPosition(incNode)
    new GroupOpcodeNode(buffer, false, position, 1)
  }

}



