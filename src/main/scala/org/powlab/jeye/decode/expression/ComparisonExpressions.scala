package org.powlab.jeye.decode.expression

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.ExpressionViewer._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._

/**
 * If-Выражения
 *
 * TODO here: вынести все классы из object
 */
object ComparisonExpressions {

  val SIGN_EQ = SignExpression("==", 9)
  val SIGN_NE = SignExpression("!=", 9)
  val SIGN_GE = SignExpression(">=", 10)
  val SIGN_GT = SignExpression(">", 10)
  val SIGN_LT = SignExpression("<", 10)
  val SIGN_LE = SignExpression("<=", 10)

  val SIGN_GROUP_OR = SignExpression("||", 5)
  val SIGN_GROUP_AND = SignExpression("&&", 4)
  val SIGN_GROUP_XOR = SignExpression("^", 6)

  def reverseSign(sign: IExpression): IExpression = {
    sign match {
      case SIGN_EQ => SIGN_NE
      case SIGN_NE => SIGN_EQ
      case SIGN_GE => SIGN_LT
      case SIGN_LT => SIGN_GE
      case SIGN_GT => SIGN_LE
      case SIGN_LE => SIGN_GT
      case SIGN_GROUP_OR => SIGN_GROUP_AND
      case SIGN_GROUP_AND => SIGN_GROUP_OR
      case _ => throw new IllegalStateException("Unknown sign -> " + sign.view(EMPTY_EXPRESSION))
    }
  }

}

trait IfExpression extends ITypedExpression {
  def reverse: IfExpression
  def descriptor(): ParameterDescriptor = TYPE_BOOLEAN_DESCRIPTOR
}

class IfSimpleExpression(val left: IExpression, val sign: IExpression, val right: IExpression) extends IfExpression {

  def reverse(): IfSimpleExpression = {
    new IfSimpleExpression(left, ComparisonExpressions.reverseSign(sign), right)
  }

  def view(parent: IExpression): String = left.view(this) + sign + right.view(this)

  def classifier(): ExpressionClassifier = EC_IF_SIMPLE
}

class IfBooleanExpression(val expression: IExpression, val negate: Boolean) extends IfExpression {

  def reverse(): IfBooleanExpression = new IfBooleanExpression(expression, !negate)

  def view(parent: IExpression): String = negate match {
    case false => expression.view(parent)
    case true => "!" + expression.view(parent)
  }

  def classifier(): ExpressionClassifier = EC_IF_BOOLEAN
}

class IfGroupExpression(val expressions: ArrayBuffer[IfExpression], val sign: IExpression) extends IfExpression {

  def reverse(): IfGroupExpression = {
    new IfGroupExpression(expressions.map(_.reverse), ComparisonExpressions.reverseSign(sign))
  }

  def view(parent: IExpression): String = {
    val baseView = expressions.map(_.view(this)).mkString(sign.view(this))
    correctBracketsOnly(baseView, false, parent.classifier, EC_IF_GROUP, EC_IF_XOR_GROUP)
  }

  def classifier(): ExpressionClassifier = EC_IF_GROUP
}

import org.powlab.jeye.decode.expression.ComparisonExpressions.SIGN_GROUP_XOR

class IfGroupXorExpression(val expressions: ArrayBuffer[IfExpression], val negate: Boolean = false) extends IfExpression {

  def reverse(): IfGroupXorExpression = new IfGroupXorExpression(expressions, !negate)

  def view(parent: IExpression): String = {
    var baseView = expressions.map(_.view(this)).mkString(SIGN_GROUP_XOR.view(this))
    if (negate) {
      baseView = "!(" + baseView + ")"
    }
    correctBracketsOnly(baseView, false, parent.classifier, EC_IF_GROUP, EC_IF_XOR_GROUP)
  }

  def classifier(): ExpressionClassifier = EC_IF_XOR_GROUP
}

class CmpExpression(val leftExpression: IExpression, val rightExpression: IExpression) extends ITypedExpression {
  def view(parent: IExpression): String = {
    String.format("(%s > %s ? 1 : %s == %s ? 0 : -1)", leftExpression.view(this), rightExpression.view(this), leftExpression.view(this), rightExpression.view(this))
  }
  def descriptor(): ParameterDescriptor = TYPE_INT_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_IF_CMP
}

class IfWordExpression(val ifExpression: IExpression) extends ITypedExpression {
  def view(parent: IExpression): String = {
    "if (" + ifExpression.view(this) + ")"
  }

  def classifier(): ExpressionClassifier = EC_IF_WORD

  def descriptor(): ParameterDescriptor = TYPE_BOOLEAN_DESCRIPTOR

//  private def wrapWithBrackets(expression: IExpression): String = {
//    if (expression.isInstanceOf[IfSimpleExpression] || expression.isInstanceOf[IfBooleanExpression]) {
//      "(" + expression.view(this) + ")"
//    } else {
//      expression.view(this)
//    }
//  }
}