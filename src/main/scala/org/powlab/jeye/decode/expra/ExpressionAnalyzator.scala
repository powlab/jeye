package org.powlab.jeye.decode.expra

import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception.EXPRA_AREA
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._

import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime.currentMirror
import scala.reflect.runtime.universe._

object ExpressionAnalyzator {

  type ExpressionHandler = (IExpression, IExpression) => Unit

  implicit class ExpressionTraversal(expression: IExpression) {
    def deleteWhere(f: IExpression => Boolean): Unit = deleteWhere[IExpression](f)

    def find(f: IExpression => Boolean): Option[(IExpression, IExpression)] = find[IExpression](f)

    def filter(f: IExpression => Boolean): Array[(IExpression, IExpression)] = filter[IExpression](f)

    def deleteWhere[T <: IExpression : TypeTag](f: T => Boolean): Unit = filter(f).collect {
      case (exp, parent) => parent match {
        case p: BlockExpression =>
          p -= exp
        case _ => None
      }
      case _ => None
    }

    def find[T <: IExpression : TypeTag](f: T => Boolean): Option[(T, IExpression)] = {

      filter(f).headOption
    }

    def filter[T <: IExpression : TypeTag](f: T => Boolean): Array[(T, IExpression)] = {
      val buffer: ArrayBuffer[(T, IExpression)] = ArrayBuffer[(T, IExpression)]()
      scan(expression, (expression, parent) => {
        if (checkType[T](expression) && f(expression.asInstanceOf[T])) {
          buffer += ((expression.asInstanceOf[T], parent))
        }
      })
      buffer.toArray
    }

    def foreach(f: ExpressionHandler) = {
      scan(expression, f)
    }
  }

  /**
   * Обходим все дерево выражений
   * TODO here: оптимизация, заменить рекурсию на стэковую обработку
   */
  def scan(expression: IExpression, handler: ExpressionHandler) {

    def process(expression: IExpression, parent: IExpression) {
      expression match {
        case exprImpl: StatementExpression =>
          process(exprImpl.getBaseExpr, parent)
          exprImpl.expressions.foreach(childExpression => {
            process(childExpression, exprImpl)
          })
        case exprImpl: BlockExpression =>
          exprImpl.expressions.foreach(childExpression => {
            process(childExpression, exprImpl)
          })
        case expr: MethodExpression =>
          handler(expr, parent)
          process(expr.annotations, expr)
          process(expr.declare, expr)
          process(expr.body, expr)
        case expr: FieldExpression =>
          handler(expr, parent)
          process(expr.annotations, expr)
          process(expr.declare, expr)
        case expr: ClassExpression =>
          handler(expr, parent)
          process(expr.annotations, expr)
          process(expr.declare, expr)
          process(expr.body, expr)
        case _ =>
          handler(expression, parent)
      }
    }

    process(expression, EMPTY_EXPRESSION)
  }

  def isTryBlock(expression: IExpression): Boolean = {
    expression.isInstanceOf[StatementExpression] &&
      expression.asInstanceOf[StatementExpression].getBaseExpr == TRY_EXPRESSION
  }

  def get[T: TypeTag](expression: IExpression): Option[T] = {
    if (checkType[T](expression)) {
      Option(expression.asInstanceOf[T])
    } else {
      None
    }
  }

  def cast[T: TypeTag](expression: IExpression): T = {
    if (checkType[T](expression)) {
      expression.asInstanceOf[T]
    } else {
      val reason = "Выражение '" + expression + "' не является типом '" + typeOf[T] + "'"
      val effect = "Приведение к типу '" + typeOf[T] + "' осуществить невозможно. Обработка выражений будет прекращена"
      val action = "Необходимо исправить бизнес логику обработки выражений"
      throw Exception(EXPRA_AREA, reason, effect, action)

    }
  }

  private def checkType[T: TypeTag](value: Any): Boolean = currentMirror.reflect(value).symbol.toType <:< typeOf[T]

}