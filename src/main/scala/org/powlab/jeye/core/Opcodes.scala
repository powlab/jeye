package org.powlab.jeye.core

/**
 * Файл с описанием всех Java Virtual Machine instruction opcodes
 *
 * TODO here: вынести классы из скопа Opcodes
 * TODO here: удалить из конструктора OpCode выражение OPCODES(code) = this
 * TODO here: добавить в класс OpCode длину, занимаемую в байткоде
 */
object Opcodes {

  private val EMPTY_PARAMS = Array.empty[Param]
  private val EMPTY_VALUES = Array.empty[Param]

  case class Param(name: String, size: Int, description: String, sign: Boolean)

  val OTYPE_CONSTANT = 1 << 0
  val OTYPE_LOAD = 1 << 1
  val OTYPE_STORE = 1 << 2
  val OTYPE_STACK = 1 << 3
  val OTYPE_MATH = 1 << 4
  val OTYPE_CONVERSION = 1 << 5
  val OTYPE_COMPARISON = 1 << 6
  val OTYPE_CONTROL = 1 << 7
  val OTYPE_REFERENCE = 1 << 8
  val OTYPE_EXTENDS = 1 << 9
  val OTYPE_RESERVED = 1 << 10
  val OTYPE_CUSTOM = 1 << 11;

  def categoryName(category: Int): String = {
    category match {
      case OTYPE_CONSTANT => "CONSTANT"
      case OTYPE_LOAD => "LOAD"
      case OTYPE_STORE => "STORE"
      case OTYPE_STACK => "STACK"
      case OTYPE_MATH => "MATH"
      case OTYPE_CONVERSION => "CONVERSION"
      case OTYPE_COMPARISON => "COMPARISON"
      case OTYPE_CONTROL => "CONTROL"
      case OTYPE_REFERENCE => "REFERENCE"
      case OTYPE_EXTENDS => "EXTENDS"
      case OTYPE_RESERVED => "RESERVED"
      case OTYPE_CUSTOM => "CUSTOM"
      case _ => "UNKNOWN"
    }
  }

  val OPCODES = new Array[OpCode](320)

  case class OpCode(name: String, code: Int, params: Array[Param], operation: String, description: String, category: Int) {
    OPCODES(code) = this

    val hex: String = "0x" + code.toHexString
  }

  val OPCODE_AALOAD = new OpCode("aaload", 50, EMPTY_PARAMS, "Load reference from array",
    "The arrayref must be of type reference and must refer to an array whose components are of type reference. " +
      "The index must be of type int. Both arrayref and index are popped from the operand stack. " +
      "The reference value in the component of the array at index is retrieved and pushed onto the operand stack.", OTYPE_LOAD)

  val OPCODE_AASTORE = new OpCode("aastore", 83, EMPTY_PARAMS, "Store into reference array",
    "The arrayref must be of type reference and must refer to an array whose components are of type reference. " +
      "The index must be of type int and value must be of type reference. The arrayref, index, and value are popped from the operand stack. " +
      "The reference value is stored as the component of the array at index.", OTYPE_STORE)

  val OPCODE_ACONST_NULL = new OpCode("aconst_null", 1, EMPTY_PARAMS, "Push null", "Push the null object reference onto the operand stack.", OTYPE_CONSTANT)

  val OPCODE_ALOAD = new OpCode("aload", 25,
    Array(new Param("index", 1, "The index is an unsigned byte that must be an index into the local variable array of the current frame", false)),
    "Load reference from local variable",
    "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at index must contain a reference. The objectref in the local variable at index is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_ALOAD_0 = new OpCode("aload_0", 42, EMPTY_PARAMS, "Load reference from local variable",
    "The <0> must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at <0> must contain a reference. The objectref in the local variable at <0> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_ALOAD_1 = new OpCode("aload_1", 43, EMPTY_PARAMS, "Load reference from local variable",
    "The <1> must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at <1> must contain a reference. The objectref in the local variable at <1> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_ALOAD_2 = new OpCode("aload_2", 44, EMPTY_PARAMS, "Load reference from local variable",
    "The <2> must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at <2> must contain a reference. The objectref in the local variable at <2> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_ALOAD_3 = new OpCode("aload_3", 45, EMPTY_PARAMS, "Load reference from local variable",
    "The <3> must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at <3> must contain a reference. The objectref in the local variable at <3> is pushed onto the operand stack.", OTYPE_LOAD)

  val OPCODE_ANEWARRAY = new OpCode("anewarray", 189,
    Array(new Param("indexbyte1", 1, "index is (indexbyte1 << 8) | indexbyte2", false),
      new Param("indexbyte2", 1, "index is (indexbyte1 << 8) | indexbyte2", false)),
    "Create new array of reference",
    "The count must be of type int. It is popped off the operand stack. " +
      "The count represents the number of components of the array to be created. " +
      "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run-time constant pool item at that index must be a symbolic reference to a class, array, or interface type. " +
      "The named class, array, or interface type is resolved (§5.4.3.1). " +
      "A new array with components of that type, of length count, is allocated from the garbage-collected heap, " +
      "and a reference arrayref to this new array object is pushed onto the operand stack. " +
      "All components of the new array are initialized to null, the default value for reference types (§2.4).", OTYPE_REFERENCE)

  val OPCODE_ARETURN = new OpCode("areturn", 176, EMPTY_PARAMS, "Return reference from method",
    "The objectref must be of type reference and must refer to an object of a type that " +
      "is assignment compatible (JLS §5.2) with the type represented by the return descriptor (§4.3.3) of the current method. " +
      "If the current method is a synchronized method, the monitor entered or reentered on invocation of the method " +
      "is updated and possibly exited as if by execution of a monitorexit instruction (§monitorexit) in the current thread. " +
      "If no exception is thrown, objectref is popped from the operand stack of the current frame (§2.6) " +
      "and pushed onto the operand stack of the frame of the invoker. " +
      "Any other values on the operand stack of the current method are discarded.", OTYPE_CONTROL)

  val OPCODE_ARRAYLENGTH = new OpCode("arraylength", 190, EMPTY_PARAMS, "Get length of array",
    "The arrayref must be of type reference and must refer to an array. It is popped from the operand stack. " +
      "The length of the array it references is determined. That length is pushed onto the operand stack as an int.", OTYPE_REFERENCE)

  val OPCODE_ASTORE = new OpCode("astore", 58,
    Array(new Param("index", 1, "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6)", false)),
    "Store reference into local variable",
    "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6). " +
      "The objectref on the top of the operand stack must be of type returnAddress or of type reference. " +
      "It is popped from the operand stack, and the value of the local variable at index is set to objectref.", OTYPE_STORE)
  val OPCODE_ASTORE_0 = new OpCode("astore_0", 75, EMPTY_PARAMS, "Store reference into local variable",
    "The <0> must be an index into the local variable array of the current frame (§2.6). " +
      "The objectref on the top of the operand stack must be of type returnAddress or of type reference. " +
      "It is popped from the operand stack, and the value of the local variable at <0> is set to objectref.", OTYPE_STORE)
  val OPCODE_ASTORE_1 = new OpCode("astore_1", 76, EMPTY_PARAMS, "Store reference into local variable",
    "The <1> must be an index into the local variable array of the current frame (§2.6). " +
      "The objectref on the top of the operand stack must be of type returnAddress or of type reference. " +
      "It is popped from the operand stack, and the value of the local variable at <1> is set to objectref.", OTYPE_STORE)
  val OPCODE_ASTORE_2 = new OpCode("astore_2", 77, EMPTY_PARAMS, "Store reference into local variable",
    "The <2> must be an index into the local variable array of the current frame (§2.6). " +
      "The objectref on the top of the operand stack must be of type returnAddress or of type reference. " +
      "It is popped from the operand stack, and the value of the local variable at <2> is set to objectref.", OTYPE_STORE)
  val OPCODE_ASTORE_3 = new OpCode("astore_3", 78, EMPTY_PARAMS, "Store reference into local variable",
    "The <3> must be an index into the local variable array of the current frame (§2.6). " +
      "The objectref on the top of the operand stack must be of type returnAddress or of type reference. " +
      "It is popped from the operand stack, and the value of the local variable at <3> is set to objectref.", OTYPE_STORE)

  val OPCODE_ATHROW = new OpCode("athrow", 191, EMPTY_PARAMS, "Throw exception or error",
    "The objectref must be of type reference and must refer to an object that is an instance of class Throwable or of a subclass of Throwable. " +
      "It is popped from the operand stack. " +
      "The objectref is then thrown by searching the current method (§2.6) for the first exception handler that matches the class of objectref, " +
      "as given by the algorithm in §2.10." +
      "If an exception handler that matches objectref is found, it contains the location of the code intended to handle this exception. " +
      "The pc register is reset to that location, the operand stack of the current frame is cleared, " +
      "objectref is pushed back onto the operand stack, and execution continues." +
      "If no matching exception handler is found in the current frame, that frame is popped. " +
      "If the current frame represents an invocation of a synchronized method, " +
      "the monitor entered or reentered on invocation of the method is exited as if by execution of a monitorexit instruction (§monitorexit). " +
      "Finally, the frame of its invoker is reinstated, if such a frame exists, and the objectref is rethrown. " +
      "If no such frame exists, the current thread exits.", OTYPE_REFERENCE)

  val OPCODE_BALOAD = new OpCode("baload", 51, EMPTY_PARAMS, "Load byte or boolean from array",
    "The arrayref must be of type reference and must refer to an array whose components are of type byte or of type boolean. " +
      "The index must be of type int. Both arrayref and index are popped from the operand stack. " +
      "The byte value in the component of the array at index is retrieved, sign-extended to an int value, " +
      "and pushed onto the top of the operand stack.", OTYPE_LOAD)

  val OPCODE_BASTORE = new OpCode("bastore", 84, EMPTY_PARAMS, "Store into byte or boolean array",
    "The arrayref must be of type reference and must refer to an array whose components are of type byte or of type boolean. " +
      "The index and the value must both be of type int. The arrayref, index, and value are popped from the operand stack. " +
      "The int value is truncated to a byte and stored as the component of the array indexed by index.", OTYPE_STORE)

  val OPCODE_BIPUSH = OpCode("bipush", 16,
    Array(new Param("byte", 1, "The immediate byte is sign-extended to an int value", true)),
    "Push byte",
    "The immediate byte is sign-extended to an int value. That value is pushed onto the operand stack.", OTYPE_CONSTANT)

  val OPCODE_CALOAD = new OpCode("caload", 52, EMPTY_PARAMS, "Load char from array",
    "The arrayref must be of type reference and must refer to an array whose components are of type char. " +
      "The index must be of type int. Both arrayref and index are popped from the operand stack. " +
      "The component of the array at index is retrieved and zero-extended to an int value. " +
      "That value is pushed onto the operand stack.", OTYPE_LOAD)

  val OPCODE_CASTORE = new OpCode("castore", 85, EMPTY_PARAMS, "Store into char array",
    "The arrayref must be of type reference and must refer to an array whose components are of type char. " +
      "The index and the value must both be of type int. The arrayref, index, and value are popped from the operand stack. " +
      "The int value is truncated to a char and stored as the component of the array indexed by index.", OTYPE_STORE)

  val OPCODE_CHECKCAST = new OpCode("checkcast", 192,
    Array(new Param("indexbyte1", 1, "the index is (indexbyte1 << 8) | indexbyte2", false),
      new Param("indexbyte2", 1, "the index is (indexbyte1 << 8) | indexbyte2", false)),
    "Check whether object is of given type",
    "The objectref must be of type reference. The unsigned indexbyte1 and indexbyte2 are used to construct an index " +
      "into the run-time constant pool of the current class (§2.6), where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run-time constant pool item at the index must be a symbolic reference to a class, array, or interface type." +
      "If objectref is null, then the operand stack is unchanged." +
      "Otherwise, the named class, array, or interface type is resolved (§5.4.3.1). " +
      "If objectref can be cast to the resolved class, array, or interface type, the operand stack is unchanged; " +
      "otherwise, the checkcast instruction throws a ClassCastException.", OTYPE_REFERENCE)

  val OPCODE_D2F = new OpCode("d2f", 144, EMPTY_PARAMS, "Convert double to float",
    "The value on the top of the operand stack must be of type double. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3) resulting in value'. " +
      "Then value' is converted to a float result using IEEE 754 round to nearest mode. The result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_D2I = new OpCode("d2i", 142, EMPTY_PARAMS, "Convert double to int",
    "The value on the top of the operand stack must be of type double. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3) resulting in value'. " +
      "Then value' is converted to an int. The result is pushed onto the operand stack:" +
      "If the value' is NaN, the result of the conversion is an int 0." +
      "Otherwise, if the value' is not an infinity, it is rounded to an integer value V, " +
      "rounding towards zero using IEEE 754 round towards zero mode. If this integer value V can be represented as an int, " +
      "then the result is the int value V" +
      "Otherwise,eitherthevalue'mustbetoosmall(anegativevalue of large magnitude or negative infinity), " +
      "and the result is the smallest representable value of type int, or the value' must be too large " +
      "(a positive value of large magnitude or positive infinity), and the result is the largest representable value of type int.", OTYPE_CONVERSION)
  val OPCODE_D2L = new OpCode("d2l", 143, EMPTY_PARAMS, "Convert double to long",
    "The value on the top of the operand stack must be of type double. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3) resulting in value'. " +
      "Then value' is converted to a long. The result is pushed onto the operand stack", OTYPE_CONVERSION)
  val OPCODE_DADD = new OpCode("dadd", 99, EMPTY_PARAMS, "Add double",
    "Both value1 and value2 must be of type double. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. The double result is value1' + value2'. The result is pushed onto the operand stack", OTYPE_MATH)
  val OPCODE_DALOAD = new OpCode("daload", 49, EMPTY_PARAMS, "Load double from array",
    "The arrayref must be of type reference and must refer to an array whose components are of type double. " +
      "The index must be of type int. Both arrayref and index are popped from the operand stack. " +
      "The double value in the component of the array at index is retrieved and pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_DASTORE = new OpCode("dastore", 82, EMPTY_PARAMS, "Store into double array",
    "The arrayref must be of type reference and must refer to an array whose components are of type double. " +
      "The index must be of type int, and value must be of type double. The arrayref, index, and value are popped from the operand stack. " +
      "The double value undergoes value set conversion (§2.8.3), resulting in value', " +
      "which is stored as the component of the array indexed by index.", OTYPE_STORE)
  val OPCODE_DCMPG = new OpCode("dcmpg", 152, EMPTY_PARAMS, "Compare double [greater]",
    "Both value1 and value2 must be of type double. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. A floating- point comparison is performed:" +
      "• Ifvalue1'isgreaterthanvalue2',theintvalue1ispushedonto the operand stack." +
      "• Otherwise, if value1' is equal to value2', the int value 0 is pushed onto the operand stack." +
      "• Otherwise, if value1' is less than value2', the int value -1 is pushed onto the operand stack." +
      "• Otherwise,at least one of value1' or value2' isNaN.The dcmpg instruction pushes the int value 1 onto the operand stack " +
      "and the dcmpl instruction pushes the int value -1 onto the operand stack.", OTYPE_COMPARISON)
  val OPCODE_DCMPL = new OpCode("dcmpl", 151, EMPTY_PARAMS, "Compare double [less]",
    "Both value1 and value2 must be of type double. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. A floating- point comparison is performed:" +
      "• Ifvalue1'isgreaterthanvalue2',theintvalue1ispushedonto the operand stack." +
      "• Otherwise, if value1' is equal to value2', the int value 0 is pushed onto the operand stack." +
      "• Otherwise, if value1' is less than value2', the int value -1 is pushed onto the operand stack." +
      "• Otherwise,at least one of value1' or value2' isNaN.The dcmpg instruction pushes the int value 1 onto the operand stack " +
      "and the dcmpl instruction pushes the int value -1 onto the operand stack.", OTYPE_COMPARISON)
  val OPCODE_DCONST_0 = new OpCode("dconst_0", 14, EMPTY_PARAMS, "Push double 0.0", "Push the double constant <0.0> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_DCONST_1 = new OpCode("dconst_1", 15, EMPTY_PARAMS, "Push double 1.0", "Push the double constant <1.0> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_DDIV = new OpCode("ddiv", 111, EMPTY_PARAMS, "Divide double",
    "Both value1 and value2 must be of type double. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. The double result is value1' / value2'. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_DLOAD = new OpCode("dload", 24,
    Array(new Param("index", 1, "The index is an unsigned byte", false)),
    "Load double from local variable",
    "The index is an unsigned byte. Both index and index+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The local variable at index must contain a double. The value of the local variable at index is pushed onto the operand stack", OTYPE_LOAD)
  val OPCODE_DLOAD_0 = new OpCode("dload_0", 38, EMPTY_PARAMS, "Load double from local variable",
    "Both <0> and <0>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The local variable at <n> must contain a double. The value of the local variable at <0> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_DLOAD_1 = new OpCode("dload_1", 39, EMPTY_PARAMS, "Load double from local variable",
    "Both <1> and <1>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The local variable at <n> must contain a double. The value of the local variable at <1> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_DLOAD_2 = new OpCode("dload_2", 40, EMPTY_PARAMS, "Load double from local variable",
    "Both <2> and <2>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The local variable at <n> must contain a double. The value of the local variable at <2> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_DLOAD_3 = new OpCode("dload_3", 41, EMPTY_PARAMS, "Load double from local variable",
    "Both <3> and <3>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The local variable at <n> must contain a double. The value of the local variable at <3> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_DMUL = new OpCode("dmul", 107, EMPTY_PARAMS, "Multiply double",
    "Both value1 and value2 must be of type double. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. The double result is value1' * value2'. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_DNEG = new OpCode("dneg", 119, EMPTY_PARAMS, "Negate double",
    "The value must be of type double. It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "The double result is the arithmetic negation of value'. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_DREM = new OpCode("drem", 115, EMPTY_PARAMS, "Remainder double",
    "Both value1 and value2 must be of type double. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. The result is calculated and pushed onto the operand stack as a double." +
      "The result of a drem instruction is not the same as that of the so- called remainder operation defined by IEEE 754. " +
      "The IEEE 754 'remainder' operation computes the remainder from a rounding division, not a truncating division, " +
      "and so its behavior is not analogous to that of the usual integer remainder operator. Instead, " +
      "the Java Virtual Machine defines drem to behave in a manner analogous to that of the Java Virtual Machine integer remainder instructions (irem and lrem); " +
      "this may be compared with the C library function fmod.", OTYPE_MATH)
  val OPCODE_DRETURN = new OpCode("dreturn", 175, EMPTY_PARAMS, "Return double from method",
    "The current method must have return type double. The value must be of type double. If the current method is a synchronized method, " +
      "the monitor entered or reentered on invocation of the method is updated and possibly exited as " +
      "if by execution of a monitorexit instruction (§monitorexit) in the current thread. If no exception is thrown, " +
      "value is popped from the operand stack of the current frame (§2.6) and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "The value' is pushed onto the operand stack of the frame of the invoker. " +
      "Any other values on the operand stack of the current method are discarded.", OTYPE_CONTROL)
  val OPCODE_DSTORE = new OpCode("dstore", 57,
    Array(new Param("index", 1, "The index is an unsigned byte", false)),
    "Store double into local variable",
    "The index is an unsigned byte. Both index and index+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type double. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "The local variables at index and index+1 are set to value'", OTYPE_STORE)
  val OPCODE_DSTORE_0 = new OpCode("dstore_0", 71, EMPTY_PARAMS, "Store double into local variable",
    "Both <0> and <0>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type double. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "The local variables at <0> and <0>+1 are set to value'.", OTYPE_STORE)
  val OPCODE_DSTORE_1 = new OpCode("dstore_1", 72, EMPTY_PARAMS, "Store double into local variable",
    "Both <1> and <1>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type double. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "The local variables at <1> and <1>+1 are set to value'.", OTYPE_STORE)
  val OPCODE_DSTORE_2 = new OpCode("dstore_2", 73, EMPTY_PARAMS, "Store double into local variable",
    "Both <2> and <2>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type double. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "The local variables at <2> and <2>+1 are set to value'.", OTYPE_STORE)
  val OPCODE_DSTORE_3 = new OpCode("dstore_3", 74, EMPTY_PARAMS, "Store double into local variable",
    "Both <3> and <3>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type double. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "The local variables at <3> and <3>+1 are set to value'.", OTYPE_STORE)
  val OPCODE_DSUB = new OpCode("dsub", 103, EMPTY_PARAMS, "Subtract double",
    "Both value1 and value2 must be of type double. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. The double result is value1' - value2'. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_DUP = new OpCode("dup", 89, EMPTY_PARAMS, "Duplicate the top operand stack value",
    "Duplicate the top value on the operand stack and push the duplicated value onto the operand stack. " +
      "The dup instruction must not be used unless value is a value of a category 1 computational type (§2.11.1).", OTYPE_STACK)
  val OPCODE_DUP_X1 = new OpCode("dup_x1", 90, EMPTY_PARAMS, "Duplicate the top operand stack value and insert two values down",
    "Duplicate the top value on the operand stack and insert the duplicated value two values down in the operand stack. " +
      "The dup_x1 instruction must not be used unless both value1 and value2 are values of a category 1 computational type (§2.11.1).", OTYPE_STACK)
  val OPCODE_DUP_X2 = new OpCode("dup_x2", 91, EMPTY_PARAMS, "Duplicate the top operand stack value and insert two or three values down",
    "Duplicate the top value on the operand stack and insert the duplicated value two or three values down in the operand stack.", OTYPE_STACK)
  val OPCODE_DUP2 = new OpCode("dup2", 92, EMPTY_PARAMS, "Duplicate the top one or two operand stack values",
    "Duplicate the top one or two values on the operand stack and push the duplicated value or values back onto the operand stack in the original order.", OTYPE_STACK)
  val OPCODE_DUP2_X1 = new OpCode("dup2_x1", 93, EMPTY_PARAMS, "Duplicate the top one or two operand stack values and insert two or three values down",
    "Duplicate the top one or two values on the operand stack and insert the duplicated values, in the original order, " +
      "one value beneath the original value or values in the operand stack.", OTYPE_STACK)
  val OPCODE_DUP2_X2 = new OpCode("dup2_x2", 94, EMPTY_PARAMS,
    "Duplicate the top one or two operand stack values and insert two, three, or four values down",
    "Duplicate the top one or two values on the operand stack and insert the duplicated values, in the original order, into the operand stack.", OTYPE_STACK)

  val OPCODE_F2D = new OpCode("f2d", 141, EMPTY_PARAMS, "Convert float to double",
    "The value on the top of the operand stack must be of type float. It is popped from the operand stack and " +
      "undergoes value set conversion (§2.8.3), resulting in value'. Then value' is converted to a double result. " +
      "This result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_F2I = new OpCode("f2i", 139, EMPTY_PARAMS, "Convert float to int",
    "The value on the top of the operand stack must be of type float. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "Then value' is converted to an int result. This result is pushed onto the operand stack", OTYPE_CONVERSION)
  val OPCODE_F2L = new OpCode("f2l", 140, EMPTY_PARAMS, "Convert float to long",
    "The value on the top of the operand stack must be of type float. " +
      "It is popped from the operand stack and undergoes value set conversion (§2.8.3), resulting in value'. " +
      "Then value' is converted to a long result. This result is pushed onto the operand stack", OTYPE_CONVERSION)
  val OPCODE_FADD = new OpCode("fadd", 98, EMPTY_PARAMS, "Add float",
    "Both value1 and value2 must be of type float. The values are popped from the operand stack and " +
      "undergo value set conversion (§2.8.3), resulting in value1' and value2'. The float result is value1' + value2'. " +
      "The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_FALOAD = new OpCode("faload", 48, EMPTY_PARAMS, "Load float from array",
    "The arrayref must be of type reference and must refer to an array whose components are of type float. " +
      "The index must be of type int. Both arrayref and index are popped from the operand stack. " +
      "The float value in the component of the array at index is retrieved and pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_FASTORE = new OpCode("fastore", 81, EMPTY_PARAMS, "Store into float array",
    "The arrayref must be of type reference and must refer to an array whose components are of type float. " +
      "The index must be of type int, and the value must be of type float. The arrayref, index, and value are popped from the operand stack. " +
      "The float value undergoes value set conversion (§2.8.3), resulting in value', and value' is stored as the component of the array indexed by index.", OTYPE_STORE)
  val OPCODE_FCMPG = new OpCode("fcmpg", 150, EMPTY_PARAMS, "Compare float [greater]",
    "Both value1 and value2 must be of type float. The values are popped from the operand stack and undergo value set conversion (§2.8.3), resulting in value1' and value2'. A floating- point comparison is performed:" +
      "• Ifvalue1'isgreaterthanvalue2',theintvalue1ispushedonto the operand stack." +
      "• Otherwise, if value1' is equal to value2', the int value 0 is pushed onto the operand stack." +
      "• Otherwise, if value1' is less than value2', the int value -1 is pushed onto the operand stack." +
      "• Otherwise,at least one of value1' or value2' is NaN.The fcmpg instruction pushes the int value 1 onto the operand stack " +
      "and the fcmpl instruction pushes the int value -1 onto the operand stack.", OTYPE_COMPARISON)
  val OPCODE_FCMPL = new OpCode("fcmpl", 149, EMPTY_PARAMS, "Compare float [least]",
    "Both value1 and value2 must be of type float. The values are popped from the operand stack and undergo value set conversion (§2.8.3), resulting in value1' and value2'. A floating- point comparison is performed:" +
      "• Ifvalue1'isgreaterthanvalue2',theintvalue1ispushedonto the operand stack." +
      "• Otherwise, if value1' is equal to value2', the int value 0 is pushed onto the operand stack." +
      "• Otherwise, if value1' is less than value2', the int value -1 is pushed onto the operand stack." +
      "• Otherwise,at least one of value1' or value2' is NaN.The fcmpg instruction pushes the int value 1 onto the operand stack " +
      "and the fcmpl instruction pushes the int value -1 onto the operand stack.", OTYPE_COMPARISON)
  val OPCODE_FCONST_0 = new OpCode("fconst_0", 11, EMPTY_PARAMS, "Push float", "Push the float constant <0.0> onto the operand stack", OTYPE_CONSTANT)
  val OPCODE_FCONST_1 = new OpCode("fconst_1", 12, EMPTY_PARAMS, "Push float", "Push the float constant <1.0> onto the operand stack", OTYPE_CONSTANT)
  val OPCODE_FCONST_2 = new OpCode("fconst_2", 13, EMPTY_PARAMS, "Push float", "Push the float constant <2.0> onto the operand stack", OTYPE_CONSTANT)
  val OPCODE_FDIV = new OpCode("fdiv", 110, EMPTY_PARAMS, "Divide float",
    "Both value1 and value2 must be of type float. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. The float result is value1' / value2'. The result is pushed onto the operand stack", OTYPE_MATH)
  val OPCODE_FLOAD = new OpCode("fload", 23,
    Array(new Param("index", 1, "The index is an unsigned byte", false)),
    "Load float from local variable",
    "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at index must contain a float. The value of the local variable at index is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_FLOAD_0 = new OpCode("fload_0", 34, EMPTY_PARAMS, "Load float from local variable",
    "The <0> must be an index into the local variable array of the current frame (§2.6). The local variable at <0> must contain a float. " +
      "The value of the local variable at <0> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_FLOAD_1 = new OpCode("fload_1", 35, EMPTY_PARAMS, "Load float from local variable",
    "The <1> must be an index into the local variable array of the current frame (§2.6). The local variable at <1> must contain a float. " +
      "The value of the local variable at <1> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_FLOAD_2 = new OpCode("fload_2", 36, EMPTY_PARAMS, "Load float from local variable",
    "The <2> must be an index into the local variable array of the current frame (§2.6). The local variable at <2> must contain a float. " +
      "The value of the local variable at <2> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_FLOAD_3 = new OpCode("fload_3", 37, EMPTY_PARAMS, "Load float from local variable",
    "The <3> must be an index into the local variable array of the current frame (§2.6). The local variable at <3> must contain a float. " +
      "The value of the local variable at <3> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_FMUL = new OpCode("fmul", 106, EMPTY_PARAMS, "Multiply float",
    "Both value1 and value2 must be of type float. The values are popped from the operand stack and " +
      "undergo value set conversion (§2.8.3), resulting in value1' and value2'. " +
      "The float result is value1' * value2'. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_FNEG = new OpCode("fneg", 118, EMPTY_PARAMS, "Negate float",
    "The value must be of type float. It is popped from the operand stack and undergoes value set conversion (§2.8.3), " +
      "resulting in value'. The float result is the arithmetic negation of value'. This result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_FREM = new OpCode("frem", 114, EMPTY_PARAMS, "Remainder float",
    "Both value1 and value2 must be of type float. The values are popped from the operand stack and " +
      "undergo value set conversion (§2.8.3), resulting in value1' and value2'. " +
      "The result is calculated and pushed onto the operand stack as a float.", OTYPE_MATH)
  val OPCODE_FRETURN = new OpCode("freturn", 174, EMPTY_PARAMS, "Return float from method",
    "The current method must have return type float. The value must be of type float. " +
      "If the current method is a synchronized method, the monitor entered or reentered on invocation of the method " +
      "is updated and possibly exited as if by execution of a monitorexit instruction (§monitorexit) in the current thread. " +
      "If no exception is thrown, value is popped from the operand stack of the current frame (§2.6) and undergoes value set conversion (§2.8.3), " +
      "resulting in value'. The value' is pushed onto the operand stack of the frame of the invoker. " +
      "Any other values on the operand stack of the current method are discarded.", OTYPE_CONTROL)
  val OPCODE_FSTORE = new OpCode("fstore", 56,
    Array(new Param("index", 1, "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6)", false)),
    "Store float into local variable",
    "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type float. It is popped from the operand stack and " +
      "undergoes value set conversion (§2.8.3), resulting in value'. The value of the local variable at index is set to value'.", OTYPE_STORE)
  val OPCODE_FSTORE_0 = new OpCode("fstore_0", 67, EMPTY_PARAMS, "Store float into local variable",
    "The <0> must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type float. It is popped from the operand stack and " +
      "undergoes value set conversion (§2.8.3), resulting in value'. The value of the local variable at <0> is set to value'.", OTYPE_STORE)
  val OPCODE_FSTORE_1 = new OpCode("fstore_1", 68, EMPTY_PARAMS, "Store float into local variable",
    "The <1> must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type float. It is popped from the operand stack and " +
      "undergoes value set conversion (§2.8.3), resulting in value'. The value of the local variable at <1> is set to value'.", OTYPE_STORE)
  val OPCODE_FSTORE_2 = new OpCode("fstore_2", 69, EMPTY_PARAMS, "Store float into local variable",
    "The <2> must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type float. It is popped from the operand stack and " +
      "undergoes value set conversion (§2.8.3), resulting in value'. The value of the local variable at <2> is set to value'.", OTYPE_STORE)
  val OPCODE_FSTORE_3 = new OpCode("fstore_3", 70, EMPTY_PARAMS, "Store float into local variable",
    "The <3> must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type float. It is popped from the operand stack and " +
      "undergoes value set conversion (§2.8.3), resulting in value'. The value of the local variable at <3> is set to value'.", OTYPE_STORE)
  val OPCODE_FSUB = new OpCode("fsub", 102, EMPTY_PARAMS, "Subtract float",
    "Both value1 and value2 must be of type float. The values are popped from the operand stack and undergo value set conversion (§2.8.3), " +
      "resulting in value1' and value2'. The float result is value1' - value2'. The result is pushed onto the operand stack.", OTYPE_MATH)

  val OPCODE_GETFIELD = new OpCode("getfield", 180,
    Array(new Param("indexbyte1", 1, "index is (indexbyte1 << 8) | indexbyte2. ", false),
      new Param("indexbyte2", 1, "index is (indexbyte1 << 8) | indexbyte2. ", false)),
    "Fetch field from object",
    "The objectref, which must be of type reference, is popped from the operand stack. " +
      "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), " +
      "which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found. " +
      "The referenced field is resolved (§5.4.3.2). The value of the referenced field in objectref is fetched and pushed onto the operand stack." +
      "The type of objectref must not be an array type. If the field is protected (§4.6), " +
      "and it is a member of a superclass of the current class, and the field is not declared in the same run-time package (§5.3) as the current class, " +
      "then the class of objectref must be either the current class or a subclass of the current class.", OTYPE_REFERENCE)

  val OPCODE_GETSTATIC = new OpCode("getstatic", 178,
    Array(new Param("indexbyte1", 1, "index is (indexbyte1 << 8) | indexbyte2. ", false),
      new Param("indexbyte2", 1, "index is (indexbyte1 << 8) | indexbyte2. ", false)),
    "Get static field from class",
    "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), " +
      "which gives the name and descriptor of the field as well as a symbolic reference to the class or interface in which the field is to be found. " +
      "The referenced field is resolved (§5.4.3.2)." +
      "On successful resolution of the field, the class or interface that declared the resolved field is initialized (§5.5) " +
      "if that class or interface has not already been initialized." +
      "The value of the class or interface field is fetched and pushed onto the operand stack.", OTYPE_REFERENCE)


  val OPCODE_GOTO = new OpCode("goto", 167,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch always",
    "The unsigned bytes branchbyte1 and branchbyte2 are used to construct a signed 16-bit branchoffset, " +
      "where branchoffset is (branchbyte1 << 8) | branchbyte2. " +
      "Execution proceeds at that offset from the address of the opcode of this goto instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this goto instruction.", OTYPE_CONTROL)

  val OPCODE_GOTO_W = new OpCode("goto_w", 200,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4", false),
      new Param("branchbyte3", 1, "branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4", false),
      new Param("branchbyte4", 1, "branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4", false)),
    "Branch always (wide index)",
    "The unsigned bytes branchbyte1, branchbyte2, branchbyte3, and branchbyte4 are used to construct a signed 32-bit branchoffset, " +
      "where branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4. " +
      "Execution proceeds at that offset from the address of the opcode of this goto_w instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this goto_w instruction.", OTYPE_CONTROL)

  val OPCODE_I2B = new OpCode("i2b", 145, EMPTY_PARAMS, "Convert int to byte",
    "The value on the top of the operand stack must be of type int. It is popped from the operand stack, " +
      "truncated to a byte, then sign- extended to an int result. That result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_I2C = new OpCode("i2c", 146, EMPTY_PARAMS, "Convert int to char",
    "The value on the top of the operand stack must be of type int. It is popped from the operand stack, truncated to char, " +
      "then zero- extended to an int result. That result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_I2D = new OpCode("i2d", 135, EMPTY_PARAMS, "Convert int to double",
    "The value on the top of the operand stack must be of type int. It is popped from the operand stack and " +
      "converted to a double result. The result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_I2F = new OpCode("i2f", 134, EMPTY_PARAMS, "Convert int to float",
    "The value on the top of the operand stack must be of type int. It is popped from the operand stack and " +
      "converted to the float result using IEEE 754 round to nearest mode. The result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_I2L = new OpCode("i2l", 133, EMPTY_PARAMS, "Convert int to long",
    "The value on the top of the operand stack must be of type int. It is popped from the operand stack and " +
      "sign-extended to a long result. That result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_I2S = new OpCode("i2s", 147, EMPTY_PARAMS, "Convert int to short",
    "The value on the top of the operand stack must be of type int. It is popped from the operand stack, truncated to a short, " +
      "then sign- extended to an int result. That result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_IADD = new OpCode("iadd", 96, EMPTY_PARAMS, "Add int",
    "Both value1 and value2 must be of type int. The values are popped from the operand stack. " +
      "The int result is value1 + value2. The result is pushed onto the operand stack." +
      "The result is the 32 low-order bits of the true mathematical result in a sufficiently wide two's-complement format, " +
      "represented as a value of type int. If overflow occurs, " +
      "then the sign of the result may not be the same as the sign of the mathematical sum of the two values." +
      "Despite the fact that overflow may occur, execution of an iadd instruction never throws a run-time exception.", OTYPE_MATH)
  val OPCODE_IALOAD = new OpCode("iaload", 46, EMPTY_PARAMS, "Load int from array",
    "The arrayref must be of type reference and must refer to an array whose components are of type int. " +
      "The index must be of type int. Both arrayref and index are popped from the operand stack. " +
      "The int value in the component of the array at index is retrieved and pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_IAND = new OpCode("iand", 126, EMPTY_PARAMS, "Boolean AND int",
    "Both value1 and value2 must be of type int. They are popped from the operand stack. " +
      "An int result is calculated by taking the bitwise AND (conjunction) of value1 and value2. " +
      "The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_IASTORE = new OpCode("iastore", 79, EMPTY_PARAMS, "Store into int array",
    "The arrayref must be of type reference and must refer to an array whose components are of type int. " +
      "Both index and value must be of type int. The arrayref, index, and value are popped from the operand stack. " +
      "The int value is stored as the component of the array indexed by index.", OTYPE_STORE)
  val OPCODE_ICONST_M1 = new OpCode("iconst_m1", 2, EMPTY_PARAMS, "Push int constant", "Push the int constant <-1> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_ICONST_0 = new OpCode("iconst_0", 3, EMPTY_PARAMS, "Push int constant", "Push the int constant <0> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_ICONST_1 = new OpCode("iconst_1", 4, EMPTY_PARAMS, "Push int constant", "Push the int constant <1> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_ICONST_2 = new OpCode("iconst_2", 5, EMPTY_PARAMS, "Push int constant", "Push the int constant <2> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_ICONST_3 = new OpCode("iconst_3", 6, EMPTY_PARAMS, "Push int constant", "Push the int constant <3> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_ICONST_4 = new OpCode("iconst_4", 7, EMPTY_PARAMS, "Push int constant", "Push the int constant <4> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_ICONST_5 = new OpCode("iconst_5", 8, EMPTY_PARAMS, "Push int constant", "Push the int constant <5> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_IDIV = new OpCode("idiv", 108, EMPTY_PARAMS, "Divide int",
    "Both value1 and value2 must be of type int. The values are popped from the operand stack. " +
      "The int result is the value of the Java programming language expression value1 / value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_IF_ACMPEQ = new OpCode("if_acmpeq", 165,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if reference comparison succeeds",
    "Both value1 and value2 must be of type reference. They are both popped from the operand stack and compared. " +
      "The results of the comparison are as follows: if_acmpeq succeeds if and only if value1 = value2" +
      "If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this if_acmpeq instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this if_acmpeq instruction." +
      "Otherwise, if the comparison fails, execution proceeds at the address of the instruction following this if_acmpeq instruction.", OTYPE_COMPARISON)
  val OPCODE_IF_ACMPNE = new OpCode("if_acmpne", 166,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if reference comparison succeeds",
    "Both value1 and value2 must be of type reference. They are both popped from the operand stack and compared. " +
      "The results of the comparison are as follows: if_acmpne succeeds if and only if value1 != value2" +
      "If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this if_acmpne instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this if_acmpne instruction." +
      "Otherwise, if the comparison fails, execution proceeds at the address of the instruction following this if_acmpne instruction.", OTYPE_COMPARISON)
  val OPCODE_IF_ICMPEQ = new OpCode("if_icmpeq", 159,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison succeeds",
    "Both value1 and value2 must be of type int. They are both popped from the operand stack and compared. " +
      "All comparisons are signed. The results of the comparison are as follows:" +
      "if_icmpeq succeeds if and only if value1 = value2", OTYPE_COMPARISON)
  val OPCODE_IF_ICMPNE = new OpCode("if_icmpne", 160,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison succeeds",
    "Both value1 and value2 must be of type int. They are both popped from the operand stack and compared. " +
      "All comparisons are signed. The results of the comparison are as follows:" +
      "if_icmpne succeeds if and only if value1 ≠ value2", OTYPE_COMPARISON)
  val OPCODE_IF_ICMPLT = new OpCode("if_icmplt", 161,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison succeeds",
    "Both value1 and value2 must be of type int. They are both popped from the operand stack and compared. " +
      "All comparisons are signed. The results of the comparison are as follows:" +
      "if_icmplt succeeds if and only if value1 < value2", OTYPE_COMPARISON)
  val OPCODE_IF_ICMPGE = new OpCode("if_icmpge", 162,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison succeeds",
    "Both value1 and value2 must be of type int. They are both popped from the operand stack and compared. " +
      "All comparisons are signed. The results of the comparison are as follows:" +
      "if_icmplt succeeds if and only if value1 ≥ value2", OTYPE_COMPARISON)
  val OPCODE_IF_ICMPGT = new OpCode("if_icmpgt", 163,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison succeeds",
    "Both value1 and value2 must be of type int. They are both popped from the operand stack and compared. " +
      "All comparisons are signed. The results of the comparison are as follows:" +
      "if_icmplt succeeds if and only if value1 > value2", OTYPE_COMPARISON)
  val OPCODE_IF_ICMPLE = new OpCode("if_icmple", 164,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison succeeds",
    "Both value1 and value2 must be of type int. They are both popped from the operand stack and compared. " +
      "All comparisons are signed. The results of the comparison are as follows:" +
      "if_icmplt succeeds if and only if value1 ≤ value2", OTYPE_COMPARISON)
  val OPCODE_IFEQ = new OpCode("ifeq", 153,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison with zero succeeds",
    "The value must be of type int. It is popped from the operand stack and compared against zero. All comparisons are signed. " +
      "The results of the comparisons are as follows: ifeq succeeds if and only if value = 0. " +
      "If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this ifeq instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this ifeq instruction." +
      "Otherwise, execution proceeds at the address of the instruction following this ifeq instruction.", OTYPE_COMPARISON)
  val OPCODE_IFNE = new OpCode("ifne", 154,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison with zero succeeds",
    "The value must be of type int. It is popped from the operand stack and compared against zero. All comparisons are signed. " +
      "The results of the comparisons are as follows: ifne succeeds if and only if value != 0. " +
      "If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this ifeq instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this ifne instruction." +
      "Otherwise, execution proceeds at the address of the instruction following this ifne instruction.", OTYPE_COMPARISON)
  val OPCODE_IFLT = new OpCode("iflt", 155,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison with zero succeeds",
    "The value must be of type int. It is popped from the operand stack and compared against zero. All comparisons are signed. " +
      "The results of the comparisons are as follows: iflt succeeds if and only if value < 0. " +
      "If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this iflt instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this iflt instruction." +
      "Otherwise, execution proceeds at the address of the instruction following this iflt instruction.", OTYPE_COMPARISON)
  val OPCODE_IFGE = new OpCode("ifge", 156,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison with zero succeeds",
    "The value must be of type int. It is popped from the operand stack and compared against zero. All comparisons are signed. " +
      "The results of the comparisons are as follows: ifge succeeds if and only if value >= 0. " +
      "If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this ifge instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this ifge instruction." +
      "Otherwise, execution proceeds at the address of the instruction following this ifge instruction.", OTYPE_COMPARISON)
  val OPCODE_IFGT = new OpCode("ifgt", 157,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison with zero succeeds",
    "The value must be of type int. It is popped from the operand stack and compared against zero. All comparisons are signed. " +
      "The results of the comparisons are as follows: fgt succeeds if and only if value > 0. " +
      "If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this ifgt instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this ifgt instruction." +
      "Otherwise, execution proceeds at the address of the instruction following this ifgt instruction.", OTYPE_COMPARISON)
  val OPCODE_IFLE = new OpCode("ifle", 158,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if int comparison with zero succeeds",
    "The value must be of type int. It is popped from the operand stack and compared against zero. All comparisons are signed. " +
      "The results of the comparisons are as follows: ifle succeeds if and only if value <= 0. " +
      "If the comparison succeeds, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this ifle instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this ifle instruction." +
      "Otherwise, execution proceeds at the address of the instruction following this ifle instruction.", OTYPE_COMPARISON)
  val OPCODE_IFNONNULL = new OpCode("ifnonnull", 199,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if reference not null",
    "The value must be of type reference. It is popped from the operand stack. If value is not null, " +
      "the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this ifnonnull instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this ifnonnull instruction.", OTYPE_COMPARISON)
  val OPCODE_IFNULL = new OpCode("ifnull", 198,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 8) | branchbyte2", false)),
    "Branch if reference is null",
    "The value must of type reference. It is popped from the operand stack. " +
      "If value is null, the unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is calculated to be (branchbyte1 << 8) | branchbyte2. " +
      "Execution then proceeds at that offset from the address of the opcode of this ifnull instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this ifnull instruction. " +
      "Otherwise, execution proceeds at the address of the instruction following this ifnull instruction.", OTYPE_COMPARISON)
  val OPCODE_IINC = new OpCode("iinc", 132,
    Array(new Param("index", 1, "The index is an unsigned byte that must be an index into the local variable array of the current frame", false),
      new Param("const", 1, "The const is an immediate signed byte.", true)),
    "Increment local variable by constant",
    "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6). " +
      "The const is an immediate signed byte. " +
      "The local variable at index must contain an int. " +
      "The value const is first sign-extended to an int, and then the local variable at index is incremented by that amount.", OTYPE_MATH)
  val OPCODE_ILOAD = new OpCode("iload", 21,
    Array(new Param("index", 1, "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6)", false)),
    "Load int from local variable",
    "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at index must contain an int. The value of the local variable at index is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_ILOAD_0 = new OpCode("iload_0", 26, EMPTY_PARAMS, "Load int from local variable",
    "The <0> must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at <0> must contain an int. " +
      "The value of the local variable at <0> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_ILOAD_1 = new OpCode("iload_1", 27, EMPTY_PARAMS, "Load int from local variable",
    "The <1> must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at <1> must contain an int. " +
      "The value of the local variable at <1> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_ILOAD_2 = new OpCode("iload_2", 28, EMPTY_PARAMS, "Load int from local variable",
    "The <2> must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at <2> must contain an int. " +
      "The value of the local variable at <2> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_ILOAD_3 = new OpCode("iload_3", 29, EMPTY_PARAMS, "Load int from local variable",
    "The <3> must be an index into the local variable array of the current frame (§2.6). " +
      "The local variable at <3> must contain an int. " +
      "The value of the local variable at <3> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_IMUL = new OpCode("imul", 104, EMPTY_PARAMS, "Multiply int",
    "Both value1 and value2 must be of type int. The values are popped from the operand stack. " +
      "The int result is value1 * value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_INEG = new OpCode("ineg", 116, EMPTY_PARAMS, "Negate int",
    "The value must be of type int. It is popped from the operand stack. " +
      "The int result is the arithmetic negation of value, -value. The result is pushed onto the operand stack." +
      "For int values, negation is the same as subtraction from zero. " +
      "Because the Java Virtual Machine uses two's-complement representation for integers and the range of two's-complement values is not symmetric, " +
      "the negation of the maximum negative int results in that same maximum negative number. " +
      "Despite the fact that overflow has occurred, no exception is thrown. " +
      "For all int values x, -x equals (~x)+1.", OTYPE_MATH)
  val OPCODE_INSTANCEOF = new OpCode("instanceof", 193,
    Array(new Param("indexbyte1", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false)),
    "Determine if object is of given type",
    "The objectref, which must be of type reference, is popped from the operand stack. " +
      "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run-time constant pool item at the index must be a symbolic reference to a class, array, or interface type. " +
      "If objectref is null, the instanceof instruction pushes an int result of 0 as an int on the operand stack. " +
      "Otherwise, the named class, array, or interface type is resolved (§5.4.3.1). " +
      "If objectref is an instance of the resolved class or array or implements the resolved interface, " +
      "the instanceof instruction pushes an int result of 1 as an int on the operand stack; otherwise, it pushes an int result of 0.", OTYPE_REFERENCE)
  val OPCODE_INVOKEDYNAMIC = new OpCode("invokedynamic", 186,
    Array(new Param("indexbyte1", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("zero1", 1, "0", false),
      new Param("zero2", 1, "0", false)),
    "Invoke dynamic method",
    "Each specific lexical occurrence of an invokedynamic instruction is called a dynamic call site. " +
      "First, the unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run-time constant pool item at that index must be a symbolic reference to a call site specifier (§5.1). " +
      "The values of the third and fourth operand bytes must always be zero. " +
      "The call site specifier is resolved (§5.4.3.6) for this specific dynamic call site to obtain a reference to a java.lang.invoke.MethodHandle instance, " +
      "a reference to a java.lang.invoke.MethodType instance, and references to static arguments. " +
      "Next, as part of the continuing resolution of the call site specifier, " +
      "the bootstrap method is invoked as if by execution of an invokevirtual instruction (§invokevirtual) that contains " +
      "a run-time constant pool index to a symbolic reference to a method (§5.1) with the following properties:", OTYPE_REFERENCE)
  val OPCODE_INVOKEINTERFACE = new OpCode("invokeinterface", 185,
    Array(new Param("indexbyte1", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("count", 1, "The count operand is an unsigned byte that must not be zero", false),
      new Param("zero", 1, "0", false)),
    "Invoke interface method",
    "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run-time constant pool item at that index must be a symbolic reference to an interface method (§5.1), " +
      "which gives the name and descriptor (§4.3.3) of the interface method as well as a symbolic reference to the interface " +
      "in which the interface method is to be found. The named interface method is resolved (§5.4.3.4). " +
      "The resolved interface method must not be an instance initialization method (§2.9) or the class or interface initialization method (§2.9)." +
      "The count operand is an unsigned byte that must not be zero. " +
      "The objectref must be of type reference and must be followed on the operand stack by nargs argument values, " +
      "where the number, type, and order of the values must be consistent with the descriptor of the resolved interface method. " +
      "The value of the fourth operand byte must always be zero.", OTYPE_REFERENCE)
  val OPCODE_INVOKESPECIAL = new OpCode("invokespecial", 183,
    Array(new Param("indexbyte1", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false)),
    "Invoke instance method; special handling for superclass, private, and instance initialization method invocations",
    "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. The run-time constant pool item at " +
      "that index must be a symbolic reference to a method (§5.1), which gives the name and descriptor (§4.3.3) " +
      "of the method as well as a symbolic reference to the class in which the method is to be found. " +
      "The named method is resolved (§5.4.3.3). Finally, if the resolved method is protected (§4.6), " +
      "and it is a member of a superclass of the current class, and the method is not declared in the same run-time package (§5.3) " +
      "as the current class, then the class of objectref must be either the current class or a subclass of the current class.", OTYPE_REFERENCE)
  val OPCODE_INVOKESTATIC = new OpCode("invokestatic", 184,
    Array(new Param("indexbyte1", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false)),
    "Invoke a class (static) method",
    "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. The run-time constant pool item " +
      "at that index must be a symbolic reference to a method (§5.1), which gives the name and descriptor (§4.3.3) of the method " +
      "as well as a symbolic reference to the class in which the method is to be found. The named method is resolved (§5.4.3.3). " +
      "The resolved method must not be an instance initialization method (§2.9) or the class or interface initialization method (§2.9). " +
      "It must be static, and therefore cannot be abstract.", OTYPE_REFERENCE)
  val OPCODE_INVOKEVIRTUAL = new OpCode("invokevirtual", 182,
    Array(new Param("indexbyte1", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false)),
    "Invoke instance method; dispatch based on class",
    "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference " +
      "to a method (§5.1), which gives the name and descriptor (§4.3.3) of the method as well as a symbolic reference to the class in " +
      "which the method is to be found. The named method is resolved (§5.4.3.3). " +
      "The resolved method must not be an instance initialization method (§2.9) or the class or interface initialization method (§2.9). " +
      "Finally, if the resolved method is protected (§4.6), and it is a member of a superclass of the current class, " +
      "and the method is not declared in the same run-time package (§5.3) as the current class, " +
      "then the class of objectref must be either the current class or a subclass of the current class.", OTYPE_REFERENCE)
  val OPCODE_IOR = new OpCode("ior", 128, EMPTY_PARAMS, "Boolean OR int",
    "Both value1 and value2 must be of type int. They are popped from the operand stack. " +
      "An int result is calculated by taking the bitwise inclusive OR of value1 and value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_IREM = new OpCode("irem", 112, EMPTY_PARAMS, "Remainder int",
    "Both value1 and value2 must be of type int. The values are popped from the operand stack. " +
      "The int result is value1 - (value1 / value2) * value2. The result is pushed onto the operand stack." +
      "The result of the irem instruction is such that (a/b)*b + (a%b) is equal to a. " +
      "This identity holds even in the special case in which the dividend is the negative int of largest possible magnitude " +
      "for its type and the divisor is -1 (the remainder is 0). It follows from this rule that the result of the remainder operation " +
      "can be negative only if the dividend is negative and can be positive only if the dividend is positive. " +
      "Moreover, the magnitude of the result is always less than the magnitude of the divisor.", OTYPE_MATH)
  val OPCODE_IRETURN = new OpCode("ireturn", 172, EMPTY_PARAMS, "Return int from method",
    "The current method must have return type boolean, byte, short, char, or int. The value must be of type int. " +
      "If the current method is a synchronized method, the monitor entered or reentered on invocation of the method " +
      "is updated and possibly exited as if by execution of a monitorexit instruction (§monitorexit) in the current thread. " +
      "If no exception is thrown, value is popped from the operand stack of the current frame (§2.6) " +
      "and pushed onto the operand stack of the frame of the invoker. Any other values on the operand stack of the current method are discarded. " +
      "The interpreter then returns control to the invoker of the method, reinstating the frame of the invoker.", OTYPE_CONTROL)
  val OPCODE_ISHL = new OpCode("ishl", 120, EMPTY_PARAMS, "Shift left int",
    "Both value1 and value2 must be of type int. The values are popped from the operand stack. " +
      "An int result is calculated by shifting value1 left by s bit positions, where s is the value of the low 5 bits of value2. " +
      "The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_ISHR = new OpCode("ishr", 122, EMPTY_PARAMS, "Arithmetic shift right int",
    "Both value1 and value2 must be of type int. The values are popped from the operand stack. " +
      "An int result is calculated by shifting value1 right by s bit positions, with sign extension, where s " +
      "is the value of the low 5 bits of value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_ISTORE = new OpCode("istore", 54,
    Array(new Param("index", 1, "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6)", false)),
    "Store int into local variable",
    "The index is an unsigned byte that must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type int. It is popped from the operand stack, " +
      "and the value of the local variable at index is set to value.", OTYPE_STORE)
  val OPCODE_ISTORE_0 = new OpCode("istore_0", 59, EMPTY_PARAMS, "Store int <0> into local variable",
    "The <0> must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type int. " +
      "It is popped from the operand stack, and the value of the local variable at <n> is set to value.", OTYPE_STORE)
  val OPCODE_ISTORE_1 = new OpCode("istore_1", 60, EMPTY_PARAMS, "Store int <1> into local variable",
    "The <1> must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type int. " +
      "It is popped from the operand stack, and the value of the local variable at <n> is set to value.", OTYPE_STORE)
  val OPCODE_ISTORE_2 = new OpCode("istore_2", 61, EMPTY_PARAMS, "Store int <2> into local variable",
    "The <2> must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type int. " +
      "It is popped from the operand stack, and the value of the local variable at <n> is set to value.", OTYPE_STORE)
  val OPCODE_ISTORE_3 = new OpCode("istore_3", 62, EMPTY_PARAMS, "Store int <3> into local variable",
    "The <3> must be an index into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type int. " +
      "It is popped from the operand stack, and the value of the local variable at <n> is set to value.", OTYPE_STORE)
  val OPCODE_ISUB = new OpCode("isub", 100, EMPTY_PARAMS, "Subtract int",
    "Both value1 and value2 must be of type int. The values are popped from the operand stack. " +
      "The int result is value1 - value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_IUSHR = new OpCode("iushr", 124, EMPTY_PARAMS, "Logical shift right int",
    "Both value1 and value2 must be of type int. The values are popped from the operand stack. " +
      "An int result is calculated by shifting value1 right by s bit positions, with zero extension, " +
      "where s is the value of the low 5 bits of value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_IXOR = new OpCode("ixor", 130, EMPTY_PARAMS, "Boolean XOR int",
    "Both value1 and value2 must be of type int. They are popped from the operand stack. " +
      "An int result is calculated by taking the bitwise exclusive OR of value1 and value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_JSR = new OpCode("jsr", 168,
    Array(new Param("branchbyte1", 1, "offset is (branchbyte1 << 8) | branchbyte2.", false),
      new Param("branchbyte2", 1, "offset, where the offset is (branchbyte1 << 8) | branchbyte2..", false)),
    "Jump subroutine",
    "The address of the opcode of the instruction immediately following this jsr instruction is pushed onto the operand stack as a value of type returnAddress. " +
      "The unsigned branchbyte1 and branchbyte2 are used to construct a signed 16-bit offset, " +
      "where the offset is (branchbyte1 << 8) | branchbyte2. Execution proceeds at that offset from the address of this jsr instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this jsr instruction.", OTYPE_CONTROL)
  val OPCODE_JSR_W = new OpCode("jsr_w", 201,
    Array(new Param("branchbyte1", 1, "branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4", false),
      new Param("branchbyte2", 1, "branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4", false),
      new Param("branchbyte3", 1, "branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4", false),
      new Param("branchbyte4", 1, "branchoffset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4", false)),
    "Jump subroutine (wide index)",
    "The address of the opcode of the instruction immediately following this jsr_w instruction is pushed onto the operand stack " +
      "as a value of type returnAddress. The unsigned branchbyte1, branchbyte2, branchbyte3, and branchbyte4 are used to construct a signed 32-bit offset," +
      " where the offset is (branchbyte1 << 24) | (branchbyte2 << 16) | (branchbyte3 << 8) | branchbyte4. " +
      "Execution proceeds at that offset from the address of this jsr_w instruction. " +
      "The target address must be that of an opcode of an instruction within the method that contains this jsr_w instruction.", OTYPE_CONTROL)

  val OPCODE_L2D = new OpCode("l2d", 138, EMPTY_PARAMS, "Convert long to double",
    "The value on the top of the operand stack must be of type long. It is popped from the operand stack and " +
      "converted to a double result using IEEE 754 round to nearest mode. The result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_L2F = new OpCode("l2f", 137, EMPTY_PARAMS, "Convert long to float",
    "The value on the top of the operand stack must be of type long. It is popped from the operand stack and " +
      "converted to a float result using IEEE 754 round to nearest mode. The result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_L2I = new OpCode("l2i", 136, EMPTY_PARAMS, "Convert long to int",
    "The value on the top of the operand stack must be of type long. It is popped from the operand stack and " +
      "converted to an int result by taking the low-order 32 bits of the long value and discarding the high-order 32 bits. " +
      "The result is pushed onto the operand stack.", OTYPE_CONVERSION)
  val OPCODE_LADD = new OpCode("ladd", 97, EMPTY_PARAMS, "Add long",
    "Both value1 and value2 must be of type long. The values are popped from the operand stack. " +
      "The long result is value1 + value2. The result is pushed onto the operand stack." +
      "The result is the 64 low-order bits of the true mathematical result in a sufficiently wide two's-complement format, " +
      "represented as a value of type long. If overflow occurs, the sign of the result may not be the same as the sign of the mathematical sum of the two values. " +
      "Despite the fact that overflow may occur, execution of an ladd instruction never throws a run-time exception.", OTYPE_MATH)
  val OPCODE_LALOAD = new OpCode("laload", 47, EMPTY_PARAMS, "Load long from array",
    "The arrayref must be of type reference and must refer to an array whose components are of type long. " +
      "The index must be of type int. Both arrayref and index are popped from the operand stack. " +
      "The long value in the component of the array at index is retrieved and pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_LAND = new OpCode("land", 127, EMPTY_PARAMS, "Boolean AND long",
    "Both value1 and value2 must be of type long. They are popped from the operand stack. " +
      "A long result is calculated by taking the bitwise AND of value1 and value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_LASTORE = new OpCode("lastore", 80, EMPTY_PARAMS, "Store into long array",
    "The arrayref must be of type reference and must refer to an array whose components are of type long. " +
      "The index must be of type int, and value must be of type long. The arrayref, index, and value are popped from the operand stack. " +
      "The long value is stored as the component of the array indexed by index.", OTYPE_STORE)
  val OPCODE_LCMP = new OpCode("lcmp", 148, EMPTY_PARAMS, "Compare long",
    "Both value1 and value2 must be of type long. They are both popped from the operand stack, and a signed integer comparison is performed. " +
      "If value1 is greater than value2, the int value 1 is pushed onto the operand stack. If value1 is equal to value2, " +
      "the int value 0 is pushed onto the operand stack. If value1 is less than value2, the int value -1 is pushed onto the operand stack.", OTYPE_COMPARISON)
  val OPCODE_LCONST_0 = new OpCode("lconst_0", 9, EMPTY_PARAMS, "Push long constant", "Push the long constant <0> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_LCONST_1 = new OpCode("lconst_1", 10, EMPTY_PARAMS, "Push long constant", "Push the long constant <1> onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_LDC = new OpCode("ldc", 18,
    Array(new Param("index", 1, "The index is an unsigned byte that must be a valid index into the run-time constant pool of the current class (§2.6).", false)),
    "Push item from run-time constant pool",
    "The index is an unsigned byte that must be a valid index into the run-time constant pool of the current class (§2.6). " +
      "The run-time constant pool entry at index either must be a run-time constant of type int or float, " +
      "or a reference to a string literal, or a symbolic reference to a class, method type, or method handle (§5.1). " +
      "If the run-time constant pool entry is a run-time constant of type int or float, " +
      "the numeric value of that run-time constant is pushed onto the operand stack as an int or float, respectively. " +
      "Otherwise, if the run-time constant pool entry is a reference to an instance of class String representing a string literal (§5.1), " +
      "then a reference to that instance, value, is pushed onto the operand stack. " +
      "Otherwise, if the run-time constant pool entry is a symbolic reference to a class (§5.1), " +
      "then the named class is resolved (§5.4.3.1) and a reference to the Class object representing that class, value, " +
      "is pushed onto the operand stack. " +
      "Otherwise, the run-time constant pool entry must be a symbolic reference to a method type or a method handle (§5.1). " +
      "The method type or method handle is resolved (§5.4.3.5) and a reference to the resulting instance of java.lang.invoke.MethodType " +
      "or java.lang.invoke.MethodHandle, value, is pushed onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_LDC_W = new OpCode("ldc_w", 19,
    Array(new Param("indexbyte1", 1, "Index is calculated as (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "Index is calculated as (indexbyte1 << 8) | indexbyte2.", false)),
    "Push item from run-time constant pool (wide index)",
    "The unsigned indexbyte1 and indexbyte2 are assembled into an unsigned 16-bit index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is calculated as (indexbyte1 << 8) | indexbyte2. " +
      "The index must be a valid index into the run-time constant pool of the current class. " +
      "The run-time constant pool entry at the index either must be a run-time constant of type int or float, " +
      "or a reference to a string literal, or a symbolic reference to a class, method type, or method handle (§5.1)." +
      "If the run-time constant pool entry is a run-time constant of type int or float, " +
      "the numeric value of that run-time constant is pushed onto the operand stack as an int or float, respectively. " +
      "Otherwise, if the run-time constant pool entry is a reference to an instance of class String representing a string literal (§5.1), " +
      "then a reference to that instance, value, is pushed onto the operand stack. " +
      "Otherwise, if the run-time constant pool entry is a symbolic reference to a class (§5.1), " +
      "then the named class is resolved (§5.4.3.1) and a reference to the Class object representing that class, value, " +
      "is pushed onto the operand stack. " +
      "Otherwise, the run-time constant pool entry must be a symbolic reference to a method type or a method handle (§5.1). " +
      "The method type or method handle is resolved (§5.4.3.5) and a reference to the resulting instance of java.lang.invoke.MethodType " +
      "or java.lang.invoke.MethodHandle, value, is pushed onto the operand stack.", OTYPE_CONSTANT)
  val OPCODE_LDC2_W = new OpCode("ldc2_w", 20,
    Array(new Param("indexbyte1", 1, "Index is calculated as (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "Index is calculated as (indexbyte1 << 8) | indexbyte2.", false)),
    "Push long or double from run-time constant pool (wide index)",
    "The unsigned indexbyte1 and indexbyte2 are assembled into an unsigned 16-bit index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is calculated as (indexbyte1 << 8) | indexbyte2. " +
      "The index must be a valid index into the run-time constant pool of the current class. " +
      "The run-time constant pool entry at the index must be a run-time constant of type long or double (§5.1). " +
      "The numeric value of that run-time constant is pushed onto the operand stack as a long or double, respectively.", OTYPE_CONSTANT)
  val OPCODE_LDIV = new OpCode("ldiv", 109, EMPTY_PARAMS, "Divide long",
    "Both value1 and value2 must be of type long. The values are popped from the operand stack. " +
      "The long result is the value of the Java programming language expression value1 / value2. The result is pushed onto the operand stack. " +
      "A long division rounds towards 0; that is, the quotient produced for long values in n / d is a long value q " +
      "whose magnitude is as large as possible while satisfying |d · q| <= |n|. " +
      "Moreover, q is positive when |n| >= |d| and n and d have the same sign, but q is negative when |n| >= |d| and n and d have opposite signs. " +
      "There is one special case that does not satisfy this rule: " +
      "if the dividend is the negative integer of largest possible magnitude for the long type and the divisor is -1, " +
      "then overflow occurs and the result is equal to the dividend; despite the overflow, no exception is thrown in this case.", OTYPE_MATH)
  val OPCODE_LLOAD = new OpCode("lload", 22,
    Array(new Param("index", 1, "The index is an unsigned byte", false)),
    "Load long from local variable",
    "The index is an unsigned byte. Both index and index+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The local variable at index must contain a long. The value of the local variable at index is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_LLOAD_0 = new OpCode("lload_0 ", 30, EMPTY_PARAMS, "Load long from local variable",
    "Both <0> and <0>+1 must be indices into the local variable array of the current frame (§2.6). The local variable at <0> must contain a long. " +
      "The value of the local variable at <0> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_LLOAD_1 = new OpCode("lload_1 ", 31, EMPTY_PARAMS, "Load long from local variable",
    "Both <1> and <1>+1 must be indices into the local variable array of the current frame (§2.6). The local variable at <1> must contain a long. " +
      "The value of the local variable at <1> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_LLOAD_2 = new OpCode("lload_2 ", 32, EMPTY_PARAMS, "Load long from local variable",
    "Both <2> and <2>+1 must be indices into the local variable array of the current frame (§2.6). The local variable at <2> must contain a long. " +
      "The value of the local variable at <2> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_LLOAD_3 = new OpCode("lload_3 ", 33, EMPTY_PARAMS, "Load long from local variable",
    "Both <3> and <3>+1 must be indices into the local variable array of the current frame (§2.6). The local variable at <3> must contain a long. " +
      "The value of the local variable at <3> is pushed onto the operand stack.", OTYPE_LOAD)
  val OPCODE_LMUL = new OpCode("lmul ", 105, EMPTY_PARAMS, "Multiply long",
    "Both value1 and value2 must be of type long. The values are popped from the operand stack. " +
      "The long result is value1 * value2. The result is pushed onto the operand stack. " +
      "The result is the 64 low-order bits of the true mathematical result in a sufficiently wide two's-complement format, " +
      "represented as a value of type long. If overflow occurs, the sign of the result may not be the same " +
      "as the sign of the mathematical sum of the two values. " +
      "Despite the fact that overflow may occur, execution of an lmul instruction never throws a run-time exception.", OTYPE_MATH)
  val OPCODE_LNEG = new OpCode("lneg ", 117, EMPTY_PARAMS, "Negate long",
    "The value must be of type long. It is popped from the operand stack. The long result is the arithmetic negation of value, -value. " +
      "The result is pushed onto the operand stack. For long values, negation is the same as subtraction from zero. " +
      "Because the Java Virtual Machine uses two's-complement representation for integers and the range of two's-complement values is not symmetric, " +
      "the negation of the maximum negative long results in that same maximum negative number. " +
      "Despite the fact that overflow has occurred, no exception is thrown. " +
      "For all long values x, -x equals (~x)+1.", OTYPE_MATH)
  val OPCODE_LOOKUPSWITCH = new OpCode("lookupswitch", 171,
    Array(new Param("dynamic", -1, "The size of bytes for this param is dynamic", false)),
    "Access jump table by key match and jump",
    "A lookupswitch is a variable-length instruction. Immediately after the lookupswitch opcode, " +
      "between zero and three bytes must act as padding, such that defaultbyte1 begins at an address " +
      "that is a multiple of four bytes from the start of the current method (the opcode of its first instruction). " +
      "Immediately after the padding follow a series of signed 32-bit values: default, npairs, and then npairs pairs of signed 32-bit values. " +
      "The npairs must be greater than or equal to 0. Each of the npairs pairs consists of an int match and a signed 32-bit offset. " +
      "Each of these signed 32-bit values is constructed from four unsigned bytes as (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4." +
      "The table match-offset pairs of the lookupswitch instruction must be sorted in increasing numerical order by match." +
      "The key must be of type int and is popped from the operand stack. The key is compared against the match values. " +
      "If it is equal to one of them, then a target address is calculated by adding the corresponding offset " +
      "to the address of the opcode of this lookupswitch instruction. If the key does not match any of the match values, " +
      "the target address is calculated by adding default to the address of the opcode of this lookupswitch instruction. " +
      "Execution then continues at the target address. " +
      "The target address that can be calculated from the offset of each match-offset pair, as well as the one calculated from default, " +
      "must be the address of an opcode of an instruction within the method that contains this lookupswitch instruction.", OTYPE_CONTROL)
  val OPCODE_LOR = new OpCode("lor ", 129, EMPTY_PARAMS, "Boolean OR long",
    "Both value1 and value2 must be of type long. They are popped from the operand stack. " +
      "A long result is calculated by taking the bitwise inclusive OR of value1 and value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_LREM = new OpCode("lrem ", 113, EMPTY_PARAMS, "Remainder long",
    "Both value1 and value2 must be of type long. The values are popped from the operand stack. " +
      "The long result is value1 - (value1 / value2) * value2. The result is pushed onto the operand stack. " +
      "The result of the lrem instruction is such that (a/b)*b + (a%b) is equal to a. " +
      "This identity holds even in the special case in which the dividend is the negative long of largest possible " +
      "magnitude for its type and the divisor is -1 (the remainder is 0). It follows from this rule that the result " +
      "of the remainder operation can be negative only if the dividend is negative and can be positive only " +
      "if the dividend is positive; moreover, the magnitude of the result is always less than the magnitude of the divisor.", OTYPE_MATH)
  val OPCODE_LRETURN = new OpCode("lreturn ", 173, EMPTY_PARAMS, "Remainder long",
    "The current method must have return type long. The value must be of type long. If the current method is a synchronized method, " +
      "the monitor entered or reentered on invocation of the method is updated and possibly exited as " +
      "if by execution of a monitorexit instruction (§monitorexit) in the current thread. " +
      "If no exception is thrown, value is popped from the operand stack of the current frame (§2.6) " +
      "and pushed onto the operand stack of the frame of the invoker. Any other values on the operand stack of the current method are discarded. " +
      "The interpreter then returns control to the invoker of the method, reinstating the frame of the invoker.", OTYPE_CONTROL)
  val OPCODE_LSHL = new OpCode("lshl ", 121, EMPTY_PARAMS, "Shift left long",
    "The value1 must be of type long, and value2 must be of type int. The values are popped from the operand stack. " +
      "A long result is calculated by shifting value1 left by s bit positions, where s is the low 6 bits of value2. " +
      "The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_LSHR = new OpCode("lshr ", 123, EMPTY_PARAMS, "Arithmetic shift right long",
    "The value1 must be of type long, and value2 must be of type int. The values are popped from the operand stack. " +
      "A long result is calculated by shifting value1 right by s bit positions, with sign extension, " +
      "where s is the value of the low 6 bits of value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_LSTORE = new OpCode("lstore", 55,
    Array(new Param("index", 1, "The index is an unsigned byte", false)),
    "Store long into local variable",
    "The index is an unsigned byte. Both index and index+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type long. It is popped from the operand stack, " +
      "and the local variables at index and index+1 are set to value.", OTYPE_STORE)
  val OPCODE_LSTORE_0 = new OpCode("lstore_0 ", 63, EMPTY_PARAMS, "Store long into local variable",
    "Both <0> and <0>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type long. It is popped from the operand stack, " +
      "and the local variables at <0> and <0>+1 are set to value.", OTYPE_STORE)
  val OPCODE_LSTORE_1 = new OpCode("lstore_1 ", 64, EMPTY_PARAMS, "Store long into local variable",
    "Both <1> and <1>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type long. It is popped from the operand stack, " +
      "and the local variables at <1> and <1>+1 are set to value.", OTYPE_STORE)
  val OPCODE_LSTORE_2 = new OpCode("lstore_2 ", 65, EMPTY_PARAMS, "Store long into local variable",
    "Both <2> and <2>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type long. It is popped from the operand stack, " +
      "and the local variables at <2> and <2>+1 are set to value.", OTYPE_STORE)
  val OPCODE_LSTORE_3 = new OpCode("lstore_3 ", 66, EMPTY_PARAMS, "Store long into local variable",
    "Both <3> and <3>+1 must be indices into the local variable array of the current frame (§2.6). " +
      "The value on the top of the operand stack must be of type long. It is popped from the operand stack, " +
      "and the local variables at <3> and <3>+1 are set to value.", OTYPE_STORE)
  val OPCODE_LSUB = new OpCode("lsub ", 101, EMPTY_PARAMS, "Subtract long",
    "Both value1 and value2 must be of type long. The values are popped from the operand stack. " +
      "The long result is value1 - value2. The result is pushed onto the operand stack. " +
      "For long subtraction, a-b produces the same result as a+(-b). For long values, subtraction from zero is the same as negation. " +
      "The result is the 64 low-order bits of the true mathematical result in a sufficiently wide two's-complement format, " +
      "represented as a value of type long. If overflow occurs, then the sign of the result may not be the same " +
      "as the sign of the mathematical sum of the two values. " +
      "Despite the fact that overflow may occur, execution of an lsub instruction never throws a run-time exception.", OTYPE_MATH)
  val OPCODE_LUSHR = new OpCode("lushr ", 125, EMPTY_PARAMS, "Logical shift right long",
    "The value1 must be of type long, and value2 must be of type int. The values are popped from the operand stack. " +
      "A long result is calculated by shifting value1 right logically (with zero extension) by the amount indicated by the low 6 bits of value2. " +
      "The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_LXOR = new OpCode("lxor ", 131, EMPTY_PARAMS, "Boolean XOR long",
    "Both value1 and value2 must be of type long. They are popped from the operand stack. " +
      "A long result is calculated by taking the bitwise exclusive OR of value1 and value2. The result is pushed onto the operand stack.", OTYPE_MATH)
  val OPCODE_MONITORENTER = new OpCode("monitorenter ", 194, EMPTY_PARAMS, "Enter monitor for object",
    "The objectref must be of type reference. Each object is associated with a monitor. " +
      "A monitor is locked if and only if it has an owner. The thread that executes monitorenter attempts to gain ownership " +
      "of the monitor associated with objectref, as follows: " +
      "• If the entry count of the monitor associated with objectref is zero, the thread enters the monitor and sets its entry count to one. " +
      "  The thread is then the owner of the monitor. " +
      "• If the thread already owns the monitor associated with objectref, it reenters the monitor, incrementing its entry count. " +
      "• If another thread already owns the monitor associated with objectref, the thread blocks until the monitor's entry count is zero, " +
      "  then tries again to gain ownership.", OTYPE_REFERENCE)
  val OPCODE_MONITOREXIT = new OpCode("monitorexit ", 195, EMPTY_PARAMS, "Exit monitor for object",
    "The objectref must be of type reference. " +
      "The thread that executes monitorexit must be the owner of the monitor associated with the instance referenced by objectref. " +
      "The thread decrements the entry count of the monitor associated with objectref. " +
      "If as a result the value of the entry count is zero, the thread exits the monitor and is no longer its owner. " +
      "Other threads that are blocking to enter the monitor are allowed to attempt to do so.", OTYPE_REFERENCE)
  val OPCODE_MULTIANEWARRAY = new OpCode("multianewarray", 197,
    Array(new Param("indexbyte1", 1, "index is (indexbyte1 << 8) | indexbyte2", false),
      new Param("indexbyte2", 1, "index is (indexbyte1 << 8) | indexbyte2", false),
      new Param("dimensions", 1, "The dimensions operand is an unsigned byte that must be greater than or equal to 1", false)),
    "Create new multidimensional array",
    "The dimensions operand is an unsigned byte that must be greater than or equal to 1. " +
      "It represents the number of dimensions of the array to be created. The operand stack must contain dimensions values. " +
      "Each such value represents the number of components in a dimension of the array to be created, must be of type int, " +
      "and must be non-negative. The count1 is the desired length in the first dimension, count2 in the second, etc." +
      "All of the count values are popped off the operand stack. " +
      "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run- time constant pool item at the index must be a symbolic reference to a class, array, or interface type. " +
      "The named class, array, or interface type is resolved (§5.4.3.1). " +
      "The resulting entry must be an array class type of dimensionality greater than or equal to dimensions." +
      "A new multidimensional array of the array type is allocated from the garbage-collected heap. " +
      "If any count value is zero, no subsequent dimensions are allocated. " +
      "The components of the array in the first dimension are initialized to subarrays of the type of the second dimension, and so on. " +
      "The components of the last allocated dimension of the array are initialized to the default initial value " +
      "(§2.3, §2.4) for the element type of the array type. A reference arrayref to the new array is pushed onto the operan", OTYPE_REFERENCE)
  val OPCODE_NEW = new OpCode("new", 187,
    Array(new Param("indexbyte1", 1, "index is (indexbyte1 << 8) | indexbyte2", false),
      new Param("indexbyte2", 1, "index is (indexbyte1 << 8) | indexbyte2", false)),
    "Create new object",
    "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. " +
      "The run-time constant pool item at the index must be a symbolic reference to a class or interface type. " +
      "The named class or interface type is resolved (§5.4.3.1) and should result in a class type. " +
      "Memory for a new instance of that class is allocated from the garbage-collected heap, " +
      "and the instance variables of the new object are initialized to their default initial values (§2.3, §2.4). " +
      "The objectref, a reference to the instance, is pushed onto the operand stack." +
      "On successful resolution of the class, it is initialized (§5.5) if it has not already been initialized.", OTYPE_REFERENCE)
  val OPCODE_NEWARRAY = new OpCode("newarray", 188,
    Array(new Param("atype", 1, "The atype is a code that indicates the type of array to create. ", false)),
    "Create new array",
    "The count must be of type int. It is popped off the operand stack. The count represents the number of elements in the array to be created. " +
      "The atype is a code that indicates the type of array to create. It must take one of the following values:" +
      "T_BOOLEAN = 4;" +
      "T_CHAR    = 5;" +
      "T_FLOAT   = 6;" +
      "T_DOUBLE  = 7;" +
      "T_BYTE    = 8;" +
      "T_SHORT   = 9;" +
      "T_INT     = 10;" +
      "T_LONG    = 11" +
      "A new array whose components are of type atype and of length count is allocated from the garbage-collected heap. " +
      "A reference arrayref to this new array object is pushed into the operand stack. " +
      "Each of the elements of the new array is initialized to the default initial value (§2.3, §2.4) for the element type of the array type.", OTYPE_REFERENCE)


  val OPCODE_NOP = new OpCode("nop", 0, EMPTY_PARAMS, "Do nothing", "Do nothing.", OTYPE_CONSTANT)

  val OPCODE_POP = new OpCode("pop", 87, EMPTY_PARAMS, "Pop the top operand stack value",
    "Pop the top value from the operand stack. " +
      "The pop instruction must not be used unless value is a value of a category 1 computational type", OTYPE_STACK)

  val OPCODE_POP2 = new OpCode("pop2", 88, EMPTY_PARAMS, "Pop the top one or two operand stack values",
    "Pop the top one or two values from the operand stack.", OTYPE_STACK)

  val OPCODE_PUTFIELD = new OpCode("putfield", 181,
    Array(new Param("indexbyte1", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false)),
    "Set field in object",
    "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), " +
      "which gives the name and descriptor of the field as well as a symbolic reference to the class in which the field is to be found. " +
      "The class of objectref must not be an array. If the field is protected (§4.6), and it is a member of a superclass of the current class, " +
      "and the field is not declared in the same run-time package (§5.3) as the current class, " +
      "then the class of objectref must be either the current class or a subclass of the current class. ", OTYPE_REFERENCE)

  val OPCODE_PUTSTATIC = new OpCode("putstatic", 179,
    Array(new Param("indexbyte1", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false),
      new Param("indexbyte2", 1, "value of the index is (indexbyte1 << 8) | indexbyte2.", false)),
    "Set static field in class",
    "The unsigned indexbyte1 and indexbyte2 are used to construct an index into the run-time constant pool of the current class (§2.6), " +
      "where the value of the index is (indexbyte1 << 8) | indexbyte2. The run-time constant pool item at that index must be a symbolic reference to a field (§5.1), " +
      "which gives the name and descriptor of the field as well as a symbolic reference to the class or interface in which the field is to be found. " +
      "The referenced field is resolved (§5.4.3.2). " +
      "On successful resolution of the field, the class or interface that declared the resolved field is initialized (§5.5) " +
      "if that class or interface has not already been initialized. " +
      "The type of a value stored by a putstatic instruction must be compatible with the descriptor of the referenced field (§4.3.2). " +
      "If the field descriptor type is boolean, byte, char, short, or int, then the value must be an int. " +
      "If the field descriptor type is float, long, or double, then the value must be a float, long, or double, respectively. " +
      "If the field descriptor type is a reference type, then the value must be of a type that is assignment compatible (JLS §5.2) with the field descriptor type. " +
      "If the field is final, it must be declared in the current class, and the instruction must occur in the <clinit> method of the current class (§2.9). ", OTYPE_REFERENCE)

  val OPCODE_RET = new OpCode("ret", 169,
    Array(new Param("index", 1, "The index is an unsigned byte between 0 and 255, inclusive", false)),
    "Return from subroutine",
    "The index is an unsigned byte between 0 and 255, inclusive. " +
      "The local variable at index in the current frame (§2.6) must contain a value of type returnAddress. " +
      "The contents of the local variable are written into the Java Virtual Machine's pc register, and execution continues there.", OTYPE_CONTROL)

  val OPCODE_RETURN = new OpCode("return", 177, EMPTY_PARAMS, "Return void from method",
    "The current method must have return type void. If the current method is a synchronized method, " +
      "the monitor entered or reentered on invocation of the method is updated and possibly exited as " +
      "if by execution of a monitorexit instruction (§monitorexit) in the current thread. " +
      "If no exception is thrown, any values on the operand stack of the current frame (§2.6) are discarded." +
      "The interpreter then returns control to the invoker of the method, reinstating the frame of the invoker.", OTYPE_CONTROL)

  val OPCODE_SALOAD = new OpCode("saload", 53, EMPTY_PARAMS, "Load short from array",
    "The arrayref must be of type reference and must refer to an array whose components are of type short. " +
      "The index must be of type int. Both arrayref and index are popped from the operand stack. " +
      "The component of the array at index is retrieved and sign-extended to an int value. " +
      "That value is pushed onto the operand stack.", OTYPE_LOAD)

  val OPCODE_SASTORE = new OpCode("sastore", 86, EMPTY_PARAMS, "Store into short array",
    "The arrayref must be of type reference and must refer to an array whose components are of type short. " +
      "Both index and value must be of type int. The arrayref, index, and value are popped from the operand stack. " +
      "The int value is truncated to a short and stored as the component of the array indexed by index. ", OTYPE_STORE)

  val OPCODE_SIPUSH = new OpCode("sipush", 17,
    Array(new Param("byte1", 1, "The value of the short is (byte1 << 8) | byte2", false),
      new Param("byte2", 1, "The value of the short is (byte1 << 8) | byte2", false)),
    "Push short",
    "The immediate unsigned byte1 and byte2 values are assembled into an intermediate short where the value of the short is (byte1 << 8) | byte2. " +
      "The intermediate value is then sign-extended to an int value. That value is pushed onto the operand stack.", OTYPE_CONSTANT)

  val OPCODE_SWAP = new OpCode("swap", 95, EMPTY_PARAMS, "Swap the top two operand stack values",
    "Swap the top two values on the operand stack. " +
      "The swap instruction must not be used unless value1 and value2 are both values of a category 1 computational type (§2.11.1).", OTYPE_STACK)

  val OPCODE_TABLESWITCH = new OpCode("tableswitch", 170,
    Array(new Param("dynamic", -1, "runtime byte offset", false)),
    "Access jump table by index and jump",
    "A tableswitch is a variable-length instruction. Immediately after the tableswitch opcode, " +
      "between zero and three bytes must act as padding, such that defaultbyte1 begins at an address " +
      "that is a multiple of four bytes from the start of the current method (the opcode of its first instruction). " +
      "Immediately after the padding are bytes constituting three signed 32-bit values: default, low, and high. " +
      "Immediately following are bytes constituting a series of high - low + 1 signed 32-bit offsets. " +
      "The value low must be less than or equal to high. The high - low + 1 signed 32-bit offsets are treated as a 0-based jump table. " +
      "Each of these signed 32-bit values is constructed as (byte1 << 24) | (byte2 << 16) | (byte3 << 8) | byte4. " +
      "The index must be of type int and is popped from the operand stack. If index is less than low or index is greater than high, " +
      "then a target address is calculated by adding default to the address of the opcode of this tableswitch instruction. " +
      "Otherwise, the offset at position index - low of the jump table is extracted. " +
      "The target address is calculated by adding that offset to the address of the opcode of this tableswitch instruction. " +
      "Execution then continues at the target address. " +
      "The target address that can be calculated from each jump table offset, as well as the one that can be calculated from default, " +
      "must be the address of an opcode of an instruction within the method that contains this tableswitch instruction.", OTYPE_CONTROL)

  val OPCODE_WIDE = new OpCode("wide", 196,
    Array(new Param("dynamic", -1, "runtime byte offset", false)),
    "Extend local variable index by additional bytes Or Same as modified instruction",
    "The wide instruction modifies the behavior of another instruction. It takes one of two formats, depending on the instruction being modified. The first form of the wide instruction modifies one of the instructions iload, fload, aload, lload, dload, istore, fstore, astore, lstore, dstore, or ret (§iload, §fload, §aload, §lload, §dload, §istore, §fstore, §astore, §lstore, §dstore, §ret). The second form applies only to the iinc instruction (§iinc). " +
      "In either case, the wide opcode itself is followed in the compiled code by the opcode of the instruction wide modifies. In either form, two unsigned bytes indexbyte1 and indexbyte2 follow the modified opcode and are assembled into a 16-bit unsigned index to a local variable in the current frame (§2.6), where the value of the index is (indexbyte1 << 8) | indexbyte2. The calculated index must be an index into the local variable array of the current frame. Where the wide instruction modifies an lload, dload, lstore, or dstore instruction, the index following the calculated index (index + 1) must also be an index into the local variable array. In the second form, two immediate unsigned bytes constbyte1 and constbyte2 follow indexbyte1 and indexbyte2 in the code stream. Those bytes are also assembled into a signed 16-bit constant, where the constant is (constbyte1 << 8) | constbyte2. " +
      "The widened bytecode operates as normal, except for the use of the wider index and, in the case of the second form, the larger increment range. ", OTYPE_EXTENDS)

  //------------------------------------------------ USER OPCODES -----------------------------------------------------
  val USCODE_TRY = new OpCode("try", 300, EMPTY_PARAMS, "Try block begining", "Open try block for catch. ", OTYPE_CUSTOM);
  val USCODE_CATCH = new OpCode("catch", 301, Array(new Param("dynamic", -1, "runtime byte offset", false)), "Catch block begining", "Open Catch block. ", OTYPE_CUSTOM);
  val USCODE_FINALLY = new OpCode("finally", 302, EMPTY_PARAMS, "Finally block begining", "Open finally block for catch. ", OTYPE_CUSTOM);
  val USCODE_CASE = new OpCode("case", 303, Array(new Param("int1", 4, "The value of the int is case value", true)), "Case block begining", "Open case block for switch.", OTYPE_CUSTOM);
  val USCODE_DEFAULT = new OpCode("default", 304, EMPTY_PARAMS, "Default block begining", "Open default block for switch.", OTYPE_CUSTOM);

}
