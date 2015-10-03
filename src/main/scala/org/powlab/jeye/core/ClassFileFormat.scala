package org.powlab.jeye.core

import org.powlab.jeye.core.Constants._
import org.powlab.jeye.utils.ConstantPoolUtils

/**
 * ----------------------------------------------------
 * Формат класс-файла.
 * Jvm specification. Chapter 4: The class File Format
 * ----------------------------------------------------
 */

abstract class ElementValue(val tag: Char)
abstract class ConstantPoolItem(val tag: Int) {}
abstract class AttributeBaseInfo(
  val attribute_name_index: Int, // u1
  val attribute_length: Int // u4
  )
abstract class VerificationBaseType(tag: Int) // u1
abstract class BaseFrame(frame_type: Int)

case class ClassFile(
  magic: Int, // u4
  minor_version: Int, // u2
  major_version: Int, // u2
  constant_pool_count: Int, // u2
  constant_pool: Array[ConstantPoolItem],
  access_flags: Int, // u2
  this_class: Int, // u2
  super_class: Int, // u2
  interfaces_count: Int, // u2
  interfaces: Array[Byte], // u2Array
  fields_count: Int, // u2
  fields: Array[MemberInfo],
  methods_count: Int, // u2
  methods: Array[MemberInfo],
  attributes_count: Int, // u2
  attributes: Array[AttributeBaseInfo]) { val constantPoolUtils = new ConstantPoolUtils(this) }

case class ConstantInvokeDynamicInfo(
  override val tag: Int, // u1
  bootstrap_method_attr_index: Int, // u2
  name_and_type_index: Int // u2
  ) extends ConstantPoolItem(tag)

case class ConstantMethodTypeInfo(
  override val tag: Int, // u1
  descriptor_index: Int // u2
  ) extends ConstantPoolItem(tag)

case class ConstantMethodHandleInfo(
  override val tag: Int, // u1
  reference_kind: Int, // u1
  reference_index: Int // u2
  ) extends ConstantPoolItem(tag)

case class ConstantUtf8Info(
  override val tag: Int, // u1
  length: Int, // u2
  bytes: Array[Byte] // u1Array
  ) extends ConstantPoolItem(tag)

case class ConstantNameAndTypeInfo(
  override val tag: Int, // u1
  name_index: Int, // u2
  descriptor_index: Int // u2
  ) extends ConstantPoolItem(tag)

case class ConstantU8Info(
  override val tag: Int, // u1
  high_bytes: Array[Byte], // fourBytes _,
  low_bytes: Array[Byte] // fourBytes _
  ) extends ConstantPoolItem(tag)

//  class ConstantLongInfo = ConstantU8Info
//  class ConstantDoubleInfo = ConstantU8Info

case class ConstantU4Info(
  override val tag: Int, // u1
  bytes: Array[Byte] // fourBytes _
  ) extends ConstantPoolItem(tag)
//  class ConstantIntegerInfo = ConstantU4Info
//  class ConstantFloatInfo = ConstantU4Info

case class ConstantStringInfo(
  override val tag: Int, // u1
  string_index: Int // u2
  ) extends ConstantPoolItem(tag)

case class ConstantRefInfo(
  override val tag: Int, // u1
  class_index: Int, // u2
  name_and_type_index: Int // u2
  ) extends ConstantPoolItem(tag)

//  class ConstantFieldRefInfo = ConstantRefInfo
//  class ConstantMethodRefInfo = ConstantRefInfo
//  class ConstantInterfaceMethodrefInfo = ConstantRefInfo

case class ConstantClassInfo(
  override val tag: Int, // u1
  name_index: Int // u2
  ) extends ConstantPoolItem(tag)

case class UnknownAttributeInfo(
  override val attribute_name_index: Int, // u1
  override val attribute_length: Int, // u4
  info: Array[Byte] // u1Array
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class RuntimeParameterAnnotationsAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  num_parameters: Int, // u1
  parameter_annotations: Array[ParameterAnnotations]) extends AttributeBaseInfo(attribute_name_index, attribute_length)

//class RuntimeVisibleParameterAnnotationsAttribute = RuntimeParameterAnnotationsAttribute
//class RuntimeInvisibleParameterAnnotationsAttribute = RuntimeParameterAnnotationsAttribute

case class RuntimeAnnotationsAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  num_annotations: Int, // u2
  annotations: Array[Annotation]) extends AttributeBaseInfo(attribute_name_index, attribute_length)

//class RuntimeVisibleAnnotationsAttribute = RuntimeAnnotationsAttribute
//class RuntimeInvisibleAnnotationsAttribute = RuntimeAnnotationsAttribute

case class DeprecatedAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int // u4
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class AnnotationDefaultAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  default_value: ElementValue) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class LocalVariableTypeTableAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  local_variable_type_table_length: Int, // u2
  local_variable_type_table: Array[LocalVariableTypeTable]) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class LocalVariableTableAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  local_variable_table_length: Int, // u2
  local_variable_table: Array[LocalVariableTable]) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class SourceDebugExtensionAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  debug_extension: Array[Byte] // u1Array
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class SourceFileAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  sourcefile_index: Int // u2
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class SignatureAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  signature_index: Int // u2
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class SyntheticAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int // u4
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class EnclosingMethodAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  class_index: Int, // u2
  method_index: Int // u2
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class InnerClassesAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  number_of_classes: Int, // u2
  classes: Array[ClassInfo]) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class ExceptionsAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  number_of_exceptions: Int, // u2
  exception_index_table: Array[Int] // u2Array
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class LineNumberTableAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  line_number_table_length: Int, // u2
  line_number_table: Array[LineNumberTable]) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class StackMapTableAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  number_of_entries: Int, // u2
  entries: BaseFrame) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class ConstantValueAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  constant_value_index: Int // u2
  ) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class CodeAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  max_stack: Int, // u2
  max_locals: Int, // u2
  code_length: Int, // u4
  code: Array[Byte], // u1Array
  exception_table_length: Int, // u2
  exception_table: Array[ExceptionTable],
  attributes_count: Int, // u2
  attributes: Array[AttributeBaseInfo]) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class BootstrapMethodsAttribute(
  override val attribute_name_index: Int, // u2
  override val attribute_length: Int, // u4
  num_bootstrap_methods: Int, // u2
  bootstrap_methods: Array[BootstrapMethods]) extends AttributeBaseInfo(attribute_name_index, attribute_length)

case class BootstrapMethods(
  bootstrap_method_ref: Int, // u2
  num_bootstrap_arguments: Int, // u2
  bootstrap_arguments: Array[Byte] // u2Array
  )

case class ClassInfoIndex(
  class_info_index: Int // u2
  ) extends ElementValue(EV_CLASS_INFO_INDEX)

case class EnumConstValue(
  type_name_index: Int, // u2
  const_name_index: Int // u2
  ) extends ElementValue(EV_ENUM_CONST_VALUE)

case class ConstValueIndex(
  override val tag: Char, // u1
  const_value_index: Int // u2
  ) extends ElementValue(tag)

case class ArrayValue(
  num_values: Int, // u2
  values: Array[ElementValue]) extends ElementValue(EV_ARRAY_VALUE)

case class LocalVariableTable(
  start_pc: Int, // u2
  length: Int, // u2
  name_index: Int, // u2
  descriptor_index: Int, // u2
  index: Int // u2
  )

case class LocalVariableTypeTable(
  start_pc: Int, // u2
  length: Int, // u2
  name_index: Int, // u2
  signature_index: Int, // u2
  index: Int // u2
  )

case class ClassInfo(
  inner_class_info_index: Int, // u2
  outer_class_info_index: Int, // u2
  inner_name_index: Int, // u2
  inner_class_access_flags: Int // u2
  )

case class ElementValuePairs(
  element_name_index: Int, // u2
  value: ElementValue)

case class Annotation(
  type_index: Int, // u2
  num_element_value_pairs: Int, // u2
  element_value_pairs: Array[ElementValuePairs]) extends ElementValue(EV_ANNOTATION_VALUE)

case class ParameterAnnotations(
  num_annotations: Int, // u2
  annotations: Array[Annotation])

case class LineNumberTable(
  start_pc: Int, // u2
  line_number: Int // u2
  )

case class TopVariableInfo(
  tag: Int // u1
  ) extends VerificationBaseType(tag)

case class IntegerVariableInfo(
  tag: Int // u1
  ) extends VerificationBaseType(tag)

case class FloatVariableInfo(
  tag: Int // u1
  ) extends VerificationBaseType(tag)

case class LongVariableInfo(
  tag: Int // u1
  ) extends VerificationBaseType(tag)

case class DoubleVariableInfo(
  tag: Int // u1
  ) extends VerificationBaseType(tag)

case class NullVariableInfo(
  tag: Int // u1
  ) extends VerificationBaseType(tag)

case class UninitializedThisVariableInfo(
  tag: Int // u1
  ) extends VerificationBaseType(tag)

case class ObjectVariableInfo(
  tag: Int, // u1
  cpool_index: Int // u2
  ) extends VerificationBaseType(tag)

case class UninitializedVariableInfo(
  tag: Int, // u1
  offset: Int // u2
  ) extends VerificationBaseType(tag)

case class FullFrame(
  frame_type: Int, // u1
  offset_delta: Int, // u2
  number_of_locals: Int, // u2
  locals: VerificationBaseType,
  number_of_stack_items: Int, // u2
  stack: Array[VerificationBaseType]) extends BaseFrame(frame_type)

case class AppendFrame(
  frame_type: Int, // u1
  offset_delta: Int, // u2
  locals: Array[VerificationBaseType]) extends BaseFrame(frame_type)

case class SameFrameExtended(
  frame_type: Int, // u1
  offset_delta: Int // u2
  ) extends BaseFrame(frame_type)

case class ChopFrame(
  frame_type: Int, // u1
  offset_delta: Int // u2
  ) extends BaseFrame(frame_type)

case class SameLocals1StackItemFrameExtended(
  frame_type: Int, // u1
  offset_delta: Int, // u2
  stack: VerificationBaseType) extends BaseFrame(frame_type)

case class SameLocals1StackItemFrame(
  frame_type: Int, // u1
  stack: VerificationBaseType) extends BaseFrame(frame_type)

case class SameFrame(
  frame_type: Int // u1
  ) extends BaseFrame(frame_type)

case class ExceptionTable(
  start_pc: Int, // u2
  end_pc: Int, // u2
  handler_pc: Int, // u2
  catch_type: Int // u2
  )

case class MemberInfo(
  access_flags: Int, // u2
  name_index: Int, // u2
  descriptor_index: Int, // u2
  attributes_count: Int, // u2
  attributes: Array[AttributeBaseInfo])

