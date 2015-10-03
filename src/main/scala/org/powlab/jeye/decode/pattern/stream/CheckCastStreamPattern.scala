package org.powlab.jeye.decode.pattern.stream

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.isCheckCastNode
import org.powlab.jeye.decode.processor.math.MathInformator.isIncrementNode

/**
 * CheckCast + CheckCast + ... + CheckCast + any -> CheckCast + any
 * Группа состоит из CheckCast + CheckCast + ... + CheckCast
 * Суть в том, что иногда можно встретить вот такой код в java:
 * return (Object) (Object) (Object) reference0
 * который по своей сути равен коду (Object) reference0
 *
 * TODO here: более правильным является перенос этой логики в трансформер,
 * а патерн нужно удалить - так будет работать быстрее. Был запилен, когда
 * еще не было трансформеров
 */
class CheckCastStreamPattern extends StreamPattern {

  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail = new OpcodeDetail(DETAIL_COMMON)

  def resolve(anyNode: OpcodeNode, tree: OpcodeTree): GroupOpcodeNode = {
    if (isCheckCastNode(anyNode) || tree.previewCount(anyNode) != 1) {
      return null
    }

    val checkCastNode1 = tree.preview(anyNode)
    if (! isCheckCastNode(checkCastNode1) || tree.previewCount(checkCastNode1) != 1) {
      return null
    }

    val checkCastNode2 = tree.preview(checkCastNode1)
    if (! isCheckCastNode(checkCastNode2) || !equalsNode(checkCastNode1, checkCastNode2)) {
      return null
    }

    val buffer = ArrayBuffer[OpcodeNode]()
    buffer += checkCastNode1
    var before = checkCastNode2
    var current = tree.preview(before)
    while (tree.previewCount(before) == 1 && isCheckCastNode(current) && equalsNode(before, current)) {
      buffer += before
      before = current
      current = tree.preview(before)
    }
    buffer += before

    val position = tree.nextPosition(checkCastNode1)
    new GroupOpcodeNode(buffer, false, position, buffer.size - 1)
  }

  private def equalsNode(checkCastNode1: OpcodeNode, checkCastNode2: OpcodeNode): Boolean = {
    val values1 = checkCastNode1.runtimeOpcode.values
    val values2 = checkCastNode2.runtimeOpcode.values

    values1(0) == values2(0) && values1(1) == values2(1)
  }

}



