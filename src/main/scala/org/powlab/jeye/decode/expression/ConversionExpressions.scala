package org.powlab.jeye.decode.expression

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types._
import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import scala.collection.immutable.Stream

object ConversionExpression {
  private val WEIGHTS: Map[BaseType, Int] = Map(
    TYPE_BOOLEAN -> 0,
    TYPE_BYTE -> 1,
    TYPE_CHAR -> 2,
    TYPE_SHORT -> 2,
    TYPE_INT -> 3,
    TYPE_FLOAT -> 3,
    TYPE_LONG -> 4,
    TYPE_DOUBLE -> 4)
  def apply(baseType: BaseType, expr: ITypedExpression, chain: Boolean = false): ITypedExpression = {
    if (expr.isInstanceOf[ConversionExpression]) {
      val singleCast = expr.asInstanceOf[ConversionExpression]
      new ConversionExpression(Stream.cons(baseType, singleCast.baseTypes), chain, singleCast.variable)
    } else {
      new ConversionExpression(Stream(baseType), chain, expr)
    }
  }

  def min(baseTypes: Stream[BaseType]): BaseType = {
    baseTypes.minBy(WEIGHTS(_))
  }
}

class ConversionExpression(val baseTypes: Stream[BaseType], val chain: Boolean, val variable: ITypedExpression) extends ITypedExpression {
  val targetType = ConversionExpression.min(baseTypes)
  // TODO here: вот они, не read-only к чему приводят! Кэшировать здесь view нельзя
  def view(parent: IExpression): String = if (chain) {
    baseTypes.map("(" + _.description + ") ").mkString("") + variable.view(this)
  } else {
    "(" + targetType.description + ") " + variable.view(this)
  }
  def descriptor(): ParameterDescriptor = getBaseTypeDescriptor(targetType)
  def classifier(): ExpressionClassifier = EC_PRIMITIVE_CAST
}