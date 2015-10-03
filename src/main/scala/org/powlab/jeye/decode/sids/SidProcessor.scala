package org.powlab.jeye.decode.sids

import scala.collection.mutable.Map
import org.powlab.jeye.core.Exception
import org.powlab.jeye.decode.expression.IfBooleanExpression
import org.powlab.jeye.decode.expression.InvokeVirtualExpression
import org.powlab.jeye.decode.graph.OpcodeDetails.IfOpcodeDetail
import org.powlab.jeye.decode.graph.OpcodeDetails.isCycleDetails
import org.powlab.jeye.decode.graph.OpcodeDetails.isIfDetails
import org.powlab.jeye.decode.graph.GroupOpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNode
import org.powlab.jeye.decode.graph.OpcodeNodes.getOpcodeNodes
import org.powlab.jeye.decode.graph.OpcodeTree
import org.powlab.jeye.decode.graph.OpcodeTreeMarker
import org.powlab.jeye.decode.processor.comparison.ComparisonInformator.isIfNode
import org.powlab.jeye.decode.processor.control.SwitchInstructionInformator.isSwitchNode
import org.powlab.jeye.decode.processor.custom.CustomInformator.isSwitchChild
import org.powlab.jeye.decode.processor.reference.ReferenceInformator
import org.powlab.jeye.decode.processor.control.GotoInstructionInformator

/**
 * Основная задача сформировать для узлов дерева правильный id
 */
object SidProcessor {

  /**
   * Получить черновой вариант selector
   */
  def draftSelector(tree: OpcodeTree): SidSelector = {
    draftSelector(tree.head, tree)
  }

  /**
   * Получить черновой вариант selector
   */
  def draftSelector(fromTop: OpcodeNode, tree: OpcodeTree): SidSelector = {
    val nodeMarker = tree.prepared
    val sid2Node = Map[String, OpcodeNode]()
    val selector = new SidSelector(sid2Node)

    def doSkan(node: OpcodeNode, fromSid: Sid) {
      var preview: OpcodeNode = null
      var current: OpcodeNode = node
      var currentBase = fromSid.base
      var order: Int = fromSid.order
      while (current != null) {
        if (nodeMarker.isMarked(current)) {
          return
        }
        val id = current.id
        // Это блок корректировки sid
        if (tree.hasPreviews(current)) {
          val correctedSid = getCorrectedSid(current, tree, nodeMarker)
          if (Sid.before(Sid.make(currentBase, order), correctedSid.sid)) {
            currentBase = correctedSid.base
            order = correctedSid.order
          }
        }
        val curdet = tree.details(current)
        nodeMarker.mark(current)
        curdet.sid = Sid.make(currentBase, order)
        sid2Node.put(curdet.sid, current)
        if (current.isInstanceOf[GroupOpcodeNode]) {
          getOpcodeNodes(current).foreach(node => {
            tree.details(node).sid = curdet.sid
          })
        }

        // Более точная обработка секции if и цикл
        if (current.branchy) {
          val branchedSid = new Sid(curdet.sid);
          var branchNumber = 1;
          tree.nexts(current).foreach(next => {
            doSkan(next, branchedSid.childSid(branchNumber))
            branchNumber += 1;
          })
          return
        }

        if (ReferenceInformator.isMonitorEnterNode(current)) {
          val monChilSid = new Sid(curdet.sid).childSid(1)
          currentBase = monChilSid.base
          order = monChilSid.order - 1
        } else if (ReferenceInformator.isMonitorExitNode(current) &&
            GotoInstructionInformator.isGotoNode(tree.next(current))) {
          val monParentSid = new Sid(curdet.sid).parentSid
          currentBase = monParentSid.base
          order = monParentSid.order
        }

        preview = current
        current = tree.next(current)
        order += 1
      }
    }
    //graph.skanWith(fromTop, node => {node.sid = null})
    doSkan(fromTop, Sid.INITIAL_SID)
    selector
  }

  /**
   * Назначить узлам структурированные id и получить объект, содержащий эти id
   */
  def selector(tree: OpcodeTree): SidSelector = {
    val nodeMarker = tree.prepared
    val sid2Node = Map[String, OpcodeNode]()
    val selector = new SidSelector(sid2Node)

    // TODO here: вынести этот метод в отдельный класс анализатора выражений
    def isPartOfSwitchByString(node: OpcodeNode):Boolean = {
      if (!isIfNode(node)) {
        return false
      }
      val sid = tree.sid(node) // sid из драфта
      if (sid == null) {
        return false
      }
      val sido = Sid(sid)
      if (!sido.hasParentSid || !sid2Node.contains(sido.parentId)) {
        return false
      }
      val parentId = Sid(sid).parentId
      val parentNode = sid2Node(parentId)
      if (isSwitchNode(parentNode)) {
        // TODO here: заменить на скан в ширь и поиск требуемого выражения InvokeVirtualExpression
        // TODO here: не только может быть IfBooleanExpression также groupif
        val expr = tree.expression(node)
        if (expr.isInstanceOf[IfBooleanExpression]) {
          val realExpr = expr.asInstanceOf[IfBooleanExpression].expression
          if (realExpr.isInstanceOf[InvokeVirtualExpression])  {
            val invokeExpr = realExpr.asInstanceOf[InvokeVirtualExpression]
            if ("equals" == invokeExpr.methodName) {
              return true
            }
          }
        }
      }
      isPartOfSwitchByString(parentNode)
    }

    def isSwitchByString(node: OpcodeNode):Boolean = {
      return tree.previews(node).find(!isPartOfSwitchByString(_)).isEmpty
    }

    def doSkan(node: OpcodeNode, fromSid: Sid) {
      var preview: OpcodeNode = null
      var current: OpcodeNode = node
      var currentBase = fromSid.base
      var order: Int = fromSid.order
      while (current != null) {
        if (nodeMarker.isMarked(current)) {
          return
        }
        val id = current.id
        // Это блок корректировки sid
        //println("--------------------------------------")
        //println("[previews] " + tree.previews(current.id))
        if (tree.hasPreviews(current)) {
          //println("[sid-previews] " + tree.previews(current.id))
          if (order == 1 && isCycleDetails(tree.details(node))) {
              // Сохраняем первый узел цикла как есть, т.е. не производим никаких манипуляций
              // код отсутсвует
          } else
          // Обработка case/default - парентом может быть только switch узел
          if (isSwitchChild(current)) {
            val switchChildSid = Sid(currentBase, order)
            val parentSid = switchChildSid.parentSid
            val parentNode = sid2Node(parentSid.sid)
            if (! isSwitchNode(parentNode)) {
              return
            }
            if (order != 1) {
              return
            }
          } else if (order > 1 && isSwitchByString(current)) {
            val targetNode = tree.previews(current).last
            val currentSid = Sid.make(currentBase, order - 1)
            val parentNode = sid2Node(currentSid)
            if (parentNode != targetNode) {
              return
            }
          } else {
            // TODO here: очень тонко, нужно переосмыслить и обложить тестами
            val correctedSid = getCorrectedSid(current, tree, nodeMarker)
            if (Sid.before(Sid.make(currentBase, order), correctedSid.sid)) {
              currentBase = correctedSid.base
              order = correctedSid.order
              val nodeSid = tree.sido(node)
              if (nodeSid.branch == 1 && order > 1) {
                val parentNode = selector.current(currentBase + "#" + (order - 1))
                if (isIfNode(parentNode)) {
                  val ifDetails = tree.ifDetails(parentNode)
                  if (!ifDetails.elseBranch && isIfDetails(ifDetails)) {
                    return
                  }
                }
              }
            }
          }
        }
        val curdet = tree.details(current)
        nodeMarker.mark(current)
        curdet.sid = Sid.make(currentBase, order)
        sid2Node.put(curdet.sid, current)
        if (current.isInstanceOf[GroupOpcodeNode]) {
          getOpcodeNodes(current).foreach(node => {
            tree.details(node).sid = curdet.sid
          })
        }

        // Более точная обработка секции if и цикл
        if (curdet.isInstanceOf[IfOpcodeDetail]) {
          val ifNodeDetails = curdet.asInstanceOf[IfOpcodeDetail]
          val ifNode = current
          if (!ifNodeDetails.elseBranch) {
            val bodyNode = tree.current(ifNodeDetails.bodyId)
            val nextNode = tree.current(ifNodeDetails.elseId)
            val sid = new Sid(curdet.sid);
            doSkan(nextNode, sid.nextSid)
            doSkan(bodyNode, sid.childSid(1))
          } else {
            val ifBody = tree.current(ifNodeDetails.bodyId)
            val ifElse = tree.current(ifNodeDetails.elseId)
            val ifSid = new Sid(curdet.sid)
            doSkan(ifBody, ifSid.childSid(1))
            doSkan(ifElse, ifSid.childSid(2))
          }
          return
        } else if (isSwitchNode(current)) {
          val nexts = tree.nexts(current)
          val sortedNexts = nexts
          val last = sortedNexts.last
          val branchedSid = new Sid(curdet.sid);
          var branchNumber = 1;
          sortedNexts.filter(_ != last).foreach(next => {
            doSkan(next, branchedSid.childSid(branchNumber))
            branchNumber += 1;
          })
          // TODO here: возможно здесь if нужно переписать по другому: !id2Node.contains(last.next.id)
          // TODO here: надо с этим разобраться
          if (sid2Node.contains(branchedSid.nextId)) {
            doSkan(last, branchedSid.childSid(branchNumber))
            return
          } else {
            tree.details(last).sid = branchedSid.childId(branchNumber)
            nodeMarker.mark(last)
            sid2Node.put(tree.details(last).sid, last)
            if (tree.nextCount(last) > 1) {
              throw new IllegalStateException("Please add code here: case has childen!");
            }
            current = last
          }
        } else if (current.branchy) {
          val branchedSid = new Sid(curdet.sid);
          var branchNumber = 1;
          tree.nexts(current).foreach(next => {
            doSkan(next, branchedSid.childSid(branchNumber))
            branchNumber += 1;
          })
          return
        }
        if (ReferenceInformator.isMonitorEnterNode(current)) {
          val monChilSid = new Sid(curdet.sid).childSid(1)
          currentBase = monChilSid.base
          order = monChilSid.order - 1
        } else if (ReferenceInformator.isMonitorExitNode(current) &&
            GotoInstructionInformator.isGotoNode(tree.next(current))) {
          val monParentSid = new Sid(curdet.sid).parentSid
          currentBase = monParentSid.base
          order = monParentSid.order
        }

        preview = current
        current = tree.next(current)
        order += 1
      }
    }
    doSkan(tree.head, Sid.INITIAL_SID)
    selector
  }

  /**
   * Получить sid для узла someNode
   */
  private def getCorrectedSid(someNode: OpcodeNode, tree: OpcodeTree, nodeMarker: OpcodeTreeMarker): Sid = {
    val parentSid = getParentSid(someNode, tree, nodeMarker)
    if (parentSid == null) {
      val reason = "Не найден родительский sid для узла " + someNode
      val effect = "Невозможно назначить узлам структурированные идентификаторы"
      val action = "Необходимо исправить логику определения структурированных идентификаторов"
      throw Exception(Exception.SID_AREA, reason, effect, action)
    }
    new Sid(parentSid).nextSid
  }

  /**
   * Определение родительского sid
   */
  private def getParentSid(someNode: OpcodeNode, tree: OpcodeTree, nodeMarker: OpcodeTreeMarker): String = {
    val marker = tree.prepared
    var sid: String = null
    def doSkanBack(node: OpcodeNode) {
      var preview: OpcodeNode = null
      var current: OpcodeNode = node
      while (current != null) {
        if (nodeMarker.isMarked(current)) {
          val csid = tree.sid(current)
          if (sid == null || Sid.before(csid, sid)) {
            sid = csid
          }
          return
        }
        if (marker.isMarked(current)) {
          return
        }
        marker.mark(current)
        if (tree.hasPreviews(current)) {
          tree.previews(current).foreach(doSkanBack(_))
          return
        }
        preview = current
        current = tree.preview(current)
      }
    }
    doSkanBack(someNode)
    sid
  }

}