package org.powlab.jeye.decode.pattern.stream

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.load.LoadInformator._
import org.powlab.jeye.decode.processor.math.MathInformator.isIncrementNode

/**
 * Inc + Load -> ++int
 * Группа состоит из Inc + Load
 */
class IncLoadPreStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = {
    new IncDetail(DETAIL_INC_LOAD_PRE, resolvedNode.runtimeOpcode.values(1), false)
  }

  def resolve(loadNode: OpcodeNode, tree: OpcodeTree): GroupOpcodeNode = {
    if (!isBaseLoadNode(loadNode) || tree.previewCount(loadNode) != 1) {
      return null
    }

    val incNode = tree.preview(loadNode)
    if (! isIncrementNode(incNode)) {
      return null
    }

    if (getBaseLoadOpcodeIndex(loadNode.runtimeOpcode) != incNode.runtimeOpcode.values(0)) {
      return null
    }

    val buffer = ArrayBuffer(incNode, loadNode)
    val position = tree.nextPosition(incNode)
    new GroupOpcodeNode(buffer, false, position, 0)
  }

}



