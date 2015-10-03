package org.powlab.jeye.core.parsing

import org.powlab.jeye.core._
import org.powlab.jeye.core.Constants._
import org.powlab.jeye.core.SimpleStream

import scala.collection.mutable.ArrayBuffer

/**
 * Парсинг класс-файла.
 * Jvm specification. Chapter 4: The class File Format
 */
object ClassFileFormatParser {

  private class ClassFileParser(stream: SimpleStream) {
    private def u1(): Int = stream.readU1()

    private def u1s(): Byte = stream.readU1S()

    private def u1Array(count: Int): Array[Byte] = stream.readBytes(count)

    private def fourBytes(): Array[Byte] = u1Array(4)

    private def u2(): Int = stream.readU2()

    private def u2Array(count: Int): Array[Byte] = stream.readBytes(count * 2)

    private def u4(): Int = stream.readU4()

    private var constant_pool = Array[ConstantPoolItem]()

    private def getUtf8(tag: Int): String = {
      new String(constant_pool(tag).asInstanceOf[ConstantUtf8Info].bytes, CP_STRING_CHARSETNAME)
    }

    private def readArray[T](count: Int, creator: () => T): ArrayBuffer[T] = {
      val arrays = ArrayBuffer[T]();
      (1 to count).foreach(index => (arrays += creator()))
      arrays
    }

    private def parseAttribute(tag: Int): AttributeBaseInfo = {
      def parseAnnotationsAttribute(tag: Int): AttributeBaseInfo = {
        def parseAnnotationValue(): Annotation = {
          val type_index = u2
          val num_element_value_pairs = u2
          val element_value_pairs = readArray(num_element_value_pairs, () => ElementValuePairs(u2, parseElementValue))
          Annotation(type_index, num_element_value_pairs, element_value_pairs.toArray)
        }
        def parseArrayValue(): ArrayValue = {
          val num_values = u2
          ArrayValue(num_values, readArray(num_values, () => parseElementValue).toArray)
        }
        def parseElementValue(): ElementValue = {
          val tag: Char = u1().asInstanceOf[Char]
          tag match {
            case value if EV_CONST_VALUE contains value => ConstValueIndex(tag, u2)
            case EV_ENUM_CONST_VALUE                    => EnumConstValue(u2, u2)
            case EV_CLASS_INFO_INDEX                    => ClassInfoIndex(u2)
            case EV_ARRAY_VALUE                         => parseArrayValue
            case EV_ANNOTATION_VALUE                    => parseAnnotationValue
            case _ => throw new IllegalStateException("Not ElementValue tag: " + tag)
          }
        }
        def parseAnnotationDefaultAttribute(tag: Int): AnnotationDefaultAttribute = {
          AnnotationDefaultAttribute(tag, u4, parseElementValue)
        }
        def parseRuntimeAnnotationsAttribute(tag: Int): RuntimeAnnotationsAttribute = {
          val attribute_length = u4
          val num_annotations = u2
          val annotations = readArray(num_annotations, () => parseAnnotationValue).toArray
          RuntimeAnnotationsAttribute(tag, attribute_length, num_annotations, annotations)
        }
        def parseRuntimeParameterAnnotationsAttribute(tag: Int): RuntimeParameterAnnotationsAttribute = {
          val attribute_length = u4
          val num_parameters = u1
          val parameter_annotations = readArray(num_parameters, () => {
            val num_annotations = u2
            val annotations = readArray(num_annotations, () => parseAnnotationValue).toArray
            ParameterAnnotations(num_annotations, annotations)
          }).toArray
          RuntimeParameterAnnotationsAttribute(tag, attribute_length, num_parameters, parameter_annotations)
        }
        val attributeName = getUtf8(tag);
        attributeName match {
          case ATTR_AnnotationDefault                       => parseAnnotationDefaultAttribute(tag)
          case ATTR_RuntimeInvisibleAnnotations
           | ATTR_RuntimeVisibleAnnotations                 => parseRuntimeAnnotationsAttribute(tag)
          case ATTR_RuntimeInvisibleParameterAnnotations
           | ATTR_RuntimeVisibleParameterAnnotations        => parseRuntimeParameterAnnotationsAttribute(tag)
          case _ => throw new IllegalStateException("Not attribute tag: " + attributeName)
        }
      }
      def parseCodeAttribute(tag: Int): CodeAttribute = {
        val attribute_length = u4
        val max_stack = u2
        val max_locals = u2
        val code_length = u4
        val code = u1Array(code_length)
        val exception_table_length = u2
        val exception_table = readArray(exception_table_length, () => ExceptionTable(u2, u2, u2, u2)).toArray
        val attributes_count = u2
        val attributes = parseAttributes(attributes_count)
        CodeAttribute(tag, attribute_length, max_stack, max_locals, code_length, code,
            exception_table_length, exception_table, attributes_count, attributes)
      }
      def parseBootstrapMethodsAttribute(tag: Int): BootstrapMethodsAttribute = {
        val attribute_length = u4
        val num_bootstrap_methods = u2;
        val bootstrap_methods = readArray(num_bootstrap_methods, () => {
          val bootstrap_method_ref = u2
          val num_bootstrap_arguments = u2
          BootstrapMethods(bootstrap_method_ref, num_bootstrap_arguments, u2Array(num_bootstrap_arguments))
        })
        BootstrapMethodsAttribute(tag, attribute_length, num_bootstrap_methods, bootstrap_methods.toArray)
      }
      def parseConstantValueAttribute(tag: Int): ConstantValueAttribute = {
        ConstantValueAttribute(tag, u4, u2)
      }
      def parseLineNumberTableAttribute(tag: Int): LineNumberTableAttribute = {
        val attribute_length = u4
        val line_number_table_length = u2
        val line_number_table = readArray(line_number_table_length, () => LineNumberTable(u2, u2))
        LineNumberTableAttribute(tag, attribute_length, line_number_table_length, line_number_table.toArray)
      }
      def parseExceptionsAttribute(tag: Int): ExceptionsAttribute = {
        val attribute_length = u4
        val number_of_exceptions = u2
        val exception_index_table = readArray(number_of_exceptions, () => u2).toArray
        ExceptionsAttribute(tag, attribute_length, number_of_exceptions, exception_index_table)
      }
      def parseInnerClassesAttribute(tag: Int): InnerClassesAttribute = {
        val attribute_length = u4
        val number_of_classes = u2
        val classes = readArray(number_of_classes, () => ClassInfo(u2, u2, u2, u2))
        InnerClassesAttribute(tag, attribute_length, number_of_classes, classes.toArray)
      }
      def parseEnclosingMethodAttribute(tag: Int): EnclosingMethodAttribute = {
        EnclosingMethodAttribute(tag, u4, u2, u2)
      }
      def parseSyntheticAttribute(tag: Int): SyntheticAttribute = {
        SyntheticAttribute(tag, u4)
      }
      def parseSignatureAttribute(tag: Int): SignatureAttribute = {
        SignatureAttribute(tag, u4, u2)
      }
      def parseSourceFileAttribute(tag: Int): SourceFileAttribute = {
        SourceFileAttribute(tag, u4, u2)
      }
      def parseSourceDebugExtensionAttribute(tag: Int): SourceDebugExtensionAttribute = {
        val attribute_length = u4
        SourceDebugExtensionAttribute(tag, attribute_length, u1Array(attribute_length))
      }
      def parseLocalVariableTableAttribute(tag: Int): LocalVariableTableAttribute = {
        val attribute_length = u4
        val local_variable_table_length = u2
        val local_variable_table = readArray(local_variable_table_length, () => LocalVariableTable(u2, u2, u2, u2, u2))
        LocalVariableTableAttribute(tag, attribute_length, local_variable_table_length, local_variable_table.toArray)
      }
      def parseLocalVariableTypeTableAttribute(tag: Int): LocalVariableTypeTableAttribute = {
        val attribute_length = u4
        val local_variable_type_table_length = u2
        val local_variable_type_table = readArray(local_variable_type_table_length, () => LocalVariableTypeTable(u2, u2, u2, u2, u2))
        LocalVariableTypeTableAttribute(tag, attribute_length, local_variable_type_table_length, local_variable_type_table.toArray)
      }
      def parseDeprecatedAttribute(tag: Int): DeprecatedAttribute = {
        DeprecatedAttribute(tag, u4)
      }
      def parseUnknowAttribute(tag: Int): UnknownAttributeInfo = {
        val attribute_length = u4
        val info = u1Array(attribute_length)
        UnknownAttributeInfo(tag, attribute_length, info)
      }
      val attributeName = getUtf8(tag);
      attributeName match {
        case ATTR_RuntimeInvisibleAnnotations
           | ATTR_RuntimeVisibleAnnotations
           | ATTR_RuntimeInvisibleParameterAnnotations
           | ATTR_RuntimeVisibleParameterAnnotations
           | ATTR_AnnotationDefault         => parseAnnotationsAttribute(tag)
        case ATTR_Code                      => parseCodeAttribute(tag)
        case ATTR_BootstrapMethods          => parseBootstrapMethodsAttribute(tag)
        case ATTR_ConstantValue             => parseConstantValueAttribute(tag)
        case ATTR_LineNumberTable           => parseLineNumberTableAttribute(tag)
        case ATTR_Exceptions                => parseExceptionsAttribute(tag)
        case ATTR_InnerClasses              => parseInnerClassesAttribute(tag)
        case ATTR_EnclosingMethod           => parseEnclosingMethodAttribute(tag)
        case ATTR_Synthetic                 => parseSyntheticAttribute(tag)
        case ATTR_Signature                 => parseSignatureAttribute(tag)
        case ATTR_SourceFile                => parseSourceFileAttribute(tag)
        case ATTR_SourceDebugExtension      => parseSourceDebugExtensionAttribute(tag)
        case ATTR_LocalVariableTable        => parseLocalVariableTableAttribute(tag)
        case ATTR_LocalVariableTypeTable    => parseLocalVariableTypeTableAttribute(tag)
        case ATTR_Deprecated                => parseDeprecatedAttribute(tag)
        case _                              => parseUnknowAttribute(tag)
      }
    }

    private def parseAttributes(count: Int):Array[AttributeBaseInfo] = {
      val attributes = ArrayBuffer[AttributeBaseInfo]()
      (1 to count).foreach(index => (attributes += parseAttribute(u2)))
      attributes.toArray
    }

    private def parseMember():MemberInfo = {
      val access_flags = u2
      val name_index = u2
      val descriptor_index = u2
      val attributes_count = u2
      val attributes = parseAttributes(attributes_count)
      MemberInfo(access_flags, name_index, descriptor_index, attributes_count, attributes)
    }

    private def parseMembers(count: Int):Array[MemberInfo] = {
      val members = ArrayBuffer[MemberInfo]()
      (1 to count).foreach(index => (members += parseMember))
      members.toArray
    }

    private def parseConstantPoolItem(tag: Int): ConstantPoolItem = {
      def parseConstantClassInfo(tag: Int): ConstantClassInfo = {
        ConstantClassInfo(tag, u2)
      }
      def parseConstantRefInfo(tag: Int): ConstantRefInfo = {
        ConstantRefInfo(tag, u2, u2)
      }
      def parseConstantStringInfo(tag: Int): ConstantStringInfo = {
        ConstantStringInfo(tag, u2)
      }
      def parseConstantU4Info(tag: Int): ConstantU4Info = {
        ConstantU4Info(tag, fourBytes)
      }
      def parseConstantU8Info(tag: Int): ConstantU8Info = {
        ConstantU8Info(tag, fourBytes, fourBytes)
      }
      def parseConstantNameAndTypeInfo(tag: Int): ConstantNameAndTypeInfo = {
        ConstantNameAndTypeInfo(tag, u2, u2)
      }
      def parseConstantUtf8Info(tag: Int): ConstantUtf8Info = {
        val count = u2
        ConstantUtf8Info(tag, count, u1Array(count))
      }
      def parseConstantMethodHandleInfo(tag: Int): ConstantMethodHandleInfo = {
        ConstantMethodHandleInfo(tag, u1, u2)
      }
      def parseConstantMethodTypeInfo(tag: Int): ConstantMethodTypeInfo = {
        ConstantMethodTypeInfo(tag, u2)
      }
      def parseConstantInvokeDynamicInfo(tag: Int): ConstantInvokeDynamicInfo = {
        ConstantInvokeDynamicInfo(tag, u2, u2)
      }
      tag match {
        case CONSTANT_Class => parseConstantClassInfo(tag)
        case CONSTANT_Fieldref => parseConstantRefInfo(tag)
        case CONSTANT_Methodref => parseConstantRefInfo(tag)
        case CONSTANT_InterfaceMethodref => parseConstantRefInfo(tag)
        case CONSTANT_String => parseConstantStringInfo(tag)
        case CONSTANT_Integer => parseConstantU4Info(tag)
        case CONSTANT_Float => parseConstantU4Info(tag)
        case CONSTANT_Long => parseConstantU8Info(tag)
        case CONSTANT_Double => parseConstantU8Info(tag)
        case CONSTANT_NameAndType => parseConstantNameAndTypeInfo(tag)
        case CONSTANT_Utf8 => parseConstantUtf8Info(tag)
        case CONSTANT_MethodHandle => parseConstantMethodHandleInfo(tag)
        case CONSTANT_MethodType => parseConstantMethodTypeInfo(tag)
        case CONSTANT_InvokeDynamic => parseConstantInvokeDynamicInfo(tag)
        case _ => throw new IllegalStateException("Unknow constant pool item tag: " + tag)
      }
    }

    private def parseConstantPool(count: Int):Array[ConstantPoolItem] = {
        val constantPool = ArrayBuffer[ConstantPoolItem]()
        constantPool += null
        var index:Int = 1;
        while (index < count) {
          val tag = u1
          constantPool += parseConstantPoolItem(tag)
          if (tag == CONSTANT_Long  || tag == CONSTANT_Double) {
            index += 1
            constantPool += null
          }
          index += 1
        }
        constantPool.toArray
    }

    def parseClassFile(): ClassFile = {
        val magic = u4()
        val minor_version = u2()
        val major_version = u2()
        val constant_pool_count = u2()
        constant_pool = parseConstantPool(constant_pool_count)
        val access_flags = u2()
        val this_class = u2()
        val super_class = u2()
        val interfaces_count = u2()
        val interfaces = u2Array(interfaces_count)
        val fields_count = u2()
        val fields = parseMembers(fields_count)
        val methods_count = u2()
        val methods = parseMembers(methods_count)
        val attributes_count = u2()
        val attributes = parseAttributes(attributes_count)
        ClassFile(magic, minor_version, major_version, constant_pool_count, constant_pool, access_flags, this_class, super_class,
            interfaces_count, interfaces, fields_count, fields, methods_count, methods, attributes_count, attributes)
    }

  }
  def parse(stream: SimpleStream):ClassFile = {
    val parser = new ClassFileParser(stream);
    parser.parseClassFile
  }

}