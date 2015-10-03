package org.powlab.jeye.decode.processor.control

import scala.collection.mutable.ArrayBuffer

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.core.Opcodes.OpCode
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.utils.DecodeUtils
import org.powlab.jeye.utils.DecodeUtils.bytesToInt

/**
 * TODO here: отрифакторить файл
 */

object ReturnInstructionInformator {
  private val RETURN_TYPES_MAP = Map(OPCODE_IRETURN -> TYPE_INT, OPCODE_LRETURN -> TYPE_LONG, OPCODE_FRETURN -> TYPE_FLOAT,
    OPCODE_DRETURN -> TYPE_DOUBLE, OPCODE_ARETURN -> TYPE_REFERENCE, OPCODE_RETURN -> TYPE_VOID)

  def getReturnType(runtimeOpcode: RuntimeOpcode): BaseType = RETURN_TYPES_MAP.getOrElse(runtimeOpcode.opcode, null)

  def getReturnType(node: OpcodeNode): BaseType = if (node == null) null else getReturnType(node.runtimeOpcode)

  def matches(opcode: OpCode) = RETURN_TYPES_MAP.contains(opcode)
  def isReturnNode(node: OpcodeNode): Boolean = node != null && matches(node.runtimeOpcode.opcode)
  def isVoidReturnNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_RETURN
}

trait SwitchBase {
  type nPairs = (Int, Int)
  type SwitchData = (Int, Array[nPairs])

  def branches(runtimeOpcode: RuntimeOpcode): SwitchData
}

object TableSwitchInstructionInformator extends SwitchBase {

  def matches(opcode: OpCode) = OPCODE_TABLESWITCH == opcode

  def branches(runtimeOpcode: RuntimeOpcode): SwitchData = {
    val number = runtimeOpcode.number;
    val npairsPairs = new ArrayBuffer[nPairs]
    val values = runtimeOpcode.values
    val defaultBranch = number + bytesToInt(values, 0);
    val low = bytesToInt(values, 4);
    val high = bytesToInt(values, 8);
    val offsetsCount = high - low + 1;
    (1 to offsetsCount).foreach(index => {
      val matcher = low + index - 1
      val branch = number + bytesToInt(values, 8 + index * 4)
      val npairs = (matcher, branch)
      npairsPairs += npairs
    })
    new SwitchData(defaultBranch, npairsPairs.toArray)
  }

  def isTableSwitchNode(node: OpcodeNode): Boolean = node != null && matches(node.runtimeOpcode.opcode)
}

private object LookupSwitchInstructionInformator extends SwitchBase {

  def matches(opcode: OpCode) = OPCODE_LOOKUPSWITCH == opcode

  def branches(runtimeOpcode: RuntimeOpcode): SwitchData = {
    val number = runtimeOpcode.number;
    val npairsPairs = new ArrayBuffer[nPairs]
    val values = runtimeOpcode.values
    val defaultBranch = number + bytesToInt(values, 0);
    val npairsCount = bytesToInt(values, 4);
    (1 to npairsCount).foreach(index => {
      val matcher = bytesToInt(values, 8 + (index - 1) * 8)
      val branch = number + bytesToInt(values, 12 + (index - 1) * 8)
      val npairs = (matcher, branch)
      npairsPairs += npairs
    })
    new SwitchData(defaultBranch, npairsPairs.toArray)
  }
}

object SwitchInstructionInformator extends SwitchBase {
  private val switches = Map[OpCode, SwitchBase](OPCODE_TABLESWITCH -> TableSwitchInstructionInformator,
    OPCODE_LOOKUPSWITCH -> LookupSwitchInstructionInformator)
  def matches(opcode: OpCode) = switches.contains(opcode)

  def branches(runtimeOpcode: RuntimeOpcode): SwitchData = switches(runtimeOpcode.opcode).branches(runtimeOpcode)

  def isSwitchCode(runtimeOpcode: RuntimeOpcode): Boolean = matches(runtimeOpcode.opcode);
  def isSwitchNode(node: OpcodeNode): Boolean = node != null && matches(node.runtimeOpcode.opcode);
}

trait BranchBase {
  def matches(runtimeOpcode: RuntimeOpcode): Boolean
  def branch(runtimeOpcode: RuntimeOpcode): Int = {
    val values = runtimeOpcode.values
    runtimeOpcode.number + DecodeUtils.getShort(values(0), values(1));
  }
}

private object GotoInstruction extends BranchBase {
  override def matches(runtimeOpcode: RuntimeOpcode): Boolean = runtimeOpcode.opcode == OPCODE_GOTO
  def pair = (OPCODE_GOTO -> GotoInstruction)
}

private object GotoWInstruction extends BranchBase {
  override def matches(runtimeOpcode: RuntimeOpcode): Boolean = runtimeOpcode.opcode == OPCODE_GOTO_W
  override def branch(runtimeOpcode: RuntimeOpcode): Int = runtimeOpcode.number + DecodeUtils.bytesToInt(runtimeOpcode.values, 0);
  def pair = (OPCODE_GOTO_W -> GotoWInstruction)
}

object GotoInstructionInformator extends BranchBase {
  private val gotos = Map[OpCode, BranchBase](GotoInstruction.pair, GotoWInstruction.pair)

  override def matches(runtimeOpcode: RuntimeOpcode): Boolean = gotos.contains(runtimeOpcode.opcode)
  override def branch(runtimeOpcode: RuntimeOpcode): Int = gotos(runtimeOpcode.opcode).branch(runtimeOpcode)

  //def isGotoCode(runtimeOpcode: RuntimeOpcode): Boolean = matches(runtimeOpcode)
  def isGotoNode(node: OpcodeNode): Boolean = node != null && matches(node.runtimeOpcode)
}

private object JsrInstruction extends BranchBase {
  override def matches(runtimeOpcode: RuntimeOpcode): Boolean = runtimeOpcode.opcode == OPCODE_JSR
  def pair = (OPCODE_JSR -> JsrInstruction)
}

private object JsrWInstruction extends BranchBase {
  override def matches(runtimeOpcode: RuntimeOpcode): Boolean = runtimeOpcode.opcode == OPCODE_JSR_W
  override def branch(runtimeOpcode: RuntimeOpcode): Int = runtimeOpcode.number + DecodeUtils.bytesToInt(runtimeOpcode.values, 0);
  def pair = (OPCODE_JSR_W -> JsrWInstruction)
}

object JsrInstructionInformator extends BranchBase {
  private val jsrs = Map[OpCode, BranchBase](JsrInstruction.pair, JsrWInstruction.pair)

  override def matches(runtimeOpcode: RuntimeOpcode): Boolean = jsrs.contains(runtimeOpcode.opcode)
  override def branch(runtimeOpcode: RuntimeOpcode): Int = jsrs(runtimeOpcode.opcode).branch(runtimeOpcode)
}