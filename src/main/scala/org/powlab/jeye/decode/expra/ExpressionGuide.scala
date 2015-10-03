package org.powlab.jeye.decode.expra

import java.util.IdentityHashMap
import scala.collection.mutable.ArrayBuffer
import scala.reflect.runtime._
import scala.reflect.runtime.universe._
import org.powlab.jeye.decode.LocalVariable
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.utils.DecodeUtils
import org.powlab.jeye.core.Exception.GUIDE_AREA
import org.powlab.jeye.core.Exception

/**
 * Путеводитель по выражениям: обход вглубь и вширь
 */
object ExpressionGuide {

  type Guide = (GuideContext, IExpression, IExpression) => Unit
  type Node[T <: IExpression] = (T, IExpression)

  implicit class ExpressionTraversal(expression: IExpression) {
    def deleteWhere(f: IExpression => Boolean): Unit = deleteWhere[IExpression](f)

    def find(f: IExpression => Boolean): Option[Node[IExpression]] = find[IExpression](f)

    def filter(f: IExpression => Boolean): Array[Node[IExpression]] = filter[IExpression](f)

    def deleteWhere[T <: IExpression : TypeTag](f: T => Boolean): Unit = filter(f).collect {
      case (exp, parent) => parent match {
        case p: BlockExpression =>
          p -= exp
        case _ => None
      }
      case _ => None
    }

    def find[T <: IExpression : TypeTag](f: T => Boolean): Option[Node[T]] = {
      filter(f).headOption
    }

    def filter[T <: IExpression : TypeTag](f: T => Boolean): Array[Node[T]] = {
      val buffer: ArrayBuffer[(T, IExpression)] = ArrayBuffer[Node[T]]()
      go(expression, (context, expression, parent) => {
        if (checkType[T](expression) && f(expression.asInstanceOf[T])) {
          buffer += ((expression.asInstanceOf[T], parent))
        }
      })
      buffer.toArray
    }

    def foreach(f: Guide) = {
      go(expression, f)
    }

    private def checkType[T: TypeTag](value: Any): Boolean = currentMirror.reflect(value).symbol.toType <:< typeOf[T]
  }

  def go(root: IExpression, handler: Guide) {
    new ExpressionGuide(root, handler).go
  }

}

/** Путеводитель по выражениям */
class ExpressionGuide(root: IExpression, handler: ExpressionGuide.Guide) {
  private val context = new GuideContext
  def go() {
    operate(root, EMPTY_EXPRESSION)
    transform()
  }

  /**
   * Обход все выражений
   */
  private def operate(expression: IExpression, parent: IExpression) {
    // TODO: debug only
    //println((DecodeUtils.pad("" + expression.classifier.head, 20),
    //         DecodeUtils.pad("" + parent.classifier.head, 20), expression.view(parent).replace("\n", "")))
    // установлен режим преращения обработки
    if (context.getState == GuideState.GS_STOP) {
      return
    }
    context.link(expression, parent)
    handler(context, expression, parent)
    context.getStep(parent).next
    val classifier = expression.classifier
    if (classifier.has(EA_WIDE)) {
      wide(expression, parent)
    }
    if (classifier.has(EA_DEPTH)) {
      context.incDepth(1)
      depth(expression, parent)
      context.incDepth(-1)
    }
    context.updateParent(expression, parent)
    context.removeLink(expression)
  }

  private def wide(expression: IExpression, parent: IExpression) {
    val classifier = expression.classifier
    if (classifier.is(EA_TYPED)) {
      context.singleStep(expression)
      val typedExpr = expression.asInstanceOf[TypedExpression]
      operate(typedExpr.name, typedExpr)
      context.removeStep(expression)
    } else if (classifier == EC_LINE) {
      val lineExpr = expression.asInstanceOf[LineExpression]
      val expressions = lineExpr.expressions
      if (expressions.nonEmpty) {
        context.newStep(expressions.size, expression)
        expressions.foreach(operate(_, lineExpr))
        context.removeStep(expression)
      }
    } else if (classifier == EC_CLASS) {
      context.newStep(3, expression)
      val classExpr = expression.asInstanceOf[ClassExpression]
      operate(classExpr.annotations, classExpr)
      operate(classExpr.declare, classExpr)
      operate(classExpr.body, classExpr)
      context.removeStep(expression)
    } else if (classifier == EC_METHOD) {
      context.newStep(4, expression)
      val methodExpr = expression.asInstanceOf[MethodExpression]
      operate(methodExpr.annotations, methodExpr)
      operate(methodExpr.declare, methodExpr)
      operate(methodExpr.body, methodExpr)
      // признак конца метода для трансформеров
      operate(EMPTY_EXPRESSION, methodExpr)
      context.removeStep(expression)
    } else if (classifier == EC_METHOD_DECLARE) {
      context.newStep(6, expression)
      val methodDeclareExpr = expression.asInstanceOf[MethodDeclareExpression]
      operate(methodDeclareExpr.modifiers, methodDeclareExpr)
      operate(methodDeclareExpr.typeParameters, methodDeclareExpr)
      operate(methodDeclareExpr.returnType, methodDeclareExpr)
      operate(methodDeclareExpr.methodName, methodDeclareExpr)
      operate(methodDeclareExpr.throwsExpression, methodDeclareExpr)
      operate(methodDeclareExpr.default, methodDeclareExpr)
      context.removeStep(expression)
    } else if (classifier == EC_FIELD) {
      context.newStep(2, expression)
      val fieldExpr = expression.asInstanceOf[FieldExpression]
      operate(fieldExpr.annotations, fieldExpr)
      operate(fieldExpr.declare, fieldExpr)
      context.removeStep(expression)
    } else if (classifier == EC_FIELD_DECLARE) {
      context.newStep(5, expression)
      val fieldDeclareExpr = expression.asInstanceOf[FieldDeclareExpression]
      operate(fieldDeclareExpr.fieldName, fieldDeclareExpr)
      operate(fieldDeclareExpr.modifiers, fieldDeclareExpr)
      operate(fieldDeclareExpr.constant, fieldDeclareExpr)
      operate(fieldDeclareExpr.signature, fieldDeclareExpr)
      operate(fieldDeclareExpr.value, fieldDeclareExpr)
      context.removeStep(expression)
    } else if (classifier == EC_ARRAY_LITERAL) {
      val annArrayExpr = expression.asInstanceOf[AnnotationArrayExpression]
      val expressions = annArrayExpr.expressions
      if (expressions.nonEmpty) {
        context.newStep(expressions.size, expression)
        expressions.foreach(operate(_, annArrayExpr))
        context.removeStep(expression)
      }
    } else if (classifier == EC_LOCAL_VARIABLE) {
      context.singleStep(expression)
      val varExpr = expression.asInstanceOf[LocalVariable]
      operate(varExpr.name, varExpr)
      context.removeStep(expression)
    } else if (classifier == EC_TERNARY_REF) {
      context.singleStep(expression)
      val ternaryRefExpr = expression.asInstanceOf[TernaryExpressionRef]
      operate(ternaryRefExpr.expression, ternaryRefExpr)
      context.removeStep(expression)
    } else if (classifier == EC_TERNARY) {
      context.newStep(3, expression)
      val ternaryExpr = expression.asInstanceOf[TernaryExpression]
      operate(ternaryExpr.ifExpr, ternaryExpr)
      operate(ternaryExpr.positiveExpr, ternaryExpr)
      operate(ternaryExpr.negativeExpr, ternaryExpr)
      context.removeStep(expression)
    } else if (classifier == EC_TERNARY_BOOLEAN) {
      context.singleStep(expression)
      val ternaryBooleanExpr = expression.asInstanceOf[TernaryBooleanExpression]
      operate(ternaryBooleanExpr.ifExpr, ternaryBooleanExpr)
      context.removeStep(expression)
    } else if (classifier == EC_WHILE_CYCLE) {
      context.singleStep(expression)
      val whileCycleExpr = expression.asInstanceOf[WhileCycleExpression]
      operate(whileCycleExpr.condition, whileCycleExpr)
      context.removeStep(expression)
    } else if (classifier == EC_FOR_CYCLE) {
      context.newStep(3, expression)
      val forCycleExpr = expression.asInstanceOf[ForExpression]
      operate(forCycleExpr.initExpr, forCycleExpr)
      operate(forCycleExpr.condExpr, forCycleExpr)
      operate(forCycleExpr.postExpr, forCycleExpr)
      context.removeStep(expression)
    } else if (classifier == EC_FOREACH_CYCLE) {
      context.newStep(2, expression)
      val foreachCycleExpr = expression.asInstanceOf[ForeachExpression]
      operate(foreachCycleExpr.varNameExpr, foreachCycleExpr)
      operate(foreachCycleExpr.iterableExpr , foreachCycleExpr)
      context.removeStep(expression)
    } else if (classifier == EC_IF_WORD) {
      context.singleStep(expression)
      val ifWordExpr = expression.asInstanceOf[IfWordExpression]
      operate(ifWordExpr.ifExpression, ifWordExpr)
      context.removeStep(expression)
    } else if (classifier == EC_IF_SIMPLE) {
      context.newStep(3, expression)
      val ifExpr = expression.asInstanceOf[IfSimpleExpression]
      operate(ifExpr.left, ifExpr)
      operate(ifExpr.sign, ifExpr)
      operate(ifExpr.right, ifExpr)
      context.removeStep(expression)
    } else if (classifier == EC_IF_BOOLEAN) {
      context.singleStep(expression)
      val ifExpr = expression.asInstanceOf[IfBooleanExpression]
      operate(ifExpr.expression, ifExpr)
      context.removeStep(expression)
    } else if (classifier == EC_IF_GROUP) {
      val ifExpr = expression.asInstanceOf[IfGroupExpression]
      context.newStep(1 + ifExpr.expressions.size, expression)
      operate(ifExpr.sign, ifExpr)
      ifExpr.expressions.foreach(operate(_, ifExpr))
      context.removeStep(expression)
    } else if (classifier == EC_IF_XOR_GROUP) {
      val ifExpr = expression.asInstanceOf[IfGroupXorExpression]
      val expressions = ifExpr.expressions
      if (expressions.nonEmpty) {
        context.newStep(expressions.size, expression)
        ifExpr.expressions.foreach(operate(_, ifExpr))
        context.removeStep(expression)
      }
    } else if (classifier == EC_IF_CMP) {
      context.newStep(2, expression)
      val ifExpr = expression.asInstanceOf[CmpExpression]
      operate(ifExpr.leftExpression, ifExpr)
      operate(ifExpr.rightExpression, ifExpr)
      context.removeStep(expression)
    } else if (classifier == EC_SWITCH) {
      context.singleStep(expression)
      val switchExpr = expression.asInstanceOf[SwitchExpression]
      operate(switchExpr.variable, switchExpr)
      context.removeStep(expression)
    } else if (classifier == EC_RETURN) {
      context.singleStep(expression)
      val returnExpr = expression.asInstanceOf[ReturnVarExpression]
      operate(returnExpr.variable, returnExpr)
      context.removeStep(expression)
    } else if (classifier == EC_PRIMITIVE_CAST) {
      context.singleStep(expression)
      val convExpr = expression.asInstanceOf[ConversionExpression]
      operate(convExpr.variable, convExpr)
      context.removeStep(expression)
    } else if (classifier == EC_CASE) {
      context.singleStep(expression)
      val caseExpr = expression.asInstanceOf[CaseExpression]
      operate(caseExpr.value, caseExpr)
      context.removeStep(expression)
    } else if (classifier == EC_GET_ARRAY_ITEM) {
      context.newStep(2, expression)
      val getArrayItemExpr = expression.asInstanceOf[GetArrayItemExpression]
      operate(getArrayItemExpr.arrayVariable, getArrayItemExpr)
      operate(getArrayItemExpr.indexVariable, getArrayItemExpr)
      context.removeStep(expression)
    } else if (classifier == EC_MATH_PAIR) {
      context.newStep(2, expression)
      val mathPairExpr = expression.asInstanceOf[MathPair]
      operate(mathPairExpr.sign, mathPairExpr)
      operate(mathPairExpr.variable, mathPairExpr)
      context.removeStep(expression)
    } else if (classifier == EC_MATH) {
      val mathExpr = expression.asInstanceOf[MathExpression]
      context.newStep(1 + mathExpr.pairs.size, expression)
      operate(mathExpr.variable, mathExpr)
      mathExpr.pairs.foreach(operate(_, mathExpr))
      context.removeStep(expression)
    } else if (classifier == EC_MATH_NEGATE) {
      context.singleStep(expression)
      val mathNegateExpr = expression.asInstanceOf[NegateExpression]
      operate(mathNegateExpr.variable, mathNegateExpr)
      context.removeStep(expression)
    } else if (classifier == EC_MATH_TILDE) {
      context.singleStep(expression)
      val mathTildeExpr = expression.asInstanceOf[TildeExpression]
      operate(mathTildeExpr.variable, mathTildeExpr)
      context.removeStep(expression)
    } else if (classifier == EC_PRE_INC) {
      context.singleStep(expression)
      val preIncExpr = expression.asInstanceOf[PreIncrementExpression]
      operate(preIncExpr.variable, preIncExpr)
      context.removeStep(expression)
    } else if (classifier == EC_POST_INC) {
      context.singleStep(expression)
      val postIncExpr = expression.asInstanceOf[PostIncrementExpression]
      operate(postIncExpr.variable, postIncExpr)
      context.removeStep(expression)
    } else if (classifier == EC_STORE_VAR) {
      context.newStep(3, expression)
      val storeExpr = expression.asInstanceOf[LocalVariableExpression]
      operate(storeExpr.variableName, storeExpr)
      operate(storeExpr.sign, storeExpr)
      operate(storeExpr.assignValue, storeExpr)
      context.removeStep(expression)
    } else if (classifier == EC_STORE_DECLARE_VAR) {
      context.singleStep(expression)
      val storeDeclareExpr = expression.asInstanceOf[DeclareLocalVariableExpression]
      operate(storeDeclareExpr.variableName, storeDeclareExpr)
      context.removeStep(expression)
    } else if (classifier == EC_STORE_NEW_VAR) {
      context.newStep(2, expression)
      val storeNewExpr = expression.asInstanceOf[NewLocalVariableExpression]
      operate(storeNewExpr.variableName, storeNewExpr)
      operate(storeNewExpr.assignValue, storeNewExpr)
      context.removeStep(expression)
    } else if (classifier == EC_STORE_ARRAY_VAR) {
      context.newStep(4, expression)
      val storeArrayExpr = expression.asInstanceOf[SetArrayItemExpression]
      operate(storeArrayExpr.arrayVariable, storeArrayExpr)
      operate(storeArrayExpr.indexVariable, storeArrayExpr)
      operate(storeArrayExpr.sign, storeArrayExpr)
      operate(storeArrayExpr.assignValue, storeArrayExpr)
      context.removeStep(expression)
    } else if (classifier == EC_GET_FIELD) {
      context.singleStep(expression)
      val getFieldExpr = expression.asInstanceOf[GetFieldExpression]
      operate(getFieldExpr.fieldOwner, getFieldExpr)
      context.removeStep(expression)
    } else if (classifier == EC_GET_STATIC_FIELD) {
      context.singleStep(expression)
      val getStaticFieldExpr = expression.asInstanceOf[GetStaticFieldExpression]
      operate(getStaticFieldExpr.fieldOwner, getStaticFieldExpr)
      context.removeStep(expression)
    } else if (classifier == EC_PUT_FIELD) {
      context.newStep(3, expression)
      val putFieldExpr = expression.asInstanceOf[PutFieldExpression]
      operate(putFieldExpr.fieldOwner, putFieldExpr)
      operate(putFieldExpr.sign , putFieldExpr)
      operate(putFieldExpr.fieldValue, putFieldExpr)
      context.removeStep(expression)
    } else if (classifier == EC_PUT_STATIC_FIELD) {
      context.newStep(3, expression)
      val putStaticFieldExpr = expression.asInstanceOf[PutStaticFieldExpression]
      operate(putStaticFieldExpr.fieldOwner, putStaticFieldExpr)
      operate(putStaticFieldExpr.sign, putStaticFieldExpr)
      operate(putStaticFieldExpr.fieldValue, putStaticFieldExpr)
      context.removeStep(expression)
    } else if (classifier == EC_INSTANCE_OF) {
      context.singleStep(expression)
      val instanceOfExpr = expression.asInstanceOf[InstanceOfExpression]
      operate(instanceOfExpr.objectref, instanceOfExpr)
      context.removeStep(expression)
    } else if (classifier == EC_ARGUMENTS) {
      val argsExpr = expression.asInstanceOf[ArgumentsExpression]
      val argumentValues = argsExpr.argumentValues
      if (argumentValues.nonEmpty) {
        context.newStep(argumentValues.size, expression)
        argumentValues.foreach(operate(_, argsExpr))
        context.removeStep(expression)
      }
    } else if (classifier == EC_INVOKE_INTERFACE) {
      context.newStep(2, expression)
      val invokeExpr = expression.asInstanceOf[InvokeInterfaceExpression]
      operate(invokeExpr.ownerValue, invokeExpr)
      operate(invokeExpr.arguments, invokeExpr)
      context.removeStep(expression)
    } else if (classifier == EC_INVOKE_VIRTUAL) {
      context.newStep(2, expression)
      val invokeExpr = expression.asInstanceOf[InvokeVirtualExpression]
      operate(invokeExpr.ownerValue, invokeExpr)
      operate(invokeExpr.arguments, invokeExpr)
      context.removeStep(expression)
    } else if (classifier == EC_CONSTRUCTOR) {
      context.singleStep(expression)
      val constructorExpr = expression.asInstanceOf[InvokeConstructorExpressioan]
      operate(constructorExpr.arguments, constructorExpr)
      context.removeStep(expression)
    } else if (classifier == EC_NEW_OBJECT) {
      context.singleStep(expression)
      val newObjectExpr = expression.asInstanceOf[NewObjectExpression]
      operate(newObjectExpr.arguments, newObjectExpr)
      context.removeStep(expression)
    } else if (classifier == EC_NEW_ARRAY) {
      val newArrayExpr = expression.asInstanceOf[NewArrayExpression]
      val dimensionValues = newArrayExpr.dimensionValues
      if (dimensionValues.nonEmpty) {
        context.newStep(dimensionValues.size, expression)
        dimensionValues.foreach(operate(_, newArrayExpr))
        context.removeStep(expression)
      }
    } else if (classifier == EC_INIT_ARRAY) {
      context.newStep(2, expression)
      val initArrayExpr = expression.asInstanceOf[InitArrayExpression]
      operate(initArrayExpr.declare, initArrayExpr)
      operate(initArrayExpr.values, initArrayExpr)
      context.removeStep(expression)
    } else if (classifier == EC_ARRAY_LENGTH) {
      context.singleStep(expression)
      val arrayLengthExpr = expression.asInstanceOf[ArrayLengthExpression]
      operate(arrayLengthExpr.arrayref, arrayLengthExpr)
      context.removeStep(expression)
    } else if (classifier == EC_INVOKE_SPECIAL) {
      context.newStep(2, expression)
      val invokeExpr = expression.asInstanceOf[InvokeSpecialExpression]
      operate(invokeExpr.ownerValue, invokeExpr)
      operate(invokeExpr.arguments, invokeExpr)
      context.removeStep(expression)
    } else if (classifier == EC_INVOKE_STATIC) {
      context.singleStep(expression)
      val invokeExpr = expression.asInstanceOf[InvokeStaticExpression]
      operate(invokeExpr.arguments, invokeExpr)
      context.removeStep(expression)
    } else if (classifier == EC_INVOKE_DYNAMIC) {
      context.newStep(3, expression)
      val invokeExpr = expression.asInstanceOf[InvokeDynamicExpression]
      operate(invokeExpr.bootstrapArguments, invokeExpr)
      operate(invokeExpr.methodTypeInvoker, invokeExpr)
      operate(invokeExpr.methodArguments, invokeExpr)
      context.removeStep(expression)
    } else if (classifier == EC_SYNCHRONIZE) {
      context.singleStep(expression)
      val syncExpr = expression.asInstanceOf[MonitorEnterExpression]
      operate(syncExpr.variable, syncExpr)
      context.removeStep(expression)
    } else if (classifier == EC_CHECK_CAST) {
      context.singleStep(expression)
      val castExpr = expression.asInstanceOf[CheckCastExpression]
      operate(castExpr.variable, castExpr)
      context.removeStep(expression)
    } else if (classifier == EC_THROW) {
      context.singleStep(expression)
      val throwExpr = expression.asInstanceOf[ThrowExpression]
      operate(throwExpr.variable, throwExpr)
      context.removeStep(expression)
    }
  }

  private def depth(expression: IExpression, parent: IExpression) {
    val classifier = expression.classifier
    if (classifier.is(EA_BLOCK)) {
      val blockExpr = expression.asInstanceOf[BlockExpression]
      context.newStep(blockExpr.count, blockExpr)
      blockExpr.expressions.foreach(operate(_, blockExpr))
      context.removeStep(blockExpr)
    } else if (classifier.is(EA_STATEMENT)) {
      val stExpr = expression.asInstanceOf[StatementExpression]
      context.newStep(stExpr.count, stExpr)
      operate(stExpr.getBaseExpr, stExpr)
      stExpr.expressions.foreach(operate(_, stExpr))
      if (stExpr.hasOutExpr) {
        operate(stExpr.getOutExpr, stExpr)
      }
      context.removeStep(stExpr)
    }
  }

  /**
   * Модификация выражения
   * Примечание: приоритет выполнения трансформации зависит от позиции в actions - чем раньше добавили
   * тем выше приоритет
   */
  private def transform() {
    val actions = context.getActions
    val transformer = new ExpressionTransformer
    transformer.transform(actions)
  }
}

/**
 * Контекст выполнения процесса обхода
 */
class GuideContext() {

  private var depth = 0;
  private val steps: IdentityHashMap[IExpression, Step] = new IdentityHashMap()
  private val parents: IdentityHashMap[IExpression, IExpression] = new IdentityHashMap()

  def incDepth(value: Int) {
    depth = depth + value
  }

  def newStep(maxIteration: Int, parent: IExpression) {
    newStep(if (maxIteration == 1) Step.SINGLE_STEP else Step(maxIteration), parent)
  }

  def singleStep(parent: IExpression) {
    newStep(Step.SINGLE_STEP, parent)
  }

  def newStep(step: Step, parent: IExpression) {
      steps.put(parent, step)
  }

  def getStep(expr: IExpression): Step = {
    val step = steps.get(expr)
    if (step == null) {
      Step.EMPTY_STEP
    } else {
      step
    }
  }

  def removeStep(expr: IExpression) {
    steps.remove(expr)
  }

  def link(expr: IExpression, parent: IExpression) {
    parents.put(expr, parent)
  }

  def removeLink(expr: IExpression) {
    parents.remove(expr)
  }

  def getParentFor(expr: IExpression): IExpression = parents.get(expr)
  def hasParentFor(expr: IExpression): Boolean = parents.containsKey(expr)

  /** TODO here: это пережиток прошлого, лучше удалить или использовать другой механизм */
  private var lastCompletedExpr: IExpression = null

  private val actions = ArrayBuffer[GuideTransformAction]()

  /** Список действий, который ожидают парента типа Block и Statement */
  private var withoutBlockParents = ArrayBuffer[GuideTransformAction]()

  /**
   *  Удалить выражение.
   *  Примечание: удалить выражение можно только из parent который содержит список выражений,
   *  обладает аттрибутом EA_DEPTH, для остального нужно использовать метода markAsReplaced
   */
  def markAsRemoved(expression: IExpression, parent: IExpression) {
    if (parent.classifier.has(EA_DEPTH)) {
      actions += GuideTransformAction.makeRemove(actions.size, expression, parent)
    }
  }

  def markAsRemoved(expressions: ArrayBuffer[IExpression], parent: IExpression) {
    expressions.foreach(markAsRemoved(_, parent))
  }

  def markAsReplaced(oldExpression: IExpression, newExpression: IExpression, parent: IExpression) {
    actions += GuideTransformAction.makeReplace(actions.size, oldExpression, newExpression, parent)
    if (!parent.classifier.has(EA_DEPTH)) {
      withoutBlockParents += actions.last
    }
  }

  private def getFirstExprWithDepthParent(fromExpr: IExpression): IExpression = {
    var ownerExpression = fromExpr
    var parent = getParentFor(ownerExpression)
    while (parent != null && !parent.classifier.has(EA_DEPTH)) {
      ownerExpression = parent
      parent = getParentFor(ownerExpression)
    }
    if (parent != null && parent.classifier == EC_STATEMENT && ExpressionHelpers.getFirst(parent) == ownerExpression) {
      ownerExpression = parent
    }
    ownerExpression
  }

  def markAsAddBefore(markerExpression: IExpression, addExpression: IExpression):IExpression = {
    val ownerExpression = getFirstExprWithDepthParent(markerExpression)
    val parent = getParentFor(ownerExpression)
    if (parent == null || !parent.classifier.has(EA_DEPTH)) {
      val reason = "Для выражения '" + markerExpression.view(EMPTY_EXPRESSION) + "' не найден контейнер с аттрибутом EA_DEPTH"
      val effect = "Добавить выражение '" + addExpression.view(EMPTY_EXPRESSION) + "'не возможно. Обработка выражений будет прекращена."
      val action = "Необходимо исправить бизнес логику одного из трансформеров"
      throw Exception(GUIDE_AREA, reason, effect, action)
    }
    markAsAddBefore(ownerExpression, addExpression, parent)
    ownerExpression
  }

  def markAsAddBefore(ownerExpression: IExpression, addExpression: IExpression, parent: IExpression) {
    actions += GuideTransformAction.makeAdd(actions.size, ownerExpression, addExpression, parent, true)
  }

  def updateParent(expression: IExpression, parent: IExpression) {
    if (!withoutBlockParents.isEmpty) {
      withoutBlockParents.foreach(action => {
        if (action.expressions.last == expression) {
          action.expressions += parent
        }
      })
      withoutBlockParents = withoutBlockParents.filter(!_.expressions.last.classifier.has(EA_DEPTH))
    }
    lastCompletedExpr = expression
  }

  /**
   * Проверить последную полностью пройденное выражение
   */
  def isLastCompletedExpr(expression: IExpression): Boolean = expression == lastCompletedExpr

  def getActions: ArrayBuffer[GuideTransformAction] = actions

  private var state: GuideState = GuideState.GS_NEXT
  def setState(state: GuideState) {
    this.state = state
  }
  def getState = state

  /**
   * Получить новое выражение для oldExpr, если такого нет, то возвращаем oldExpr
   */
  def getReplaceExprFor(oldExpr: IExpression): IExpression = {
    val actionOpt = actions.find(action => {
      action.code == GuideTransformAction.TA_REPLACE && action.expressions.head == oldExpr
    })
    if (actionOpt.isDefined) {
      return actionOpt.get.expressions(1)
    }
    oldExpr
  }

  /**
   * Замена не нужна
   */
  def removeReplaceExprFor(oldExpr: IExpression) {
    val actionOpt = actions.find(action => {
      action.code == GuideTransformAction.TA_REPLACE && action.expressions.head == oldExpr
    })
    if (actionOpt.isDefined) {
      actions -= actionOpt.get
    }
  }

  /**
   * Добавление не нужно
   */
  def removeAddExprFor(addExpr: IExpression) {
    val actionOpt = actions.find(action => {
      action.code == GuideTransformAction.TA_ADD && action.expressions(1) == addExpr
    })
    if (actionOpt.isDefined) {
      actions -= actionOpt.get
    }
  }

  def removeAddExprForParent(parentExpr: IExpression) {
    val actionOpt = actions.find(action => {
      action.code == GuideTransformAction.TA_ADD && getParentFor(action.expressions(1)) == parentExpr
    })
    if (actionOpt.isDefined) {
      actions -= actionOpt.get
    }
  }

  def isRemoved(expr: IExpression): Boolean = {
    actions.find(action => {
      action.code == GuideTransformAction.TA_REMOVE && action.expressions.head == expr
    }).isDefined
  }

  def esid(expr: IExpression): ExprSid = {
    val baseExpr = getFirstExprWithDepthParent(expr)
    val baseParent = getParentFor(baseExpr)
    val stepId = getStep(baseParent)
    if (depth == 1) {
      return new ExprSid(Array[Int](stepId.now))
    }
    val value = new Array[Int](depth)
    var index = depth - 1
    var curExpr = baseParent
    var curStepId = stepId
    do {
      value(index) = curStepId.now
      curExpr = getParentFor(curExpr)
      curStepId = getStep(curExpr)
      index -= 1;
    } while (index >= 0 && curExpr != null)
    new ExprSid(value)
  }
}

/**
 *  Структурированный id выражения
 *  TODO here: можно оптимизировать, когда value содержит 1, 2 или 3 элемента. Сделать 3 имплементации
 */
class ExprSid(value: Array[Int]) {
  def reachable(esid: ExprSid): Boolean = {
    // быстрый ответ
    if (count == 1) {
      return true;
    }
    // быстрый ответ
    if (count > esid.count || (count > 1 && apply(0) != esid(0))) {
      return false
    }
    // Начинается с
    value.indices.find(index => {
      if (count > index + 1) {
        value(index) != esid(index)
      } else {
        value(index) > esid(index)
      }
    }).isEmpty
  }
  def count: Int = value.length
  def apply(index: Int): Int = value(index)
  def part(size: Int): Array[Int] = if (size == count) value else value.slice(0, size)
  /**
   * Получить количество общих предков
   */
  def commonCount(esid: ExprSid): Int = {
    var total = 0;
    value.indices.find(index => {
      if (apply(index) != esid(index)) {
        true
      } else {
        total = total + 1
        false
      }
    })
    total
  }

  override def toString(): String = value.mkString(":")
}

object Step {
  val SINGLE_STEP = new FixStep(1)
  val EMPTY_STEP = new FixStep(-1)
  def apply(max: Int): Step = new Step(max)
}

class Step(val max: Int) {
  private var current: Int = 0
  def next() {
    current = current + 1
  }
  def now() :Int = current
  def isFirst: Boolean = current == 0
  def isLast: Boolean = current == max -1
  override def toString(): String = (current, max).toString
}

class FixStep(val value: Int) extends Step(value) {
  override def next(){}
}

object GuideState {
  val GS_NEXT = new GuideState("next") // следующий
  val GS_BREAK = new GuideState("break") // прервать обработку родительского узла
  val GS_STOP = new GuideState("stop") // прервать всю обработку

}

class GuideState(name: String) {
}

object GuideTransformAction {
  private val TA_TEXTS = Array("REMOVE", "REPLACE", "ADD")
  /** Transform Action  - remove */
  val TA_REMOVE = 1
  /** Transform Action - replace */
  val TA_REPLACE = 2
  /** Transform Action - add */
  val TA_ADD = 3

  def makeRemove(id: Int, expression: IExpression, parent: IExpression): GuideTransformAction = {
    new GuideTransformAction(id, TA_REMOVE, ArrayBuffer(expression, parent))
  }

  def makeReplace(id: Int, oldExpression: IExpression, newExpression: IExpression, parent: IExpression): GuideTransformAction = {
    new GuideTransformAction(id, TA_REPLACE, ArrayBuffer[IExpression]() ++ ArrayBuffer(oldExpression, newExpression, parent))
  }

  def makeAdd(id: Int, markerExpression: IExpression, addExpression: IExpression, parent: IExpression, before: Boolean): GuideTransformAddAction = {
    new GuideTransformAddAction(id, TA_ADD, ArrayBuffer[IExpression]() ++ ArrayBuffer(markerExpression, addExpression, parent), before)
  }
}

class GuideTransformAction(val id: Int, val code: Int, val expressions: ArrayBuffer[IExpression]) {
  override def toString(): String = "[" + id + ", " + GuideTransformAction.TA_TEXTS(code - 1) + "] " + expressions(0) + " | " + expressions(1)
}

class GuideTransformAddAction(id: Int, code: Int, expressions: ArrayBuffer[IExpression], val before: Boolean) extends
    GuideTransformAction(id, code, expressions) {

}