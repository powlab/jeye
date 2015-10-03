package org.powlab.jeye.core

object Types {

  val TYPE_BOOLEAN_CHAR = 'Z'
  val TYPE_CHAR_CHAR = 'C'
  val TYPE_BYTE_CHAR = 'B'
  val TYPE_SHORT_CHAR = 'S'
  val TYPE_INT_CHAR = 'I'
  val TYPE_LONG_CHAR = 'J'
  val TYPE_FLOAT_CHAR = 'F'
  val TYPE_DOUBLE_CHAR = 'D'
  val TYPE_ARRAY_CHAR = '['
  val TYPE_REFERENCE_CHAR = 'L'
  val TYPE_VOID_CHAR = 'V'
  val TYPE_IDENTIFIER_CHAR = 'T'

  def isBaseType(symbol: Char): Boolean = {
    symbol == TYPE_BOOLEAN_CHAR ||
      symbol == TYPE_CHAR_CHAR ||
      symbol == TYPE_BYTE_CHAR ||
      symbol == TYPE_SHORT_CHAR ||
      symbol == TYPE_INT_CHAR ||
      symbol == TYPE_LONG_CHAR ||
      symbol == TYPE_FLOAT_CHAR ||
      symbol == TYPE_DOUBLE_CHAR
  }

  def isVoidType(symbol: Char): Boolean = symbol == TYPE_VOID_CHAR

  def isArrayType(symbol: Char): Boolean = symbol == TYPE_ARRAY_CHAR

  def isReferenceType(symbol: Char): Boolean = symbol == TYPE_REFERENCE_CHAR

  /**
   * TODO here: выделить в отдельный класс работу с примитивами
   */
  val TYPE_BOOLEAN = BaseType("" + TYPE_BOOLEAN_CHAR, "boolean")
  val TYPE_CHAR = BaseType("" + TYPE_CHAR_CHAR, "char");
  val TYPE_BYTE = BaseType("" + TYPE_BYTE_CHAR, "byte")
  val TYPE_SHORT = BaseType("" + TYPE_SHORT_CHAR, "short")
  val TYPE_INT = BaseType("" + TYPE_INT_CHAR, "int")
  val TYPE_LONG = BaseType("" + TYPE_LONG_CHAR, "long")
  val TYPE_FLOAT = BaseType("" + TYPE_FLOAT_CHAR, "float")
  val TYPE_DOUBLE = BaseType("" + TYPE_DOUBLE_CHAR, "double")
  val TYPE_ARRAY = BaseType("" + TYPE_ARRAY_CHAR, "array")
  val TYPE_REFERENCE = BaseType("" + TYPE_REFERENCE_CHAR, "reference")
  val TYPE_VOID = BaseType("" + TYPE_VOID_CHAR, "void")
  val TYPE_IDENTIFIER = BaseType("" + TYPE_IDENTIFIER_CHAR, "identifier")

  def isBaseType(baseType: BaseType): Boolean = {
    baseType == TYPE_BOOLEAN ||
      baseType == TYPE_CHAR ||
      baseType == TYPE_BYTE ||
      baseType == TYPE_SHORT ||
      baseType == TYPE_INT ||
      baseType == TYPE_LONG ||
      baseType == TYPE_FLOAT ||
      baseType == TYPE_DOUBLE
  }

  def isSubInt(baseType: BaseType): Boolean = {
    baseType == TYPE_BOOLEAN || baseType == TYPE_CHAR || baseType == TYPE_BYTE || baseType == TYPE_SHORT
  }

  def isIntable(baseType: BaseType): Boolean = {
    baseType == TYPE_INT || isSubInt(baseType)
  }

  private val TYPES = scala.collection.mutable.Map[Char, BaseType]()
  TYPES(TYPE_BOOLEAN_CHAR) = TYPE_BOOLEAN
  TYPES(TYPE_CHAR_CHAR) = TYPE_CHAR
  TYPES(TYPE_BYTE_CHAR) = TYPE_BYTE
  TYPES(TYPE_SHORT_CHAR) = TYPE_SHORT
  TYPES(TYPE_INT_CHAR) = TYPE_INT
  TYPES(TYPE_LONG_CHAR) = TYPE_LONG
  TYPES(TYPE_FLOAT_CHAR) = TYPE_FLOAT
  TYPES(TYPE_DOUBLE_CHAR) = TYPE_DOUBLE
  TYPES(TYPE_ARRAY_CHAR) = TYPE_ARRAY
  TYPES(TYPE_REFERENCE_CHAR) = TYPE_REFERENCE
  TYPES(TYPE_VOID_CHAR) = TYPE_VOID
  TYPES(TYPE_IDENTIFIER_CHAR) = TYPE_IDENTIFIER

  def getRegistryTypes = TYPES

  def getType(symbol: Char) = TYPES(symbol)

  private val TYPES_DESCRIPTION = scala.collection.mutable.Map[String, BaseType]()
  TYPES_DESCRIPTION(TYPE_BOOLEAN.description) = TYPE_BOOLEAN
  TYPES_DESCRIPTION(TYPE_CHAR.description) = TYPE_CHAR
  TYPES_DESCRIPTION(TYPE_BYTE.description) = TYPE_BYTE
  TYPES_DESCRIPTION(TYPE_SHORT.description) = TYPE_SHORT
  TYPES_DESCRIPTION(TYPE_INT.description) = TYPE_INT
  TYPES_DESCRIPTION(TYPE_LONG.description) = TYPE_LONG
  TYPES_DESCRIPTION(TYPE_FLOAT.description) = TYPE_FLOAT
  TYPES_DESCRIPTION(TYPE_DOUBLE.description) = TYPE_DOUBLE
  TYPES_DESCRIPTION(TYPE_ARRAY.description) = TYPE_ARRAY
  TYPES_DESCRIPTION(TYPE_REFERENCE.description) = TYPE_REFERENCE
  TYPES_DESCRIPTION(TYPE_VOID.description) = TYPE_VOID
  TYPES_DESCRIPTION(TYPE_IDENTIFIER.description) = TYPE_IDENTIFIER

  def getTypeByDesc(description: String) = TYPES_DESCRIPTION(description)

  /** ----------------- Array type codes ----------------------------- */
  val ARRAY_TYPES = scala.collection.mutable.Map[Number, ArrayType]()

  val AT_BOOLEAN = ArrayType(4, "boolean")
  val AT_CHAR = ArrayType(5, "char")
  val AT_FLOAT = ArrayType(6, "float")
  val AT_DOUBLE = ArrayType(7, "double")
  val AT_BYTE = ArrayType(8, "byte")
  val AT_SHORT = ArrayType(9, "short")
  val AT_INT = ArrayType(10, "int")
  val AT_LONG = ArrayType(11, "long")
}

/** Базовый класс для всех сигнатур и типов */
abstract class IType(signature: String) {
  protected var name = ""

  override def toString = {
    "[" + name + "] = " + signature;
  }
}

case class BaseType(symbol: String, description: String) extends IType(symbol) {
  name = description
}
case class ArrayType(code: Number, val typeAlias: String) {
  Types.ARRAY_TYPES(code) = this
}
