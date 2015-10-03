package org.powlab.jeye.decode.corrector

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode
import org.powlab.jeye.decode.processor.control.ReturnInstructionInformator.isReturnNode
import org.powlab.jeye.decode.graph.OpcodeDetails.isIfDetails
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.{isAthrowNode, isMonitorExitNode}
import org.powlab.jeye.decode.graph.GotoTypes._

/**
 * Цель: убрать лишние else в if
 */
object IfDetailsCorrector {


  /**
   * Цель: убрать лишние else в if
   * Напимер консирукция
   * if (...) {
   *   throw Exception(...);
   * } else {
   *   doSomting();
   * }
   * можно смело переписать в if без else
   * if (...) {
   *   throw Exception(...);
   * }
   * doSomting();
   *
   */
  def resetElseBranch(ifNodes: ArrayBuffer[OpcodeNode], tree: OpcodeTree) {
    val selsector = tree.selector
    ifNodes.filter(ifNode => isIfDetails(tree.details(ifNode))).foreach(ifNode => {
      val ifDetails = tree.ifDetails(ifNode)
      val bodySid = tree.details(ifDetails.bodyId).sid
      val bodyLastNode = selsector.last(bodySid)
      if (isReturnNode(bodyLastNode)) {
        ifDetails.elseBranch = false
      } else if (isGotoNode(bodyLastNode)) {
        if (bodyLastNode == selsector.current(bodySid) || detect(bodyLastNode, tree, false) != GT_JUMP) {
          ifDetails.elseBranch = false
        }/* Пока это утверждение не верно!
        else {
            val jumtToNode = tree.next(bodyLastNode)
            if (isReturnNode(jumtToNode) || isAthrowNode(jumtToNode)) {
              ifDetails.elseBranch = false
            }
        }*/
      } else if (isAthrowNode(bodyLastNode)) {
        ifDetails.elseBranch = false
      } else if (isMonitorExitNode(bodyLastNode) && bodyLastNode == selsector.current(bodySid)) {
        ifDetails.elseBranch = false
      } else if (tree.has(ifDetails.elseId)) {
        // TODO here: костыль
        val elsenode = tree.current(ifDetails.elseId)
        val elsedet = tree.details(elsenode)
        if (elsedet.sid == null || !elsedet.sid.startsWith(ifDetails.sid)) {
          ifDetails.elseBranch = false
        }
      }
    })
  }

}


