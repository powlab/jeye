package org.powlab.jeye.decode

import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.{ CaseExpression, CatchExpression, SwitchExpression, _ }
import org.powlab.jeye.utils.DecodeUtils._

import scala.collection.mutable.ListBuffer

/**
 * Простой форматер, основанный на анализе expressions
 */
object Formater {

  private val NEW_LINE = "\n"

  def format(expression: IExpression, parent: IExpression, indent: Int = 0): String = {
    val buffer = new StringBuilder
    formatExpression(expression, parent, buffer, indent)
    buffer.toString
  }

  def formatExpression(expression: IExpression, parent: IExpression, buffer: StringBuilder, indent: Int) {
    if (expression.isInstanceOf[StatementExpression]) {
      val exprImpl = expression.asInstanceOf[StatementExpression]
      if (exprImpl.getBaseExpr.isInstanceOf[SwitchExpression]) {
        formatSwitchExpression(exprImpl, buffer, indent)
      } else {
        formatStatementExpression(exprImpl, buffer, indent)
      }
    } else if (expression.isInstanceOf[ClassExpression]) {
      formatClassExpression(expression.asInstanceOf[ClassExpression], buffer, indent)
    } else if (expression.isInstanceOf[MethodExpression]) {
      formatMethodExpression(expression.asInstanceOf[MethodExpression], buffer, indent)
    } else if (expression.isInstanceOf[BlockExpression]) {
      formatBlockExpression(expression.asInstanceOf[BlockExpression], buffer, indent)
    } else {
      formatSingleExpression(expression, parent, buffer, indent)
    }
  }

  private def formatClassExpression(classExpr: ClassExpression, buffer: StringBuilder, indent: Int) {
    val block = new BlockExpression += classExpr.annotations += (new StatementExpression(classExpr.declare) += classExpr.body)
    formatBlockExpression(block, buffer, indent)
  }

  private def formatMethodExpression(methodExpr: MethodExpression, buffer: StringBuilder, indent: Int) {
    val statement = if (!methodExpr.isAbstract) new StatementExpression(methodExpr.declare) += methodExpr.body else methodExpr.declare
    val block = (new BlockExpression ++= methodExpr.annotations += statement)
    formatBlockExpression(block, buffer, indent)
  }

  private def formatSwitchExpression(switchExpr: StatementExpression, buffer: StringBuilder, indent: Int) {
    val padding = spaces(indent)
    buffer.append(padding).append(switchExpr.getBaseExpr.view(switchExpr)).append(" {").append(NEW_LINE)
    switchExpr.expressions.foreach(expression => {
      var childIndent = 8
      var expr = expression
      if (expression.isInstanceOf[BlockExpression]) {
        val expressions = expression.asInstanceOf[BlockExpression].expressions
        if (expressions.nonEmpty) {
          expr = expressions.head
        }
      }
      if (expr.isInstanceOf[CaseExpression] || expr == SWITCH_DEFAULT_EXPRESSION) {
        childIndent = 4
      }
      formatExpression(expression, switchExpr, buffer, indent + childIndent)
    })
    buffer.append(padding).append("}").append(NEW_LINE)
  }

  private def formatStatementExpression(expression: StatementExpression, buffer: StringBuilder, indent: Int) {
    val padding = spaces(indent)
    formatSingleExpression(expression.getBaseExpr, expression, buffer, indent, false)
    buffer.append(select(buffer.last == ' ', "{", " {")).append(NEW_LINE)
    formatExpressions(expression.expressions, expression, buffer, indent + 4)
    buffer.append(padding).append("}")
    if (expression.hasOutExpr) {
      buffer.append(" ").append(expression.getOutExpr)
    }
    buffer.append(NEW_LINE)
  }

  private def formatBlockExpression(expression: BlockExpression, buffer: StringBuilder, indent: Int) {
    formatExpressions(expression.expressions, expression, buffer, indent)
  }

  private def formatExpressions(expressions: ListBuffer[IExpression], parent: IExpression, buffer: StringBuilder, indent: Int) {
    expressions.foreach(expression => {
      formatExpression(expression, parent, buffer, indent)
    })
  }

  private def formatSingleExpression(expression: IExpression, parent: IExpression,
    buffer: StringBuilder, indent: Int, newLine: Boolean = true) {
    if (keepLine(expression)) {
      buffer.setCharAt(buffer.length - 1, ' ')
      buffer.append(expression.view(parent)).append(' ')
    } else {
      val padding = spaces(indent)
      buffer.append(padding).append(expression.view(parent))
      if (newLine) {
        buffer.append(NEW_LINE)
      }
    }
  }

  private def keepLine(expression: IExpression): Boolean = {
    expression == ELSE_EXPRESSION ||
      expression.isInstanceOf[CatchExpression] ||
      expression == FINALLY_EXPRESSION
  }

}
