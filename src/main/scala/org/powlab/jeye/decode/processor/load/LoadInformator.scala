package org.powlab.jeye.decode.processor.load

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Opcodes.OpCode
import org.powlab.jeye.core.Types._
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.processor.IndexType
import org.powlab.jeye.decode.graph.OpcodeNode

object LoadInformator extends IndexType {

  // maps
  private val ILOAD_MAP:Map[OpCode, getIndex] = Map(OPCODE_ILOAD -> getIndexR, OPCODE_ILOAD_0 -> getIndex0, OPCODE_ILOAD_1 -> getIndex1,
                      OPCODE_ILOAD_2 -> getIndex2, OPCODE_ILOAD_3 -> getIndex3)
  private val LLOAD_MAP:Map[OpCode, getIndex] = Map(OPCODE_LLOAD -> getIndexR, OPCODE_LLOAD_0 -> getIndex0, OPCODE_LLOAD_1 -> getIndex1,
                      OPCODE_LLOAD_2 -> getIndex2, OPCODE_LLOAD_3 -> getIndex3)
  private val FLOAD_MAP:Map[OpCode, getIndex] = Map(OPCODE_FLOAD -> getIndexR, OPCODE_FLOAD_0 -> getIndex0, OPCODE_FLOAD_1 -> getIndex1,
                      OPCODE_FLOAD_2 -> getIndex2, OPCODE_FLOAD_3 -> getIndex3)
  private val DLOAD_MAP:Map[OpCode, getIndex] = Map(OPCODE_DLOAD -> getIndexR, OPCODE_DLOAD_0 -> getIndex0, OPCODE_DLOAD_1 -> getIndex1,
                      OPCODE_DLOAD_2 -> getIndex2, OPCODE_DLOAD_3 -> getIndex3)
  private val ALOAD_MAP:Map[OpCode, getIndex] = Map(OPCODE_ALOAD -> getIndexR, OPCODE_ALOAD_0 -> getIndex0, OPCODE_ALOAD_1 -> getIndex1,
                      OPCODE_ALOAD_2 -> getIndex2, OPCODE_ALOAD_3 -> getIndex3)
  private val BASE_LOAD_MAP = ILOAD_MAP ++ LLOAD_MAP ++ FLOAD_MAP ++ DLOAD_MAP  ++ ALOAD_MAP
  private val ARRAY_LOAD_MAP:Map[OpCode, Boolean] = Map(OPCODE_BALOAD -> true, OPCODE_CALOAD -> true, OPCODE_SALOAD -> true,
      OPCODE_AALOAD -> true, OPCODE_IALOAD -> true, OPCODE_LALOAD -> true, OPCODE_FALOAD -> true, OPCODE_DALOAD -> true)

  private val LOAD_TYPES_MAP:Map[OpCode, BaseType] =
    Map(OPCODE_BALOAD -> TYPE_BYTE) ++
    Map(OPCODE_CALOAD -> TYPE_CHAR) ++
    Map(OPCODE_SALOAD -> TYPE_SHORT) ++
    map(Array(OPCODE_ILOAD, OPCODE_ILOAD_0, OPCODE_ILOAD_1, OPCODE_ILOAD_2, OPCODE_ILOAD_3, OPCODE_IALOAD), TYPE_INT) ++
    map(Array(OPCODE_LLOAD, OPCODE_LLOAD_0, OPCODE_LLOAD_1, OPCODE_LLOAD_2, OPCODE_LLOAD_3, OPCODE_LALOAD), TYPE_LONG) ++
    map(Array(OPCODE_FLOAD, OPCODE_FLOAD_0, OPCODE_FLOAD_1, OPCODE_FLOAD_2, OPCODE_FLOAD_3, OPCODE_FALOAD), TYPE_FLOAT) ++
    map(Array(OPCODE_DLOAD, OPCODE_DLOAD_0, OPCODE_DLOAD_1, OPCODE_DLOAD_2, OPCODE_DLOAD_3, OPCODE_DALOAD), TYPE_DOUBLE) ++
    map(Array(OPCODE_ALOAD, OPCODE_ALOAD_0, OPCODE_ALOAD_1, OPCODE_ALOAD_2, OPCODE_ALOAD_3, OPCODE_AALOAD), TYPE_REFERENCE)

  def getIntLoadOpcodeIndex(runtimeOpcode: RuntimeOpcode): Int = ILOAD_MAP.getOrElse(runtimeOpcode.opcode, getIndexM1)(runtimeOpcode)
  def getBaseLoadOpcodeIndex(runtimeOpcode: RuntimeOpcode): Int = BASE_LOAD_MAP.getOrElse(runtimeOpcode.opcode, getIndexM1)(runtimeOpcode);
  def isReferenceLoadOpcode(runtimeOpcode: RuntimeOpcode): Boolean = ALOAD_MAP.contains(runtimeOpcode.opcode)
  def isReferenceLoadNode(node: OpcodeNode): Boolean = node != null && isReferenceLoadOpcode(node.runtimeOpcode)
  def isArrayLoadOpcode(runtimeOpcode: RuntimeOpcode): Boolean = ARRAY_LOAD_MAP.getOrElse(runtimeOpcode.opcode, false)
  def getLoadOpcodeType(runtimeOpcode: RuntimeOpcode): BaseType = LOAD_TYPES_MAP.getOrElse(runtimeOpcode.opcode, null)

  def isBaseLoadNode(node: OpcodeNode): Boolean = node != null && BASE_LOAD_MAP.contains(node.runtimeOpcode.opcode);
}