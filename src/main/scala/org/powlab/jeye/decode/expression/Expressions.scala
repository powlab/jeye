package org.powlab.jeye.decode.expression

import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.utils.DecodeUtils.getViewType
import scala.collection.mutable.{ ArrayBuffer, ListBuffer }
import scala.collection.mutable.Buffer

/**
 * Требования к наследникам IExpression:
 * 1) Должны быть только ReadOnly, за исключением IUpdatedExpression
 * 2) Не должны содержать бизнес логики (исключая логику построения самого выражения)
 */
object Expressions {

  type Sex = StringExpression
  val Sex = StringExpression

  val EMPTY_EXPRESSION = TypedExpression("", TYPE_OBJECT_DESCRIPTOR, EC_EMPTY)
  val EMPTY_BLOCK_EXPRESSION = new BlockExpression
  val THIS_EXPRESSION = Sex("this")
  val TRY_EXPRESSION = Sex("try")
  val FINALLY_EXPRESSION = Sex("finally")
  val JUMPED_EXPRESSION = Sex("goto")
  val DO_EXPRESSION = Sex("do")
  val ELSE_EXPRESSION = Sex("else")
  val BREAK_EXPRESSION = Sex("break;")
  val CONTINUE_EXPRESSION = Sex("continue;")
  val SWITCH_DEFAULT_EXPRESSION = Sex("default:");
  val EMPTY_RETURN_EXPRESSION = Sex("return;");
  val MONITOR_EXIT_EXPRESSION = Sex("monitor_exit")
  val NULL_EXPRESSION = TypedExpression("null", TYPE_OBJECT_DESCRIPTOR, EC_NULL)
  val STATIC_EXPRESSION = Sex("static")
  val FALSE_EXPRESSION = BooleanLiteralExpression(false)
  val TRUE_EXPRESSION = BooleanLiteralExpression(true)
  val INT_M1_EXPRESSION = IntLiteralExpression(-1)
  val INT_0_EXPRESSION = IntLiteralExpression(0)
  val INT_1_EXPRESSION = IntLiteralExpression(1)
  val INT_2_EXPRESSION = IntLiteralExpression(2)
  val INT_3_EXPRESSION = IntLiteralExpression(3)
  val INT_4_EXPRESSION = IntLiteralExpression(4)
  val INT_5_EXPRESSION = IntLiteralExpression(5)
  val LONG_0_EXPRESSION = LongLiteralExpression(0)
  val LONG_1_EXPRESSION = LongLiteralExpression(1)
  val FLOAT_0_EXPRESSION = FloatLiteralExpression(0.0f)
  val FLOAT_1_EXPRESSION = FloatLiteralExpression(1.0f)
  val FLOAT_2_EXPRESSION = FloatLiteralExpression(2.0f)
  val DOUBLE_0_EXPRESSION = DoubleLiteralExpression(0.0)
  val DOUBLE_1_EXPRESSION = DoubleLiteralExpression(1.0)

}

import org.powlab.jeye.decode.expression.Expressions._

trait IExpression {
  def view(parent: IExpression): String

  def classifier(): ExpressionClassifier

  override def toString = view(EMPTY_EXPRESSION)
}

/**
 * Типизированное выражение
 */
trait ITypedExpression extends IExpression {
  def descriptor(): ParameterDescriptor;
}

/**
 * Контракт изменяемых выражений.
 * Используется для оптимизации при трансформации выражений.
 * Побочный эфект - наследники этого контракта становятся изменяемыми.
 */
trait IUpdatedExpression {
  def update(oldExpression: IExpression, newExpression: IExpression)
}

object TypedExpression {
  def apply(name: String, descriptor: ParameterDescriptor): TypedExpression = {
    TypedExpression(name, descriptor, EC_TYPE)
  }

  def apply(name: String, descriptor: ParameterDescriptor, classifier: ExpressionClassifier): TypedExpression = {
    new TypedExpression(Sex(name), descriptor, classifier)
  }
}

class TypedExpression(val name: IExpression, val descriptor: ParameterDescriptor, val classifier: ExpressionClassifier) extends ITypedExpression {
  def view(parent: IExpression): String = name.view(this)
}

case class StringExpression(value: String) extends IExpression {
  def view(parent: IExpression): String = value

  def apply(value: String) = StringExpression(value)

  def classifier(): ExpressionClassifier = EC_WORD
}

case class SignExpression(sign: String, val prior: Int) extends IExpression {
  def view(parent: IExpression): String = " " + sign + " "

  def classifier(): ExpressionClassifier = EC_SIGN
}

/**
 * spliter может принимать значение '+'
 */
class LineExpression(val expressions: Buffer[IExpression], val spliter: String = " ") extends IExpression with IUpdatedExpression {

  def this() = this(new ArrayBuffer[IExpression])

  def +=(that: IExpression): LineExpression = {
    expressions += that
    this
  }

  def update(oldExpression: IExpression, newExpression: IExpression) {
    val index = expressions.indexOf(oldExpression)
    if (index != -1) {
      expressions.update(index, newExpression)
    }
  }

  override def view(parent: IExpression): String = expressions.map(_.view(this)).mkString(spliter)

  def classifier(): ExpressionClassifier = EC_LINE
}

case class ThrowsExpression(exceptions: Seq[String]) extends IExpression {
  override def view(parent: IExpression): String = exceptions.mkString("throws ", ", ", "")

  override def classifier(): ExpressionClassifier = EC_THROWS_EXPRESSION
}

class BlockExpression extends IExpression with IUpdatedExpression {
  val expressions = new ListBuffer[IExpression]()

  def +=(that: IExpression): BlockExpression = {
    expressions += that
    this
  }

  def ++=(exps: IExpression*): BlockExpression = {
    expressions ++= exps
    this
  }

  def ++=(exps: List[IExpression]): BlockExpression = {
    expressions ++= exps
    this
  }

  def -=(expression: IExpression): BlockExpression = {
    expressions -= expression
    this
  }

  def asList = expressions.toList

  def view(parent: IExpression): String = expressions.map(_.view(this)).filter(!_.isEmpty).mkString("\n")

  def classifier(): ExpressionClassifier = EC_BLOCK

  def update(oldExpression: IExpression, newExpression: IExpression) {
    val index = expressions.indexOf(oldExpression)
    if (index != -1) {
      expressions.remove(index)
      expressions.insert(index, newExpression)
    }
  }

  def add(markerExpression: IExpression, addExpression: IExpression, before: Boolean) {
    val index = expressions.indexOf(markerExpression)
    if (index != -1) {
      if (before) {
        expressions.insert(index, addExpression)
      } else if (index == expressions.size - 1) {
        expressions += addExpression
      } else {
        expressions.insert(index + 1, addExpression)
      }
    }
  }

  def count(): Int = expressions.size

}

class StatementExpression(expression: IExpression, postExpression: IExpression = EMPTY_EXPRESSION) extends BlockExpression {
  private var baseExpr = expression
  private var outExpr = postExpression

  override def view(parent: IExpression): String = {
    baseExpr.view(this) + " {\n" + super.view(this) + "\n}" + outExpr
  }

  def getBaseExpr = baseExpr

  def getOutExpr = outExpr

  def hasOutExpr: Boolean = outExpr != EMPTY_EXPRESSION

  override def update(oldExpression: IExpression, newExpression: IExpression) {
    if (oldExpression == baseExpr) {
      baseExpr = newExpression
    } else if (oldExpression == outExpr) {
      outExpr = newExpression
    } else {
      super.update(oldExpression, newExpression)
    }
  }

  override def count(): Int = 1 + expressions.size + (if (hasOutExpr) 1 else 0)

  override def classifier(): ExpressionClassifier = EC_STATEMENT
}

case class CharLiteralExpression(val value: Int) extends ITypedExpression {
  def view(parent: IExpression): String = {
    if (value == 8) {
      return "'\\b'" // Backspace
    }
    if (value == 9) {
      return "'\\t'" // Tab
    }
    if (value == 10) {
      return "'\\n'" // Newline
    }
    if (value == 12) {
      return "'\\f'" // Formfeed
    }
    if (value == 13) {
      return "'\\r'" // Carriage return
    }
    if (value == 39) {
      return "'\\''" // Single quotation mark
    }
    if (value == 92) {
      return "'\\\\'" // Double quotation mark
    }
    if (value >= 32 && value < 127) {
      return "'" + value.toChar + "'"
    }
    value.toString
  }

  def descriptor(): ParameterDescriptor = TYPE_CHAR_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_CHAR
}

object IntLiteralExpression {
  def apply(value: Int, intView: String): IntLiteralExpression = new IntLiteralExpression(value, intView)
  def apply(value: Int): IntLiteralExpression = apply(value, value.toString)

}

class IntLiteralExpression(val value: Int, intView: String) extends ITypedExpression {
  def view(parent: IExpression): String = intView

  def descriptor(): ParameterDescriptor = TYPE_INT_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_INT
}

case class ShortLiteralExpression(val value: Int) extends ITypedExpression {
  def view(parent: IExpression): String = value.toString

  def descriptor(): ParameterDescriptor = TYPE_SHORT_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_SHORT
}

case class ByteLiteralExpression(val value: Int) extends ITypedExpression {
  def view(parent: IExpression): String = value.toString

  def descriptor(): ParameterDescriptor = TYPE_BYTE_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_BYTE
}

case class BooleanLiteralExpression(val value: Boolean) extends ITypedExpression {
  def view(parent: IExpression): String = value.toString

  def descriptor(): ParameterDescriptor = TYPE_BOOLEAN_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_BOOLEAN
}

object LongLiteralExpression {
  def apply(value: Long, longView: String): LongLiteralExpression = new LongLiteralExpression(value, longView)
  def apply(value: Long): LongLiteralExpression = apply(value, s"${value}L")
}

class LongLiteralExpression(val value: Long, longView: String) extends ITypedExpression {
  def view(parent: IExpression): String = longView

  def descriptor(): ParameterDescriptor = TYPE_LONG_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_LONG
}

object DoubleLiteralExpression {
  def apply(value: Double, doubleView: String): DoubleLiteralExpression = new DoubleLiteralExpression(value, doubleView)
  def apply(value: Double): DoubleLiteralExpression = apply(value, value.toString)
}

class DoubleLiteralExpression(val value: Double, doubleView: String) extends ITypedExpression {
  def view(parent: IExpression): String = doubleView

  def descriptor(): ParameterDescriptor = TYPE_DOUBLE_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_DOUBLE
}

case class StringLiteralExpression(value: String) extends ITypedExpression {
  def view(parent: IExpression): String = "\"" + value.replace("\"", "\\\"") + "\""

  def descriptor(): ParameterDescriptor = TYPE_STRING_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_STRING
}

case class FloatLiteralExpression(value: Float) extends ITypedExpression {
  def view(parent: IExpression): String = s"${value}f"

  def descriptor(): ParameterDescriptor = TYPE_FLOAT_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_FLOAT
}

case class ClassLiteralExpression(descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = s"${getViewType(descriptor.meta)}.class"

  def classifier(): ExpressionClassifier = EC_CLASS_CONST
}

