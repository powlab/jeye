package org.powlab.jeye.utils

import org.powlab.jeye.core._
import org.powlab.jeye.core.Constants._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.parsing.DescriptorParser.Pisc
import org.powlab.jeye.core.{ Types, Utils }
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.utils.DecodeUtils.{ getClassMeta, getJavaNotationClassName, getSimpleClassName, getViewType, isConstructor }

class ConstantPoolUtils(classFile: ClassFile) {

  val keepConstantPool = classFile.constant_pool

  def getFloat(constantPoolFloat: ConstantU4Info) = {
    Utils.toFloat(constantPoolFloat.bytes)
  }

  // ----------------------- Получение структур --------------------------------

  def get(index: Int) = keepConstantPool(index)

  def getUtf8Struct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantUtf8Info]

  def getStringStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantStringInfo]

  def getIntegerStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantU4Info]

  def getFloatStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantU4Info]

  def getLongStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantU8Info]

  def getDoubleStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantU8Info]

  def getMethodStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantRefInfo]

  def getFieldStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantRefInfo]

  def getNameAndTypeStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantNameAndTypeInfo]

  def getClassStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantClassInfo]

  def getInvokeDynamicStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantInvokeDynamicInfo]

  def getMethodHandleStruct(index: Int) = keepConstantPool(index).asInstanceOf[ConstantMethodHandleInfo]

  // ----------------------- Получение констант --------------------------------

  def getUtf8(index: Int) = new String(getUtf8Struct(index).bytes, CP_STRING_CHARSETNAME)

  def getUtf8(constant: ConstantUtf8Info) = new String(constant.bytes, CP_STRING_CHARSETNAME)

  def getClassName(index: Int) = getUtf8(keepConstantPool(index).asInstanceOf[ConstantClassInfo].name_index)

  def getString(constant: ConstantStringInfo): String = getUtf8(constant.string_index)

  def getString(index: Int): String = getString(getStringStruct(index))

  def getInteger(constant: ConstantU4Info): Int = Utils.toInt(constant.bytes)

  def getInteger(index: Int): Int = getInteger(getIntegerStruct(index))

  def getFloat(index: Int) = Utils.toFloat(getFloatStruct(index).bytes)

  def getDouble(constant: ConstantU8Info): Double = Utils.toDouble(constant.high_bytes, constant.low_bytes)

  def getDouble(index: Int): Double = getDouble(getDoubleStruct(index))

  def getLong(constant: ConstantU8Info): Long = Utils.toLong(constant.high_bytes, constant.low_bytes)

  def getLong(index: Int): Long = getLong(getLongStruct(index))

  /**
   * TODO here: этому методу здесь не место, так как он ссылается на выражение ITypedExpression
   * Лучше всего его расположить ExpressionHelpers
   */
  def getConstantVariable(index: Int): ITypedExpression = {
    val tag = keepConstantPool(index).tag
    tag match {
      case CONSTANT_String => StringLiteralExpression(getString(index))
      case CONSTANT_Integer => IntLiteralExpression(getInteger(index))
      case CONSTANT_Float => FloatLiteralExpression(getFloat(index))
      case CONSTANT_Double => DoubleLiteralExpression(getDouble(index))
      case CONSTANT_Long => LongLiteralExpression(getLong(index))
      case CONSTANT_Class => {
        val clazz = getClassInformator(index)
        val descriptor = Pisc(clazz.meta)
        ClassLiteralExpression(descriptor)
      }
      case _ => null
    }
  }

  // ----------------------- Информаторы  --------------------------------
  def getNameAndTypeInfo(cpItem: ConstantRefInfo) = NameAndTypeInfo(cpItem.name_and_type_index)

  def getNameAndTypeInfo(name_and_type_index: Int) = NameAndTypeInfo(name_and_type_index)

  case class NameAndTypeInfo(aNameAndTypeIndex: Int) {
    var memberNameAndType = keepConstantPool(aNameAndTypeIndex).asInstanceOf[ConstantNameAndTypeInfo]

    val name = getUtf8(memberNameAndType.name_index)
    val descriptor = getUtf8(memberNameAndType.descriptor_index)
  }

  def getClassInformator(index: Int) = {
    val name = getClassName(index)
    if (Types.isArrayType(name.charAt(0))) {
      val desc = Pisc(name)
      val baseType = desc.lowType
      if (Types.isBaseType(baseType.symbol.charAt(0))) {
        new PrimitiveClassInformator(name, baseType.description, desc.meta, true)
      } else {
        new ClassInformator(name, desc.meta, true)
      }
    } else {
      new ClassInformator(name, getClassMeta(name), false)
    }
  }

  val thisClass = getClassInformator(classFile.this_class)
  val superClass = getClassInformator(classFile.super_class)

  val constructorCount = classFile.methods.count(method => isConstructor(getUtf8(method.name_index)))

}

class ClassInformator(val name: String, val meta: String, val isArray: Boolean) {
  def javaName = getJavaNotationClassName(name)
  def simpleName = getSimpleClassName(name)
  def viewName = getViewType(meta)
}

class PrimitiveClassInformator(name: String, viewType: String, meta: String, isArray: Boolean) extends ClassInformator(name, meta, isArray) {
  override def javaName = viewType
  override def simpleName = viewType
}
