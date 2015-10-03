package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.core.Constants._
import org.powlab.jeye.core.parsing.DescriptorParser.Pisc
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.decoders.AnnotationDecodeUtils.{AnnotationDefaultExpression, AnnotationExpression}
import org.powlab.jeye.utils.{Clazz, ConstantPoolUtils, DecodeUtils}

object AnnotationDecodeUtils {

  def processConstPrimitive(value: ConstValueIndex)(implicit cpUtils: ConstantPoolUtils): IExpression = value.tag match {
    case EV_STRING => StringLiteralExpression(cpUtils.getUtf8(value.const_value_index))
    case EV_INT => IntLiteralExpression(cpUtils.getInteger(value.const_value_index))
    case EV_FLOAT => FloatLiteralExpression(cpUtils.getFloat(value.const_value_index))
    case EV_LONG => LongLiteralExpression(cpUtils.getLong(value.const_value_index))
    case EV_DOUBLE => DoubleLiteralExpression(cpUtils.getDouble(value.const_value_index))
  }

  def processElementValue(v: ElementValue)(implicit cpUtils: ConstantPoolUtils): IExpression = v match {
    case value: ConstValueIndex => processConstPrimitive(value)
    case value: EnumConstValue => EnumLiteralExpression(cpUtils.getUtf8(value.const_name_index), cpUtils.getUtf8(value.type_name_index))
    case value: ClassInfoIndex => ClassLiteralExpression(Pisc(cpUtils.getUtf8(value.class_info_index)))
    case value: Annotation => AnnotationExpression(value)
    case value: ArrayValue => AnnotationArrayExpression(value.values.map(processElementValue))
  }

  /**
   * TODO here: сделано не в стиле выражений
   */
  case class AnnotationExpression(annotation: Annotation)(implicit cpUtils: ConstantPoolUtils) extends IExpression {

    override def view(parent: IExpression): String = {
      val name = DecodeUtils.getSimpleClassName(cpUtils.getUtf8(annotation.type_index))
      val params = annotation.element_value_pairs.map(pairs =>
        cpUtils.getUtf8(pairs.element_name_index) + " = " + processElementValue(pairs.value).view(this)).toList.mkString("(", ", ", ")")

      "@" + name + params
    }

    def classifier(): ExpressionClassifier = EC_ANNOTATION
  }

  /**
   * TODO here: сделано не в стиле выражений
   */
  case class AnnotationDefaultExpression(attribute: AnnotationDefaultAttribute)(implicit cpUtils: ConstantPoolUtils) extends IExpression {
    val element = processElementValue(attribute.default_value)

    override def view(parent: IExpression): String = "default " + element.view(this)

    def classifier(): ExpressionClassifier = EC_DEFAULT_VALUE
  }

}

class ClassAnnotationDecoder(classFile: ClassFile) {
  implicit val cpUtils = classFile.constantPoolUtils

  def decode(annotation: Annotation): AnnotationExpression = AnnotationExpression(annotation)
}

class AnnotationDefaultsDecoder(classFile: ClassFile) {
  implicit val cpUtils = classFile.constantPoolUtils

  def decode(attribute: AnnotationDefaultAttribute): IExpression = AnnotationDefaultExpression(attribute)(cpUtils)
}

object ClassAnnotationsDecoder extends IDecoder {
  override def decode(clazz: Clazz): BlockExpression = {
    val classFile = clazz.classFile
    val annotationDecoder = new ClassAnnotationDecoder(classFile)
    val expressions = classFile.attributes.filter(_.isInstanceOf[RuntimeAnnotationsAttribute]).flatMap({
      case attr: RuntimeAnnotationsAttribute =>
        attr.annotations.map(annotation => {
          annotationDecoder.decode(annotation)
        })
    })
    new BlockExpression ++= expressions.toList
  }
}
