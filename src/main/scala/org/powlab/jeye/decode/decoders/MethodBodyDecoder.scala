package org.powlab.jeye.decode.decoders

import org.powlab.jeye.core._
import org.powlab.jeye.core.Exception
import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.FrameStack
import org.powlab.jeye.decode._
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.{GraphExpressions, OpcodeGraph}
import org.powlab.jeye.decode.{ExceptionHandlers, LocalVariableStore, MethodContext}
import org.powlab.jeye.utils.{AttributeUtils, PrintUtils}
import scala.collection.mutable.{Map, Set}
import org.powlab.jeye.decode.processor.Processors
import org.powlab.jeye.decode.RuntimeOpcodes
import org.powlab.jeye.decode.LocalVariableTrace
import org.powlab.jeye.decode.graph.OpcodeNode

class MethodBodyDecoder(classFile: ClassFile) extends MethodPartDecoder(classFile) {

  /** Используется для отображения в GUI */
  var graph: OpcodeGraph = null

  def decode(method: MemberInfo, extendedContext: ExtraInfo): IExpression = {
    val namer = getNamer(method)
    val codeAttribute = AttributeUtils.get[CodeAttribute](method.attributes)
    val runtimeOpcodes = RuntimeOpcodes.parseRuntimeOpcodes(codeAttribute.code)
    val exceptions = new ExceptionHandlers(codeAttribute, cpUtils)
    val localVariables = LocalVariableStore(cpUtils, method, namer)
    val initialVars = localVariables.store.clone

    println("Метод " + cpUtils.getUtf8(method.name_index) + " класса " + cpUtils.thisClass.javaName)
    println("Входные параметры для метода:")
    PrintUtils.printVariables(localVariables.store)
    println("Runtime Opcodes метода:")
    PrintUtils.printRuntimeOpcodes(runtimeOpcodes, cpUtils)
    println(exceptions)

    graph = new OpcodeGraph(runtimeOpcodes, exceptions)
    //println("-- graph --")
    //PrintUtils.printGraph(graph.top)

    val trace = LocalVariableTrace()
    val draftContext = new MethodContext(classFile, method, new FrameStack, localVariables, namer,
        graph.getDraftTree, true, extendedContext, trace)
    val draftScope = buildDraft(draftContext)

    trace.complete
    val baseLocVars = localVariables.copy(initialVars)
    val baseContext = new MethodContext(classFile, method, new FrameStack, baseLocVars, namer,
        graph.getResultTree, false, extendedContext, trace)
    decodeGraph(baseContext, draftScope)
  }

  private def buildDraft(methodContext: MethodContext): DraftScope = {
    val tree = methodContext.tree
    val marker = tree.prepared
    val processors = new Processors(methodContext)
    val frames = methodContext.frames
    def decoder(top: OpcodeNode) {
      var current = top
      while (current != null) {
        if (marker.isMarked(current)) {
          return
        }
        // помечаем узел как пройденный
        marker.mark(current)
        processors.process(current)
        // У таких блоков следующий элемент (current.next) должен указывать на начало нового блока, миную содержимого текущего
        if (current.branchy) {
          // copy state for OperandStack/LocalVariable and create NamedBlockExpression
          tree.nexts(current).foreach(branchNode => {
            frames.pushFrame
            decoder(branchNode)
            frames.popFrame
          })
          return
        } else {
          current = tree.next(current)
        }
      }
    }
    println("------------------------")
    decoder(tree.head)
    val waitIds = graph.processDraftTree()
    new DraftScope(waitIds)
  }

  class DraftScope(waitIds: Set[String]) {
    def hasWaitId(id: String): Boolean = waitIds.contains(id)
  }

  private def decodeGraph(methodContext: MethodContext, draft: DraftScope): IExpression = {
    val tree = methodContext.tree
    val marker = tree.prepared
    val processors = new Processors(methodContext)
    val frames = methodContext.frames

    val questions = Map[String, TernaryExpressionRef]()
    def checkQuestion(id: String, methodContext: MethodContext) {
      // Вот здесь супер кахом, архитектурным ляпом идет наполнение оператора ?
      if (draft.hasWaitId(id)) {
        val stack = frames
        if (stack.isEmpty) {
          val reason = "Операнд стэк пуст для id " + id + ". Ожидалось значение в операнд стэке."
          val effect = "Невозможно получить значение для тернарного оператора, обработка графа инструкций будет прекращена."
          val action = "Необходимо исправить логику определения тернарного оператора"
          throw Exception(Exception.TERNARY_AREA, reason, effect, action)
        }
        var quesExprOpt = questions.get(id)
        val variable = stack.pop
        if (quesExprOpt.isEmpty) {
          val ternaryRef = new TernaryExpressionRef(variable.descriptor)
          questions(id) = ternaryRef
          ternaryRef += variable
          stack.push(ternaryRef)
        } else {
          quesExprOpt.get += variable
        }
      }

    }

    def decoder(top: OpcodeNode) {
      var current = top
      while (current != null) {
        checkQuestion(current.id, methodContext)
        if (marker.isMarked(current)) {
          return
        }
        // помечаем узел как пройденный
        marker.mark(current)
        processors.process(current)
        // У таких блоков следующий элемент (current.next) должен указывать на начало нового блока, миную содержимого текущего
        if (current.branchy) {
          // copy state for OperandStack/LocalVariable and create NamedBlockExpression
          tree.nexts(current).foreach(branchNode => {
            // TODO here: N1. ВСКРЫЛАСЬ БОЛЬШАЯ ПРОБЛЕМА, что последний элемент в ветке не
            // флашится в expression а вместо этого прокидывается наверх - это следствие еще
            // более глобальной проблемы - несовсем корректно обходится дерево опкодов
            // нужны правильные сиды сразу
            frames.pushFrame
            decoder(branchNode)
            frames.popFrame
          })
        }
        current = tree.next(current)
      }
    }
    decoder(tree.head)
    graph.processResultTree(questions)

    GraphExpressions.make(methodContext)
  }

}