package org.powlab.jeye.decode.graph

import org.powlab.jeye.decode.expression._
import org.powlab.jeye.decode.expression.Expressions._
import org.powlab.jeye.decode.expression.ExpressionClassifiers._
import org.powlab.jeye.decode.processor.comparison._
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.{ isIfNode }
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator.{ isGotoNode }
import org.powlab.jeye.decode.processor.control.ReturnInstructionInformator.{ isVoidReturnNode, isReturnNode }
import org.powlab.jeye.decode.processor.control.SwitchInstructionInformator.isSwitchNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.{ isDefaultNode, isCaseNode, isTryNode }
import org.powlab.jeye.decode.processor.reference.ReferenceInformator.isMonitorEnterNode
import org.powlab.jeye.decode.graph.OpcodeNodes._
import org.powlab.jeye.decode.graph.OpcodeDetails._
import org.powlab.jeye.decode.sids.Sid
import org.powlab.jeye.core.Exception
import org.powlab.jeye.core.Exception.EXPRESSION_AREA
import org.powlab.jeye.decode.expra.ExpressionGuideWorker
import org.powlab.jeye.decode.transformer.InitArrayTransformer
import org.powlab.jeye.decode.expression.WhileCycleExpression
import org.powlab.jeye.decode.transformer._
import org.powlab.jeye.decode.ClassFacade
import org.powlab.jeye.decode.MethodContext

/**
 * Построение итогового выражения
 * TODO here: нужен рефакторинг в связи с новыми сущностями
 */
object GraphExpressions {

  def make(methodContext: MethodContext): IExpression = {
    val expression = build(methodContext.tree)
    transform(expression, methodContext)
    expression
  }

  private def build(tree: OpcodeTree): IExpression = {
    val selector = tree.selector
    val marker = tree.prepared
    val plainTree = tree.plainTree

    def buildExpression(node: OpcodeNode, parentSid: String, block: BlockExpression) {
      var current: OpcodeNode = node
      while (current != null) {
        var details = tree.details(current)
        if (marker.isMarked(current) || !details.sid.startsWith(parentSid)) {
          return
        }
        marker.mark(current)
        if (isCycleDetails(details)) {
          current = buildCycleExpression(current, block)
        } else if (isIfDetails(details)) {
          current = buildIfExpression(current, block)
        } else if (isSwitchNode(current)) {
          current = buildSwitchExpression(current, block)
        } else if (isMonitorEnterNode(current)) {
          current = buildSynchronizedExpression(current, block)
        } else if (isTryNode(current)) {
          current = buildTryExpression(current, block)
        } else if (isVoidReturnNode(current) && !plainTree.hasNext(current)) {
          // последний return отображать не нужно, если он не возвращает значения
          current = null;
        } else {
          if (current.branchy) {
            val reason = "Обнаружена инструкция '" + current + "' для которой не найдена секция построения выражения"
            val effect = "Построение выражения метода будет прекращено."
            val action = "Необходимо добавить секцию обработки инструкции '" + current + "' для корректного построения выражения"
            throw Exception(EXPRESSION_AREA, reason, effect, action)
          }
          // пропускаем узлы, которые не содержат выражения. Используется для отладки, но это необходимо проверить
          if (details.expression != null) {
            block += details.expression
          }
          current = tree.next(current)
        }
      }
    }

    /**
     * Построение цикла
     */
    def buildCycleExpression(cycleNode: OpcodeNode, block: BlockExpression): OpcodeNode = {
      val details = tree.cycleDetails(cycleNode)
      val expression = getCycleExpression(cycleNode, tree)
      if (tree.owner(details.bodyId) != cycleNode) {
        buildExpression(tree.current(details.bodyId), details.sid, expression)
      }
      if (details.label != null) {
        block += new LabelExpression(details.label)
      }
      block += expression
      tree.current(details.elseId)
    }

    /**
     *  Построение if выражений
     */
    def buildIfExpression(ifNode: OpcodeNode, block: BlockExpression): OpcodeNode = {
      val details = tree.ifDetails(ifNode)
      val expression = getIfExpression(ifNode, tree)
      buildExpression(tree.current(details.bodyId), details.sid, expression)
      block += expression
      val sid = new Sid(details.sid)
      val elseNode = tree.current(details.elseId)
      val elseSid = tree.sid(elseNode)
      // Если имеется ветка else
      if (sid.childId(2) == elseSid) {
        if (details.elseBranch) {
          val secondChildSid = selector.next(elseSid)
          val isIfElseBlock = (secondChildSid == null && isIfNode(elseNode) && !isCycleDetails(tree.details(elseNode)))
          // если это else if
          if (isIfElseBlock) {
            block += ELSE_EXPRESSION
            return elseNode
          } else {
            val elseExpr = new StatementExpression(ELSE_EXPRESSION)
            block += elseExpr
            buildExpression(elseNode, details.sid, elseExpr)
            return selector.next(details.sid)
          }
        }
        buildExpression(elseNode, details.sid, block)
        return selector.next(details.sid)
      } else {
        // Случай, когда if последний в цикле завершается break, то оптимизатор опускает break и прыгает с if
        // Пример:
        // while (...) {
        // ...
        //     if (....) {
        //         break;
        //     }
        //     break;
        // }
        // Оптимизатор последний break переносит в if-jumping
        // Проверим, выше изложенное
        if (selector.last(details.sid) == ifNode && sid.parentId != null &&
          sid.parentSid.nextId == elseSid) {
          val pNode = selector.current(sid.parentId)
          if (isCycleDetails(tree.details(pNode))) {
            block += BREAK_EXPRESSION
          }
        }
        if (!details.elseBranch && elseSid != null && selector.contains(elseSid)) {
          selector.current(elseSid)
        } else {
          selector.next(details.sid)
        }
      }
    }

    /**
     * Построение switch выражения
     */
    def buildSwitchExpression(switchNode: OpcodeNode, block: BlockExpression): OpcodeNode = {
      val details = tree.details(switchNode)
      val switchExpr = new StatementExpression(details.expression)
      val nexts = tree.nexts(switchNode)
      val last = nexts.last
      nexts.filter(_ != last).foreach(nextNode => {
        buildExpression(nextNode, tree.sido(nextNode).base, switchExpr)
      })
      val lastSid = tree.sido(last)
      // Вырезаем ненужный default и или избыточные case, которые могут идти вместе с default
      if (getOpcodeNodes(last).find(isDefaultNode).isDefined) {
        if (selector.current(lastSid.nextId) != null) {
          tree.details(last).expression = SWITCH_DEFAULT_EXPRESSION
          buildExpression(last, details.sid, switchExpr)
        }
      } // Обрабатываем последний case
      else if (isCaseNode(last)) {
        if (selector.current(lastSid.nextId) != null) {
          buildExpression(last, details.sid, switchExpr)
        } else {
          switchExpr += tree.details(last).expression
          switchExpr += BREAK_EXPRESSION
        }
      }
      block += switchExpr
      selector.next(details.sid)
    }

    /**
     * Построение try выражения
     */
    def buildTryExpression(tryNode: OpcodeNode, block: BlockExpression): OpcodeNode = {
      val details = tree.details(tryNode)
      // TODO here try-with-resources может быть c 1 веткой (это пока не реализовано)
      val tryExpr = new StatementExpression(details.expression)
      if (tree.nextCount(tryNode) == 1) {
        buildExpression(tree.next(tryNode), details.sid, tryExpr)
        block += tryExpr
      } else {
        val nexts = tree.nexts(tryNode)
        buildExpression(nexts(0), details.sid, tryExpr)
        block += tryExpr
        nexts.tail.foreach(catchOrFinallyNode => {
          val catchOrFinallyDetails = tree.details(catchOrFinallyNode)
          val catchOrFinallyExpr = new StatementExpression(catchOrFinallyDetails.expression)
          buildExpression(tree.next(catchOrFinallyNode), details.sid, catchOrFinallyExpr)
          block += catchOrFinallyExpr
        })
      }
      selector.next(details.sid)
    }

    /**
     * Построить synch выражение
     */
    def buildSynchronizedExpression(synchNode: OpcodeNode, block: BlockExpression): OpcodeNode = {
      val details = tree.details(synchNode)
      val synchExpr = new StatementExpression(details.expression)
      buildExpression(tree.next(synchNode), details.sid, synchExpr)
      block += synchExpr
      selector.next(details.sid)
    }

    // Для бесконечных циклов
    marker.mark(tree.head)
    val body = new BlockExpression
    buildExpression(tree.top, "", body)

    body
  }

  private def getIfExpression(ifNode: OpcodeNode, tree: OpcodeTree): StatementExpression = {
    new StatementExpression(new IfWordExpression(tree.details(ifNode).expression))
  }

  /**
   * TODO here: создание while вынести в место, где создается соответствующий узел
   */
  private def getCycleExpression(cycleNode: OpcodeNode, tree: OpcodeTree): BlockExpression = {
    val cycleDetails = tree.cycleDetails(cycleNode)
    if (cycleDetails.detailType == DETAIL_CYCLE_WHILE) {
      val whileCycleExpr = new WhileCycleExpression(cycleDetails.expression)
      new StatementExpression(whileCycleExpr)
    } else if (cycleDetails.detailType == DETAIL_CYCLE_FOR) {
      return new StatementExpression(cycleDetails.expression)
    } else if (cycleDetails.detailType == DETAIL_CYCLE_DO_WHILE) {
      new StatementExpression(DO_EXPRESSION, cycleDetails.expression)
    } else {
      null
    }
  }

  /**
   * TODO оптимизация: добавление трансформеров нужно сделать еще по более строгому условию,
   * например, для ForeachIterableTransformer, добавлять имеет смысл если еще встретилась
   * сигнатура вызывающего метода равной Ljava/util/Iterator;
   */
  private def transform(expression: IExpression, methodContext: MethodContext) {
    val guide = new ExpressionGuideWorker(expression)
    val classifiers = methodContext.tree.resources.getClassifiers
    if (classifiers.contains(EC_NEW_ARRAY)) {
      guide += new InitArrayTransformer
    }
    if (classifiers.contains(EC_FOR_CYCLE) && classifiers.contains(EC_ARRAY_LENGTH)) {
      guide += new ForeachArrayTransformer
    }
    if (classifiers.contains(EC_WHILE_CYCLE) && classifiers.contains(EC_INVOKE_INTERFACE)) {
      guide += new ForeachIterableTransformer
    }
    // TODO here: просмотр методов: valueOf и [int,double,float,short,byte]Value
    if (classifiers.contains(EC_INVOKE_STATIC) ||
      classifiers.contains(EC_INVOKE_VIRTUAL) ||
      classifiers.contains(EC_INVOKE_SPECIAL) ||
      classifiers.contains(EC_PRIMITIVE_CAST)) {
      guide += new AutoBoxingTransformer(methodContext.extra.classFacade)
    }
    // TODO here: написать условие выбора
    guide += new PrettyViewTransformer
    // TODO here: написать условие отбора
    if (classifiers.contains(EC_INVOKE_VIRTUAL)) {
      guide += new StringBuilderToStringTransformer
    }
    if (classifiers.contains(EC_CONSTRUCTOR)) {
      guide += new ConstructorTransformer(methodContext.classFile)
    }
    // TODO here: VaribaleCorrectTransformer перенести в class scope
    // Если есть локальные переменные
    if (methodContext.localVariables.localCount > 0) {
      guide += new VaribaleCorrectTransformer
    }
    if (classifiers.contains(EC_SYNCHRONIZE)) {
      guide += new SynchronizeCorrectTransformer
    }
    guide.go
  }
}

/**
 * TODO here: нужно подумать куда перенести эти классы
 */

private class LabelExpression(label: String) extends IExpression {
  def view(parent: IExpression): String = label + ":"

  def classifier(): ExpressionClassifier = EC_LABEL
}

class BreakWithLabelExpression(label: String) extends IExpression {
  def view(parent: IExpression): String = "break " + label + ";"

  def classifier(): ExpressionClassifier = EC_BREAK_LABEL
}

class ContinueWithLabelExpression(label: String) extends IExpression {
  def view(parent: IExpression): String = "continue " + label + ";"

  def classifier(): ExpressionClassifier = EC_CONTINUE_LABEL
}