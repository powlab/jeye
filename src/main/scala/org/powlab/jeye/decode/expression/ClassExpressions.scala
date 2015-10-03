package org.powlab.jeye.decode.expression

import org.powlab.jeye.core.AccessFlags._
import org.powlab.jeye.core.Flag
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.utils.DecodeUtils.getViewType
import scala.collection.mutable.{ ArrayBuffer, ListBuffer }
import scala.collection.mutable.Buffer
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.utils.DecodeUtils
import scala.collection.mutable.StringBuilder
import org.powlab.jeye.core.ParameterDescriptor

class ImportExpression(val className: String) extends IExpression {
  def view(parent: IExpression): String = "import " + className + ";"

  def classifier(): ExpressionClassifier = EC_IMPORT
}

case class ClassDeclareExpression(modifiers: String, className: String, postfix: String) extends IExpression {
  override def view(parent: IExpression): String = modifiers + className + postfix

  def classifier(): ExpressionClassifier = EC_CLASS_DECLARE
}

case class ClassExpression(annotations: BlockExpression, declare: ClassDeclareExpression, body: BlockExpression) extends IExpression {
  val block = new BlockExpression += annotations += (new StatementExpression(declare) += body)

  override def view(parent: IExpression): String = block.view(this)

  def classifier(): ExpressionClassifier = EC_CLASS
}

case class MethodExpression(annotations: BlockExpression, declare: MethodDeclareExpression, body: IExpression, isAbstract: Boolean) extends IExpression {
  override def view(parent: IExpression): String = {
    val statement = if (!isAbstract) new StatementExpression(declare) += body else declare
    (new BlockExpression ++= annotations += statement) view (this)
  }

  def classifier(): ExpressionClassifier = EC_METHOD
}

case class ModifiersExpression(accessFlags: Int, flags: List[Flag]) extends IExpression {
  override def view(parent: IExpression): String = if (flags.isEmpty) "" else flags.map(_.title).mkString(" ")

  override def classifier(): ExpressionClassifier = EC_MODIFIERS
}

case class TypeParametersExpression(parameters: Seq[String]) extends IExpression {
  override def view(parent: IExpression): String = if (parameters.isEmpty) "" else parameters.mkString("<", ", ", ">")

  override def classifier(): ExpressionClassifier = EC_TYPE_PARAMETERS
}

/**
 * TODO here: добавить метрики, статический блок, конструктор и т.д.
 */
case class MethodDeclareExpression(modifiers: ModifiersExpression, typeParameters: IExpression, returnType: IExpression,
  methodName: IExpression, var parametersSeq: Seq[String], throwsExpression: IExpression,
  default: IExpression, semicolon: String, isConstructor: Boolean) extends IExpression {
  override def view(parent: IExpression): String = {
    val buffer = new StringBuilder
    buffer += ' '
    buffer.append(modifiers)
    if (methodName != EMPTY_EXPRESSION) {
      append(buffer, typeParameters.view(this))
      append(buffer, returnType.view(this))
      append(buffer, methodName.view(this))
      buffer.append("(").append(parametersSeq.mkString(", ")).append(")")
      append(buffer, throwsExpression.view(this))
      append(buffer, default.view(this))
      buffer.append(semicolon)
    }
    buffer.tail.toString
  }

  private def append(buffer: StringBuilder, value: Any) {
    val view = value.toString.trim
    if (buffer.last != ' ' && !view.isEmpty) {
      buffer.append(' ')
    }
    buffer.append(value)
  }

  def classifier(): ExpressionClassifier = EC_METHOD_DECLARE
}

case class EnumLiteralExpression(value: String, descriptor: String) extends IExpression {
  override def view(parent: IExpression): String = s"${DecodeUtils.getSimpleClassName(descriptor)}.$value"

  def classifier(): ExpressionClassifier = EC_ENUM_LITERAL
}

case class AnnotationArrayExpression(expressions: Array[IExpression]) extends IExpression {
  override def view(parent: IExpression): String = expressions.map(_.view(this)).mkString("{", ", ", "}")

  def classifier(): ExpressionClassifier = EC_ARRAY_LITERAL
}

/**
 * Вынести в свой пакет org.powlab.jeye.decode.field
 */
case class FieldExpression(annotations: BlockExpression, declare: FieldDeclareExpression) extends IExpression {
  override def view(parent: IExpression): String = {
    annotations.view(this) + declare.view(this)
  }

  def classifier(): ExpressionClassifier = EC_FIELD
}

/**
 * TODO here: переделать на более простое выражение
 */
case class FieldDeclareExpression(fieldName: IExpression, modifiers: ModifiersExpression, constant: IExpression,
  signature: IExpression, val value: IExpression, val fieldDescriptor: ParameterDescriptor, val end: String) extends IExpression {
  override def view(parent: IExpression): String = {
    val buffer = new StringBuilder
    buffer += ' '
    if (!modifiers.flags.contains(ACC_ENUM)) {
        append(buffer, modifiers.view(this))
        if (signature != EMPTY_EXPRESSION) {
          append(buffer, signature.view(this))
        } else {
          append(buffer, DecodeUtils.getViewType(fieldDescriptor.meta))
        }
    }
    append(buffer, fieldName.view(this))
    if (constant != EMPTY_EXPRESSION) {
      append(buffer, "= ")
      append(buffer, constant.view(this))
    } else if (value != EMPTY_EXPRESSION) {
      append(buffer, value.view(this))
    }
    buffer.append(end)

    buffer.tail.toString
  }

  private def append(buffer: StringBuilder, value: Any) {
    val view = value.toString.trim
    if (buffer.last != ' ' && !view.isEmpty) {
      buffer.append(' ')
    }
    buffer.append(value)
  }

  def classifier(): ExpressionClassifier = EC_FIELD_DECLARE
}
