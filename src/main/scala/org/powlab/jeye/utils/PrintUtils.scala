package org.powlab.jeye.utils

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.RuntimeOpcodes._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.OpcodeTree
import scala.collection.mutable.Map
import org.powlab.jeye.decode.LocalVariable
import org.powlab.jeye.decode.graph.OpcodeNode

/**
 * Для печати сущностей
 */
object PrintUtils {

  def opcodeToString(opcode: OpCode) = opcode.name + " // " + opcode.operation + " (code " + opcode.code + " hex " + opcode.hex + ")"

  def printVariables(localVariables: Array[LocalVariable]) {
    localVariables.filter(_ != null).foreach(localVariable => println(localVariable.name.view(localVariable)))
  }

  def printRuntimeOpcodes(runtimeOpcodes: Array[RuntimeOpcode], cpUtils: ConstantPoolUtils, numberPad: Int = 2) {
    runtimeOpcodes.foreach(runtimeOpcode => println(runtimeOpcodeToString(runtimeOpcode, cpUtils, numberPad)))
  }

  /**
   * TODO here: использовать маркер
   */
  def printGraph(tree: OpcodeTree) {
    val nodeToState = Map[String, Boolean]()
    def print(node: OpcodeNode, indent: String) {
      var current = node
      while (current != null) {
        val number = current.id;
        if (!nodeToState.getOrElse(number, false)) {
          nodeToState.put(number, true)
        } else {
          return
        }
        println(indent + current)
        if (current.branchy) {

          tree.nexts(current).foreach(node => {
            println(indent + "    ---- new block");
            print(node, indent + "    ")
          })
          return
        } else {
          current = tree.next(current)
        }
      }
    }
    print(tree.head, "");
  }

}
