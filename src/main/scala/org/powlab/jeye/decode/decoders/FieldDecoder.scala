package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.core.Constants._
import org.powlab.jeye.core.AccessFlags._
import org.powlab.jeye.core.parsing.DescriptorParser._
import org.powlab.jeye.core.parsing.SignatureParser._
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.utils.{AttributeUtils, Clazz, DecodeUtils}

object EnumFieldsDecoder extends IDecoder {
  override def decode(clazz: Clazz): IExpression = {
    val classFile = clazz.classFile
    val fieldDecoder = new FieldDecoder(classFile)
    new BlockExpression ++= classFile.fields.filter(field => {
      val name = classFile.constantPoolUtils.getUtf8(field.name_index)
      name != "$VALUES" && !isSynthetic(field.access_flags)
    }).map(fieldDecoder.decode).toList
  }
}

object FieldsDecoder extends IDecoder {
  override def decode(clazz: Clazz): IExpression = {
    val classFile = clazz.classFile
    val block = new BlockExpression
    val fieldDecoder = new FieldDecoder(classFile)
    block ++= classFile.fields.filter(field => !isSynthetic(field.access_flags)).map(fieldDecoder.decode).toList
    block
  }
}

class FieldDecoder(classFile: ClassFile) {

  val cpUtils = classFile.constantPoolUtils

  val annotationDecoder = new ClassAnnotationDecoder(classFile)
  val signatureDecoder = new SignatureDecoder

  def decode(field: MemberInfo): IExpression = {
    val flags = getFlags(StructureScope.FIELD_SCOPE, field.access_flags)
    //val classFlags = AccessFlags.getFlags(StructureScope.FIELD_SCOPE, classFile.access_flags)
    val descriptor = parseFieldDescriptor(cpUtils.getUtf8(field.descriptor_index))

    val annotations: BlockExpression = new BlockExpression ++= field.attributes.collect {
      case attr: RuntimeAnnotationsAttribute =>
        attr.annotations.map(annotation => {
          annotationDecoder.decode(annotation)
        })
    }.flatten.toList

    val signatureType: Option[IExpression] = AttributeUtils.find[SignatureAttribute](field.attributes) collect {
      case attribute: SignatureAttribute =>
        signatureDecoder.decode(parseSignature(cpUtils.getUtf8(attribute.signature_index), StructureScope.FIELD_SCOPE))
    }

    val value: Option[IExpression] = AttributeUtils.find[ConstantValueAttribute](field.attributes) collect {
      case constant => val item = classFile.constant_pool(constant.constant_value_index)
        item.tag match {
          case CONSTANT_Float => FloatLiteralExpression(cpUtils.getFloat(item.asInstanceOf[ConstantU4Info]))
          case CONSTANT_Long => LongLiteralExpression(cpUtils.getLong(item.asInstanceOf[ConstantU8Info]))
          case CONSTANT_Double => DoubleLiteralExpression(cpUtils.getDouble(item.asInstanceOf[ConstantU8Info]))
          case CONSTANT_String => StringLiteralExpression(cpUtils.getString(item.asInstanceOf[ConstantStringInfo]))
          case CONSTANT_Integer => IntLiteralExpression(cpUtils.getInteger(item.asInstanceOf[ConstantU4Info]))
        }
    }

    // Хак для корректного выставления ; для полей Enum, ; должна стоять только у последнего поля
    // но так как в Enum последнее поле это сгенерированный VALUES, то реальное последнее поле имеет индекс length - 2
    //    val end = if (classFlags.contains(ACC_ENUM) && classFile.fields.indexOf(field) != classFile.fields.length - 2) "," else ";"

    val fieldNameExpr = Sex(cpUtils.getUtf8(field.name_index))
    val modifiersExpr = ModifiersExpression(field.access_flags, flags)
    val constExp = if (value.isEmpty) EMPTY_EXPRESSION else value.get
    val signExp = if (signatureType.isEmpty) EMPTY_EXPRESSION else signatureType.get
    val declareExpr = FieldDeclareExpression(fieldNameExpr, modifiersExpr, constExp, signExp, EMPTY_EXPRESSION, descriptor, ";")
    FieldExpression(annotations, declareExpr)
  }
}
