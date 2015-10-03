package org.powlab.jeye.decode.processor.store

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.processor.control.ReturnInstructionInformator
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.processor.IndexType
import org.powlab.jeye.decode.processor.load.LoadInformator
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.graph.OpcodeNode

object StoreInformator extends IndexType {

  // maps
  private val ISTORE_MAP: Map[OpCode, getIndex] = Map(OPCODE_ISTORE -> getIndexR, OPCODE_ISTORE_0 -> getIndex0, OPCODE_ISTORE_1 -> getIndex1,
    OPCODE_ISTORE_2 -> getIndex2, OPCODE_ISTORE_3 -> getIndex3)
  private val LSTORE_MAP: Map[OpCode, getIndex] = Map(OPCODE_LSTORE -> getIndexR, OPCODE_LSTORE_0 -> getIndex0, OPCODE_LSTORE_1 -> getIndex1,
    OPCODE_LSTORE_2 -> getIndex2, OPCODE_LSTORE_3 -> getIndex3)
  private val FSTORE_MAP: Map[OpCode, getIndex] = Map(OPCODE_FSTORE -> getIndexR, OPCODE_FSTORE_0 -> getIndex0, OPCODE_FSTORE_1 -> getIndex1,
    OPCODE_FSTORE_2 -> getIndex2, OPCODE_FSTORE_3 -> getIndex3)
  private val DSTORE_MAP: Map[OpCode, getIndex] = Map(OPCODE_DSTORE -> getIndexR, OPCODE_DSTORE_0 -> getIndex0, OPCODE_DSTORE_1 -> getIndex1,
    OPCODE_DSTORE_2 -> getIndex2, OPCODE_DSTORE_3 -> getIndex3)
  private val ASTORE_MAP: Map[OpCode, getIndex] = Map(OPCODE_ASTORE -> getIndexR, OPCODE_ASTORE_0 -> getIndex0, OPCODE_ASTORE_1 -> getIndex1,
    OPCODE_ASTORE_2 -> getIndex2, OPCODE_ASTORE_3 -> getIndex3)
  private val BASE_STORE_MAP = ISTORE_MAP ++ LSTORE_MAP ++ FSTORE_MAP ++ DSTORE_MAP ++ ASTORE_MAP
  private val ARRAY_STORE_MAP: Map[OpCode, Boolean] = Map(OPCODE_BASTORE -> true, OPCODE_CASTORE -> true, OPCODE_SASTORE -> true,
    OPCODE_AASTORE -> true, OPCODE_IASTORE -> true, OPCODE_LASTORE -> true, OPCODE_FASTORE -> true, OPCODE_DASTORE -> true)

  private val STORE_TYPES_MAP: Map[OpCode, BaseType] =
    Map(OPCODE_BASTORE -> TYPE_BYTE) ++
      Map(OPCODE_CASTORE -> TYPE_CHAR) ++
      Map(OPCODE_SASTORE -> TYPE_SHORT) ++
      map(Array(OPCODE_ISTORE, OPCODE_ISTORE_0, OPCODE_ISTORE_1, OPCODE_ISTORE_2, OPCODE_ISTORE_3, OPCODE_IASTORE), TYPE_INT) ++
      map(Array(OPCODE_LSTORE, OPCODE_LSTORE_0, OPCODE_LSTORE_1, OPCODE_LSTORE_2, OPCODE_LSTORE_3, OPCODE_LASTORE), TYPE_LONG) ++
      map(Array(OPCODE_FSTORE, OPCODE_FSTORE_0, OPCODE_FSTORE_1, OPCODE_FSTORE_2, OPCODE_FSTORE_3, OPCODE_FASTORE), TYPE_FLOAT) ++
      map(Array(OPCODE_DSTORE, OPCODE_DSTORE_0, OPCODE_DSTORE_1, OPCODE_DSTORE_2, OPCODE_DSTORE_3, OPCODE_DASTORE), TYPE_DOUBLE) ++
      map(Array(OPCODE_ASTORE, OPCODE_ASTORE_0, OPCODE_ASTORE_1, OPCODE_ASTORE_2, OPCODE_ASTORE_3, OPCODE_AASTORE), TYPE_REFERENCE)

  def isReferenceStoreOpcode(runtimeOpcode: RuntimeOpcode): Boolean = ASTORE_MAP.contains(runtimeOpcode.opcode)
  def isReferenceStoreNode(node: OpcodeNode): Boolean = node != null && isReferenceStoreOpcode(node.runtimeOpcode)
  def getBaseStoreOpcodeIndex(runtimeOpcode: RuntimeOpcode): Int = BASE_STORE_MAP.getOrElse(runtimeOpcode.opcode, getIndexM1)(runtimeOpcode)
  def isArrayStoreOpcode(runtimeOpcode: RuntimeOpcode): Boolean = ARRAY_STORE_MAP.getOrElse(runtimeOpcode.opcode, false)
  def getStoreOpcodeType(runtimeOpcode: RuntimeOpcode): BaseType = STORE_TYPES_MAP.getOrElse(runtimeOpcode.opcode, null)
  def matches(runtimeOpcode: RuntimeOpcode) = BASE_STORE_MAP.contains(runtimeOpcode.opcode) || ARRAY_STORE_MAP.contains(runtimeOpcode.opcode)

  def isBaseStoreNode(node: OpcodeNode): Boolean = node != null && BASE_STORE_MAP.contains(node.runtimeOpcode.opcode)

  def isArrayStoreNode(node: OpcodeNode): Boolean = node != null && isArrayStoreOpcode(node.runtimeOpcode)
}