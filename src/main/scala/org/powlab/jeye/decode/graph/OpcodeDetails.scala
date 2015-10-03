package org.powlab.jeye.decode.graph

import org.powlab.jeye.decode.expression.{IExpression}
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.graph.OpcodeTreeListeners._
import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception.NODE_DETAILS_AREA

/**
 * Описание узлов.
 * Требования: ReadOnly
 */
object OpcodeDetails {

  object DetailType {
    val DETAILS = new ArrayBuffer[DetailType]()
    def apply(name: String): DetailType = {
      val details = new DetailType(name)
      DETAILS += details
      details
    }
  }

  class DetailType(name: String) {
    override def toString(): String = name
  }

  val DETAIL_COMMON             = DetailType("common")
  val DETAIL_STORE_DUPS         = DetailType("store_dups")
  val DETAIL_STORE_DUPS_ARRAY   = DetailType("store_dups_array")
  val DETAIL_INC_LOAD_PRE       = DetailType("inc_pre")
  val DETAIL_INC_LOAD_POST      = DetailType("inc_post")
  val DETAIL_REF_INC_PRE        = DetailType("ref_inc_pre")
  val DETAIL_REF_INC_POST       = DetailType("ref_inc_post")
  val DETAIL_FIELD_INC_PRE      = DetailType("field_inc_pre")
  val DETAIL_FIELD_INC_POST     = DetailType("field_inc_post")
  val DETAIL_ARRAY_DUP          = DetailType("array_dup")
  val DETAIL_IF_SINGLE          = DetailType("if_single")
  val DETAIL_IF_OR_AND_GROUP    = DetailType("if_or_and_group")
  val DETAIL_IF_XOR_GROUP       = DetailType("if_xor_group")
  val DETAIL_CYCLE_WHILE        = DetailType("cycle_while")
  val DETAIL_CYCLE_FOR          = DetailType("cycle_for")
  val DETAIL_CYCLE_DO_WHILE     = DetailType("cycle_do_while")

  class OpcodeDetail(val detailType: DetailType) {
    def this() = this(DETAIL_COMMON)

    var sid: String = null
    var expression: IExpression = null

    override def toString(): String = "" + sid + " | " + expression + " | " + detailType
  }

  class IncDetail(detailType: DetailType, val value: Int, val dup: Boolean) extends OpcodeDetail(detailType)

  /** Детали по узлу if */
  class IfOpcodeDetail(detailType: DetailType) extends OpcodeDetail(detailType) with IdChanger {
    def this() = this(DETAIL_IF_SINGLE)
    var bodyId: String = null
    var elseId: String = null
    var elseBranch: Boolean = true

    override def change(oldId: String, newId: String) {
      if (bodyId == oldId) {
        bodyId = newId
      }
      if (elseId == oldId) {
        elseId = newId
      }
    }

    override def toString(): String = super.toString + " " + (bodyId, elseId, elseBranch)
  }

  /** Детали по узлу if */
  class CycleOpcodeDetail(detailType: DetailType) extends IfOpcodeDetail(detailType) {
    def this() = this(DETAIL_CYCLE_WHILE)
    elseBranch = false
    var label: String = null

    override def toString(): String = super.toString + ", label: " + label
  }

  /** Список деталей, которые можно назвать как DETAIL_COMMON */
  private val COMMON_DETAILS = Array(DETAIL_COMMON, DETAIL_STORE_DUPS, DETAIL_STORE_DUPS_ARRAY,
      DETAIL_ARRAY_DUP).map(value => value -> value).toMap

  def isCommonDetails(details: OpcodeDetail): Boolean = {
    details != null && COMMON_DETAILS.contains(details.detailType)
  }

  def isIncDetails(details: OpcodeDetail): Boolean = {
    details.isInstanceOf[IncDetail] &&
      (details.detailType == DETAIL_REF_INC_POST || details.detailType == DETAIL_REF_INC_PRE ||
        details.detailType == DETAIL_FIELD_INC_POST || details.detailType == DETAIL_FIELD_INC_PRE ||
        details.detailType == DETAIL_INC_LOAD_POST || details.detailType == DETAIL_INC_LOAD_PRE)
  }

  def isIfDetails(details: OpcodeDetail): Boolean = {
    details != null && details.isInstanceOf[IfOpcodeDetail] && (
    details.detailType == DETAIL_IF_SINGLE ||
    details.detailType == DETAIL_IF_OR_AND_GROUP ||
    details.detailType == DETAIL_IF_XOR_GROUP)
  }

  def isCycleDetails(details: OpcodeDetail): Boolean = {
    details != null && details.isInstanceOf[IfOpcodeDetail] && (
    details.detailType == DETAIL_CYCLE_WHILE ||
    details.detailType == DETAIL_CYCLE_FOR ||
    details.detailType == DETAIL_CYCLE_DO_WHILE)
  }

  def isIfable(details: OpcodeDetail): Boolean = isIfDetails(details) || isCycleDetails(details)

  def makeIfDetails(detailType: DetailType, details: IfOpcodeDetail, expression: IExpression): IfOpcodeDetail = {
    val newDetails = new IfOpcodeDetail(detailType)
    newDetails.expression  = expression
    newDetails.bodyId = details.bodyId
    newDetails.elseId = details.elseId
    newDetails.elseBranch = details.elseBranch
    newDetails
  }

  /**
   * Копирование деталей
   */
  def copyDetails(details: OpcodeDetail):OpcodeDetail = {
    def fillCommonDetails(commonDetails: OpcodeDetail, baseDetails: OpcodeDetail): OpcodeDetail = {
      commonDetails.expression = baseDetails.expression
      commonDetails.sid = baseDetails.sid
      commonDetails
    }
    def fillIfDetails(ifDetails: IfOpcodeDetail, baseIfDetails: IfOpcodeDetail): IfOpcodeDetail = {
      fillCommonDetails(ifDetails, baseIfDetails)
      ifDetails.bodyId = baseIfDetails.bodyId
      ifDetails.elseId = baseIfDetails.elseId
      ifDetails.elseBranch = baseIfDetails.elseBranch
      ifDetails
    }
    def fillCycleDetails(cycleDetails: CycleOpcodeDetail, baseCycleDetails: CycleOpcodeDetail): CycleOpcodeDetail = {
      fillIfDetails(cycleDetails, baseCycleDetails)
      cycleDetails.elseBranch = false
      cycleDetails.label = baseCycleDetails.label
      cycleDetails
    }
    details match {
      case value if isCommonDetails(details) => fillCommonDetails(new OpcodeDetail(details.detailType), details)
      case value if isIncDetails(details) => {
        val incDetails = details.asInstanceOf[IncDetail]
        fillCommonDetails(new IncDetail(details.detailType, incDetails.value, incDetails.dup), incDetails)
      }
      case value if isIfDetails(details) => fillIfDetails(new IfOpcodeDetail(details.detailType), details.asInstanceOf[IfOpcodeDetail])
      case value if isCycleDetails(details) => fillCycleDetails(new CycleOpcodeDetail(details.detailType), details.asInstanceOf[CycleOpcodeDetail])
      case _ => {
        val reason = "Обнаружен тип деталей '" + details + "' инструкции, для которого не найден метод копирования.";
        val effect = "Копирование деталей инструкций будет прекращено."
        val action = "Необходимо добавить метод копирования для деталей '" + details + "' инструкции"
        throw Exception(NODE_DETAILS_AREA, reason, effect, action)
      }
    }
  }

}