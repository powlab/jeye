package org.powlab.jeye.decode.expression

import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expra.ExpressionHelpers._

/**
 * Отвечает за создание корректного представления выражения
 */
object ExpressionViewer {

  val FLUSH_TYPE = 0 // в конце точка с запятой
  val BRACKET_TYPE = 1 // выражение в скобках
  val BASE_TYPE = 2 // выражение без изменений

  def correctView(baseView: String, ownerClsf: ExpressionClassifier, baseClsfs: ExpressionClassifier*): String = {
    val state = if (ownerClsf == EC_BLOCK || ownerClsf == EC_STATEMENT) {
      FLUSH_TYPE
    } else if (baseClsfs.contains(ownerClsf)) {
      BASE_TYPE
    } else {
      BRACKET_TYPE
    }
    correctView(baseView, state)
  }

  def correctFlush(baseView: String, ownerClsf: ExpressionClassifier): String = {
    val state = if (ownerClsf == EC_BLOCK || ownerClsf == EC_STATEMENT) {
      FLUSH_TYPE
    } else {
      BASE_TYPE
    }
    correctView(baseView, state)
  }

  def correctBrackets(baseView: String, ownerClsf: ExpressionClassifier, bracketsClsfs: ExpressionClassifier*): String = {
    val state = if (ownerClsf == EC_BLOCK || ownerClsf == EC_STATEMENT) {
      FLUSH_TYPE
    } else if (bracketsClsfs.contains(ownerClsf)) {
      BRACKET_TYPE
    } else {
      BASE_TYPE
    }
    correctView(baseView, state)
  }

  def correctBracketsOnly(baseView: String, cond: Boolean, ownerClsf: ExpressionClassifier, bracketsClsfs: ExpressionClassifier*): String = {
    val state = if (cond || bracketsClsfs.contains(ownerClsf)) {
       BRACKET_TYPE
    } else {
      BASE_TYPE
    }
    correctView(baseView, state)
  }


  private def correctView(baseView: String, state: Int): String = {
    state match {
      case FLUSH_TYPE => baseView + ";"
      case BRACKET_TYPE => "(" + baseView + ")"
      case BASE_TYPE => baseView
    }
  }

}