package org.powlab.jeye.core

object Constants {

  val CONSTANT_Class = 7
  val CONSTANT_Fieldref = 9
  val CONSTANT_Methodref = 10
  val CONSTANT_InterfaceMethodref = 11
  val CONSTANT_String = 8
  val CONSTANT_Integer = 3
  val CONSTANT_Float = 4
  val CONSTANT_Long = 5
  val CONSTANT_Double = 6
  val CONSTANT_NameAndType = 12
  val CONSTANT_Utf8 = 1
  val CONSTANT_MethodHandle = 15
  val CONSTANT_MethodType = 16
  val CONSTANT_InvokeDynamic = 18

  val ATTR_ConstantValue = "ConstantValue"
  val ATTR_Code = "Code"
  val ATTR_StackMapTable = "StackMapTable"
  val ATTR_Exceptions = "Exceptions"
  val ATTR_InnerClasses = "InnerClasses"
  val ATTR_EnclosingMethod = "EnclosingMethod"
  val ATTR_Synthetic = "Synthetic"
  val ATTR_Signature = "Signature"
  val ATTR_SourceFile = "SourceFile"
  val ATTR_SourceDebugExtension = "SourceDebugExtension"
  val ATTR_LineNumberTable = "LineNumberTable"
  val ATTR_LocalVariableTable = "LocalVariableTable"
  val ATTR_LocalVariableTypeTable = "LocalVariableTypeTable"
  val ATTR_Deprecated = "Deprecated"
  val ATTR_RuntimeVisibleAnnotations = "RuntimeVisibleAnnotations"
  val ATTR_RuntimeInvisibleAnnotations = "RuntimeInvisibleAnnotations"
  val ATTR_RuntimeVisibleParameterAnnotations = "RuntimeVisibleParameterAnnotations"
  val ATTR_RuntimeInvisibleParameterAnnotations = "RuntimeInvisibleParameterAnnotations"
  val ATTR_AnnotationDefault = "AnnotationDefault"
  val ATTR_BootstrapMethods = "BootstrapMethods"

  val EMPTY_OBJECT = {
    "toString" ->() -> {
      "EMPTY_OBJECT"
    }
  }

  val SAME_FRAME_TAG = Range(0, 63)
  val SAME_LOCALS_1_STACK_ITEM_FRAME = Range(64, 127)
  val SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = Range(247, 247)
  val CHOP_FRAME = Range(248, 250)
  val SAME_FRAME_EXTENDED = Range(251, 251)
  val APPEND_FRAME = Range(252, 254)
  val FULL_FRAME = Range(255, 255)

  val ITEM_TOP = 0
  val ITEM_INTEGER = 1
  val ITEM_FLOAT = 2
  val ITEM_DOUBLE = 3
  val ITEM_LONG = 4
  val ITEM_NULL = 5
  val ITEM_UNINITIALIZED_THIS = 6
  val ITEM_OBJECT = 7
  val ITEM_UNINITIALIZED = 8

  /** ----------------- Method Handler Types Block ----------------------------- */
  class MethodHandlerType(code: Number, mnemonic: String, description: String) {
    override def toString = "code: " + code + ", mnemonic: " + mnemonic + ", description: " + description

    MH_TYPES(code) = this
  }

  val MH_TYPES = scala.collection.mutable.Map[Number, MethodHandlerType]()
  val MH_GET_FIELD = new MethodHandlerType(1, "getfield", "getfield C.f:T")
  val MH_GET_STATIC = new MethodHandlerType(2, "getstatic", "getstatic C.f:T")
  val MH_PUT_FIELD = new MethodHandlerType(3, "putfield", "putfield C.f:T")
  val MH_PUT_STATIC = new MethodHandlerType(4, "putstatic", "putstatic C.f:T")
  val MH_INVOKE_VIRTUAL = new MethodHandlerType(5, "invokevirtual", "invokevirtual C.m:(A*)T")
  val MH_INVOKE_STATIC = new MethodHandlerType(6, "invokestatic", "invokestatic C.m:(A*)T")
  val MH_INVOKE_SPECIAL = new MethodHandlerType(7, "invokespecial", "invokespecial C.m:(A*)T")
  val MH_INVOKE_NEW_SPECIAL = new MethodHandlerType(8, "newinvokespecial", "new C; dup; invokespecial C.<init>:(A*)void")
  val MH_INVOKE_INTERFACE = new MethodHandlerType(9, "invokeinterface", "invokeinterface C.m:(A*)T")

  def getMethodHandlerType(code: Int): MethodHandlerType = MH_TYPES(code)

  /** ----------------- Modifiers Block ----------------------------- */
  val ZERO_STRUCT = {
    toString ->() -> "ZERO_STRUCT"
  }

  val EV_BYTE = 'B'
  val EV_CHAR = 'C'
  val EV_DOUBLE = 'D'
  val EV_FLOAT = 'F'
  val EV_INT = 'I'
  val EV_LONG = 'J'
  val EV_SHORT = 'S'
  val EV_BOOLEAN = 'Z'
  val EV_STRING = 's'
  val EV_CONST_VALUE = List('B', 'C', 'D', 'F', 'I', 'J', 'S', 'Z', 's')
  val EV_ENUM_CONST_VALUE = 'e'
  val EV_CLASS_INFO_INDEX = 'c'
  val EV_ANNOTATION_VALUE = '@'
  val EV_ARRAY_VALUE = '['

  // TODO here:  перенести в ExpressionViewer
  val THIS_CONSTANT = "this"
  val SUPER_CONSTANT = "super"

  val WILDCARD_PLUS = '+'
  val WILDCARD_MINUS = '-'
  val WILDCARD_ASTERISK = '*'

  val DIRECTIVE_ELSE = 1
  val DIRECTIVE_JUMP = 2

  val CP_STRING_CHARSETNAME = "UTF8"

}
