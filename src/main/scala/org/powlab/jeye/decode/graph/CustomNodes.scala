package org.powlab.jeye.decode.graph

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.CatchHandler
import org.powlab.jeye.decode.graph.OpcodeNodes.makeId

class CustomNodes(tree: OpcodeTree) {

  def makeNode(runtimeOpcode: RuntimeOpcode, offset: Int = 0): OpcodeNode = {
    OpcodeTreeBuilder.makeOpcodeNode(runtimeOpcode, tree.nextPosition(runtimeOpcode.number) + offset)
  }

  def makeTryNode(number: Int): OpcodeNode = {
    val runtimeOpcode = new RuntimeOpcode(number, USCODE_TRY, Array())
    val node = makeNode(runtimeOpcode)
    node
  }

  def makeCatchNode(catchHandler: CatchHandler): OpcodeNode = {
    val values = catchHandler.catchTypes.toArray
    val runtimeOpcode = new RuntimeOpcode(catchHandler.number, USCODE_CATCH, values)
    makeNode(runtimeOpcode)
  }

  def makeFinallyNode(number: Int): OpcodeNode = {
    val runtimeOpcode = new RuntimeOpcode(number, USCODE_FINALLY, Array())
    makeNode(runtimeOpcode)
  }

  def makeCaseNode(number: Int, switchNumber: Int, caseValue: Int, offset: Int): OpcodeNode = {
    val runtimeOpcode = new RuntimeOpcode(number, USCODE_CASE, Array(switchNumber, caseValue))
    makeNode(runtimeOpcode, offset)
  }

  def makeDefaultNode(number: Int, offset: Int): OpcodeNode = {
    val runtimeOpcode = new RuntimeOpcode(number, USCODE_DEFAULT, Array())
    makeNode(runtimeOpcode, offset)
  }
}