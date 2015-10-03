package org.powlab.jeye.decode

import org.powlab.jeye.core.Types._;
import org.powlab.jeye.core.Descriptors._;
import org.powlab.jeye.decode.expression._;
import scala.collection.mutable.Stack

/**
 * Логику с Variable перенести в объект Variables
 */
object FrameStack {

  type OperandStack = Stack[ITypedExpression]

  /** Стэк фрэймов */
}

import org.powlab.jeye.decode.FrameStack.OperandStack

class FrameStack {
  var top: OperandStack = new OperandStack
  private var stack = new Stack[OperandStack]

  def pushFrame() {
    stack.push(top)
    // Формируем новый операнд стэк на основе предыдущего
    top = top.clone
  }

  def popFrame() {
    top = stack.pop
  }

  /** Все операции производятся над операнд стэком, который в данный момент наверху основного стэка */

  def pop(): ITypedExpression = top.pop
  def push(variable: ITypedExpression) {
    top.push(variable)
  }
  def clear() {
    top.clear
  }
  def size(): Int = top.size
  def isEmpty: Boolean = top.isEmpty
}
