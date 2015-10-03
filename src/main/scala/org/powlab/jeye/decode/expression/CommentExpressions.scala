package org.powlab.jeye.decode.expression

import org.powlab.jeye.decode.expression.ExpressionClassifiers.EС_COMMENT

object CommentExpression {
  def apply(text: String, partLine: Boolean = false): IExpression = {
    if (partLine) {
      new PartLineCommentExpression(text)
    } else {
      new LineCommentExpression(text)
    }
  }
}

class LineCommentExpression(val text: String) extends IExpression {
  def view(parent: IExpression): String = "// " + text

  def classifier(): ExpressionClassifier = EС_COMMENT
}

class PartLineCommentExpression(val text: String) extends IExpression {
  def view(parent: IExpression): String = "/* " + text + " */"

  def classifier(): ExpressionClassifier = EС_COMMENT
}