package org.powlab.jeye.decode

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.utils.{ DecodeUtils, ConstantPoolUtils }
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.utils.DecodeUtils._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.core.Utils

object RuntimeOpcodes {

  def parseRuntimeOpcodes(bytes: Array[Byte]): Array[RuntimeOpcode] = {
    val stream = getBytesStream(bytes)
    val runtimeOpcodes = new ArrayBuffer[RuntimeOpcode]()

    def u1(): Int = stream.readU1()

    def u1Array(count: Int): Array[Byte] = stream.readBytes(count)

    def u1s(): Byte = stream.readU1S()

    def readArray[T](count: Int, creator: Int => T): ArrayBuffer[T] = {
      val arrays = ArrayBuffer[T]()
      (0 until count).foreach(index => arrays += creator(index))
      arrays
    }

    def readWideOpcode(index: Int, opcode: OpCode): (Int, RuntimeOpcode) = {
      val values = new ArrayBuffer[Int]
      values ++= readArray(3, (index) => u1)
      if (values(0) == OPCODE_IINC.code) {
        values ++= readArray(2, (index) => u1)
      }
      (1 + values.length, new RuntimeOpcode(index, opcode, values.toArray))
    }

    def readTableSwitchOpcode(index: Int, opcode: OpCode): (Int, RuntimeOpcode) = {
      val paddingCount = 3 - (index % 4)
      u1Array(paddingCount) // skip padding
      val values = new ArrayBuffer[Int]
      values ++= readArray(12, (index) => u1)
      val low = bytesToInt(values, 4)
      val high = bytesToInt(values, 8)
      val offsetsCount = high - low + 1
      values ++= readArray(4 * offsetsCount, (index) => u1)
      (1 + paddingCount + values.length, new RuntimeOpcode(index, opcode, values.toArray))
    }

    def readLookupSwitchOpcode(index: Int, opcode: OpCode): (Int, RuntimeOpcode) = {
      val paddingCount = 3 - (index % 4)
      u1Array(paddingCount) // skip padding
      val values = new ArrayBuffer[Int]
      values ++= readArray(8, (index) => u1) // default, npairs
      val npairs = bytesToInt(values, 4)
      values ++= readArray(4 * npairs * 2, (index) => u1)
      (1 + paddingCount + values.length, new RuntimeOpcode(index, opcode, values.toArray))
    }

    def readRuntimeOpcode(index: Int, opcode: OpCode): (Int, RuntimeOpcode) = {
      val params = opcode.params
      val values = readArray(params.length, (index) => {
        if (params(index).sign) u1s else u1
      }).toArray
      (1 + values.length, new RuntimeOpcode(index, opcode, values))
    }

    var index = 0
    while (index < bytes.length) {
      val opcode = OPCODES(u1)
      val (opcodeLength, runtimeOpcode) = opcode match {
        case OPCODE_WIDE => readWideOpcode(index, opcode)
        case OPCODE_TABLESWITCH => readTableSwitchOpcode(index, opcode)
        case OPCODE_LOOKUPSWITCH => readLookupSwitchOpcode(index, opcode)
        case _ => readRuntimeOpcode(index, opcode)
      }
      runtimeOpcodes += runtimeOpcode
      index += opcodeLength
    }
    runtimeOpcodes.toArray
  }

  val OPCODES_WITH_OFFSET_2 = List(Opcodes.OPCODE_GOTO, Opcodes.OPCODE_JSR)
  val OPCODES_WITH_OFFSET_4 = List(Opcodes.OPCODE_GOTO_W, Opcodes.OPCODE_JSR_W)

  // @TODO к этому делу нужно написать тесты
  // TODO here: Эту функцию нужно вынести в отдельный класс, детализировать большее количество opcode
  def runtimeOpcodeToString(runtimeOpcode: RuntimeOpcode, cpUtils: ConstantPoolUtils, maxCount: Int = 0): String = {
    var view = pad(runtimeOpcode.number, maxCount)
    val opcode = runtimeOpcode.opcode
    view += " " + opcode.name
    val values: Array[Int] = runtimeOpcode.values
    values.foreach(value => view += " " + value)
    view += spaces(28 - view.length())
    view += " //" + opcode.operation
    if ((opcode.category == OTYPE_COMPARISON && values.size == 2) || OPCODES_WITH_OFFSET_2.contains(opcode)) {
      view += s", jump to ${runtimeOpcode.number + Utils.toShort(values(0), values(1))}"
    } else if (opcode == Opcodes.OPCODE_BIPUSH || opcode == Opcodes.OPCODE_SIPUSH) {
      view += s", param = ${values(0)}"
    } else if (OPCODES_WITH_OFFSET_4.contains(opcode)) {
      view += s", param = ${runtimeOpcode.number + getInt(0, values)}"
    } else if (opcode == Opcodes.OPCODE_IINC) {

    } else if (opcode == Opcodes.OPCODE_INVOKEINTERFACE) {
      val constant = cpUtils.get(getShort(0, values)).asInstanceOf[ConstantRefInfo]
      val method = cpUtils.getClassName(constant.class_index).replace("/", ".") + "." +
        cpUtils.getNameAndTypeInfo(constant.name_and_type_index).name
      view += s", param = $method"
    } else if (opcode == Opcodes.OPCODE_LDC) {
      val param = cpUtils.get(values(0)) match {
        case constant: ConstantU4Info =>
        case constant: ConstantStringInfo => cpUtils.getString(constant)
        case constant: ConstantClassInfo => cpUtils.getUtf8(constant.name_index)
        case constant: ConstantRefInfo => cpUtils.getClassName(constant.class_index).replace("/", ".") + "." +
          cpUtils.getNameAndTypeInfo(constant.name_and_type_index).name
      }
      view += s", param = $param}"
    } else if (opcode == Opcodes.OPCODE_LDC2_W) {
      view += s", long = ${cpUtils.getLong(getShort(0, values))}, double = ${cpUtils.getDouble(getShort(0, values))}"
    } else if (opcode == Opcodes.OPCODE_LOOKUPSWITCH) {
      val default = getInt(0, values)
      val count = getInt(1, values)
      view += s", default = ${default + runtimeOpcode.number}"
      view += s", count = $count"
      for (i <- 1 to count) {
        val index = i * 2
        view += s", case ${getInt(index, values)}: ${getInt(index + 1, values) + runtimeOpcode.number}"
      }
    } else if (opcode == Opcodes.OPCODE_TABLESWITCH) {
      val index1 = getInt(1, values)
      val index2 = getInt(2, values)
      val count = index2 - index1
      view += s", default = ${getInt(0, values) + runtimeOpcode.number}, range = $index1 to $index2 "
      for (i <- 1 to count) {
        view += s", case ${index1 + i - 1}: ${getInt(i + 2, values) + runtimeOpcode.number}"
      }
    } else if (opcode == Opcodes.OPCODE_MULTIANEWARRAY) {
      val className = DecodeUtils.getViewType(cpUtils.getClassName(getShort(0, values)))
      view += s", param = $className"
    } else if (opcode == Opcodes.OPCODE_NEWARRAY) {
      view += s", type = ${ARRAY_TYPES(values(0)).typeAlias}"
    } else if (opcode == Opcodes.OPCODE_RET) {
      view += s", param = ${values(0)}"
    } else if (opcode == Opcodes.OPCODE_WIDE) {

    } else if (values.size == 2) {
      val index = (values(0) << 8) | values(1)
      val param = cpUtils.get(index) match {
        case constant: ConstantUtf8Info => cpUtils.getUtf8(constant)
        case constant: ConstantStringInfo => cpUtils.getString(constant)
        case constant: ConstantU4Info => cpUtils.getInteger(constant)
        case constant: ConstantRefInfo => cpUtils.getClassName(constant.class_index).replace("/", ".") + "." +
          cpUtils.getNameAndTypeInfo(constant.name_and_type_index).name
        case constant: ConstantClassInfo => cpUtils.getUtf8(constant.name_index).replace("/", ".")
        case skip => skip
      }
      view += s", param = $param"
    }
    view
  }

  def getInt(index: Int, values: Array[Int]) = {
    val i = 4 * index
    values(i) << 24 | values(i + 1) << 16 | values(i + 2) << 8 | values(i + 3)
  }

  def getShort(index: Int, values: Array[Int]) = {
    val i = 2 * index
    values(i) << 8 | values(i + 1)
  }

}

class RuntimeOpcode(val number: Int, val opcode: OpCode, val values: Array[Int]) {
  override def toString() = {
    "" + number + " " + opcode.name
  }
}
