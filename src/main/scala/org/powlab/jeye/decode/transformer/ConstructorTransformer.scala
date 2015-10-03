package org.powlab.jeye.decode.transformer

import org.powlab.jeye.decode.expra.ExpressionGuide.Guide
import org.powlab.jeye.decode.expra.ExpressionHelpers.getInvokable
import org.powlab.jeye.decode.expra.GuideContext
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.IExpression
import org.powlab.jeye.core.ClassFile
import org.powlab.jeye.core.ConstantValueAttribute
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.utils.AttributeUtils
import org.powlab.jeye.decode.expression.InvokeConstructorExpressioan

/**
 * Трансформер конструктора. Вырезаем лишние элементы, например вызов super()
 */
class ConstructorTransformer(classFile: ClassFile) extends Guide {

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
    //if (true) return
    val clfE = expr.classifier
    // super()
    if (clfE == EC_CONSTRUCTOR) {
      val invokableExpr = expr.asInstanceOf[InvokeConstructorExpressioan]
      if (! invokableExpr.isThis && invokableExpr.arguments.argumentValues.isEmpty) {
        context.markAsRemoved(expr, parent)
      }
      val exprs = getChildren(parent)
      val position = exprs.indexOf(invokableExpr)
      // если позиция выражения super не первая - то это внутренний класс и нужно вычистить хлам
      if (position != -1 && position != 0) {
        var index = 0;
        while (index < position) {
          context.markAsRemoved(exprs(index), parent)
          index += 1
        }
      }
    }
    // уже проинициализирован
    if (clfE == EC_PUT_FIELD && classFile.fields_count > 0) {
      val field = getFieldInstructionData(expr)
      if (hasFieldConstant(field.name)) {
        context.markAsRemoved(expr, parent)
      }
    }
  }

  private def hasFieldConstant(fieldName: String): Boolean = {
    val fieldOpt = classFile.fields.find(field => {
      fieldName == classFile.constantPoolUtils.getUtf8(field.name_index)
    })
    fieldOpt.isDefined && AttributeUtils.has[ConstantValueAttribute](fieldOpt.get.attributes)
  }

}
