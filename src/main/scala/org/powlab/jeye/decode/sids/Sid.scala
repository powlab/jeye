package org.powlab.jeye.decode.sids

import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.OpcodeTree
import scala.collection.mutable.Map
import scala.collection.mutable.ListBuffer

/**
 * Api по sid'у
 * #1
 * #2
 * #3_1#1
 * #3_1#2
 *
 * TODO here: закэшировать первые 100 - 500 SID
 */
object Sid {

  private val SHARP_SYMBOL = "#"
  private val UNDERSCORE_SYMBOL = "_"
  val INITIAL_ID = "#1"
  val INITIAL_SID = new Sid(INITIAL_ID)

  def before(id1: String, id2: String): Boolean = {
    if (id1 == id2 || id1.startsWith(id2)) {
      return false;
    }
    if (id2.startsWith(id1)) {
      return true;
    }
    val prefix = id1.zip(id2).takeWhile(tupe => { tupe._1 == tupe._2 }).map(_._1).mkString
    val tail1 = id1.substring(prefix.length())
    val tail2 = id2.substring(prefix.length())
    val order1 = tail1.takeWhile(symbol => { Character.isDigit(symbol) }).mkString.toInt
    val order2 = tail2.takeWhile(symbol => { Character.isDigit(symbol) }).mkString.toInt
    order1 < order2
  }

  /**
   * Создать структурированный id
   */
  def make(base: String, order: Int): String = base + SHARP_SYMBOL + order

  /**
   * Получить базу структурированного id
   */
  private def getBase(sid: String): String = {
    val sharpIndex = sid.lastIndexOf(SHARP_SYMBOL)
    sid.substring(0, sharpIndex)
  }

  /**
   * Получить порядковый номер структурированного id
   */
  private def getOrder(sid: String): Int = {
    val sharpIndex = sid.lastIndexOf(SHARP_SYMBOL)
    sid.substring(sharpIndex + 1).toInt
  }

  /**
   * Получить номер ветки
   */
  private def getBranch(base: String): Int = {
    val underIndex = base.lastIndexOf(UNDERSCORE_SYMBOL)
    if (underIndex != -1) {
      base.substring(underIndex + 1).toInt
    } else {
      0
    }
  }

  def apply(sid: String): Sid = new Sid(sid)

  def apply(base: String, order: Int): Sid = new Sid(make(base, order))

}

import org.powlab.jeye.decode.sids.Sid._

/**
 * Структурированный id
 */
class Sid(val sid: String) {
  /** база структурированного id */
  val base = getBase(sid)
  /** порядковый номер структурированного id */
  val order = getOrder(sid)
  /** Получить номер ветки */
  def branch = getBranch(base)

  def nextId: String = make(base, order + 1)

  def previewId: String = make(base, order - 1)

  def childId(branchNumber: Int): String = sid + UNDERSCORE_SYMBOL + branchNumber + INITIAL_ID

  def nextSid: Sid = new Sid(nextId)

  def previewSid: Sid = new Sid(previewId)

  def childSid(branchNumber: Int): Sid = new Sid(childId(branchNumber))

  def hasParentSid(): Boolean = parentId != null

  def parentId(): String = {
    val underscoreIndex = sid.lastIndexOf(UNDERSCORE_SYMBOL)
    if (underscoreIndex == -1) {
      return null
    }
    sid.substring(0, underscoreIndex)
  }

  def parentSid(): Sid = {
    val psid = parentId()
    if (psid != null) new Sid(psid) else null
  }

  def isParentFor(childSid: String): Boolean = childSid != null && childSid != sid && childSid.startsWith(sid)

  override def toString(): String = sid
}