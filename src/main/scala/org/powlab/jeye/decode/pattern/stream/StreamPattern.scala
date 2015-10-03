package org.powlab.jeye.decode.pattern.stream

import org.powlab.jeye.decode.graph.OpcodeDetails.OpcodeDetail
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeTree

/**
 * Основной контракт на определение патернов
 * Очень важно: все реализации должны быть ReadOnly (потокобезопасными)
 */
trait StreamPattern {
  def details(resolvedNode: OpcodeNode, tree: OpcodeTree): OpcodeDetail
  def resolve(node: OpcodeNode, tree: OpcodeTree): OpcodeNode
}

