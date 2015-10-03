package org.powlab.jeye.decode.transformer

import org.powlab.jeye.decode.expression.IExpression

trait PostTransformer {

  def transform(expression: IExpression)

}
