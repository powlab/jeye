package org.powlab.jeye.decode

import org.powlab.jeye.core._
import org.powlab.jeye.core.Opcodes.OpCode
import org.powlab.jeye.decode.expression.IExpression
import org.powlab.jeye.utils.PrintUtils
import org.powlab.jeye.utils.DecodeUtils.pad
import org.powlab.jeye.utils.ConstantPoolUtils
import scala.collection.mutable.ArrayBuffer
import scala.collection.mutable.Map
import scala.collection.mutable.Set
import scala.collection.mutable.HashSet

/**
 *  TODO here: нужен рефакторинг.
 *  Переименовать классы серии Handler в Region
 *  FinallyHandler -> FinallyRegion
 *  TryHandler -> TryRegion
 *  CatchHandler -> CatchRegion
 *  ExceptionHandler -> ExceptionRegion
 */
class ExceptionHandlers(code: CodeAttribute, cpUtils: ConstantPoolUtils) {

  private val targetNumbers = Map[Int, ExceptionHandler]()
  private val exceptions = new ArrayBuffer[ExceptionHandler]
  private val exceptionStatements = new ArrayBuffer[ExceptionStatement]
  private val exceptionMap = Map[Int, ArrayBuffer[ExceptionStatement]]()

  private def valid(exception: ExceptionHandler): Boolean = {
    // TODO here: условие exception.target != exception.from надо проработать
    exception.target != exception.from && targetNumbers.find(pair => {
      pair._1 == exception.target && pair._2.from == exception.from
    }).isDefined
  }

  private def processExceptionTable(exceptionTable: ExceptionTable) {
    val catchHandlerName = if (exceptionTable.catch_type == 0) "any" else cpUtils.getClassName(exceptionTable.catch_type)
    val exception = new ExceptionHandler(exceptionTable, catchHandlerName)
    exceptions += exception
    if (!targetNumbers.isDefinedAt(exception.target)) {
      targetNumbers.put(exception.target, exception)
    }
  }
  code.exception_table.foreach(processExceptionTable)

  private def processExceptions() {
    var previewException: ExceptionHandler = null
    var linkedExceptions = new ArrayBuffer[ExceptionHandler]

    def createNewStatement() {
      if (!linkedExceptions.isEmpty) {
        println("-- new exception")
        println(linkedExceptions.mkString("\n"))
        val catchTypesToTarget = Map[Int, ArrayBuffer[Int]]()
        def processCatch(exception: ExceptionHandler) {
          val target = exception.target
          var catches = catchTypesToTarget.getOrElse(target, null)
          if (catches == null) {
            catches = new ArrayBuffer[Int]
            catchTypesToTarget.put(target, catches);
          }
          catches += exception.catchType
        }
        linkedExceptions.filter(_.isCatch).foreach(processCatch)
        val finalException = linkedExceptions.find(_.isFinally)

        val number = linkedExceptions.head.from
        val catches = catchTypesToTarget.map(value => new CatchHandler(value._1, value._2.toArray, linkedExceptions.head)).toArray
        val finalNumber = if (finalException.isDefined) new FinallyHandler(finalException.get.target, finalException.get) else null
        val tryHandler = new TryHandler(number, linkedExceptions.head)
        val statement = new ExceptionStatement(tryHandler, catches.sortWith(_.number < _.number), finalNumber)
        exceptionStatements += statement
        println(statement)
        linkedExceptions = new ArrayBuffer[ExceptionHandler]
      }
    }

    def isNewStatement(exception: ExceptionHandler): Boolean = {
      previewException != null && (previewException.from != exception.from ||
        (previewException.to != exception.to
          && !(previewException.isCatch && exception.isFinally)))
    }

    def processException(exception: ExceptionHandler) {
      if (isNewStatement(exception)) {
        createNewStatement()
      }
      linkedExceptions += exception
      previewException = exception
    }
    exceptions.filter(valid).sortWith((lt, rt) => {
      if (lt.from == rt.from) {
        if (lt.to == rt.to) {
          if (lt.target == rt.target) {
            lt.catchHandler < rt.catchHandler
          } else {
            lt.target < rt.target
          }
        } else {
          lt.to < rt.to
        }
      } else {
        lt.from < rt.from
      }
    }).foreach(processException)
    createNewStatement()
  }
  processExceptions

  def hasExceptions = !exceptionStatements.isEmpty
  def getExceptions: ArrayBuffer[ExceptionStatement] = exceptionStatements
  def getExceptionHandlers: ArrayBuffer[ExceptionHandler] = exceptions

  def findExceptionStatement(exceptionHandler: ExceptionHandler): Option[ExceptionStatement] = {
    exceptionStatements.find(exceptionStatement => {
         exceptionStatement.catches.find(_.handler == exceptionHandler).isDefined
      })
  }

  override def toString(): String = {
    val buf = new StringBuilder
    buf.append("Exceptions: \n")
    if (exceptions.isEmpty) {
      buf.append("no exceptions")
    } else {
      val padSize = 8
      buf.append(pad("from", 8)).append(pad("to", 8)).append(pad("target", 8)).append(pad("catch", 8)).append("\n")
      exceptions.foreach(buf.append(_).append("\n"))
    }
    buf.toString
  }

}

class ExceptionHandler(table: ExceptionTable, val catchHandlerName: String) {

    def from: Int = table.start_pc
            def to: Int = table.end_pc
            def target: Int = table.handler_pc
            def catchType: Int = table.catch_type
            val isFinally = catchType == 0
            val isCatch = catchType != 0
            def catchHandler: String = catchHandlerName

            override def toString(): String = {
                    val buf = new StringBuilder
                            val padSize = 8
                            buf.append(pad(pad(from, 2), 8))
                            buf.append(pad(pad(to, 2), 8))
                            buf.append(pad(pad(target, 2), 8))
                            buf.append(catchHandler)
                            buf.toString
            }
}

/** TODO here: Возможно класс избыточен */
class TryHandler(val number: Int, val handler: ExceptionHandler)
class CatchHandler(val number: Int, val catchTypes: Array[Int], val handler: ExceptionHandler)
/** TODO here: Возможно класс избыточен */
class FinallyHandler(val number: Int, val handler: ExceptionHandler)
class ExceptionStatement(val tryHandler: TryHandler, val catches: Array[CatchHandler], val finalNumber: FinallyHandler) {
  override def toString(): String = {
    val buf = new StringBuilder
    buf.append("try from: ").append(tryHandler.number)
    if (!catches.isEmpty) {
      buf.append(catches.map(value => "catch from: " + value.number).toList.mkString("\n", "\n", ""))
    }
    if (finalNumber != null) {
      buf.append("\nfinally from: ").append(finalNumber.number)
    }
    buf.toString
  }
}

