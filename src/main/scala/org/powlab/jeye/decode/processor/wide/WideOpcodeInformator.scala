package org.powlab.jeye.decode.processor.wide

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core.Opcodes.OPCODES
import org.powlab.jeye.core.Opcodes.OPCODE_IINC
import org.powlab.jeye.core.Opcodes.OPCODE_WIDE
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.utils.DecodeUtils
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.MethodContext

object WideOpcodeInformator {
  private def unwrapWideOpcode(runtimeOpcode: RuntimeOpcode): RuntimeOpcode = {
    val number = runtimeOpcode.number
    val values = runtimeOpcode.values
    val newOpcode = OPCODES(values(0));
    val newValues = new ArrayBuffer[Int];
    newValues += (values(1) << 8) | values(2);
    if (newOpcode == OPCODE_IINC) {
      newValues += DecodeUtils.getShort(values(3), values(4))
    }
    new RuntimeOpcode(number, newOpcode, newValues.toArray)

  }
  def unwrap(runtimeOpcode: RuntimeOpcode): RuntimeOpcode = {
    runtimeOpcode.opcode match {
      case OPCODE_WIDE => unwrapWideOpcode(runtimeOpcode)
      case _ => runtimeOpcode
    }
  }
}