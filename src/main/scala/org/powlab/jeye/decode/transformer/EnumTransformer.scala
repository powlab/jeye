package org.powlab.jeye.decode.transformer

import org.powlab.jeye.core.AccessFlags.ACC_ENUM
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expra.ExpressionGuide.ExpressionTraversal

class EnumTransformer extends PostTransformer {

  def transform(expression: IExpression): Unit = {
//    expression.find[MethodExpression](exp => {
//      exp.declare.methodName.view() == "static"
//    }) collect {
//      case (static, _) =>
//        val enumFields = expression.filter[FieldDeclareExpression](exp => exp.flags.contains(ACC_ENUM))
//        if (enumFields.nonEmpty) {
//          enumFields.init.foreach(_._1.end = ",")
//          enumFields.last._1.end = ";"
//          enumFields collect {
//            case (fieldDeclare, _) =>
//              static.body.find[PutStaticFieldExpression](bodyExp => {
//                bodyExp.fieldName == fieldDeclare.name
//              }) collect {
//                case (PutStaticFieldExpression(_, _, NewObjectExpression(innerClassName, arguments, _), _, _), _) =>
//                  // По умолчанию 2 параметра, название енума и номер
//                  val argumentsExpression = if (arguments.argumentValues.length > 2) {
//                    Sex("(" + ArgumentsExpression(arguments.paramTypes, arguments.argumentValues.dropRight(2)).view() + ")")
//                  } else {
//                    EMPTY_EXPRESSION
//                  }
//                  expression.find[ClassDeclareExpression](declare => {
//                    declare.className.trim().equalsIgnoreCase(innerClassName.trim())
//                  }) collect {
//                    case (declare, parent: ClassExpression) =>
//                      fieldDeclare.value = Option(new StatementExpression(argumentsExpression) += parent.body)
//                      expression.deleteWhere[ClassExpression](e => {
//                        e.declare.equals(declare)
//                      })
//                  }
//              }
//          }
//        }
//        expression.deleteWhere[MethodExpression](_.declare.methodName == static.declare.methodName)
//    }
  }
}