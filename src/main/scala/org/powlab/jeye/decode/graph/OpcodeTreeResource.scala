package org.powlab.jeye.decode.graph

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.isGotoNode
import org.powlab.jeye.decode.processor.control.SwitchInstructionInformator.isSwitchNode
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.{isMonitorExitNode,isAthrowNode}
import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception.RESOURCE_AREA
import scala.collection.mutable.Set
import org.powlab.jeye.decode.expression.ExpressionClassifier

/**
 * Ресурсы
 */
class OpcodeTreeResource {
  /** Храним узлы, которые потребуются при обработке */
  private val ifNodes = new ArrayBuffer[OpcodeNode]
  /** Храним узлы, которые потребуются при обработке */
  private val switchNodes = new ArrayBuffer[OpcodeNode]
  /** Храним узлы, которые потребуются при обработке */
  private val monitorExitNodes = new ArrayBuffer[OpcodeNode]
  /** Храним узлы, которые потребуются при обработке */
  private val gotoNodes = new ArrayBuffer[OpcodeNode]
  /** Храним узлы, которые потребуются при обработке */
  private val athrowFinallyNodes = new ArrayBuffer[OpcodeNode]
  /** Шаблоны */
  private val patternResults = new ArrayBuffer[PatternResult]
  /** Используемые выражения */
  private val classifiers = Set[ExpressionClassifier]()

  def +=(node: OpcodeNode): OpcodeTreeResource = {
    if (isIfNode(node)) {
      ifNodes += node
    } else if (isSwitchNode(node)) {
      switchNodes += node
    } else if (isMonitorExitNode(node)) {
      monitorExitNodes += node
    } else if (isGotoNode(node)) {
      gotoNodes += node
    } else if (isAthrowNode(node)) {
      athrowFinallyNodes += node
    } else {
      val reason = "Обнаружена инструкция '" + node + "'  для которой не найден контейнер для хранения"
      val effect = "Обработка дерева инструкций будет прекращена."
      val action = "Необходимо добавить новый контейнер для '" + node + "' инструкции"
      throw Exception(RESOURCE_AREA, reason, effect, action)
    }
    this
  }

  def +=(classifier: ExpressionClassifier): OpcodeTreeResource = {
    classifiers += classifier
    this
  }

  def +=(patternResult: PatternResult): OpcodeTreeResource = {
    patternResults += patternResult
    this
  }

  def ++=(patternResults: ArrayBuffer[PatternResult]): OpcodeTreeResource = {
    this.patternResults ++= patternResults
    this
  }

  def ++=(classifiers: Set[ExpressionClassifier]): OpcodeTreeResource = {
    this.classifiers ++= classifiers
    this
  }

  def -=(node: OpcodeNode): OpcodeTreeResource = {
    ifNodes -= node
    switchNodes -= node
    monitorExitNodes -= node
    gotoNodes -= node
    athrowFinallyNodes -= node
    this
  }

  def getIfNodes = ifNodes

  def getSwitchNodes = switchNodes

  def getMonitorExitNodes = monitorExitNodes

  def getGotoNodes = gotoNodes

  def getAthrowFinallyNodes = athrowFinallyNodes

  def getPatternResults = patternResults

  def getClassifiers = classifiers

  def scanAll(handler: (OpcodeNode => Unit)) {
    ifNodes.foreach(handler)
    switchNodes.foreach(handler)
    monitorExitNodes.foreach(handler)
    gotoNodes.foreach(handler)
    athrowFinallyNodes.foreach(handler)
  }

}