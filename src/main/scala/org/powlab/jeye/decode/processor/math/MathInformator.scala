package org.powlab.jeye.decode.processor.math

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.expression.MathExpression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.SignExpression
import org.powlab.jeye.decode.graph.OpcodeNode

object MathInformator {

  val ADD_OPCODES_MAP = Map(OPCODE_IADD -> SIGN_ADD, OPCODE_LADD  -> SIGN_ADD, OPCODE_FADD -> SIGN_ADD, OPCODE_DADD  -> SIGN_ADD)
  val SUB_OPCODES_MAP = Map(OPCODE_ISUB -> SIGN_SUB, OPCODE_LSUB  -> SIGN_SUB, OPCODE_FSUB -> SIGN_SUB, OPCODE_DSUB  -> SIGN_SUB)
  val MUL_OPCODES_MAP = Map(OPCODE_IMUL -> SIGN_MUL, OPCODE_LMUL  -> SIGN_MUL, OPCODE_FMUL -> SIGN_MUL, OPCODE_DMUL  -> SIGN_MUL)
  val DIV_OPCODES_MAP = Map(OPCODE_IDIV -> SIGN_DIV, OPCODE_LDIV  -> SIGN_DIV, OPCODE_FDIV -> SIGN_DIV, OPCODE_DDIV  -> SIGN_DIV)
  val REM_OPCODES_MAP = Map(OPCODE_IREM -> SIGN_REM, OPCODE_LREM  -> SIGN_REM, OPCODE_FREM -> SIGN_REM, OPCODE_DREM  -> SIGN_REM)
  val SHL_OPCODES_MAP = Map(OPCODE_ISHL -> SIGN_SHL, OPCODE_LSHL  -> SIGN_SHL)
  val SHR_OPCODES_MAP = Map(OPCODE_ISHR -> SIGN_SHR, OPCODE_LSHR  -> SIGN_SHR)
  val USHR_OPCODES_MAP = Map(OPCODE_IUSHR -> SIGN_USHR, OPCODE_LUSHR  -> SIGN_USHR)
  val AND_OPCODES_MAP = Map(OPCODE_IAND -> SIGN_AND, OPCODE_LAND  -> SIGN_AND)
  val OR_OPCODES_MAP = Map(OPCODE_IOR -> SIGN_OR, OPCODE_LOR  -> SIGN_OR)
  val XOR_OPCODES_MAP = Map(OPCODE_IXOR -> SIGN_XOR, OPCODE_LXOR  -> SIGN_XOR)

  val TWO_OPERATION = ADD_OPCODES_MAP ++ SUB_OPCODES_MAP ++ MUL_OPCODES_MAP ++ DIV_OPCODES_MAP ++ REM_OPCODES_MAP ++
                      SHL_OPCODES_MAP ++ SHR_OPCODES_MAP ++ USHR_OPCODES_MAP ++ AND_OPCODES_MAP ++ OR_OPCODES_MAP ++ XOR_OPCODES_MAP

  def getTwoOperationSign(opcode: OpCode) = TWO_OPERATION.getOrElse(opcode, null)

  val SIGN_NEG = Sex("-")
  val NEGATE_OPCODES_MAP = Map(OPCODE_INEG -> SIGN_NEG, OPCODE_LNEG  -> SIGN_NEG, OPCODE_FNEG -> SIGN_NEG, OPCODE_DNEG  -> SIGN_NEG)

  def isNegate(opcode: OpCode) = NEGATE_OPCODES_MAP.contains(opcode)

  def isIncrementNode(node: OpcodeNode) = node != null && node.runtimeOpcode.opcode == OPCODE_IINC
  def isAddNode(node: OpcodeNode) = node != null && ADD_OPCODES_MAP.contains(node.runtimeOpcode.opcode)
  def isSubNode(node: OpcodeNode) = node != null && SUB_OPCODES_MAP.contains(node.runtimeOpcode.opcode)
}