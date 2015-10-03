package org.powlab.jeye.decode.expression

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.ListBuffer
import org.powlab.jeye.core._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.ExpressionViewer._
import org.powlab.jeye.utils.DecodeUtils.select
import java.util.IdentityHashMap

/**
 * Math-Выражения
 *
 */
object MathExpression {
  val MAX_PRIORITY = 15

  val SIGN_ADD = SignExpression("+", 12)
  val SIGN_SUB = SignExpression("-", 12)
  val SIGN_MUL = SignExpression("*", 13)
  val SIGN_DIV = SignExpression("/", 13)
  val SIGN_REM = SignExpression("%", 13)
  val SIGN_SHL = SignExpression("<<", 11)
  val SIGN_SHR = SignExpression(">>", 11)
  val SIGN_USHR = SignExpression(">>>", 11)
  val SIGN_AND = SignExpression("&", 8)
  val SIGN_OR = SignExpression("|", 7)
  val SIGN_XOR = SignExpression("^", 6)

  val SIGN_ASSIGN = SignExpression("=", 1)
  val SIGN_ADD_ASSIGN = SignExpression("+=", 1)
  val SIGN_SUB_ASSIGN = SignExpression("-=", 1)
  val SIGN_MUL_ASSIGN = SignExpression("*=", 1)
  val SIGN_DIV_ASSIGN = SignExpression("/=", 1)
  val SIGN_REM_ASSIGN = SignExpression("%=", 1)
  val SIGN_XOR_ASSIGN = SignExpression("^=", 1)
  val SIGN_ASSIGNABLE_MAP = new IdentityHashMap[SignExpression, SignExpression]
  SIGN_ASSIGNABLE_MAP.put(SIGN_ADD, SIGN_ADD_ASSIGN)
  SIGN_ASSIGNABLE_MAP.put(SIGN_SUB, SIGN_SUB_ASSIGN)
  SIGN_ASSIGNABLE_MAP.put(SIGN_MUL, SIGN_MUL_ASSIGN)
  SIGN_ASSIGNABLE_MAP.put(SIGN_DIV, SIGN_DIV_ASSIGN)
  SIGN_ASSIGNABLE_MAP.put(SIGN_REM, SIGN_REM_ASSIGN)
  SIGN_ASSIGNABLE_MAP.put(SIGN_XOR, SIGN_XOR_ASSIGN)

  def apply(variableLeft: ITypedExpression, sign: SignExpression, variableRight: ITypedExpression): MathExpression = {
    if (variableLeft.isInstanceOf[MathExpression] && !variableRight.isInstanceOf[MathExpression]) {
      // 1. Пример: (1 - 2 + 3 - 4) + 5 => (1 - 2 + 3 - 4 + 5)
      val math = variableLeft.asInstanceOf[MathExpression]
      if (math.prior == sign.prior) {
        return new MathExpression(math.variable, math.pairs ++ Array(MathPair(sign, variableRight)))
      }
    } else if (!variableLeft.isInstanceOf[MathExpression] && variableRight.isInstanceOf[MathExpression]) {
      // 2. Пример: 1 - (2 + 3 - 4 + 5) => (1 - 2 + 3 - 4 + 5)
      val math = variableRight.asInstanceOf[MathExpression]
      if (math.prior == sign.prior) {
        return new MathExpression(variableLeft, Array(MathPair(sign, math.variable)) ++ math.pairs)
      }
    } else if (variableLeft.isInstanceOf[MathExpression] && variableRight.isInstanceOf[MathExpression]) {
      // 3. Пример: (1 - 2) + (3 - 4 + 5) => (1 - 2 + 3 - 4 + 5)
      val mathLeft = variableLeft.asInstanceOf[MathExpression]
      val mathRight = variableRight.asInstanceOf[MathExpression]
      if (mathLeft.prior == sign.prior && sign.prior == mathRight.prior) {
        return new MathExpression(mathLeft.variable,
          mathLeft.pairs ++ Array(MathPair(sign, mathRight.variable)) ++ mathRight.pairs)
      }
    }
    new MathExpression(variableLeft, Array(MathPair(sign, variableRight)))
  }
}


class MathExpression(val variable: ITypedExpression, val pairs: Array[MathPair]) extends ITypedExpression {
  def sign: SignExpression = pairs.head.sign
  def prior: Int = sign.prior
  def view(parent: IExpression): String = {
    val baseView = variable.view(this) + pairs.map(_.view(this)).mkString("")
    val parentProir = if (parent.classifier == EC_MATH) {
      parent.asInstanceOf[MathExpression].prior
    } else {
      -1
    }
    correctBracketsOnly(baseView, parentProir > prior, parent.classifier, EC_IF_WORD,
        EC_PRIMITIVE_CAST, EC_IF_SIMPLE, EC_LINE)
  }
  def descriptor(): ParameterDescriptor = variable.descriptor

  def classifier(): ExpressionClassifier = EC_MATH
}

case class MathPair(sign: SignExpression, variable: ITypedExpression) extends IExpression {
  def view(parent: IExpression): String = sign.view(this) + variable.view(parent)
  def classifier(): ExpressionClassifier = EC_MATH_PAIR
}

class NegateExpression(val variable: ITypedExpression) extends ITypedExpression {
  def view(parent: IExpression): String = "-" + variable.view(this)
  def descriptor(): ParameterDescriptor = variable.descriptor

  def classifier(): ExpressionClassifier = EC_MATH_NEGATE
}

class TildeExpression(val variable: ITypedExpression) extends ITypedExpression {
  def view(parent: IExpression): String = "~" + variable.view(this)
  def descriptor(): ParameterDescriptor = variable.descriptor

  def classifier(): ExpressionClassifier = EC_MATH_TILDE
}

/**
 * TODO here: разбить expressions
 */
class PreIncrementExpression(val variable: ITypedExpression, val constant: Int) extends ITypedExpression {
  def view(parent: IExpression): String = {
    var view = variable.view(this)
    if (constant == 1) {
      view = "++" + view
    } else if (constant == -1) {
      view = "--" + view
    } else if (constant > 1) {
      view = view + " += " + constant
    } else if (constant > 1) {
      view = view + " -= " + constant
    }
    if (constant == -1 || constant == 1) {
      correctFlush(view, parent.classifier)
    } else {
      correctView(view, parent.classifier, EC_PUT_FIELD)
    }
  }
  def descriptor(): ParameterDescriptor = variable.descriptor
  def classifier(): ExpressionClassifier = EC_PRE_INC
}

/**
 * TODO here: разбить expressions
 */
class PostIncrementExpression(val variable: ITypedExpression, val constant: Int) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val baseView = variable.view(this) + select(constant == 1, "++", "--")
    correctFlush(baseView, parent.classifier)
  }
  def descriptor(): ParameterDescriptor = variable.descriptor
  def classifier(): ExpressionClassifier = EC_POST_INC
}

