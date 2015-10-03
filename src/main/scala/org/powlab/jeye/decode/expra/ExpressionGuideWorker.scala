package org.powlab.jeye.decode.expra

import scala.collection.mutable.ArrayBuffer

import org.powlab.jeye.decode.expression.IExpression
import org.powlab.jeye.decode.expra.ExpressionGuide.Guide

/**
 * Путеводитель для 'многих'
 */
class ExpressionGuideWorker(expression: IExpression) {
  private val guides = new ArrayBuffer[Guide]
  private var guide1: Guide = null
  private var guide2: Guide = null
  private var guide3: Guide = null
  private var guide4: Guide = null
  private var guide5: Guide = null
  def +=(guide: Guide): ExpressionGuideWorker = {
    guides += guide
    this
  }

  def go {
    if (guides.isEmpty) {
      return
    } else if (guides.size == 1) {
      guide1 = guides(0)
      ExpressionGuide.go(expression, handler1)
    } else if (guides.size == 2) {
      guide1 = guides(0)
      guide2 = guides(1)
      ExpressionGuide.go(expression, handler2)
    } else if (guides.size == 3) {
      guide1 = guides(0)
      guide2 = guides(1)
      guide3 = guides(2)
      ExpressionGuide.go(expression, handler3)
    } else if (guides.size == 4) {
      guide1 = guides(0)
      guide2 = guides(1)
      guide3 = guides(2)
      guide4 = guides(3)
      ExpressionGuide.go(expression, handler4)
    } else if (guides.size == 5) {
      guide1 = guides(0)
      guide2 = guides(1)
      guide3 = guides(2)
      guide4 = guides(3)
      guide5 = guides(4)
      ExpressionGuide.go(expression, handler5)
    } else {
      ExpressionGuide.go(expression, handlers)
    }
  }

  def handlers(context: GuideContext, expr: IExpression, parent: IExpression) {
    guides.foreach(_(context, expr, parent))
  }

  def handler1(context: GuideContext, expr: IExpression, parent: IExpression) {
    guide1(context, expr, parent)
  }

  def handler2(context: GuideContext, expr: IExpression, parent: IExpression) {
    guide1(context, expr, parent)
    guide2(context, expr, parent)
  }

  def handler3(context: GuideContext, expr: IExpression, parent: IExpression) {
    guide1(context, expr, parent)
    guide2(context, expr, parent)
    guide3(context, expr, parent)
  }

  def handler4(context: GuideContext, expr: IExpression, parent: IExpression) {
    guide1(context, expr, parent)
    guide2(context, expr, parent)
    guide3(context, expr, parent)
    guide4(context, expr, parent)
  }

  def handler5(context: GuideContext, expr: IExpression, parent: IExpression) {
    guide1(context, expr, parent)
    guide2(context, expr, parent)
    guide3(context, expr, parent)
    guide4(context, expr, parent)
    guide5(context, expr, parent)
  }

}