package org.powlab.jeye.decode

import org.powlab.jeye.core._
import scala.collection.mutable
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.graph.OpcodeNode

/**
 * Namer отвечает за наименование локальных переменных.
 * Принцип - название типа + индекс переменной(например boolean0, boolean1 и т.д.)
 */
class Namer {

  private val reserved = mutable.ListBuffer[String]()

  private val indexes = mutable.Map[BaseType, Int]().withDefaultValue(0)

  /**
   * Зарезервированно ли имя локальной переменной
   * @param name имя локальной переменной
   * @return true, если зарезервированно, false в противном случае
   */
  def isReserved(name: String): Boolean = reserved.contains(name)

  def getNextName(aType: BaseType, argIndex: Int): String = {
    getNextName(aType)
  }

  def getNextName(aType: BaseType, node: OpcodeNode, tree: OpcodeTree): String = {
    getNextName(aType)
  }

  /**
   * Получить следующее имя для переменной типа
   * @param aType тип
   * @return имя
   */
  def getNextName(aType: BaseType): String = {
    var index = indexes(aType)
    var name = aType.description + index
    if (!isReserved(name)) {
      indexes(aType) = index + 1
      registryName(name, aType)
    } else {
      do {
        index = index + 1
        name = aType.description + index
        indexes(aType) = index + 1
      } while (isReserved(name))
    }
    name
  }

  protected def registryName(name: String, aType: BaseType): String = {
    if (! reserved.contains(name)) {
      reserved += name
    }
    name
  }

  /**
   * Получить количество вариантов для данного кода
   */
  def getVariantsCount(node: OpcodeNode, tree: OpcodeTree): Int = {
    return -1;
  }
}
