package org.powlab.jeye.decode.processor.reference

import org.powlab.jeye.core._
import org.powlab.jeye.core.Constants.{MH_INVOKE_STATIC, getMethodHandlerType}
import org.powlab.jeye.core.Descriptors._
import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.core.Types.{getTypeByDesc, _}
import org.powlab.jeye.core.parsing.DescriptorParser.Pisc
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.MathExpression.SIGN_ASSIGN
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.processor.AbstractInstructionProcessor
import org.powlab.jeye.utils.AttributeUtils
import org.powlab.jeye.utils.DecodeUtils._
import scala.collection.mutable.ArrayBuffer
import org.powlab.jeye.decode.graph.OpcodeDetails.DETAIL_ARRAY_DUP
import scala.collection.mutable.Buffer
import org.powlab.jeye.utils.ClassInformator
import org.powlab.jeye.decode.graph.OpcodeDetails
import org.powlab.jeye.decode.expra.ExpressionHelpers
import org.powlab.jeye.decode.LocalVariable

class ReferenceInstructionProcessor(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  def process(node: OpcodeNode) {
    node.runtimeOpcode.opcode match {
      case OPCODE_GETFIELD => processGetFieldOpcode(node);
      case OPCODE_PUTFIELD => processPutFieldOpcode(node);
      case OPCODE_GETSTATIC => processGetStaticFieldOpcode(node);
      case OPCODE_PUTSTATIC => processPutStaticFieldOpcode(node);
      case OPCODE_INSTANCEOF => processInstanceOfOpcode(node);
      case OPCODE_INVOKEDYNAMIC => processInvokeDynamicOpcode(node);
      case OPCODE_INVOKEINTERFACE => processInvokeInterfaceOpcode(node);
      case OPCODE_INVOKESTATIC => processInvokeStaticOpcode(node);
      case OPCODE_INVOKESPECIAL => processInvokeSpecialOpcode(node);
      case OPCODE_INVOKEVIRTUAL => processInvokeVirtualOpcode(node);
      case OPCODE_NEW => processNewOpcode(node);
      case OPCODE_NEWARRAY => processNewArrayOpcode(node);
      case OPCODE_ANEWARRAY => processANewArrayOpcode(node);
      case OPCODE_MULTIANEWARRAY => processMultiANewArrayOpcode(node);
      case OPCODE_ARRAYLENGTH => processArrayLengthOpcode(node);
      case OPCODE_CHECKCAST => processCheckCastOpcode(node);
      case OPCODE_ATHROW => processAThrowOpcode(node);
      case OPCODE_MONITORENTER => processMonitorEnterOpcode(node);
      case OPCODE_MONITOREXIT => processMonitorExitOpcode(node);
      case _ => processException(node)
    }
  }

  private def processGetFieldOpcode(node: OpcodeNode) {
    val field = FieldInstructionData(node, cpUtils);
    val fieldOwnerVar = operandStacks.pop
    val fieldOwnerExpr = getFieldOwnerExpr(fieldOwnerVar, field)
    val fieldExpr = new GetFieldExpression(fieldOwnerExpr, field);
    push(fieldExpr);
  }

  private def processGetStaticFieldOpcode(node: OpcodeNode) {
    val field = FieldInstructionData(node, cpUtils);
    val fieldOwnerExpr = getFieldOwnerExpr(field)
    val fieldExpr = new GetStaticFieldExpression(fieldOwnerExpr, field);
    push(fieldExpr);
  }

  private def processPutFieldOpcode(node: OpcodeNode) {
    val fieldValueVar = operandStacks.pop
    val fieldOwnerVar = operandStacks.pop
    if (! processPutFieldPattern(node, fieldValueVar)) {
      val field = FieldInstructionData(node, cpUtils)
      val details = tree.details(node)
      val flush = details.detailType != OpcodeDetails.DETAIL_STORE_DUPS
      val fieldOwnerExpr = getFieldOwnerExpr(fieldOwnerVar, field)
      val putFieldExpr = new PutFieldExpression(fieldOwnerExpr, SIGN_ASSIGN, transform(fieldValueVar, field.descriptor), field)
      if (! flush) {
        operandStacks.pop
        push(putFieldExpr)
      } else {
        storeExpression(node, putFieldExpr)
      }
    }
  }

  private def processPutStaticFieldOpcode(node: OpcodeNode) {
    val fieldValueVar = operandStacks.pop
    if (! processPutFieldPattern(node, fieldValueVar)) {
      val field = FieldInstructionData(node, cpUtils)
      val details = tree.details(node)
      val flush = details.detailType != OpcodeDetails.DETAIL_STORE_DUPS
      val fieldOwnerExpr = getFieldOwnerExpr(field)
      val putStaticFieldExpr = new PutStaticFieldExpression(fieldOwnerExpr, SIGN_ASSIGN, fieldValueVar, field)
      if (! flush) {
        operandStacks.pop
        push(putStaticFieldExpr)
      } else {
        storeExpression(node, putStaticFieldExpr)
      }
    }
  }

  private def processPutFieldPattern(node: OpcodeNode, operValue: ITypedExpression): Boolean = {
    val details = tree.details(node)
    if (OpcodeDetails.isIncDetails(details)) {
      val incDetails = tree.incDetails(node)
      val value = ExpressionHelpers.getFirst(operValue).asInstanceOf[ITypedExpression]
      val incExpr = if (incDetails.detailType == OpcodeDetails.DETAIL_FIELD_INC_PRE)
        new PreIncrementExpression(value, incDetails.value) else
        new PostIncrementExpression(value, incDetails.value)
      if (incDetails.dup) {
        operandStacks.pop
        push(incExpr)
      } else {
        storeExpression(node, incExpr)
      }
      true
    } else {
      false
    }
  }

  private def processInvokeInterfaceOpcode(node: OpcodeNode) {
    val method = MethodInstructionData(node, cpUtils);
    val argumentValues = popAndFixVariables(method.name, method.parameters)
    val argumentsExpr = new ArgumentsExpression(method.parameters, argumentValues)
    val objectref = operandStacks.pop
    val invokeExpr = new InvokeInterfaceExpression(objectref, argumentsExpr, method);
    if (method.isVoid) {
      storeExpression(node, invokeExpr)
    } else {
      push(invokeExpr);
    }
  }

  private def processInvokeVirtualOpcode(node: OpcodeNode) {
    val method = MethodInstructionData(node, cpUtils);
    val argumentValues = popAndFixVariables(method.name, method.parameters)
    val argumentsExpr = new ArgumentsExpression(method.parameters, argumentValues)
    val objectref = castIfNull(operandStacks.pop, method.clazz)
    val invokeExpr = new InvokeVirtualExpression(objectref, argumentsExpr, method);
    if (method.isVoid) {
      storeExpression(node, invokeExpr)
    } else {
      push(invokeExpr);
    }
  }

  private def processInvokeSpecialOpcode(node: OpcodeNode) {
    val method = MethodInstructionData(node, cpUtils);
    val argumentValues = popAndFixVariables(method.name, method.parameters)
    val argumentsExpr = new ArgumentsExpression(method.parameters, argumentValues)
    val objectref = operandStacks.pop
    if (isConstructor(method.name)) {
      // TODO here: на проработку, определение вызова родительского конструктора на грани :)
      // вызов конструктора родителя -> super(...)
      if (objectref.view(EMPTY_EXPRESSION) == THIS_EXPRESSION.view(EMPTY_EXPRESSION)) {
        val thisClass = cpUtils.thisClass
        storeExpression(node, new InvokeConstructorExpressioan(thisClass.name, argumentsExpr, method))
      } else {
        // создание нового объекта -> new SomeObject(...)
        val newNodeDetails = tree.details(node)
        val newObjectExpr = new NewObjectExpression(getSimpleClassName(objectref.view(EMPTY_EXPRESSION), "."), argumentsExpr, objectref.descriptor);
        if (operandStacks.isEmpty) {
          storeExpression(node, newObjectExpr)
        } else {
          val classobj = operandStacks.pop
          if (classobj == objectref) {
            push(newObjectExpr);
          } else {
            operandStacks.push(classobj)
            storeExpression(node, newObjectExpr)
          }
        }
      }
    } else {
      val invokeExpr = new InvokeSpecialExpression(objectref, argumentsExpr, method);
      if (method.isVoid) {
        storeExpression(node, invokeExpr)
      } else {
        push(invokeExpr)
      }
    }
  }

  private def processInvokeStaticOpcode(node: OpcodeNode) {
    val method = MethodInstructionData(node, cpUtils);
    val argumentValues = popAndFixVariables(method.name, method.parameters)
    val argumentsExpr = new ArgumentsExpression(method.parameters, argumentValues)
    val thisClass = cpUtils.thisClass
    val invokeExpr = new InvokeStaticExpression(thisClass.name, argumentsExpr, method);
    if (method.isVoid) {
      storeExpression(node, invokeExpr)
    } else {
      push(invokeExpr)
    }
  }

  private def processInvokeDynamicOpcode(node: OpcodeNode) {
    val bootstrapMethod = new BootstrapInstructionData(node, classFile);
    if (MH_INVOKE_STATIC == bootstrapMethod.methodHandlerType) {
      val thisClass = cpUtils.thisClass
      val mtiExpr = new MethodTypeInvokerExpression(bootstrapMethod.descriptor, thisClass.simpleName);
      val bootArgsExpr = new ArgumentsExpression(bootstrapMethod.getBootstrapParamTypes(), bootstrapMethod.getBootstrapArguments);
      val methodParamTypes = bootstrapMethod.methodParameters
      val methodArgumentValues = popAndFixVariables(bootstrapMethod.bootstrapMethodName, methodParamTypes)
      val methodArgumentsExpr = new ArgumentsExpression(methodParamTypes, methodArgumentValues);
      val methodReturnType = bootstrapMethod.methodReturnType
      val flushable = methodReturnType.baseType == TYPE_VOID
      val invokeExpr = new InvokeDynamicExpression(
        bootstrapMethod.bootstrapMethodName,
        bootstrapMethod.bootstrapClass.simpleName,
        bootArgsExpr,
        mtiExpr,
        bootstrapMethod.methodName,
        methodArgumentsExpr, methodReturnType);
      if (!flushable) {
        push(invokeExpr)
      } else {
        storeExpression(node, invokeExpr)
      }
    }
    // TODO here: кидать другое сообщение об ошибке про MH - добавить поддержку ...
    else {
      processException(node);
    }
  }

  private def processNewOpcode(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val cPoolIndex = unsignShort(runtimeOpcode.values)
    val clazz = cpUtils.getClassInformator(cPoolIndex)
    val variable = TypedExpression(clazz.javaName, Pisc(clazz.meta));
    push(variable)
  }

  private def processNewArrayOpcode(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val arrayType = ARRAY_TYPES(runtimeOpcode.values(0));
    val baseType = getTypeByDesc(arrayType.typeAlias)
    val descriptor = Pisc(TYPE_ARRAY_CHAR + baseType.symbol)
    val newArrayExpr = new NewArrayExpression(arrayType.typeAlias, Array(operandStacks.pop), descriptor);
    processArray(node, newArrayExpr)
  }

  private def processANewArrayOpcode(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val values = runtimeOpcode.values
    val cPoolIndex = unsignShort(values)
    val clazz = cpUtils.getClassInformator(cPoolIndex)
    val meta = TYPE_ARRAY_CHAR + clazz.meta
    val descriptor = Pisc(meta).asInstanceOf[ArrayDescriptor]
    val dimensionValues = processDimension(1, descriptor)
    val newArrayExpr = new NewArrayExpression(clazz.simpleName, dimensionValues, descriptor)
    processArray(node, newArrayExpr)
  }

  private def processMultiANewArrayOpcode(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val values = runtimeOpcode.values
    val cPoolIndex = unsignShort(values)
    val dimension = values(2)
    val clazz = cpUtils.getClassInformator(cPoolIndex)
    val descriptor = Pisc(clazz.meta).asInstanceOf[ArrayDescriptor]
    val dimensionValues = processDimension(dimension, descriptor)
    val newArrayExpr = new NewArrayExpression(clazz.simpleName, dimensionValues, descriptor)
    push(newArrayExpr);
  }

  private def processDimension(fromStack: Int, descriptor: ArrayDescriptor): Array[ITypedExpression] = {
    val dimension = descriptor.dimension
    val dimensionValues = new ArrayBuffer[ITypedExpression](dimension)
    (fromStack + 1 to dimension).foreach { _ => dimensionValues += EMPTY_EXPRESSION }
    (1 to fromStack).foreach { _ => dimensionValues += popAndFix(TYPE_INT_DESCRIPTOR) }
    dimensionValues.toArray.reverse
  }

  private def processArray(node: OpcodeNode, expression: ITypedExpression) {
    val descriptor = expression.descriptor
    tree.resources += expression.classifier
    if (DETAIL_ARRAY_DUP == tree.details(node).detailType) {
      val newNameExpr = Sex(namer.getNextName(TYPE_ARRAY))
      val newLocalVarExpr = new NewLocalVariableExpression(newNameExpr, expression, descriptor);
      storeExpression(node, newLocalVarExpr)
      push(LocalVariable(newNameExpr, newLocalVarExpr.descriptor))
    } else {
      push(expression)
    }
  }

  private def processArrayLengthOpcode(node: OpcodeNode) {
    val arrayLengthExpr = new ArrayLengthExpression(operandStacks.pop)
    push(arrayLengthExpr)
  }

  private def processInstanceOfOpcode(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val values = runtimeOpcode.values
    val cPoolIndex = unsignShort(values)
    val clazz = cpUtils.getClassInformator(cPoolIndex)
    val instanceOfExpr = new InstanceOfExpression(operandStacks.pop, clazz.simpleName);
    push(instanceOfExpr);
  }

  private def processCheckCastOpcode(node: OpcodeNode) {
    val variable = operandStacks.pop
    if (variable != NULL_EXPRESSION) {
      val runtimeOpcode = node.runtimeOpcode
      val values = runtimeOpcode.values
      val cPoolIndex = unsignShort(values)
      val clazz = cpUtils.getClassInformator(cPoolIndex)
      val castType = if (clazz.isArray) getViewType(clazz.name) else clazz.simpleName
      val checkCastExpr = new CheckCastExpression(castType, variable, Pisc(clazz.meta))
      push(checkCastExpr)
    } else {
      push(variable)
    }
  }

  private def processAThrowOpcode(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val objectref = operandStacks.pop
    storeExpression(node, new ThrowExpression(objectref))
  }

  private def processMonitorEnterOpcode(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    val variable = operandStacks.pop
    storeExpression(node, new MonitorEnterExpression(variable))
  }

  private def processMonitorExitOpcode(node: OpcodeNode) {
    val freeVariable = operandStacks.pop
    storeExpression(node, MONITOR_EXIT_EXPRESSION)
  }

  private def popAndFixVariables(methodName: String, descriptors: Array[ParameterDescriptor]): Buffer[ITypedExpression] = {
    if (descriptors.nonEmpty) {
      val variables = new ArrayBuffer[ITypedExpression](descriptors.length)
      descriptors.reverse.foreach(descriptor => {
        val value = popAndFix(descriptor)
        variables += value
      })
      variables.reverse
    } else {
      ArrayBuffer.empty[ITypedExpression]
    }
  }

  /**
   * Привести объект к типу, и обернуть, если expr -  null
   */
  private def castIfNull(expr: ITypedExpression, clazz: ClassInformator): ITypedExpression = {
    if (expr == NULL_EXPRESSION) {
      new CheckCastExpression(clazz.viewName, expr, Pisc(clazz.meta))
    } else {
      expr
    }
  }

  private def getFieldOwnerExpr(fieldOwner: ITypedExpression, field: FieldInstructionData): ITypedExpression = {
    if (namer.isReserved(field.name) || !Constants.THIS_CONSTANT.equals(fieldOwner.view(EMPTY_EXPRESSION))) {
      fieldOwner
    } else {
      EMPTY_EXPRESSION
    }
  }

  private def getFieldOwnerExpr(field: FieldInstructionData): IExpression = {
    if (namer.isReserved(field.name) || cpUtils.thisClass.name != field.clazz.name) {
      Sex(field.clazz.simpleName)
    } else {
      EMPTY_EXPRESSION
    }
  }


}