package org.powlab.jeye.decode.expression

import org.powlab.jeye.core.Constants._
import org.powlab.jeye.core._
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.ExpressionViewer._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.processor.reference.MethodInstructionData
import org.powlab.jeye.utils.DecodeUtils._
import scala.collection.mutable.Buffer
import org.powlab.jeye.decode.processor.reference.FieldInstructionData

class GetFieldExpression(val fieldOwner: IExpression, val field: FieldInstructionData) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val fieldOwnerView = select(fieldOwner != EMPTY_EXPRESSION, fieldOwner.view(this) + "." , fieldOwner.view(this))
    return fieldOwnerView + field.name
  }
  def descriptor(): ParameterDescriptor = field.descriptor
  def classifier(): ExpressionClassifier = EC_GET_FIELD
}

class GetStaticFieldExpression(val fieldOwner: IExpression, val field: FieldInstructionData) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val fieldOwnerView = select(fieldOwner != EMPTY_EXPRESSION, fieldOwner.view(this) + "." , fieldOwner.view(this))
    return fieldOwnerView + field.name
  }
  def descriptor(): ParameterDescriptor = field.descriptor
  def classifier(): ExpressionClassifier = EC_GET_STATIC_FIELD
}

class PutFieldExpression(val fieldOwner: IExpression, val sign: SignExpression, val fieldValue: IExpression,
                         val field: FieldInstructionData) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val fieldOwnerView = select(fieldOwner != EMPTY_EXPRESSION, fieldOwner.view(this) + "." , fieldOwner.view(this))
    val baseView = fieldOwnerView + field.name + sign.view(this) + fieldValue.view(this)
    correctView(baseView, parent.classifier, EC_PUT_FIELD, EC_PUT_STATIC_FIELD, EC_STORE_VAR, EC_STORE_ARRAY_VAR, EC_SYNCHRONIZE)
  }
  def descriptor(): ParameterDescriptor = field.descriptor
  def classifier(): ExpressionClassifier = EC_PUT_FIELD
}

class PutStaticFieldExpression(val fieldOwner: IExpression, val sign: SignExpression, val fieldValue: IExpression,
                               val field: FieldInstructionData) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val fieldOwnerView = select(fieldOwner != EMPTY_EXPRESSION, fieldOwner.view(this) + "." , fieldOwner.view(this))
    val baseExpr = fieldOwnerView + field.name + sign.view(this) + fieldValue.view(this)
    correctView(baseExpr, parent.classifier, EC_PUT_FIELD, EC_PUT_STATIC_FIELD, EC_STORE_VAR, EC_STORE_ARRAY_VAR, EC_SYNCHRONIZE)
  }
  def descriptor(): ParameterDescriptor = field.descriptor
  def classifier(): ExpressionClassifier = EC_PUT_STATIC_FIELD
}

/**
 * @param objectref
 * @param className
 */
class InstanceOfExpression(val objectref: IExpression, val className: String) extends ITypedExpression {
  def view(parent: IExpression): String = objectref.view(this) + " instanceof " + className

  def descriptor(): ParameterDescriptor = TYPE_BOOLEAN_DESCRIPTOR
  def classifier(): ExpressionClassifier = EC_INSTANCE_OF
}

/**
 * @param paramTypes
 * @param argumentValues
 */
case class ArgumentsExpression(paramTypes: Array[ParameterDescriptor],
  argumentValues: Buffer[ITypedExpression]) extends IExpression with IUpdatedExpression {
  def view(parent: IExpression): String = argumentValues.map(_.view(this)).mkString(", ")
  def classifier(): ExpressionClassifier = EC_ARGUMENTS
  def update(oldExpression: IExpression, newExpression: IExpression) {
    val index = argumentValues.indexOf(oldExpression)
    if (index != -1 && newExpression.isInstanceOf[ITypedExpression]) {
      argumentValues(index) = newExpression.asInstanceOf[ITypedExpression]
    }
  }
}

abstract class Invokeable extends ITypedExpression {
  def arguments: ArgumentsExpression
  def method: MethodInstructionData
}

class InvokeInterfaceExpression(val ownerValue: IExpression, val arguments: ArgumentsExpression,
  val method: MethodInstructionData) extends Invokeable {
  val methodName = method.name
  val methodDescriptor = method.descriptor
  def view(parent: IExpression): String = {
    val baseView = ownerValue.view(this) + "." + methodName + "(" + arguments.view(this) + ")"
    correctFlush(baseView, parent.classifier)
  }
  def descriptor(): ParameterDescriptor = method.returnType
  def classifier(): ExpressionClassifier = EC_INVOKE_INTERFACE
}

class InvokeVirtualExpression(val ownerValue: ITypedExpression, val arguments: ArgumentsExpression,
  val method: MethodInstructionData) extends Invokeable {
  val methodName = method.name
  val methodDescriptor = method.descriptor
  def view(parent: IExpression): String = {
    val ownerView = if (THIS_CONSTANT != ownerValue.view(this)) ownerValue.view(this) + "." else ""
    val baseView = ownerView + methodName + "(" + arguments.view(this) + ")"
    correctFlush(baseView, parent.classifier)
  }
  def descriptor(): ParameterDescriptor = method.returnType
  def classifier(): ExpressionClassifier = EC_INVOKE_VIRTUAL
}

class InvokeSpecialExpression(val ownerValue: ITypedExpression, val arguments: ArgumentsExpression,
  val method: MethodInstructionData) extends Invokeable {
  val methodName = method.name
  val methodDescriptor = method.descriptor
  val isSuper = ownerValue.descriptor.meta != method.clazz.meta
  private val isThis = ownerValue.view(EMPTY_EXPRESSION) == THIS_EXPRESSION.view(EMPTY_EXPRESSION)
  def view(parent: IExpression): String = {
    val ownerView = if (! isThis) {
      ownerValue.view(parent) + "."
    } else if (isSuper) {
      SUPER_CONSTANT + "."
    } else {
      ""
    }
    val baseView = ownerView + methodName + "(" + arguments.view(this) + ")"
    correctFlush(baseView, parent.classifier)
  }
  def descriptor(): ParameterDescriptor = method.returnType
  def classifier(): ExpressionClassifier = EC_INVOKE_SPECIAL
}

class InvokeStaticExpression(val thisClassName: String, val arguments: ArgumentsExpression,
  val method: MethodInstructionData) extends Invokeable {
  val methodName = method.name
  val methodDescriptor = method.descriptor
  val classOwner = method.clazz
  val isSameClass = classOwner.name == thisClassName
  def view(parent: IExpression): String = {
    val ownerView = select(!isSameClass, getSimpleClassName(classOwner.name) + ".", "")
    val baseView = ownerView + methodName + "(" + arguments.view(this) + ")"
    correctFlush(baseView, parent.classifier)
  }
  def descriptor(): ParameterDescriptor = method.returnType
  def classifier(): ExpressionClassifier = EC_INVOKE_STATIC
}

class InvokeConstructorExpressioan(val thisClassName: String, val arguments: ArgumentsExpression, val method: MethodInstructionData) extends Invokeable {
  val isThis: Boolean = {
    val classOwner = method.clazz
    classOwner.name == thisClassName
  }
  def view(parent: IExpression): String = {
    val baseWord = select(isThis, "this", "super")
    baseWord + "(" + arguments.view(this) + ");"
  }
  def descriptor(): ParameterDescriptor = method.returnType
  def classifier(): ExpressionClassifier = EC_CONSTRUCTOR
}

case class NewObjectExpression(simpleClassName: String, arguments: ArgumentsExpression,
  descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val baseView = "new " + simpleClassName + "(" + arguments.view(this) + ")"
    correctFlush(baseView, parent.classifier)
  }
  def classifier(): ExpressionClassifier = EC_NEW_OBJECT
}

class NewArrayExpression(val componentType: String, val dimensionValues: Array[ITypedExpression],
  val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val baseView = "new " + componentType + dimensionValues.map("[" + _.view(this) + "]").mkString("")
    correctFlush(baseView, parent.classifier)
  }
  def classifier(): ExpressionClassifier = EC_NEW_ARRAY
}

/**
 * Инициализация массива, пример new char[] {'f', 'b', 'c'}, где
 * declare = new char[]
 * values  = 'f', 'b', 'c'
 */
class InitArrayExpression(val declare: ITypedExpression, val values: ArgumentsExpression) extends ITypedExpression {
  private val space = if (declare != EMPTY_EXPRESSION) " " else "" // это элемент форматирования
  def view(parent: IExpression): String = declare.view(this) + space + "{" + values.view(this) + "}"
  def descriptor(): ParameterDescriptor = declare.descriptor
  def classifier(): ExpressionClassifier = EC_INIT_ARRAY
}

/**
 * Получение длины массива, пример array1.length, где
 * arrayref = array1
 *
 * @param arrayref
 */
class ArrayLengthExpression(val arrayref: ITypedExpression) extends ITypedExpression {
  def view(parent: IExpression): String = arrayref.view(this) + ".length"

  def descriptor(): ParameterDescriptor = TYPE_INT_DESCRIPTOR

  def classifier(): ExpressionClassifier = EC_ARRAY_LENGTH
}

/**
 * @param methodTypeDescriptor
 * @param className
 */
class MethodTypeInvokerExpression(methodTypeDescriptor: String, className: String) extends ITypedExpression {
  def view(parent: IExpression): String = "MethodType.fromMethodDescriptorString(\"" + methodTypeDescriptor + "\", " + className + ".class.getClassLoader())"

  def descriptor(): ParameterDescriptor = TYPE_METHOD_TYPE_DESCRIPTOR

  def classifier(): ExpressionClassifier = EС_METHOD_TYPE
}

/**
 * @param bootstrapMethodName
 * @param bootstrapClassName
 * @param bootstrapArguments
 * @param methodTypeInvoker
 * @param methodName
 * @param methodArgumentsExpression
 * @param descriptor
 */
class InvokeDynamicExpression(val bootstrapMethodName: String, val bootstrapClassName: String, val bootstrapArguments: ArgumentsExpression,
  val methodTypeInvoker: IExpression, val methodName: String,
  val methodArguments: ArgumentsExpression, val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = {
    var view = bootstrapClassName + "." + bootstrapMethodName
    view += "(MethodHandles.lookup(), \"" + methodName + "\", " + methodTypeInvoker.view(this)
    var baExpre = bootstrapArguments.view(this)
    if (!"".equals(baExpre)) {
      view += ", " + baExpre
    }
    view += ").getTarget().invoke(" + methodArguments.view(this) + ")"
    correctFlush(view, parent.classifier)
  }

  def classifier(): ExpressionClassifier = EC_INVOKE_DYNAMIC
}

/**
 * @param {Variable} variable
 */
class MonitorEnterExpression(val variable: IExpression) extends IExpression {
  def view(parent: IExpression): String = "synchronized(" + variable.view(this) + ")"

  def classifier(): ExpressionClassifier = EC_SYNCHRONIZE
}

/**
 * @param castType
 * @param variable
 * @param descriptor
 */
class CheckCastExpression(val castType: String, val variable: IExpression, val descriptor: ParameterDescriptor) extends ITypedExpression {
  def view(parent: IExpression): String = {
    val baseView = "(" + castType + ") " + variable.view(this)
    correctBrackets(baseView, parent.classifier, EC_INVOKE_VIRTUAL, EC_INVOKE_INTERFACE, EC_INVOKE_SPECIAL,
        EC_GET_FIELD, EC_GET_STATIC_FIELD, EC_GET_ARRAY_ITEM, EC_ARRAY_LENGTH)
  }
  def classifier(): ExpressionClassifier = EC_CHECK_CAST
}

class ThrowExpression(val variable: IExpression) extends IExpression {
  def view(parent: IExpression): String = "throw " + variable.view(this) + ";"
  def classifier(): ExpressionClassifier = EC_THROW
}
