package org.powlab.jeye.decode.processor.custom

import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types
import org.powlab.jeye.core.parsing.DescriptorParser.Pisc
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.decode.processor.load.LoadInformator.{ isReferenceLoadNode, getBaseLoadOpcodeIndex }
import org.powlab.jeye.decode.processor.store.StoreInformator.getBaseStoreOpcodeIndex
import org.powlab.jeye.decode.expression.SwitchExpression
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.utils.DecodeUtils
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.decode.LocalVariable
import org.powlab.jeye.decode.expression.CatchExpression
import org.powlab.jeye.decode.expression.CaseExpression
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.GroupOpcodeNode

class CustomInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    runtimeOpcode.opcode match {
      case USCODE_TRY => processTryUscode(node)
      case USCODE_CATCH => processCatchUscode(node)
      case USCODE_FINALLY => processFinallyUscode(node)
      case USCODE_CASE => processCaseUscode(node)
      case USCODE_DEFAULT => processDefaultUscode(node)
      case _ => processException(node)
    }
  }

  private def processTryUscode(node: OpcodeNode) {
    storeExpression(node, TRY_EXPRESSION)
  }

  private def processCatchUscode(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val values = runtimeOpcode.values
    val clazz = cpUtils.getClassInformator(values(0))
    val catchTypes = new ArrayBuffer[String]
    values.foreach(value => {
      val clazz = cpUtils.getClassInformator(value)
      catchTypes += clazz.simpleName
    })
    val storeNode = if (node.isInstanceOf[GroupOpcodeNode]) {
      node.asInstanceOf[GroupOpcodeNode].opcodes(1)
    } else {
      node
    }
    val exceptionName = namer.getNextName(Types.TYPE_REFERENCE, storeNode, tree)
    storeExpression(node, new CatchExpression(exceptionName, catchTypes.toArray))

    val autoException = TypedExpression(exceptionName, Pisc(DecodeUtils.getClassMeta(catchTypes(0))))
    push(autoException)
    if (node.isInstanceOf[GroupOpcodeNode]) {
      val exception = operandStacks.pop
      val index = getBaseStoreOpcodeIndex(storeNode.runtimeOpcode)
      localVariables(index) = LocalVariable(index, exception)
    }
  }

  private def processFinallyUscode(node: OpcodeNode) {
    storeExpression(node, FINALLY_EXPRESSION)
    val autoException = TypedExpression("any" + node.runtimeOpcode.number, TYPE_THROW_DESCRIPTOR)
    push(autoException)
    if (node.isInstanceOf[GroupOpcodeNode]) {
      val exception = operandStacks.pop
      val finallyGroup = node.asInstanceOf[GroupOpcodeNode]
      val indexNode = finallyGroup.opcodes(1)
      val index = if (isReferenceLoadNode(indexNode)) getBaseLoadOpcodeIndex(indexNode.runtimeOpcode) else getBaseStoreOpcodeIndex(indexNode.runtimeOpcode)
      localVariables(index) = LocalVariable(index, exception)
    }
  }

  private def processCaseUscode(node: OpcodeNode) {
    processSwitchChild(node)
  }

  private def processDefaultUscode(node: OpcodeNode) {
    processSwitchChild(node)
  }

  private def processSwitchChild(node: OpcodeNode) {
    if (node.isInstanceOf[GroupOpcodeNode]) {
      val block = new BlockExpression
      val groupedNode = node.asInstanceOf[GroupOpcodeNode]
      groupedNode.opcodes.foreach(block += makeSwitchChildExpression(_))
      storeExpression(node, block)
    } else {
      storeExpression(node, makeSwitchChildExpression(node))
    }
  }

  private def makeSwitchChildExpression(node: OpcodeNode): IExpression = {
    if (CustomInformator.isCaseNode(node)) {
      val values = node.runtimeOpcode.values
      val switchNumber = values(0)
      val switchNode = tree.currentLast(switchNumber)
      val switchExpression = tree.expression(switchNode).asInstanceOf[SwitchExpression]
      val variable = switchExpression.variable
      val caseValue = values(1)
      var value: IExpression = IntLiteralExpression(caseValue)
      val descriptor = variable.descriptor
      if (isCharDescriptor(descriptor)) {
        value = CharLiteralExpression(caseValue)
      } else if (isStringDescriptor(descriptor)) {

      } else if (isReferenceDescriptor(descriptor) && mc.extra.hasEnums) {
        val className = DecodeUtils.getSimpleClassName(descriptor.meta)
        val enumFieldName = mc.extra.getEnumFieldName(className, caseValue)
        if (enumFieldName != null) {
          value = Sex(enumFieldName)
        } else {

        }
      }
      new CaseExpression(value)
    } else {
      SWITCH_DEFAULT_EXPRESSION
    }
  }

}