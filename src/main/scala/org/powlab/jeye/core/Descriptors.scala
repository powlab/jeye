package org.powlab.jeye.core

import org.powlab.jeye.core.Types._

/**
 * 4.3 Descriptors and Signatures
 *
 * TODO here: выделить в отдельный класс работу с примитивами
 */
object Descriptors {

  private val RefDesc = (meta: String) => new ParameterDescriptor(TYPE_REFERENCE, meta)

  val TYPE_BOOLEAN_DESCRIPTOR = new ParameterDescriptor(TYPE_BOOLEAN, TYPE_BOOLEAN.symbol)
  val TYPE_CHAR_DESCRIPTOR = new ParameterDescriptor(TYPE_CHAR, TYPE_CHAR.symbol)
  val TYPE_BYTE_DESCRIPTOR = new ParameterDescriptor(TYPE_BYTE, TYPE_BYTE.symbol)
  val TYPE_SHORT_DESCRIPTOR = new ParameterDescriptor(TYPE_SHORT, TYPE_SHORT.symbol)
  val TYPE_INT_DESCRIPTOR = new ParameterDescriptor(TYPE_INT, TYPE_INT.symbol)
  val TYPE_LONG_DESCRIPTOR = new ParameterDescriptor(TYPE_LONG, TYPE_LONG.symbol)
  val TYPE_FLOAT_DESCRIPTOR = new ParameterDescriptor(TYPE_FLOAT, TYPE_FLOAT.symbol)
  val TYPE_DOUBLE_DESCRIPTOR = new ParameterDescriptor(TYPE_DOUBLE, TYPE_DOUBLE.symbol)
  val TYPE_VOID_DESCRIPTOR = new ParameterDescriptor(TYPE_VOID, TYPE_VOID.symbol)

  val TYPE_REF_NUMBER_DESCRIPTOR = RefDesc("Ljava/lang/Number;")
  val TYPE_REF_INTEGER_DESCRIPTOR = RefDesc("Ljava/lang/Integer;")
  val TYPE_REF_CHARACTER_DESCRIPTOR = RefDesc("Ljava/lang/Character;")
  val TYPE_REF_BYTE_DESCRIPTOR = RefDesc("Ljava/lang/Byte;")
  val TYPE_REF_BOOLEAN_DESCRIPTOR = RefDesc("Ljava/lang/Boolean;")
  val TYPE_REF_SHORT_DESCRIPTOR = RefDesc("Ljava/lang/Short;")
  val TYPE_REF_LONG_DESCRIPTOR = RefDesc("Ljava/lang/Long;")
  val TYPE_REF_FLOAT_DESCRIPTOR = RefDesc("Ljava/lang/Float;")
  val TYPE_REF_DOUBLE_DESCRIPTOR = RefDesc("Ljava/lang/Double;")

  val STRING_META = "Ljava/lang/String;"
  val TYPE_STRING_DESCRIPTOR = RefDesc(STRING_META)
  val TYPE_METHOD_TYPE_DESCRIPTOR = RefDesc("Ljava/lang/invoke/MethodType")
  val TYPE_OBJECT_DESCRIPTOR = RefDesc("Ljava/lang/Object;")
  val TYPE_CLASS_DESCRIPTOR = RefDesc("Ljava/lang/Class;")
  val TYPE_EXCEPTION_DESCRIPTOR = RefDesc("Ljava/lang/Exception;")
  val TYPE_THROW_DESCRIPTOR = RefDesc("Ljava/lang/Throwable;")
  val TYPE_ITERATOR_DESCRIPTOR = RefDesc("Ljava/util/Iterator;")

  val TYPE_STRING_BUILDER_DESCRIPTOR = RefDesc("Ljava/lang/StringBuilder;")
  val TYPE_STRING_BUFFER_DESCRIPTOR = RefDesc("Ljava/lang/StringBuffer;")

  private val baseTypes = Map[BaseType, ParameterDescriptor](
    TYPE_BOOLEAN -> TYPE_BOOLEAN_DESCRIPTOR,
    TYPE_CHAR -> TYPE_CHAR_DESCRIPTOR,
    TYPE_BYTE -> TYPE_BYTE_DESCRIPTOR,
    TYPE_SHORT -> TYPE_SHORT_DESCRIPTOR,
    TYPE_INT -> TYPE_INT_DESCRIPTOR,
    TYPE_LONG -> TYPE_LONG_DESCRIPTOR,
    TYPE_FLOAT -> TYPE_FLOAT_DESCRIPTOR,
    TYPE_DOUBLE -> TYPE_DOUBLE_DESCRIPTOR)

  def getBaseTypeDescriptor(baseType: BaseType): ParameterDescriptor = baseTypes(baseType)

  def isSubIntDesc(descriptor: ParameterDescriptor): Boolean = {
    descriptor == TYPE_BOOLEAN_DESCRIPTOR || descriptor == TYPE_CHAR_DESCRIPTOR ||
      descriptor == TYPE_BYTE_DESCRIPTOR || descriptor == TYPE_SHORT_DESCRIPTOR
  }

  def isIntableDesc(descriptor: ParameterDescriptor): Boolean = {
    descriptor == TYPE_INT_DESCRIPTOR || isSubIntDesc(descriptor)
  }

  def minDesc(descriptor1: ParameterDescriptor, descriptor2: ParameterDescriptor): ParameterDescriptor = {
    if (descOrder(descriptor1) < descOrder(descriptor2)) descriptor1 else descriptor2
  }

  val MAX_ORDER = 8

  def descOrder(descriptor: ParameterDescriptor): Int = {
    if (descriptor == TYPE_BOOLEAN_DESCRIPTOR) {
      1
    } else if (descriptor == TYPE_BYTE_DESCRIPTOR) {
      2
    } else if (descriptor == TYPE_CHAR_DESCRIPTOR) {
      3
    } else if (descriptor == TYPE_SHORT_DESCRIPTOR) {
      4
    } else if (descriptor == TYPE_INT_DESCRIPTOR) {
      5
    } else if (descriptor == TYPE_FLOAT_DESCRIPTOR) {
      6
    } else if (descriptor == TYPE_LONG_DESCRIPTOR) {
      7
    } else if (descriptor == TYPE_DOUBLE_DESCRIPTOR) {
      7
    } else {
      MAX_ORDER
    }
  }

  /** TODO here: сделать исследование часто используемых классов и добавить сюда*/
  private val commonlyUsed = Array(
    TYPE_BOOLEAN_DESCRIPTOR, TYPE_CHAR_DESCRIPTOR, TYPE_BYTE_DESCRIPTOR, TYPE_SHORT_DESCRIPTOR,
    TYPE_INT_DESCRIPTOR, TYPE_LONG_DESCRIPTOR, TYPE_FLOAT_DESCRIPTOR, TYPE_DOUBLE_DESCRIPTOR,
    TYPE_VOID_DESCRIPTOR, TYPE_STRING_DESCRIPTOR, TYPE_OBJECT_DESCRIPTOR, TYPE_CLASS_DESCRIPTOR,
    TYPE_EXCEPTION_DESCRIPTOR, TYPE_THROW_DESCRIPTOR, TYPE_ITERATOR_DESCRIPTOR,
    TYPE_REF_INTEGER_DESCRIPTOR, TYPE_REF_CHARACTER_DESCRIPTOR, TYPE_REF_BYTE_DESCRIPTOR,
    TYPE_REF_BOOLEAN_DESCRIPTOR, TYPE_REF_SHORT_DESCRIPTOR, TYPE_REF_LONG_DESCRIPTOR,
    TYPE_REF_FLOAT_DESCRIPTOR, TYPE_REF_DOUBLE_DESCRIPTOR, TYPE_REF_NUMBER_DESCRIPTOR,
    TYPE_STRING_BUILDER_DESCRIPTOR, TYPE_STRING_BUFFER_DESCRIPTOR,
    RefDesc("Ljava/util/Date;"),
    RefDesc("Ljava/util/Map;"), RefDesc("Ljava/util/HashMap;"),
    RefDesc("Ljava/util/List;"), RefDesc("Ljava/util/ArrayList;")).map(descriptor => descriptor.meta -> descriptor).toMap

  def getCommonlyUsed(meta: String): Option[ParameterDescriptor] = commonlyUsed.get(meta)

  def getCommonlyUsed(symbol: Char): ParameterDescriptor = commonlyUsed(String.valueOf(symbol))

  def isCharDescriptor(descriptor: ParameterDescriptor): Boolean = {
    descriptor != null && descriptor.baseType == Types.TYPE_CHAR
  }

  def isStringDescriptor(descriptor: ParameterDescriptor): Boolean = {
    descriptor == TYPE_STRING_DESCRIPTOR || descriptor.meta == STRING_META
  }

  def isReferenceDescriptor(descriptor: ParameterDescriptor): Boolean = {
    descriptor.baseType == TYPE_REFERENCE
  }

  def equalParams(param1: ParameterDescriptor, param2: ParameterDescriptor): Boolean = {
    param1 == param2 || (param1 != null && param2 != null && param1.meta == param2.meta)
  }

  def isCategory2(descriptor: ParameterDescriptor) = {
    descriptor != null && (descriptor.baseType == Types.TYPE_DOUBLE || descriptor.baseType == Types.TYPE_LONG)
  }

}

class ParameterDescriptor(val baseType: BaseType, val meta: String) {
  def isArray = baseType == TYPE_ARRAY
  def lowType = baseType
  override def toString(): String = meta
}

class ArrayDescriptor(val componentType: ParameterDescriptor, val dimension: Int, override val meta: String) extends ParameterDescriptor(TYPE_ARRAY, meta) {
  override def lowType = componentType.baseType
}

import org.powlab.jeye.core.Descriptors._
/**
 * TODO here: требует оптимизации логики сравнения
 */
object MethodDescriptor {
  def same(mDesc1: MethodDescriptor, mDesc2: MethodDescriptor): Boolean = {
    if (mDesc1 == mDesc2) {
      true
    } else if (mDesc1 != null && mDesc2 != null) {
      equalParams(mDesc1.returnType, mDesc2.returnType) &&
        (mDesc1.parameters.corresponds(mDesc2.parameters)(equalParams(_, _)))
    } else {
      false
    }
  }
}

class MethodDescriptor(val parameters: Array[ParameterDescriptor], val returnType: ParameterDescriptor) {
  override def toString(): String = parameters.mkString("(", ", ", ")") + returnType
}
