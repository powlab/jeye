package org.powlab.jeye.decode.transformer

import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expra.ExpressionAnalyzator.ExpressionTraversal

class EnumConstructorTransformer extends PostTransformer {

  def transform(expression: IExpression): Unit = {
//    expression.find[MethodExpression](_.declare.isConstructor) collect {
//      case (method, _) =>
//        method.body.deleteWhere[InvokeConstructorExpressioan](m => true)
//        if (method.declare.parametersSeq.size > 3) {
//          method.declare.parametersSeq = method.declare.parametersSeq.drop(2).dropRight(1)
//        } else {
//          //          expression.deleteWhere[MethodExpression](_.signature == method.signature)
//        }
//    }
  }
}