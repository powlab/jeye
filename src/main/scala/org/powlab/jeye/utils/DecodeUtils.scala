package org.powlab.jeye.utils

import org.powlab.jeye.core._
import org.powlab.jeye.core.Types._

/**
 * TODO here: это вовсе не декод утилс - это сборная солянка.
 * Нужно переосмыслить
 */
object DecodeUtils {

  def getType(symbol: Char): BaseType = {
    val symbolType = Types.getType(symbol)
    if (symbolType == null) {
      throw new IllegalAccessException("Can't detect parameter type for symbol " + symbol)
    }
    symbolType
  }

  def getShort(unsignedbyte1: Int, unsignedbyte2: Int) = Utils.toShort(unsignedbyte1, unsignedbyte2);

  def unsignShort(values: Array[Int]): Int = (values(0) << 8) | values(1)

  def bytesToInt(values: IndexedSeq[Int], fromIndex: Int): Int = {
    (values(fromIndex) << 24) | (values(fromIndex + 1) << 16) | (values(fromIndex + 2) << 8) | values(fromIndex + 3);
  }

  def intToByte(value: Int) = Utils.intToByte(value)

  def getBytesStream(bytes: Array[Byte]) = Utils.getBytesStream(bytes)

  def pad(num: Int, size: Int): String = {
    var view = num.toString;
    while (view.length() < size) {
      view = "0" + view;
    }
    view;
  }

  def pad(value: String, size: Int, left: Boolean = false): String = {
    if (value == null || size <= value.length()) {
      return value
    }
    val padSize = size - value.length()
    val spacesValue = spaces(padSize)
    if (left) {
      spacesValue + value
    } else {
      value + spacesValue
    }
  }

  def spaces(count: Int): String = count match {
    case count if count >= 8 => "        " + spaces(count - 8)
    case count if count >= 4 => "    " + spaces(count - 4)
    case count if count >= 2 => "  " + spaces(count - 2)
    case count if count == 1 => " "
    case _ => ""
  }

  def getViewType(meta: String): String = {
    val baseType = getType(meta.charAt(0));
    baseType match {
      case TYPE_REFERENCE => getSimpleClassName(meta.substring(1, meta.length() - 1))
      case TYPE_ARRAY => getViewType(meta.substring(1)) + "[]"
      case _ => baseType.description
    }
  }

  def select[T](condition: Boolean, t1: T, t2: T): T = if (condition) t1 else t2

  def getSimpleClassName(className: String, delim: String = "/") = {
    val index = className.lastIndexOf(delim);
    select(index == -1, className, className.substring(index + 1))
  }

  // getReferenceClassName -> getClassMeta
  def getClassMeta(className: String) = TYPE_REFERENCE_CHAR + className + ";"

  def isConstructor(methodName: String) = methodName == "<init>"

  def isStaticBlock(methodName: String) = methodName == "<clinit>"

  def getJavaNotationClassName(className: String) = className.replace("/", ".")

  def getPathClassName(className: String) = className.replace(".", "/")

  private val CLASS_NAME_DELIMS = Array[Character]('.', '/', ';', '[')

  def getSimpleClassName(className: String): String = {
    var word: String = null
    if (className != null && className.nonEmpty) {
      StringWordParser.parseBack(className, CLASS_NAME_DELIMS, name => {
        word = name.replace('$', '.') //@TODO Это не правильно, нужно удалить это
        true
      })
    }
    word
  }

}
