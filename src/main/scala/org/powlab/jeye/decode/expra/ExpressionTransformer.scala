package org.powlab.jeye.decode.expra

import java.util.IdentityHashMap
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.LocalVariable
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.core.Exception.TRANSFORM_AREA
import org.powlab.jeye.core.Exception

/**
 * Трансформер: Логика удаления и замены выражений
 */
class ExpressionTransformer {

  private val removedExps = ArrayBuffer[IExpression]()
  private val replacedExps = new IdentityHashMap[IExpression, IExpression]()
  private val conflictExprs = ArrayBuffer[IExpression]()
  def transform(actions: ArrayBuffer[GuideTransformAction]) {
    val orderedActions = sortActions(actions)
    checkConflicts(orderedActions)
    orderedActions.foreach(process)
  }

  /**
   * Сортировка выражений имеет ключевое значение:
   * Сначала должны трансформироваться саммые 'длинные' ветки от общего НЕизменяемого предка, потом более короткие.
   * Сохранен порядок добавления действий для разных типов
   */
  private def sortActions(actions: ArrayBuffer[GuideTransformAction]): ArrayBuffer[GuideTransformAction] = {
    actions.sortWith((action1: GuideTransformAction, action2: GuideTransformAction) => {
      if (action1.code == GuideTransformAction.TA_REPLACE && action1.expressions.last == action2.expressions.last) {
        if (action1.expressions.size == action2.expressions.size) {
          // Пояснение: если кол-во выражений совпало, проверяем, не содержится ли одно из заменяемых выражений
          // внутри другого? Похоже на хак, а возможно и правильная сортировка.
          if (action2.expressions.contains(action1.expressions.head) &&
              !action1.expressions.contains(action2.expressions.head)) {
            true
          } else {
            action1.id < action2.id
          }
        } else {
          action1.expressions.size > action2.expressions.size
        }
      } else {
        action1.id < action2.id
      }
    })
  }

  private def checkConflicts(actions: ArrayBuffer[GuideTransformAction]) {
    val map = new IdentityHashMap[IExpression, IExpression]()
    actions.filter(_.code == GuideTransformAction.TA_REPLACE).foreach(action => {
      val checkedExpr = action.expressions(0)
      val replaceExpr = action.expressions(1)
      if (map.containsKey(checkedExpr)) {
        if (! conflictExprs.contains(checkedExpr)) {
          conflictExprs += checkedExpr
        }
      } else {
        map.put(checkedExpr, replaceExpr)
      }
    })
  }

  private def process(transformAction: GuideTransformAction) {
    if (transformAction.code == GuideTransformAction.TA_REMOVE) {
      remove(transformAction)
    } else if (transformAction.code == GuideTransformAction.TA_REPLACE) {
      replace(transformAction)
    } else if (transformAction.code == GuideTransformAction.TA_ADD) {
      add(transformAction.asInstanceOf[GuideTransformAddAction])
    } else {
      val reason = "Неизвестное действие с кодом " + transformAction.code + " в трансформере выражений "
      val effect = "Обработать действие с таким кодом невозможно. Трансформация выражений будет остановлена."
      val action = "Необходимо исправить бизнес логику обработки действий над выражениями, добавив обработчик кода " + transformAction.code
      throw Exception(TRANSFORM_AREA, reason, effect, action)
    }
  }

  /**
   * Удалить выражение
   */
  private def remove(action: GuideTransformAction) {
    val expressions = action.expressions
    val parent = expressions.last
    val expression = getExpression(expressions.head)
    removedExps += expression
    parent.asInstanceOf[BlockExpression] -= expression
  }

  /**
   * Заменить - вот тут все гораздо сложнее
   */
  private def replace(action: GuideTransformAction) {
    val expressions = updateExpressions(action.expressions)
    var oldExpression = expressions(0)
    var newExpression = expressions(1)
    expressions.tail.tail.foreach(parent => {
      val result = replace(oldExpression, newExpression, parent)
      oldExpression = parent
      newExpression = result
    })
  }

  private def add(action: GuideTransformAddAction) {
    val expressions = updateExpressions(action.expressions)
    val markerExpression = expressions(0)
    val addExpression = expressions(1)
    val target = expressions(2).asInstanceOf[BlockExpression]
    target.add(markerExpression, addExpression, action.before)
  }

  /**
   * Так как ранее некоторые выражения могли быть заменены, обновляем список этими изменениями
   */
  private def updateExpressions(expressions: ArrayBuffer[IExpression]): ArrayBuffer[IExpression] = {
    expressions.map(expression => {
      val newExpr = replacedExps.get(expression)
      if (newExpr != null) {
        val newExpr2 = replacedExps.get(newExpr)
        if (newExpr2 != null) {
          replacedExps.put(expression, newExpr2)
          newExpr2
        } else {
          newExpr
        }
      } else {
        expression
      }
    })
  }

  private def getExpression(expression: IExpression): IExpression = {
    val replacedExpr = replacedExps.get(expression)
    if (replacedExpr == null) expression else replacedExpr
  }

  /**
   * TODO here: переосмыслить и сделать все выражения заменяемыми
   * Проблемма в том, что например, выражение NULL - находится в 1 экземпляер в системе
   * и ссылочное сравнение при поиске будет выдавать один и тот же результат
   */
  private def markReplaced(exprOld: IExpression, exprNew: IExpression) {
    if (! exprOld.classifier.has(EA_FIXED) && !conflictExprs.contains(exprOld)) {
      replacedExps.put(exprOld, exprNew)
    }
  }

  private def replace(exprOld: IExpression, exprNew: IExpression, parent: IExpression): IExpression = {
    val newParentExpr = recreate(exprOld, exprNew, parent)
    if (exprOld != exprNew) {
      markReplaced(exprOld, exprNew)
    }
    newParentExpr
  }

  private def recreate(exprOld: IExpression, exprNew: IExpression, parent: IExpression): IExpression = {
    if (exprOld == exprNew) {
      return parent
    } else if (parent.isInstanceOf[IUpdatedExpression]) {
      parent.asInstanceOf[IUpdatedExpression](exprOld) = exprNew
      return parent
    } else if (parent.classifier.has(EA_WIDE)) {
      val classifier = parent.classifier
      if (EC_TYPE == classifier) {
        val typedExpr = parent.asInstanceOf[TypedExpression]
        if (typedExpr.name == exprOld) {
          return new TypedExpression(exprOld, typedExpr.descriptor, EC_TYPE)
        }
      } else if (EC_LOCAL_VARIABLE == classifier) {
        val varExpr = parent.asInstanceOf[LocalVariable]
        if (varExpr.name == exprOld) {
          return new LocalVariable(varExpr.index, exprNew, varExpr.argument, varExpr.descriptor)
        }
      } else if (EC_TERNARY_REF == classifier) {
        val ternaryRefExpr = parent.asInstanceOf[TernaryExpressionRef]
        if (ternaryRefExpr.expression == exprOld) {
          ternaryRefExpr.expression = exprNew
        }
        return ternaryRefExpr
      } else if (EC_TERNARY == classifier) {
        val ternaryExpr = parent.asInstanceOf[TernaryExpression]
        if (ternaryExpr.ifExpr == exprOld) {
          return new TernaryExpression(exprNew, ternaryExpr.positiveExpr, ternaryExpr.negativeExpr)
        }
        if (ternaryExpr.positiveExpr == exprOld) {
          return new TernaryExpression(ternaryExpr.ifExpr, exprNew, ternaryExpr.negativeExpr)
        }
        if (ternaryExpr.negativeExpr == exprOld) {
          return new TernaryExpression(ternaryExpr.ifExpr, ternaryExpr.positiveExpr, exprNew)
        }
      } else if (EC_TERNARY_BOOLEAN == classifier) {
        val ternaryBooleanExpr = parent.asInstanceOf[TernaryBooleanExpression]
        if (ternaryBooleanExpr.ifExpr == exprOld) {
          return new TernaryBooleanExpression(exprNew)
        }
      } else if (EC_WHILE_CYCLE == classifier) {
        val whileCycleExpr = parent.asInstanceOf[WhileCycleExpression]
        if (whileCycleExpr.condition == exprOld) {
          return new WhileCycleExpression(exprNew)
        }
      } else if (EC_FOR_CYCLE == classifier) {
        val forExpr = parent.asInstanceOf[ForExpression]
        if (forExpr.initExpr == exprOld) {
          return new ForExpression(exprNew, forExpr.condExpr, forExpr.postExpr)
        }
        if (forExpr.condExpr == exprOld) {
          return new ForExpression(forExpr.initExpr, exprNew, forExpr.postExpr)
        }
        if (forExpr.postExpr == exprOld) {
          return new ForExpression(forExpr.initExpr, forExpr.condExpr, exprNew)
        }
      } else if (EC_FOREACH_CYCLE == classifier) {
        val foreachCycleExpr = parent.asInstanceOf[ForeachExpression]
        if (foreachCycleExpr.varNameExpr == exprOld) {
          return new ForeachExpression(makeTyped(exprNew), foreachCycleExpr.iterableExpr)
        }
        if (foreachCycleExpr.iterableExpr == exprOld) {
          return new ForeachExpression(foreachCycleExpr.varNameExpr, exprNew)
        }
      } else if (EC_IF_WORD == classifier) {
        val ifWordExpr = parent.asInstanceOf[IfWordExpression]
        if (ifWordExpr.ifExpression == exprOld) {
          return new IfWordExpression(exprNew)
        }
      } else if (EC_IF_SIMPLE == classifier) {
        val ifExpr = parent.asInstanceOf[IfSimpleExpression]
        if (ifExpr.left == exprOld) {
          return new IfSimpleExpression(exprNew, ifExpr.sign, ifExpr.right)
        }
        if (ifExpr.sign == exprOld) {
          return new IfSimpleExpression(ifExpr.left, exprNew, ifExpr.right)
        }
        if (ifExpr.right == exprOld) {
          return new IfSimpleExpression(ifExpr.left, ifExpr.sign, exprNew)
        }
      } else if (EC_IF_BOOLEAN == classifier) {
        val ifExpr = parent.asInstanceOf[IfBooleanExpression]
        if (ifExpr.expression == exprOld) {
          return new IfBooleanExpression(exprNew, ifExpr.negate)
        }
      } else if (EC_IF_GROUP == classifier) {
        val ifExpr = parent.asInstanceOf[IfGroupExpression]
        if (ifExpr.sign == exprOld) {
          return new IfGroupExpression(ifExpr.expressions, exprNew)
        }
        val index = ifExpr.expressions.indexOf(exprOld)
        if (index != -1 && exprNew.classifier.has(EA_IFABLE)) {
          val newExprs = ifExpr.expressions.updated(index, exprNew.asInstanceOf[IfExpression])
          return new IfGroupExpression(newExprs, ifExpr.sign)
        }
      } else if (EC_IF_XOR_GROUP == classifier) {
        val ifExpr = parent.asInstanceOf[IfGroupXorExpression]
        val index = ifExpr.expressions.indexOf(exprOld)
        if (index != -1 && exprNew.classifier.has(EA_IFABLE)) {
          val newExprs = ifExpr.expressions.updated(index, exprNew.asInstanceOf[IfExpression])
          return new IfGroupXorExpression(newExprs, ifExpr.negate)
        }
      } else if (EC_IF_CMP == classifier) {
        val ifExpr = parent.asInstanceOf[CmpExpression]
        if (ifExpr.leftExpression == exprOld) {
          return new CmpExpression(exprNew, ifExpr.rightExpression)
        }
        if (ifExpr.rightExpression == exprOld) {
          return new CmpExpression(ifExpr.leftExpression, exprNew)
        }
      } else if (EC_SWITCH == classifier) {
        val switchExpr = parent.asInstanceOf[SwitchExpression]
        if (switchExpr.variable == exprOld) {
          return new SwitchExpression(makeTyped(exprNew))
        }
      } else if (EC_RETURN == classifier) {
        val returnExpr = parent.asInstanceOf[ReturnVarExpression]
        if (returnExpr.variable == exprOld) {
          return new ReturnVarExpression(makeTyped(exprNew))
        }
      } else if (EC_PRIMITIVE_CAST == classifier) {
        val convExpr = parent.asInstanceOf[ConversionExpression]
        if (convExpr.variable == exprOld) {
          return new ConversionExpression(convExpr.baseTypes, convExpr.chain, makeTyped(exprNew))
        }
      } else if (EC_CASE == classifier) {
        val caseExpr = parent.asInstanceOf[CaseExpression]
        if (caseExpr.value == exprOld) {
          return new CaseExpression(exprNew)
        }
      } else if (EC_GET_ARRAY_ITEM == classifier) {
        val getArrayItemExpr = parent.asInstanceOf[GetArrayItemExpression]
        if (getArrayItemExpr.arrayVariable == exprOld) {
          return new GetArrayItemExpression(exprNew, getArrayItemExpr.indexVariable, getArrayItemExpr.descriptor)
        }
        if (getArrayItemExpr.indexVariable == exprOld) {
          return new GetArrayItemExpression(getArrayItemExpr.arrayVariable, exprNew, getArrayItemExpr.descriptor)
        }
      } else if (EC_MATH_PAIR == classifier) {
        val mathPairExpr = parent.asInstanceOf[MathPair]
        if (mathPairExpr.sign == exprOld && exprNew.classifier == EC_SIGN) {
          return new MathPair(exprNew.asInstanceOf[SignExpression], mathPairExpr.variable)
        }
        if (mathPairExpr.variable == exprOld) {
          return new MathPair(mathPairExpr.sign, makeTyped(exprNew))
        }
      } else if (EC_MATH == classifier) {
        val mathExpr = parent.asInstanceOf[MathExpression]
        if (mathExpr.variable == exprOld) {
          return new MathExpression(makeTyped(exprNew), mathExpr.pairs)
        }
        val index = mathExpr.pairs.indexOf(exprOld)
        if (index != -1 && exprNew.classifier == EC_MATH_PAIR) {
          val newExprs = mathExpr.pairs.updated(index, exprNew.asInstanceOf[MathPair])
          return new MathExpression(mathExpr.variable, newExprs)
        }
      } else if (EC_MATH_NEGATE == classifier) {
        val mathNegateExpr = parent.asInstanceOf[NegateExpression]
        if (mathNegateExpr.variable == exprOld) {
          return new NegateExpression(makeTyped(exprNew))
        }
      } else if (EC_MATH_TILDE == classifier) {
        val mathTildeExpr = parent.asInstanceOf[TildeExpression]
        if (mathTildeExpr.variable == exprOld) {
          return new TildeExpression(makeTyped(exprNew))
        }
      } else if (EC_PRE_INC == classifier) {
        val preIncExpr = parent.asInstanceOf[PreIncrementExpression]
        if (preIncExpr.variable == exprOld) {
          return new PreIncrementExpression(makeTyped(exprNew), preIncExpr.constant)
        }
      } else if (EC_POST_INC == classifier) {
        val postIncExpr = parent.asInstanceOf[PostIncrementExpression]
        if (postIncExpr.variable == exprOld) {
          return new PostIncrementExpression(makeTyped(exprNew), postIncExpr.constant)
        }
      } else if (EC_STORE_VAR == classifier) {
        val storeExpr = parent.asInstanceOf[LocalVariableExpression]
        if (storeExpr.variableName == exprOld) {
          return new LocalVariableExpression(exprNew, storeExpr.sign, storeExpr.assignValue, storeExpr.descriptor)
        }
        if (storeExpr.assignValue == exprOld) {
          return new LocalVariableExpression(storeExpr.variableName, storeExpr.sign, exprNew, storeExpr.descriptor)
        }
      } else if (EC_STORE_DECLARE_VAR == classifier) {
        val storeDeclareExpr = parent.asInstanceOf[DeclareLocalVariableExpression]
        if (storeDeclareExpr.variableName == exprOld) {
          return new DeclareLocalVariableExpression(exprNew, storeDeclareExpr.descriptor)
        }
      } else if (EC_STORE_NEW_VAR == classifier) {
        val storeNewExpr = parent.asInstanceOf[NewLocalVariableExpression]
        if (storeNewExpr.variableName == exprOld) {
          return new NewLocalVariableExpression(exprNew, storeNewExpr.assignValue, storeNewExpr.descriptor)
        }
        if (storeNewExpr.assignValue == exprOld) {
          return new NewLocalVariableExpression(storeNewExpr.variableName, exprNew, storeNewExpr.descriptor)
        }
      } else if (EC_STORE_ARRAY_VAR == classifier) {
        val storeArrayExpr = parent.asInstanceOf[SetArrayItemExpression]
        if (storeArrayExpr.arrayVariable == exprOld) {
          return new SetArrayItemExpression(exprNew, storeArrayExpr.indexVariable, storeArrayExpr.sign, storeArrayExpr.assignValue, storeArrayExpr.descriptor)
        }
        if (storeArrayExpr.indexVariable == exprOld) {
          return new SetArrayItemExpression(storeArrayExpr.arrayVariable, exprNew, storeArrayExpr.sign, storeArrayExpr.assignValue, storeArrayExpr.descriptor)
        }
        if (storeArrayExpr.assignValue == exprOld) {
          return new SetArrayItemExpression(storeArrayExpr.arrayVariable, storeArrayExpr.indexVariable, storeArrayExpr.sign, exprNew, storeArrayExpr.descriptor)
        }
      } else if (EC_GET_FIELD == classifier) {
        val getFieldExpr = parent.asInstanceOf[GetFieldExpression]
        if (getFieldExpr.fieldOwner == exprOld) {
          return new GetFieldExpression(exprNew, getFieldExpr.field)
        }
      } else if (EC_GET_STATIC_FIELD == classifier) {
        val getStaticFieldExpr = parent.asInstanceOf[GetStaticFieldExpression]
        if (getStaticFieldExpr.fieldOwner == exprOld) {
          return new GetStaticFieldExpression(exprNew, getStaticFieldExpr.field)
        }
      } else if (EC_PUT_FIELD == classifier) {
        val putFieldExpr = parent.asInstanceOf[PutFieldExpression]
        if (putFieldExpr.fieldOwner == exprOld) {
          return new PutFieldExpression(exprNew, putFieldExpr.sign, putFieldExpr.fieldValue, putFieldExpr.field)
        }
        if (putFieldExpr.fieldValue == exprOld) {
          return new PutFieldExpression(putFieldExpr.fieldOwner, putFieldExpr.sign, exprNew, putFieldExpr.field)
        }
      } else if (EC_PUT_STATIC_FIELD == classifier) {
        val putStaticFieldExpr = parent.asInstanceOf[PutStaticFieldExpression]
        if (putStaticFieldExpr.fieldOwner == exprOld) {
          return new PutStaticFieldExpression(exprNew, putStaticFieldExpr.sign, putStaticFieldExpr.fieldValue, putStaticFieldExpr.field)
        }
        if (putStaticFieldExpr.fieldValue == exprOld) {
          return new PutStaticFieldExpression(putStaticFieldExpr.fieldOwner, putStaticFieldExpr.sign, exprNew, putStaticFieldExpr.field)
        }
      } else if (EC_INSTANCE_OF == classifier) {
        val instanceOfExpr = parent.asInstanceOf[InstanceOfExpression]
        if (instanceOfExpr.objectref == exprOld) {
          return new InstanceOfExpression(exprNew, instanceOfExpr.className)
        }
      } else if (EC_INVOKE_INTERFACE == classifier) {
        val invokeExpr = parent.asInstanceOf[InvokeInterfaceExpression]
        if (invokeExpr.ownerValue == exprOld) {
          return new InvokeInterfaceExpression(exprNew, invokeExpr.arguments, invokeExpr.method)
        }
        if (invokeExpr.arguments == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          return new InvokeInterfaceExpression(invokeExpr.ownerValue, exprNew.asInstanceOf[ArgumentsExpression], invokeExpr.method)
        }
      } else if (EC_INVOKE_VIRTUAL == classifier) {
        val invokeExpr = parent.asInstanceOf[InvokeVirtualExpression]
        if (invokeExpr.ownerValue == exprOld) {
          return new InvokeVirtualExpression(makeTyped(exprNew), invokeExpr.arguments, invokeExpr.method)
        }
        if (invokeExpr.arguments == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          return new InvokeVirtualExpression(invokeExpr.ownerValue, exprNew.asInstanceOf[ArgumentsExpression], invokeExpr.method)
        }
      } else if (EC_CONSTRUCTOR == classifier) {
        val constructorExpr = parent.asInstanceOf[InvokeConstructorExpressioan]
        if (constructorExpr.arguments == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          return new InvokeConstructorExpressioan(constructorExpr.thisClassName, exprNew.asInstanceOf[ArgumentsExpression], constructorExpr.method)
        }
      } else if (EC_NEW_OBJECT == classifier) {
        val newObjectExpr = parent.asInstanceOf[NewObjectExpression]
        if (newObjectExpr.arguments == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          return new NewObjectExpression(newObjectExpr.simpleClassName, exprNew.asInstanceOf[ArgumentsExpression], newObjectExpr.descriptor)
        }
      } else if (EC_NEW_ARRAY == classifier) {
        val newArrayExpr = parent.asInstanceOf[NewArrayExpression]
        val index = newArrayExpr.dimensionValues.indexOf(exprOld)
        if (index != -1 && exprNew.classifier.has(EA_TYPED)) {
          val newExprs = newArrayExpr.dimensionValues.updated(index, exprNew.asInstanceOf[ITypedExpression])
          return new NewArrayExpression(newArrayExpr.componentType, newExprs, newArrayExpr.descriptor)
        }
      } else if (EC_INIT_ARRAY == classifier) {
        val initArrayExpr = parent.asInstanceOf[InitArrayExpression]
        if (initArrayExpr.declare == exprOld) {
          return new InitArrayExpression(makeTyped(exprNew), initArrayExpr.values)
        }
        if (initArrayExpr.values == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          return new InitArrayExpression(initArrayExpr.declare, exprNew.asInstanceOf[ArgumentsExpression])
        }
      } else if (EC_ARRAY_LENGTH == classifier) {
        val arrayLengthExpr = parent.asInstanceOf[ArrayLengthExpression]
        if (arrayLengthExpr.arrayref == exprOld) {
          return new ArrayLengthExpression(makeTyped(exprNew))
        }
      } else if (EC_INVOKE_SPECIAL == classifier) {
        val invokeExpr = parent.asInstanceOf[InvokeSpecialExpression]
        if (invokeExpr.ownerValue == exprOld) {
          return new InvokeSpecialExpression(makeTyped(exprNew), invokeExpr.arguments, invokeExpr.method)
        }
        if (invokeExpr.arguments == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          return new InvokeSpecialExpression(invokeExpr.ownerValue, exprNew.asInstanceOf[ArgumentsExpression], invokeExpr.method)
        }
      } else if (EC_INVOKE_STATIC == classifier) {
        val invokeExpr = parent.asInstanceOf[InvokeStaticExpression]
        if (invokeExpr.arguments == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          return new InvokeStaticExpression(invokeExpr.thisClassName, exprNew.asInstanceOf[ArgumentsExpression], invokeExpr.method)
        }
      } else if (EC_INVOKE_DYNAMIC == classifier) {
        val invokeExpr = parent.asInstanceOf[InvokeDynamicExpression]
        if (invokeExpr.bootstrapArguments == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          val newBootArgs = exprNew.asInstanceOf[ArgumentsExpression]
          return new InvokeDynamicExpression(invokeExpr.bootstrapMethodName, invokeExpr.bootstrapClassName, newBootArgs,
            invokeExpr.methodTypeInvoker, invokeExpr.methodName, invokeExpr.methodArguments, invokeExpr.descriptor)
        }
        if (invokeExpr.methodTypeInvoker == exprOld) {
          return new InvokeDynamicExpression(invokeExpr.bootstrapMethodName, invokeExpr.bootstrapClassName, invokeExpr.bootstrapArguments,
            exprNew, invokeExpr.methodName, invokeExpr.methodArguments, invokeExpr.descriptor)
        }
        if (invokeExpr.methodArguments == exprOld && exprNew.classifier == EC_ARGUMENTS) {
          val newMethodArgs = exprNew.asInstanceOf[ArgumentsExpression]
          return new InvokeDynamicExpression(invokeExpr.bootstrapMethodName, invokeExpr.bootstrapClassName, invokeExpr.bootstrapArguments,
            invokeExpr.methodTypeInvoker, invokeExpr.methodName, newMethodArgs, invokeExpr.descriptor)
        }
      } else if (EC_SYNCHRONIZE == classifier) {
        val syncExpr = parent.asInstanceOf[MonitorEnterExpression]
        if (syncExpr.variable == exprOld) {
          return new MonitorEnterExpression(exprNew)
        }
      } else if (EC_CHECK_CAST == classifier) {
        val castExpr = parent.asInstanceOf[CheckCastExpression]
        if (castExpr.variable == exprOld) {
          return new CheckCastExpression(castExpr.castType, exprNew, castExpr.descriptor)
        }
      } else if (EC_THROW == classifier) {
        val throwExpr = parent.asInstanceOf[ThrowExpression]
        if (throwExpr.variable == exprOld) {
          return new ThrowExpression(exprNew)
        }
      }
    }

    parent
  }

  private def makeTyped(exprNew: IExpression): ITypedExpression = {
    if (exprNew.classifier.has(EA_TYPED)) {
      exprNew.asInstanceOf[ITypedExpression]
    } else {
      new TypedExpression(exprNew, ExpressionHelpers.getDescriptor(exprNew), exprNew.classifier)
    }
  }

}