package org.powlab.jeye.decode.processor

import org.powlab.jeye.core.Opcodes._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.RuntimeOpcodes
import org.powlab.jeye.decode.processor.comparison.ComparisonInstructionProcessor
import org.powlab.jeye.decode.processor.math.MathInstructionProcessor
import org.powlab.jeye.decode.processor.reference.ReferenceInstructionProcessor
import org.powlab.jeye.decode.processor.constant.ConstantInstructionProcessor
import org.powlab.jeye.decode.processor.control.ControlInstructionProcessor
import org.powlab.jeye.decode.processor.conversion.ConversionInstructionProcessor
import org.powlab.jeye.decode.processor.stack.StackInstructionProcessor
import org.powlab.jeye.decode.processor.custom.CustomInstructionProcessor
import org.powlab.jeye.decode.processor.store.StoreInstructionProcessor
import org.powlab.jeye.decode.processor.load.LoadInstructionProcessor
import org.powlab.jeye.decode.MethodContext
import org.powlab.jeye.decode.graph.OpcodeNode

class Processors(mc: MethodContext) extends AbstractInstructionProcessor(mc) {

  lazy private val comparison = new ComparisonInstructionProcessor(mc)
  lazy private val constant = new ConstantInstructionProcessor(mc)
  lazy private val control = new ControlInstructionProcessor(mc)
  lazy private val conversion = new ConversionInstructionProcessor(mc)
  lazy private val load = new LoadInstructionProcessor(mc)
  lazy private val math = new MathInstructionProcessor(mc)
  lazy private val reference = new ReferenceInstructionProcessor(mc)
  lazy private val stack = new StackInstructionProcessor(mc)
  lazy private val store = new StoreInstructionProcessor(mc)
  lazy private val custom = new CustomInstructionProcessor(mc)

  def process(node: OpcodeNode) {
    val runtimeOpcode = node.runtimeOpcode
    //TODO here: Для отладки
    //println("process = " + node + ", cat " + categoryName(runtimeOpcode.opcode.category))
    val opcode = runtimeOpcode.opcode
    val category = opcode.category
    category match {
      case OTYPE_CONSTANT => constant.process(node)
      case OTYPE_COMPARISON => comparison.process(node)
      case OTYPE_CONTROL => control.process(node)
      case OTYPE_CONVERSION => conversion.process(node)
      case OTYPE_LOAD => load.process(node)
      case OTYPE_MATH => math.process(node)
      case OTYPE_REFERENCE => reference.process(node)
      case OTYPE_STACK => stack.process(node)
      case OTYPE_STORE => store.process(node)
      case OTYPE_CUSTOM  => custom.process(node)
      case _ => processException(node)
    }
  }

}