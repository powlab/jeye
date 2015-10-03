package org.powlab.jeye.decode.processor.reference

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core.Constants.getMethodHandlerType
import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types.TYPE_VOID
import org.powlab.jeye.core.parsing.DescriptorParser.parseFieldDescriptor
import org.powlab.jeye.core.parsing.DescriptorParser.parseMethodDescriptor
import org.powlab.jeye.decode.expression.ITypedExpression
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.utils.ConstantPoolUtils
import org.powlab.jeye.utils.DecodeUtils.unsignShort
import org.powlab.jeye.utils.AttributeUtils
import scala.collection.mutable.Buffer
import org.powlab.jeye.utils.ClassInformator
import org.powlab.jeye.decode.ClassFacades
import org.powlab.jeye.utils.DecodeUtils

object ReferenceInformator {
  def isMonitorEnterCode(runtimeOpcode: RuntimeOpcode): Boolean = runtimeOpcode.opcode == OPCODE_MONITORENTER
  def isMonitorEnterNode(node: OpcodeNode): Boolean = node != null && isMonitorEnterCode(node.runtimeOpcode)
  def isMonitorExitNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_MONITOREXIT
  def isAthrowNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_ATHROW
  def isCheckCastNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_CHECKCAST
  def isAnewarrayNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_ANEWARRAY
  def isNewarrayNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_NEWARRAY
  def isNewNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_NEW
  def isInvokeStaticNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_INVOKESTATIC
  def isInvokeVirtualNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_INVOKEVIRTUAL
  def isGetFieldNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_GETFIELD
  def isGetStaticNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_GETSTATIC
  def isPutFieldNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_PUTFIELD
  def isPutStaticNode(node: OpcodeNode): Boolean = node != null && node.runtimeOpcode.opcode == OPCODE_PUTSTATIC

}

/**
 * Мэнеджер инструкции типа GetField/PutField/GetStatic/PutStatic
 * @param {RuntimeOpcode} runtimeOpcode
 */
object FieldInstructionData {
  def apply(node: OpcodeNode, cpUtils: ConstantPoolUtils): FieldInstructionData = {
    val runtimeOpcode = node.runtimeOpcode
    val values = runtimeOpcode.values
    val cPoolIndex = unsignShort(values)
    val fieldRef = cpUtils.getFieldStruct(cPoolIndex)
    val fieldNameAndType = cpUtils.getNameAndTypeInfo(fieldRef)

    val name = fieldNameAndType.name
    val clazz = cpUtils.getClassInformator(fieldRef.class_index)
    val descriptor = parseFieldDescriptor(fieldNameAndType.descriptor)
    new FieldInstructionData(name, clazz, descriptor)
  }
}

/**
 * Мэнеджер инструкции типа GetField/PutField/GetStatic/PutStatic
 * @param {RuntimeOpcode} runtimeOpcode
 */
class FieldInstructionData(val name: String, val clazz: ClassInformator, val descriptor: ParameterDescriptor) {
}

/**
 * Мэнеджер инструкции типа InvokeInterface/InvokeStatic/InvokeSpecial/Invokevirtual
 * @param {RuntimeOpcode} runtimeOpcode
 */
object MethodInstructionData {
  // Это фактически билдер
  // TODO here: подумать об оптимизации: всегда ли нужно вызывать parseMethodDescriptor?
  // или возможно на уровне парса класса иметь кэш для parseMethodDescriptor
  def apply(node: OpcodeNode, cpUtils: ConstantPoolUtils): MethodInstructionData = {
    val cPoolIndex = unsignShort(node.runtimeOpcode.values)
    val methodRef = cpUtils.getMethodStruct(cPoolIndex)
    val methodNameAndType = cpUtils.getNameAndTypeInfo(methodRef)
    val descriptor = parseMethodDescriptor(methodNameAndType.descriptor)
    val clazz = cpUtils.getClassInformator(methodRef.class_index)
    val classFacadeOpt = ClassFacades.get(clazz.meta)
    if (classFacadeOpt.isDefined) {
      val mid = classFacadeOpt.get.getMid(methodNameAndType.name, descriptor)
      if (mid != null) {
        return mid
      }
    }
    // TODO here: информацию о том, какие accessFlags у метода, взять неоткуда
    new MethodInstructionData(methodNameAndType.name, 0, descriptor, clazz)
  }
}

class MethodInstructionData(val name: String, accessFlags: Int, val descriptor: MethodDescriptor, val clazz: ClassInformator) {
  val parameters = descriptor.parameters
  val parametersCount = parameters.length
  val returnType = descriptor.returnType
  val isVoid = returnType.baseType == TYPE_VOID
  val isVarargs = AccessFlags.isVarargs(accessFlags)

  override def toString() : String = {
    clazz.javaName + "." + name + parameters.map(_.toString).map(DecodeUtils.getViewType).mkString("(", ", ", ")") + returnType
  }
}

/**
 * Мэнеджер инструкции типа InvokeDynamic
 */
class BootstrapInstructionData(node: OpcodeNode, classFile: ClassFile) {
  private val cpUtils: ConstantPoolUtils = classFile.constantPoolUtils
  private val runtimeOpcode = node.runtimeOpcode
  private val values = runtimeOpcode.values
  private val cPoolIndex = unsignShort(values)
  private val invokeDynamic = cpUtils.getInvokeDynamicStruct(cPoolIndex)
  private val bootstrapMethod = AttributeUtils.get[BootstrapMethodsAttribute](classFile.attributes).bootstrap_methods(invokeDynamic.bootstrap_method_attr_index)
  private val bootstrapMethodHadnler = cpUtils.getMethodHandleStruct(bootstrapMethod.bootstrap_method_ref)
  private val referenceKind = bootstrapMethodHadnler.reference_kind;
  private val referenceMethod = cpUtils.getMethodStruct(bootstrapMethodHadnler.reference_index)
  private val referenceMethodNameAndType = cpUtils.getNameAndTypeInfo(referenceMethod)
  private val methodNameAndType = cpUtils.getNameAndTypeInfo(invokeDynamic.name_and_type_index)
  private val methodDescriptor = parseMethodDescriptor(methodNameAndType.descriptor)

  val descriptor = methodNameAndType.descriptor
  val methodName = methodNameAndType.name
  val bootstrapClass = cpUtils.getClassInformator(referenceMethod.class_index)
  val bootstrapMethodName = referenceMethodNameAndType.name
  val methodParameters = methodDescriptor.parameters
  val methodReturnType = methodDescriptor.returnType
  val methodHandlerType = getMethodHandlerType(referenceKind)

  def getBootstrapParamTypes(): Array[ParameterDescriptor] = {
    val descriptors = new ArrayBuffer[ParameterDescriptor]
    bootstrapMethod.bootstrap_arguments.foreach(cpIndex => {
      val varConstant: ITypedExpression = cpUtils.getConstantVariable(cpIndex)
      descriptors += varConstant.descriptor
    })
    descriptors.toArray
  }

  /**
   * @returns {[]Variable}
   */
  def getBootstrapArguments(): Buffer[ITypedExpression] = {
    val arguments = new ArrayBuffer[ITypedExpression]
    bootstrapMethod.bootstrap_arguments.foreach(cpIndex => {
      val varConstant: ITypedExpression = cpUtils.getConstantVariable(cpIndex)
      arguments += varConstant
    })
    arguments
  }

}
