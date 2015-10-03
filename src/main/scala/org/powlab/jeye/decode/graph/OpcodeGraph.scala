package org.powlab.jeye.decode.graph

import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import org.powlab.jeye.decode.expression.ExpressionClassifiers.{EC_FOR_CYCLE, EC_WHILE_CYCLE}
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.TernaryExpressionRef
import org.powlab.jeye.decode.RuntimeOpcode
import org.powlab.jeye.decode.ExceptionHandlers
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.{ isGotoNode, branch }
import org.powlab.jeye.utils.PrintUtils
import org.powlab.jeye.decode.pattern.LogicPatterns
import org.powlab.jeye.decode.pattern.ternary.TernaryOperator
import org.powlab.jeye.decode.corrector.IfDetailsCorrector
import org.powlab.jeye.decode.sids.SidProcessor
import org.powlab.jeye.decode.corrector.OpcodeTreeSimplifier
import org.powlab.jeye.decode.corrector.TryCatchFinallyCorrector
import org.powlab.jeye.decode.pattern.cycle.Cycles

class OpcodeGraph(runtimeOpcodes: Array[RuntimeOpcode], exceptions: ExceptionHandlers) {

  private var nativeTree: OpcodeTree = null
  private var draftTree: OpcodeTree = null
  private var resultTree: OpcodeTree = null
  initialize()

  /**
   * Необходимо построить корректную модель, т.е. проанализировать все if блоки, goto, try/cacth/finally, synchronized
   * Здесь должна быть сосредоточена часть логики, связанная с выделением блоков
   */
  private def initialize() {
    val plainTree = OpcodeTreeBuilder.buildPlainTree(runtimeOpcodes)
    nativeTree = OpcodeTreeBuilder.buildTree(plainTree)
    OpcodeTreeInjector.inject(nativeTree, exceptions)
    // для черновых работ
    draftTree = OpcodeTreeHelper.copy(nativeTree)
    // Удаление лишних опкодов finally из try/catch
    TryCatchFinallyCorrector.processFinallyNodes(exceptions, draftTree)
    // все найденные шаблоны во время создания дерева должны быть применены
    StreamPatterns.process(draftTree.resources.getPatternResults, draftTree)
    //println("draft tree");
    //PrintUtils.printGraph(draftTree)
    // для окончательных работ
    resultTree = OpcodeTreeHelper.copy(draftTree)
  }

  def getNativeTree(): OpcodeTree = nativeTree
  def getDraftTree(): OpcodeTree = draftTree
  def getResultTree(): OpcodeTree = resultTree

  /**
   * Цель: Построить черновой вариант дерева. Черновой вариант пригодится для определения
   * корректных типов (boolean, byte, char, short), а также конструкций  типа ?
   * TODO here: вернуть DraftResult вместо Set[String]
   */
  def processDraftTree(): Set[String] = {
    // 1. Оставляем только те узлы, которые содержат expression
    OpcodeTreeRepacker.repack(draftTree)

    // 2. Обработка инструкций if - объединяем все if и сжимаем дерево еще раз
    val ifGroups = LogicPatterns.groupIfNodes(draftTree)

    // 3 Определяем if инструкции тернарного оператора
    TernaryOperator.getTargets(ifGroups, draftTree)
  }

  /**
   * TODO here: Map[String, IfQuestionExpression] - переделать на цельный объект - результат процесоров
   */
  def processResultTree(questions: Map[String, TernaryExpressionRef]) {
    // 1. Оставляем только те узлы, которые содержат expression
    OpcodeTreeRepacker.repack(resultTree)

    // 2. Обработка инструкций if - объединяем все if и сжимаем дерево еще раз
    val ifGroups = LogicPatterns.groupIfNodes(resultTree)

    // 3 Упроздняем if вида 'condition ? value1 : value2'
    val ternaryNodes = TernaryOperator.processTernary(questions, ifGroups, resultTree)
    ifGroups --= ternaryNodes

    // 3.1 Упроздняем конструкции подобные тернарнxым выражениям
    val exprTernaryNodes = TernaryOperator.compactSimilarTernary(ifGroups, resultTree)
    ifGroups --= exprTernaryNodes

    // 4. Формируем первичный sid, потом его еще раз затюним после определения циклов!
    resultTree.selector = SidProcessor.draftSelector(resultTree)

    // 5. Выделение циклов (while)
    val resources = resultTree.resources
    val cycleNodes = Cycles.detectCycles(ifGroups, resultTree)
    if (cycleNodes.nonEmpty) {
        // 5.11 Сбрасываем признак if-else конструкции, там, где ветка else  - избыточна
        IfDetailsCorrector.resetElseBranch(ifGroups, resultTree)

        // 5.12 Строим сиды
        resultTree.selector = SidProcessor.selector(resultTree)

        // 5.13 Определение типа циклов (делаем после корректировки sid) for/foreach/while
        val forCycles = Cycles.detectForCycles(cycleNodes, resultTree)
        if (forCycles != null && forCycles.nonEmpty) {
            resources += EC_FOR_CYCLE
        }
        if (cycleNodes.nonEmpty && (forCycles == null || forCycles.size < cycleNodes.size)) {
            resources += EC_WHILE_CYCLE
        }
    }
    // 6. Сбрасываем признак if-else конструкции еще раз
    IfDetailsCorrector.resetElseBranch(ifGroups, resultTree)

    // 7. Строим сиды
    // TODO here: этот проход нужен, если изменилась структура дерева
    // например после отработки detectForCycles -> tree.isolate
    // а в общем этот проход избыточен - нужна оптимизация
    resultTree.selector = SidProcessor.selector(resultTree)

    // 11. Определяем бесконечные циклы ('while (true)') и do/while
    val groupCycles = Cycles.detectInfinityCycles(resources.getGotoNodes, ifGroups --= cycleNodes, resultTree)
    if (groupCycles.notEmpty) {
      resources += EC_WHILE_CYCLE
      // 11.1 Сбрасываем признак if-else конструкции еще раз
      ifGroups --= groupCycles.ifNodes
      IfDetailsCorrector.resetElseBranch(ifGroups, resultTree)
      // 11.2 Строим сиды
      resultTree.selector = SidProcessor.selector(resultTree)
    }

    // 13. Упрощаем дерево, заменяем слова goto и monitor_exit на правильный эквивалент
    OpcodeTreeSimplifier.simplify(resultTree)
  }

}

