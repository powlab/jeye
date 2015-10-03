package org.powlab.jeye.decode.transformer

import org.powlab.jeye.decode.expra.ExpressionGuide.Guide
import org.powlab.jeye.decode.expra.ExpressionHelpers.getInvokable
import org.powlab.jeye.decode.expra.GuideContext
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.IExpression
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.core.ClassFile
import org.powlab.jeye.core.ConstantValueAttribute
import org.powlab.jeye.decode.expra.ExpressionHelpers._
import org.powlab.jeye.utils.AttributeUtils
import org.powlab.jeye.decode.expression.MethodExpression
import org.powlab.jeye.core.AccessFlags

/**
 * [scope=class]
 * Трансформер для удаления лишних конструкторов и методов, которые образуются, например,
 * в результате инициализации полей класса, которые уже проинициализированы константным значением
 */
class RemoveRedundantMethodTransformer(classFile: ClassFile) extends Guide {

  def apply(context: GuideContext, expr: IExpression, parent: IExpression) {
    //if (true) return
    val clfE = expr.classifier
    if (clfE == EC_METHOD) {
      // Удаление пустых конструкторов
      val methodExpr = expr.asInstanceOf[MethodExpression]
      val declareExpr = methodExpr.declare
      val methodName = declareExpr.methodName
      if (declareExpr.returnType == EMPTY_EXPRESSION &&
          declareExpr.parametersSeq.isEmpty &&
          (classFile.constantPoolUtils.constructorCount == 1 || methodName == EMPTY_EXPRESSION) &&
          getChildren(methodExpr.body).isEmpty) {
        context.markAsRemoved(expr, parent)
        return
      }
      // удаление синтетических методов.
      if (AccessFlags.isSynthetic(declareExpr.modifiers.accessFlags)) {
        context.markAsRemoved(expr, parent)
      }

    }
  }

}
